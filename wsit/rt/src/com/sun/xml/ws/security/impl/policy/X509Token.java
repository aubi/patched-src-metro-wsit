/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

/*
 * X509Token.java
 *
 * Created on January 5, 2006, 3:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import static com.sun.xml.ws.security.impl.policy.Constants.*;
import javax.xml.namespace.QName;


/**
 *
 * @author K.Venugopal@sun.com Abhijit.Das@Sun.Com
 */

public class X509Token extends PolicyAssertion implements com.sun.xml.ws.security.policy.X509Token,Cloneable, SecurityAssertionValidator{
    
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private boolean populated = false;
    private String tokenType = null;
    private HashSet<String> referenceType = null;
    private SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    private static QName itQname;
    private String includeToken;
    
    /**
     * Creates a new instance of X509Token
     */
    private String id = null;
    private boolean reqDK=false;
    
    private boolean isServer = false;
    
    public X509Token() {
        id= PolicyUtil.randomUUID();
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        includeToken = spVersion.includeTokenAlways;
        referenceType = new HashSet<String>();
    }
    
    public X509Token(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        
        id= PolicyUtil.randomUUID();
        referenceType = new HashSet<String>();
        
        String nsUri = getName().getNamespaceURI();
        if(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri.equals(nsUri)){
            spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
        } else if(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri.equals(nsUri)){
            spVersion = SecurityPolicyVersion.SECURITYPOLICY12NS;
        }
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        includeToken = spVersion.includeTokenAlways;
    }
    
    
    
    public void addTokenReferenceType(String tokenRefType) {
        referenceType.add(tokenRefType);
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public String getTokenType() {
        populate();
        return tokenType;
    }
    
    public Set getTokenRefernceType() {
        populate();
        return referenceType;
    }
    
    public String getIncludeToken() {
        populate();
        return includeToken;
    }
    
    public void setIncludeToken(String type) {
        includeToken = type;
    }
    
    
    public String getTokenId() {
        return id;
    }
    
    public boolean isRequireDerivedKeys() {
        populate();
        return reqDK;
    }
    
    public AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }
    private void populate(){
        populate(false);
    }
    
    private synchronized AssertionFitness populate(boolean isServer) {
        if(!populated){
            if(this.getAttributeValue(itQname)!=null){
                this.includeToken = this.getAttributeValue(itQname);
            }
            NestedPolicy policy = this.getNestedPolicy();
            if(policy == null){
                if(logger.getLevel() == Level.FINE){
                    logger.log(Level.FINE,"NestedPolicy is null");
                }
                populated = true;
                return fitness;
            }
            AssertionSet assertionSet = policy.getAssertionSet();
            for(PolicyAssertion assertion: assertionSet){
                if(PolicyUtil.isTokenReferenceType(assertion, spVersion)){
                    referenceType.add(assertion.getName().getLocalPart().intern());
                }else if(PolicyUtil.isTokenType(assertion, spVersion)) {
                    tokenType = assertion.getName().getLocalPart();
                }else if (PolicyUtil.isRequireDerivedKeys(assertion, spVersion)) {
                    reqDK = true;
                }else{
                    if(!assertion.isOptional()){
                        log_invalid_assertion(assertion, isServer,"X509Token");
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                    }
                }
            }
            
            populated = true;
        }
        return fitness;
    }
    
    public Object clone()throws CloneNotSupportedException{
        throw new UnsupportedOperationException("Fix me");
        //return new X509Token(this.nestedPolicy,getAttributes(),id);
    }

    public SecurityPolicyVersion getSecurityPolicyVersion() {
        return spVersion;
    }
}