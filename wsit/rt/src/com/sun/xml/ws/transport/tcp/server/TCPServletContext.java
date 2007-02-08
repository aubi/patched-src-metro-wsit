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

package com.sun.xml.ws.transport.tcp.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;

/**
 * @author Alexey Stashok
 */
@SuppressWarnings({"unchecked"})
public final class TCPServletContext implements TCPContext {
    
    private final ServletContext servletContext;
    private final Map<String, Object> attributes = new HashMap<String, Object>();
    
    public TCPServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
    public InputStream getResourceAsStream(final String resource) throws IOException {
        return servletContext.getResourceAsStream(resource);
    }
    
    public Set<String> getResourcePaths(final String path) {
        return (Set<String>) servletContext.getResourcePaths(path);
    }
    
    public URL getResource(final String resource) throws MalformedURLException {
        return servletContext.getResource(resource);
    }
    
    
    public Object getAttribute(final String name) {
        return attributes.get(name);
    }
    
    public void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }
}
