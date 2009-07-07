/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
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

package com.sun.xml.ws.management.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import java.util.Collection;
import javax.xml.namespace.QName;

/**
 *
 * @author Fabian Ritzmann
 */
public class ManagementAssertion extends PolicyAssertion {

    // TODO: Consolidate with other declarations of this namespace
    private static final String MANAGEMENT_NAMESPACE = "http://java.sun.com/xml/ns/metro/management";
    private static final QName MANAGED_SERVICE_NAME = new QName(MANAGEMENT_NAMESPACE, "ManagedService");

    public ManagementAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative)
            throws AssertionCreationException {
        super(data, assertionParameters, nestedAlternative);
        if (!MANAGED_SERVICE_NAME.equals(data.getName())) {
            throw new AssertionCreationException(data, "Expected policy assertion ManagedService in this namespace");
        }
        if (!data.containsAttribute(new QName("", "id"))) {
            throw new AssertionCreationException(data, "ManagedService policy assertion must have id attribute");
        }
    }

}