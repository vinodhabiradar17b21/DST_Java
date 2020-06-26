package com.increasingly.recommender.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import com.google.common.base.MoreObjects;

@Provider
@Compress
public class GZIPWriterInterceptor implements WriterInterceptor {
	private HttpHeaders httpHeaders;

    public GZIPWriterInterceptor(@Context @NotNull HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
}
	 
    @Override
    public void aroundWriteTo(WriterInterceptorContext context) 
        throws IOException, WebApplicationException {

        MultivaluedMap<String,String> requestHeaders =  httpHeaders.getRequestHeaders();
        List<String> acceptEncoding = MoreObjects.firstNonNull(
                requestHeaders.get(HttpHeaders.ACCEPT_ENCODING), new ArrayList<String>());

        // Compress if client accepts gzip encoding
        for (String s : acceptEncoding) {
            if(s.contains("gzip")) {
               
                MultivaluedMap<String, Object> headers = context.getHeaders();
                headers.add(HttpHeaders.CONTENT_ENCODING, "gzip"); 
                    //com.google.common.net.MediaType.GZIP);

                final OutputStream outputStream = context.getOutputStream();
                context.setOutputStream(new GZIPOutputStream(outputStream));

                break;
            }
        }
        context.proceed();
}
}