/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.sun.xml.ws.rx.rm.runtime;

import com.oracle.webservices.api.rm.InboundAccepted;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.commons.ha.HaContext;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.protocol.AcknowledgementData;
import com.sun.xml.ws.rx.rm.runtime.delivery.Postman;
import com.sun.xml.ws.rx.rm.runtime.transaction.TransactionPropertySet;
import com.sun.xml.ws.rx.util.AbstractResponseHandler;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
class ServerDestinationDeliveryCallback implements Postman.Callback {

    private static class ResponseCallbackHandler extends AbstractResponseHandler implements Fiber.CompletionCallback {

        /**
         * The property with this key may be set by JCaps in the message context to indicate
         * whether the message that was delivered to the application endpoint should be
         * acknowledged or not.
         *
         * The property value may be "true" or "false", "true" is default.
         *
         * Introduction of this property is required as a temporary workaround for missing
         * concept of distinguishing between system and application errors in JAXWS RI.
         * The workaround should be removed once the missing concept is introduced.
         */
        private static final String RM_ACK_PROPERTY_KEY = "RM_ACK";
        //
        private final JaxwsApplicationMessage request;
        private final RuntimeContext rc;

        public ResponseCallbackHandler(JaxwsApplicationMessage request, RuntimeContext rc) {
            super(rc.suspendedFiberStorage, request.getCorrelationId());
            this.request = request;
            this.rc = rc;
        }

        public void onCompletion(Packet response) {
            try {
                HaContext.initFrom(response);
                /**
                 * This if clause is a part of the RM-JCaps private contract. JCaps may decide
                 * that the request it received should be resent and thus it should not be acknowledged.
                 *
                 * For more information, see documentation of RM_ACK_PROPERTY_KEY constant field.
                 */
                String rmAckPropertyValue = String.class.cast(response.invocationProperties.remove(RM_ACK_PROPERTY_KEY));
                if (rmAckPropertyValue == null || Boolean.parseBoolean(rmAckPropertyValue)) {
                    rc.destinationMessageHandler.acknowledgeApplicationLayerDelivery(request);

                    //Commit the TX if it was started by us and if it is still active
                    TransactionPropertySet ps = 
                            response.getSatellite(TransactionPropertySet.class);
                    boolean txOwned = (ps != null && ps.isTransactionOwned());

                    if (txOwned) {
                        if (rc.transactionHandler.isActive() || rc.transactionHandler.isMarkedForRollback()) {

                            if (LOGGER.isLoggable(Level.FINE)) {
                                LOGGER.fine("Transaction status before commit: " + rc.transactionHandler.getStatusAsString());
                            }

                            rc.transactionHandler.commit();
                        } else {
                            if (LOGGER.isLoggable(Level.SEVERE)) {
                                LOGGER.severe("Unexpected transaction status before commit: " + rc.transactionHandler.getStatusAsString());
                            }
                        }
                    } else {
                        //Do nothing as we don't own the TX
                    }
                } else {
                    /**
                     * Private contract between Metro RM and Sun JavaCAPS (BPM) team
                     * to let them control the acknowledgement of the message.
                     * Does not apply to anyone else.
                     */
                    LOGGER.finer(String.format("Value of the '%s' property is '%s'. The request has not been acknowledged.", RM_ACK_PROPERTY_KEY, rmAckPropertyValue));
                    RedeliveryTaskExecutor.register(
                            request,
                            rc.configuration.getRmFeature().getRetransmissionBackoffAlgorithm().getDelayInMillis(request.getNextResendCount(), rc.configuration.getRmFeature().getMessageRetransmissionInterval()),
                            TimeUnit.MILLISECONDS,
                            rc.destinationMessageHandler,
                            response.component);
                    return;
                }

                if (response.getMessage() == null) {
                    //was one-way request - create empty acknowledgement message if needed
                    AcknowledgementData ackData = rc.destinationMessageHandler.getAcknowledgementData(request.getSequenceId());
                    if (ackData.getAckReqestedSequenceId() != null || ackData.containsSequenceAcknowledgementData()) {
                        //create acknowledgement response only if there is something to send in the SequenceAcknowledgement header
                        response = rc.communicator.setEmptyResponseMessage(response, request.getPacket(), rc.rmVersion.protocolVersion.sequenceAcknowledgementAction);
                        rc.protocolHandler.appendAcknowledgementHeaders(response, ackData);
                    }

                    resumeParentFiber(response);
                } else {
                    //two-way, response is stored as part of registerMessage to support replay
                    JaxwsApplicationMessage message = new JaxwsApplicationMessage(response, getCorrelationId());
                    rc.sourceMessageHandler.registerMessage(message, rc.getBoundSequenceId(request.getSequenceId()));
                    rc.sourceMessageHandler.putToDeliveryQueue(message);
                }

                // TODO handle RM faults
            } catch (final Throwable t) {
                //Roll back the TX if it was started by us and if it is not committed
                TransactionPropertySet ps =
                        response.getSatellite(TransactionPropertySet.class);
                boolean txOwned = (ps != null && ps.isTransactionOwned());

                if (txOwned) {
                    if (rc.transactionHandler.isActive() || rc.transactionHandler.isMarkedForRollback()) {

                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.fine("Transaction status before rollback: " + rc.transactionHandler.getStatusAsString());
                        }

                        rc.transactionHandler.rollback();
                    } else {
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.severe("Unexpected transaction status before rollback: " + rc.transactionHandler.getStatusAsString());
                        }
                    }
                } else {
                    //Don't roll back as we don't own the TX but if active then mark for roll back
                    if (rc.transactionHandler.userTransactionAvailable() &&
                            rc.transactionHandler.isActive()) {
                        rc.transactionHandler.setRollbackOnly();
                    }
                }

                onCompletion(t);
            } finally {
                HaContext.clear();
            }
        }

        public void onCompletion(Throwable error) {
            //Resume original Fiber with Throwable. 
            //No retry attempts to send request to application layer.
            resumeParentFiber(error);
        }
    }
    
    private static final Logger LOGGER = Logger.getLogger(ServerDestinationDeliveryCallback.class);
    private final RuntimeContext rc;

    public ServerDestinationDeliveryCallback(RuntimeContext rc) {
        this.rc = rc;
    }

    public void deliver(ApplicationMessage message) {
        if (message instanceof JaxwsApplicationMessage) {
            deliver(JaxwsApplicationMessage.class.cast(message));
        } else {
            throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSRM_1141_UNEXPECTED_MESSAGE_CLASS(
                    message.getClass().getName(),
                    JaxwsApplicationMessage.class.getName())));
        }
    }

    private void deliver(JaxwsApplicationMessage message) {
        Fiber.CompletionCallback responseCallback = new ResponseCallbackHandler(message, rc);
        InboundAccepted inboundAccepted = new InboundAcceptedImpl(message, rc);
        Packet request = message.getPacket().copy(true);
        request.addSatellite(inboundAccepted);
        rc.communicator.sendAsync(request, responseCallback, null);
    }
    
    @Override
    public RuntimeContext getRuntimeContext() {
        return rc;
    }
}
