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

package com.sun.xml.ws.transport.tcp.encoding;

import com.sun.xml.ws.transport.tcp.encoding.WSTCPFastInfosetStreamReaderRecyclable.RecycleAwareListener;
import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.encoding.ContentTypeImpl;
import com.sun.xml.ws.encoding.StreamSOAPCodec;
import com.sun.xml.ws.encoding.fastinfoset.FastInfosetStreamSOAPCodec;
import com.sun.xml.ws.message.stream.StreamHeader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

/**
 * @author Alexey Stashok
 */
public abstract class WSTCPFastInfosetStreamCodec implements Codec {
    private static final int DEFAULT_INDEXED_STRING_SIZE_LIMIT = 32;
    private static final int DEFAULT_INDEXED_STRING_MEMORY_LIMIT = 4 * 1024 * 1024; //4M limit
    
    private StAXDocumentParser _statefulParser;
    private StAXDocumentSerializer _serializer;
    
    private final StreamSOAPCodec _soapCodec;
    private final boolean _retainState;
    
    protected final ContentType _defaultContentType;
    
    private final RecycleAwareListener _readerRecycleListener;
    
    /* package */ WSTCPFastInfosetStreamCodec(SOAPVersion soapVersion,
            RecycleAwareListener readerRecycleListener, boolean retainState, String mimeType) {
        _soapCodec = StreamSOAPCodec.create(soapVersion);
        _readerRecycleListener = readerRecycleListener;
        _retainState = retainState;
        _defaultContentType = new ContentTypeImpl(mimeType);
    }
    
    /* package */ WSTCPFastInfosetStreamCodec(WSTCPFastInfosetStreamCodec that) {
        this._soapCodec = that._soapCodec.copy();
        this._readerRecycleListener = that._readerRecycleListener;
        this._retainState = that._retainState;
        this._defaultContentType = that._defaultContentType;
    }
    
    public String getMimeType() {
        return _defaultContentType.getContentType();
    }
    
    public ContentType getStaticContentType(Packet packet) {
        return getContentType(packet.soapAction);
    }
    
    public ContentType encode(Packet packet, OutputStream out) {
        if (packet.getMessage() != null) {
            final XMLStreamWriter writer = getXMLStreamWriter(out);
            try {
                packet.getMessage().writeTo(writer);
                writer.flush();
            } catch (XMLStreamException e) {
                throw new WebServiceException(e);
            }
        }
        return getContentType(packet.soapAction);
    }
    
    public ContentType encode(Packet packet, WritableByteChannel buffer) {
        //TODO: not yet implemented
        throw new UnsupportedOperationException();
    }
    
    public void decode(InputStream in, String contentType, Packet response) throws IOException {
        response.setMessage(
                _soapCodec.decode(getXMLStreamReader(in)));
    }
    
    public void decode(ReadableByteChannel in, String contentType, Packet response) {
        throw new UnsupportedOperationException();
    }
    
    protected abstract StreamHeader createHeader(XMLStreamReader reader, XMLStreamBuffer mark);
    
    protected abstract ContentType getContentType(String soapAction);
    
    private XMLStreamWriter getXMLStreamWriter(OutputStream out) {
        if (_serializer != null) {
            _serializer.setOutputStream(out);
            return _serializer;
        } else {
            StAXDocumentSerializer serializer = new StAXDocumentSerializer(out);
            if (_retainState) {
                SerializerVocabulary vocabulary = new SerializerVocabulary();
                serializer.setVocabulary(vocabulary);
                serializer.setAttributeValueSizeLimit(DEFAULT_INDEXED_STRING_SIZE_LIMIT);
                serializer.setCharacterContentChunkSizeLimit(DEFAULT_INDEXED_STRING_SIZE_LIMIT);
                serializer.setAttributeValueMapMemoryLimit(DEFAULT_INDEXED_STRING_MEMORY_LIMIT);
                serializer.setCharacterContentChunkMapMemoryLimit(DEFAULT_INDEXED_STRING_MEMORY_LIMIT);
            }
            _serializer = serializer;
            return serializer;
        }
    }
    
    private XMLStreamReader getXMLStreamReader(InputStream in) {
        if (_statefulParser != null) {
            _statefulParser.setInputStream(in);
            return _statefulParser;
        } else {
            StAXDocumentParser parser = new WSTCPFastInfosetStreamReaderRecyclable(in, _readerRecycleListener);
            parser.setStringInterning(true);
            if (_retainState) {
                ParserVocabulary vocabulary = new ParserVocabulary();
                parser.setVocabulary(vocabulary);
            }
            _statefulParser = parser;
            return _statefulParser;
        }
    }
    
    /**
     * Creates a new {@link FastInfosetStreamSOAPCodec} instance.
     *
     * @param version the SOAP version of the codec.
     * @return a new {@link WSTCPFastInfosetStreamCodec} instance.
     */
    public static WSTCPFastInfosetStreamCodec create(SOAPVersion version, RecycleAwareListener readerRecycleListener, boolean retainState) {
        if(version==null)
            // this decoder is for SOAP, not for XML/HTTP
            throw new IllegalArgumentException();
        switch(version) {
            case SOAP_11:
                return new WSTCPFastInfosetStreamSOAP11Codec(readerRecycleListener, retainState);
            case SOAP_12:
                return new WSTCPFastInfosetStreamSOAP12Codec(readerRecycleListener, retainState);
            default:
                throw new AssertionError();
        }
    }
}
