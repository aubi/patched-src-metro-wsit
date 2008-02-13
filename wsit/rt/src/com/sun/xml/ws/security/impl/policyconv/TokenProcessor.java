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

package com.sun.xml.ws.security.impl.policyconv;

import com.sun.xml.ws.addressing.policy.Address;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.security.impl.policy.Constants;
import com.sun.xml.ws.security.impl.policy.LogStringsMessages;
import com.sun.xml.ws.security.impl.policy.PolicyUtil;
import com.sun.xml.ws.security.policy.IssuedToken;
import com.sun.xml.ws.security.policy.KerberosToken;
import com.sun.xml.ws.security.policy.SamlToken;
import com.sun.xml.ws.security.policy.SecureConversationToken;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.ws.security.policy.Token;
import com.sun.xml.ws.security.policy.UserNameToken;
import com.sun.xml.ws.security.policy.X509Token;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.policy.PolicyGenerationException;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.DerivedTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.IssuedTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.KeyBindingBase;
import com.sun.xml.wss.impl.policy.mls.SecureConversationTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import static com.sun.xml.ws.security.impl.policy.Constants.logger;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class TokenProcessor {
    protected boolean isServer = false;
    protected boolean isIncoming = false;
    private PolicyID pid = null;
    /** Creates a new instance of TokenProcessor */
    public TokenProcessor(boolean isServer,boolean isIncoming, PolicyID pid) {
        this.isServer =isServer;
        this.isIncoming = isIncoming;
        this.pid = pid;
    }
    
    protected void setX509TokenRefType(AuthenticationTokenPolicy.X509CertificateBinding x509CB,X509Token token){
        Set tokenRefTypes = token.getTokenRefernceType();
        if(!tokenRefTypes.isEmpty()){
            if(tokenRefTypes.contains(Token.REQUIRE_THUMBPRINT_REFERENCE)){
                if(logger.isLoggable(Level.FINEST)){
                    logger.log(Level.FINEST," ReferenceType set to KeyBinding"+x509CB+" is Thumbprint");
                }
                x509CB.setReferenceType(SecurityPolicyUtil.convertToXWSSConstants(Token.REQUIRE_THUMBPRINT_REFERENCE));
            }else if(tokenRefTypes.contains(Token.REQUIRE_KEY_IDENTIFIER_REFERENCE)){
                if(logger.isLoggable(Level.FINEST)){
                    logger.log(Level.FINEST," ReferenceType set to KeyBinding"+x509CB+" is KeyIdentifier");
                }
                x509CB.setReferenceType(SecurityPolicyUtil.convertToXWSSConstants(Token.REQUIRE_KEY_IDENTIFIER_REFERENCE));
            }else if(tokenRefTypes.contains(Token.REQUIRE_ISSUER_SERIAL_REFERENCE)){
                if(logger.isLoggable(Level.FINEST)){
                    logger.log(Level.FINEST," ReferenceType set to KeyBinding "+x509CB+" is IssuerSerial");
                }
                x509CB.setReferenceType(SecurityPolicyUtil.convertToXWSSConstants(Token.REQUIRE_ISSUER_SERIAL_REFERENCE));
            }else{
                if(logger.isLoggable(Level.FINEST)){
                    logger.log(Level.FINEST," ReferenceType "+x509CB+" set is DirectReference");
                }
                x509CB.setReferenceType(MessageConstants.DIRECT_REFERENCE_TYPE);
            }
        }else{
            if(logger.isLoggable(Level.FINEST)){
                logger.log(Level.FINEST," ReferenceType set is REQUIRE_THUMBPRINT_REFERENCE");
            }
            x509CB.setReferenceType(MessageConstants.DIRECT_REFERENCE_TYPE);
        }
    }
    
    protected void setKerberosTokenRefType(AuthenticationTokenPolicy.KerberosTokenBinding kerberosBinding,
            KerberosToken token){
        Set tokenRefTypes = token.getTokenRefernceType();
        if(!tokenRefTypes.isEmpty()){
            if(tokenRefTypes.contains(Token.REQUIRE_KEY_IDENTIFIER_REFERENCE)){
                if(logger.isLoggable(Level.FINEST)){
                    logger.log(Level.FINEST," ReferenceType set to KeyBinding"+kerberosBinding+" is KeyIdentifier");
                }
                kerberosBinding.setReferenceType(SecurityPolicyUtil.convertToXWSSConstants(Token.REQUIRE_KEY_IDENTIFIER_REFERENCE));
            } else{
                if(logger.isLoggable(Level.FINEST)){
                    logger.log(Level.FINEST," ReferenceType "+kerberosBinding+" set is DirectReference");
                }
                kerberosBinding.setReferenceType(MessageConstants.DIRECT_REFERENCE_TYPE);
            }
        } else{
            if(logger.isLoggable(Level.FINEST)){
                logger.log(Level.FINEST," ReferenceType set is DirectReference");
            }
            kerberosBinding.setReferenceType(MessageConstants.DIRECT_REFERENCE_TYPE);
        }
    }
    
    public void addKeyBinding(WSSPolicy policy, Token token,boolean ignoreDK) throws PolicyException{
        PolicyAssertion tokenAssertion = (PolicyAssertion)token;
        SecurityPolicyVersion spVersion = SecurityPolicyUtil.getSPVersion(tokenAssertion);
        if(PolicyUtil.isX509Token(tokenAssertion, spVersion)){
            AuthenticationTokenPolicy.X509CertificateBinding x509CB =new AuthenticationTokenPolicy.X509CertificateBinding();
            //        (AuthenticationTokenPolicy.X509CertificateBinding)policy.newX509CertificateKeyBinding();
            X509Token x509Token = (X509Token)tokenAssertion;
            x509CB.setUUID(token.getTokenId());
            setX509TokenRefType(x509CB, x509Token);
            setTokenInclusion(x509CB,(Token) tokenAssertion);
            setTokenValueType(x509CB, tokenAssertion);
            
            if(x509Token.getIssuer() != null){
                Address addr = x509Token.getIssuer().getAddress();
                if(addr != null)
                    x509CB.setIssuer(addr.getURI().toString());
            } else if(x509Token.getIssuerName() != null){
                x509CB.setIssuer(x509Token.getIssuerName().getIssuerName());
            }
            
            if(x509Token.getClaims() != null){
                x509CB.setClaims(x509Token.getClaims().getClaimsAsBytes());
            }
            
            //x509CB.setPolicyToken(token);
            if(!ignoreDK && x509Token.isRequireDerivedKeys()){
                DerivedTokenKeyBinding dtKB =  new DerivedTokenKeyBinding();
                dtKB.setOriginalKeyBinding(x509CB);
                policy.setKeyBinding(dtKB);
                dtKB.setUUID(pid.generateID());
                
            }else{
                policy.setKeyBinding(x509CB);
            }
            
        }else if(PolicyUtil.isSamlToken(tokenAssertion, spVersion)){
            AuthenticationTokenPolicy.SAMLAssertionBinding sab = new AuthenticationTokenPolicy.SAMLAssertionBinding();
            //(AuthenticationTokenPolicy.SAMLAssertionBinding)policy.newSAMLAssertionKeyBinding();
            SamlToken samlToken = (SamlToken)tokenAssertion;
            sab.setUUID(token.getTokenId());
            sab.setSTRID(token.getTokenId());
            sab.setReferenceType(MessageConstants.DIRECT_REFERENCE_TYPE);
            setTokenInclusion(sab,(Token) tokenAssertion);
            //sab.setPolicyToken((Token) tokenAssertion);
            
            if(samlToken.getIssuer() != null){
                Address addr = samlToken.getIssuer().getAddress();
                if(addr != null)
                    sab.setIssuer(addr.getURI().toString());
            } else if(samlToken.getIssuerName() != null){
                sab.setIssuer(samlToken.getIssuerName().getIssuerName());
            }
            
            if(samlToken.isRequireDerivedKeys()){
                DerivedTokenKeyBinding dtKB =  new DerivedTokenKeyBinding();
                dtKB.setOriginalKeyBinding(sab);
                policy.setKeyBinding(dtKB);
                dtKB.setUUID(pid.generateID());
            }else{
                policy.setKeyBinding(sab);
            }
        }else if(PolicyUtil.isIssuedToken(tokenAssertion, spVersion)){
            IssuedTokenKeyBinding itkb = new IssuedTokenKeyBinding();
            setTokenInclusion(itkb,(Token) tokenAssertion);
            //itkb.setPolicyToken((Token) tokenAssertion);
            itkb.setUUID(((Token)tokenAssertion).getTokenId());
            IssuedToken it = (IssuedToken)tokenAssertion;
            
            if(it.getIssuer() != null){
                Address addr = it.getIssuer().getAddress();
                if(addr != null)
                    itkb.setIssuer(addr.getURI().toString());
            } else if(it.getIssuerName() != null){
                itkb.setIssuer(it.getIssuerName().getIssuerName());
            }
            
            if(it.isRequireDerivedKeys()){
                DerivedTokenKeyBinding dtKB =  new DerivedTokenKeyBinding();
                dtKB.setOriginalKeyBinding(itkb);
                policy.setKeyBinding(dtKB);
                dtKB.setUUID(pid.generateID());
            }else{
                policy.setKeyBinding(itkb);
            }
        }else if(PolicyUtil.isSecureConversationToken(tokenAssertion, spVersion)){
            SecureConversationTokenKeyBinding sct = new SecureConversationTokenKeyBinding();
            SecureConversationToken sctPolicy = (SecureConversationToken)tokenAssertion;
            
            if(sctPolicy.getIssuer() != null){
                Address addr = sctPolicy.getIssuer().getAddress();
                if(addr != null)
                    sct.setIssuer(addr.getURI().toString());
            } else if(sctPolicy.getIssuerName() != null){
                sct.setIssuer(sctPolicy.getIssuerName().getIssuerName());
            }
            
            if(sctPolicy.isRequireDerivedKeys()){
                DerivedTokenKeyBinding dtKB =  new DerivedTokenKeyBinding();
                dtKB.setOriginalKeyBinding(sct);
                policy.setKeyBinding(dtKB);
                dtKB.setUUID(pid.generateID());
            }else{
                policy.setKeyBinding(sct);
            }
            setTokenInclusion(sct,(Token) tokenAssertion);
            //sct.setPolicyToken((Token)tokenAssertion);
            sct.setUUID(((Token)tokenAssertion).getTokenId());
        }else{
            throw new UnsupportedOperationException("addKeyBinding for "+ token + "is not supported");
        }
        if(logger.isLoggable(Level.FINEST)){
            logger.log(Level.FINEST,"KeyBinding type "+policy.getKeyBinding()+ "has been added to policy"+policy);
        }
    }
    
    
    protected void setTokenInclusion(KeyBindingBase xwssToken , Token token) throws PolicyException  {
        boolean change = false;
        SecurityPolicyVersion spVersion = token.getSecurityPolicyVersion();
        if(this.isServer && !isIncoming){
            if(!spVersion.includeTokenAlways.equals(token.getIncludeToken())){
                xwssToken.setIncludeToken(SecurityPolicyVersion.SECURITYPOLICY200507.includeTokenNever);
                if(logger.isLoggable(Level.FINEST)){
                    logger.log(Level.FINEST,"Token Inclusion value of INCLUDE NEVER has been set to Token"+ xwssToken);
                }
                return;
            }
        }else if(!this.isServer && isIncoming){
            if(spVersion.includeTokenAlwaysToRecipient.equals(token.getIncludeToken()) ||
                    spVersion.includeTokenOnce.equals(token.getIncludeToken())){
                xwssToken.setIncludeToken(SecurityPolicyVersion.SECURITYPOLICY200507.includeTokenNever);
                
                if(logger.isLoggable(Level.FINEST)){
                    logger.log(Level.FINEST,"Token Inclusion value of INCLUDE NEVER has been set to Token"+ xwssToken);
                }
                return;
            }
        }
        
        if(logger.isLoggable(Level.FINEST)){
            logger.log(Level.FINEST,"Token Inclusion value of"+token.getIncludeToken()+" has been set to Token"+ xwssToken);
        }
        if(spVersion == SecurityPolicyVersion.SECURITYPOLICY200507){
            xwssToken.setIncludeToken(token.getIncludeToken());
        } else{
            // SecurityPolicy 1.2
            if(spVersion.includeTokenAlways.equals(token.getIncludeToken())){
                xwssToken.setIncludeToken(SecurityPolicyVersion.SECURITYPOLICY200507.includeTokenAlways);
            } else if(spVersion.includeTokenAlwaysToRecipient.equals(token.getIncludeToken())){
                xwssToken.setIncludeToken(SecurityPolicyVersion.SECURITYPOLICY200507.includeTokenAlwaysToRecipient);
            } else if(spVersion.includeTokenNever.equals(token.getIncludeToken())){
                xwssToken.setIncludeToken(SecurityPolicyVersion.SECURITYPOLICY200507.includeTokenNever);
            } else if(spVersion.includeTokenOnce.equals(token.getIncludeToken())){
                xwssToken.setIncludeToken(SecurityPolicyVersion.SECURITYPOLICY200507.includeTokenOnce);
            }
        }
    }
    
    public WSSPolicy getWSSToken(Token token) throws PolicyException {
        //TODO: IncludeToken
        SecurityPolicyVersion spVersion = SecurityPolicyUtil.getSPVersion((PolicyAssertion)token);
        if(PolicyUtil.isUsernameToken((PolicyAssertion) token, spVersion)){
            AuthenticationTokenPolicy.UsernameTokenBinding key = null;
            key  =  new AuthenticationTokenPolicy.UsernameTokenBinding();
            try {
                key.newTimestampFeatureBinding();
            } catch (PolicyGenerationException ex) {
                throw new PolicyException(ex);
            }
            key.setUUID(token.getTokenId());
            setTokenInclusion(key,token);
            UserNameToken ut = (UserNameToken)token;
            if(!ut.hasPassword()){
                key.setNoPassword(true);
            }
            //key.setPolicyToken(token);
            return key;
        }else if(PolicyUtil.isSamlToken((PolicyAssertion) token, spVersion)){
            AuthenticationTokenPolicy.SAMLAssertionBinding  key = null;
            key  = new AuthenticationTokenPolicy.SAMLAssertionBinding();
            setTokenInclusion(key,token);
            key.setAssertionType(AuthenticationTokenPolicy.SAMLAssertionBinding.SV_ASSERTION);
            //key.setPolicyToken(token);
            key.setUUID(token.getTokenId());
            key.setSTRID(token.getTokenId());
            return key;
        }else if(PolicyUtil.isIssuedToken((PolicyAssertion) token, spVersion)){
            IssuedTokenKeyBinding key = new IssuedTokenKeyBinding();
            setTokenInclusion(key,token);
            //key.setPolicyToken(token);
            key.setUUID(token.getTokenId());
            return key;
        }else if(PolicyUtil.isSecureConversationToken((PolicyAssertion) token, spVersion)){
            SecureConversationTokenKeyBinding key =  new SecureConversationTokenKeyBinding();
            setTokenInclusion(key,token);
            //key.setPolicyToken(token);
            key.setUUID(token.getTokenId());
            return key;
        }else if(PolicyUtil.isX509Token((PolicyAssertion) token, spVersion)){
            AuthenticationTokenPolicy.X509CertificateBinding  xt =  new AuthenticationTokenPolicy.X509CertificateBinding();
            xt.setUUID(token.getTokenId());
            //xt.setPolicyToken(token);
            setTokenInclusion(xt,token);
            setX509TokenRefType(xt, (X509Token) token);
            return xt;
        }
        if(logger.isLoggable(Level.SEVERE)){
            logger.log(Level.SEVERE,LogStringsMessages.SP_0107_UNKNOWN_TOKEN_TYPE(token));
        }
        
        throw new UnsupportedOperationException("Unsupported  "+ token + "format");
    }
    
    public void setTokenValueType(AuthenticationTokenPolicy.X509CertificateBinding x509CB, PolicyAssertion tokenAssertion){
        
        NestedPolicy policy = tokenAssertion.getNestedPolicy();
        if(policy==null){
            return;
        }
        AssertionSet as = policy.getAssertionSet();
        Iterator<PolicyAssertion> itr = as.iterator();
        while(itr.hasNext()){
            PolicyAssertion policyAssertion = (PolicyAssertion)itr.next();
            if(policyAssertion.getName().getLocalPart().equals(Constants.WssX509V1Token11)||policyAssertion.getName().getLocalPart().equals(Constants.WssX509V1Token10)){
                x509CB.setValueType(MessageConstants.X509v1_NS);
            }else if(policyAssertion.getName().getLocalPart().equals(Constants.WssX509V3Token10)||policyAssertion.getName().getLocalPart().equals(Constants.WssX509V3Token11)){
                x509CB.setValueType(MessageConstants.X509v3_NS);
            }
        }
    }
    
    public void setTokenValueType(AuthenticationTokenPolicy.KerberosTokenBinding kerberosBinding, PolicyAssertion tokenAssertion){
        
        NestedPolicy policy = tokenAssertion.getNestedPolicy();
        if(policy==null){
            return;
        }
        AssertionSet as = policy.getAssertionSet();
        Iterator<PolicyAssertion> itr = as.iterator();
        while(itr.hasNext()){
            PolicyAssertion policyAssertion = (PolicyAssertion)itr.next();
            if(policyAssertion.getName().getLocalPart().equals(Constants.WssKerberosV5ApReqToken11)){
                kerberosBinding.setValueType(MessageConstants.KERBEROS_V5_APREQ);
            } else if(policyAssertion.getName().getLocalPart().equals(Constants.WssGssKerberosV5ApReqToken11)){
                kerberosBinding.setValueType(MessageConstants.KERBEROS_V5_GSS_APREQ_1510);
            }
        }
    }
    
    
}
