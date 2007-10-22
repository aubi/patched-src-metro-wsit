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
package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator.Fitness;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import static com.sun.xml.ws.security.impl.policy.Constants.*;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.ArrayList;
import javax.xml.namespace.QName;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class SecurityPolicySelector {
    private static final ArrayList<QName> supportedAssertions = new ArrayList<QName>();
    static{
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,CanonicalizationAlgorithm));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Basic256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Basic192));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Basic128));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,TripleDes));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Basic256Rsa15));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Basic192Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Basic192Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,TripleDesRsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Basic256Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Basic256Rsa15));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Basic192Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Basic128Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Basic192Sha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,TripleDesSha256));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Basic256Sha256Rsa15));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Basic192Sha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Basic128Sha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,TripleDesSha256Rsa15));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,InclusiveC14N));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,InclusiveC14NWithComments));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,ExclusiveC14NWithComments));
        //     supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,SoapNormalization10));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,STRTransform10));
        //      supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,XPath10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,XPathFilter20));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Strict));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Lax));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,LaxTsFirst));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,LaxTsLast));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,IncludeTimestamp));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,EncryptBeforeSigning));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,EncryptSignature));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,ProtectTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,OnlySignEntireHeadersAndBody));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Body));
        //     supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Header));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,XPath));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssUsernameToken10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssUsernameToken11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,Issuer));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,RequestSecurityTokenTemplate));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,RequireDerivedKeys));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,RequireExternalReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,RequireInternalReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,RequireKeyIdentifierReference));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,RequireIssuerSerialReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,RequireEmbeddedTokenReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,RequireThumbprintReference));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssX509V1Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssX509V3Token10));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssX509Pkcs7Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssX509PkiPathV1Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssX509V1Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssX509V3Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssX509Pkcs7Token11));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssX509PkiPathV1Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssKerberosV5ApReqToken11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssGssKerberosV5ApReqToken11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,SC10SecurityContextToken));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssSamlV10Token10));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssSamlV11Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssSamlV10Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssSamlV11Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssSamlV20Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssRelV10Token10));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssRelV20Token10));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssRelV10Token11));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,WssRelV20Token11));
        //     supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,X509V3Token));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,SupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,SignedSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,EndorsingSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,SignedEndorsingSupportingTokens));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,MustSupportRefKeyIdentifier));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,MustSupportRefIssuerSerial));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,MustSupportRefExternalURI));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,MustSupportRefEmbeddedToken));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,MustSupportRefKeyIdentifier));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,MustSupportRefIssuerSerial));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,MustSupportRefExternalURI));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,MustSupportRefEmbeddedToken));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,MustSupportRefThumbprint));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,MustSupportRefEncryptedKey));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,MustSupportClientChallenge));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,MustSupportServerChallenge));
        
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,RequireClientEntropy));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,RequireServerEntropy));
        supportedAssertions.add(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri,MustSupportIssuedTokens));
        
        supportedAssertions.add(new QName(TRUST_NS,RequestSecurityToken));
        supportedAssertions.add(new QName(TRUST_NS,RequestType));
        supportedAssertions.add(new QName(TRUST_NS,TokenType));
        supportedAssertions.add(new QName(TRUST_NS,AuthenticationType));
        
        supportedAssertions.add(new QName(TRUST_NS,OnBehalfOf));
        supportedAssertions.add(new QName(TRUST_NS,KeyType));
        supportedAssertions.add(new QName(TRUST_NS,KeySize));
        supportedAssertions.add(new QName(TRUST_NS,SignatureAlgorithm));
        
        supportedAssertions.add(new QName(TRUST_NS,EncryptionAlgorithm));
        supportedAssertions.add(new QName(TRUST_NS,CanonicalizationAlgorithm));
        supportedAssertions.add(new QName(TRUST_NS,ComputedKeyAlgorithm));
        
        supportedAssertions.add(new QName(TRUST_NS,Encryption));
        supportedAssertions.add(new QName(TRUST_NS,ProofEncryption));
        supportedAssertions.add(new QName(TRUST_NS,UseKey));
        supportedAssertions.add(new QName(TRUST_NS,SignWith));
        
        supportedAssertions.add(new QName(TRUST_NS,EncryptWith));
        supportedAssertions.add(new QName("http://schemas.sun.com/2006/03/wss/server","DisableStreamingSecurity"));
        supportedAssertions.add(new QName("http://schemas.sun.com/2006/03/wss/client","DisableStreamingSecurity"));
        
        supportedAssertions.add(new QName("http://schemas.sun.com/2006/03/wss/server","DisableTimestampSigning"));
        supportedAssertions.add(new QName("http://schemas.sun.com/2006/03/wss/client","DisableTimestampSigning"));

        supportedAssertions.add(new QName("http://schemas.sun.com/2006/03/wss/server","DisableInclusivePrefixList"));
        supportedAssertions.add(new QName("http://schemas.sun.com/2006/03/wss/client","DisableInclusivePrefixList"));
        
        supportedAssertions.add(new QName("http://schemas.sun.com/2006/03/wss/server","EncryptHeaderContent"));
        supportedAssertions.add(new QName("http://schemas.sun.com/2006/03/wss/client","EncryptHeaderContent"));
        
        //supportedAssertions.add(new QName(SUN_WSS_SECURITY_CLIENT_POLICY_NS,CallbackHandler));
        //supportedAssertions.add(new QName(SUN_WSS_SECURITY_CLIENT_POLICY_NS,KeyStore));
        //supportedAssertions.add(new QName(SUN_WSS_SECURITY_CLIENT_POLICY_NS,TrustStore));
        
        //supportedAssertions.add(new QName(SUN_SECURE_CLIENT_CONVERSATION_POLICY_NS,"SCClientConfiguration"));
        
    }
    
    /** Creates a new instance of SecurityPolicySelector. To be used by appropriate service finder */
    public SecurityPolicySelector() {
    }
    
    public Fitness getFitness(PolicyAssertion policyAssertion) {
        if (policyAssertion instanceof SecurityAssertionValidator) {
            return (((SecurityAssertionValidator)policyAssertion).validate(true) == SecurityAssertionValidator.AssertionFitness.IS_VALID )? Fitness.SUPPORTED : Fitness.UNSUPPORTED;
        } else if (supportedAssertions.contains(policyAssertion.getName())) {
            return Fitness.SUPPORTED;
        } else {
            return Fitness.UNKNOWN;
        }
    }
}
