/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.xml.ws.transport.tcp.wsit;

import javax.xml.namespace.QName;

/**
 * @author Alexey Stashok
 */
public final class TCPConstants {
    
    /** Prevents instantiation */
    private TCPConstants() {}
    
    public static final String TCPTRANSPORT_POLICY_NAMESPACE_URI = "http://java.sun.com/xml/ns/wsit/2006/09/policy/soaptcp/service";
    public static final QName TCPTRANSPORT_POLICY_ASSERTION = new QName(TCPTRANSPORT_POLICY_NAMESPACE_URI, "OptimizedTCPTransport");
    
    public static final String CLIENT_TRANSPORT_NS = "http://java.sun.com/xml/ns/wsit/2006/09/policy/transport/client";
    public static final QName SELECT_OPTIMAL_TRANSPORT_ASSERTION = new QName(CLIENT_TRANSPORT_NS, "AutomaticallySelectOptimalTransport");

    public static final String TCPTRANSPORT_CONNECTION_MANAGEMENT_NAMESPACE_URI = "http://java.sun.com/xml/ns/wsit/2006/09/policy/soaptcp";
    public static final QName TCPTRANSPORT_CONNECTION_MANAGEMENT_ASSERTION = new QName(TCPTRANSPORT_CONNECTION_MANAGEMENT_NAMESPACE_URI, "ConnectionManagement");
    public static final String TCPTRANSPORT_CONNECTION_MANAGEMENT_HIGH_WATERMARK_ATTR = "HighWatermark";
    public static final String TCPTRANSPORT_CONNECTION_MANAGEMENT_MAX_PARALLEL_CONNECTIONS_ATTR = "MaxParallelConnections";
    public static final String TCPTRANSPORT_CONNECTION_MANAGEMENT_NUMBER_TO_RECLAIM_ATTR = "NumberToReclaim";

}
