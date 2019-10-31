/*
 * Copyright 2019 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.looseboxes.msofficekiosk.net;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 26, 2019 4:03:15 PM
 */
public class OkHttpRequestClient<T> implements RequestClient<T>{

    private static final Logger LOG = Logger.getLogger(OkHttpRequestClient.class.getName());

    private final OkHttpClient httpClient;
    private final Function<Response, T> responseParser;
    private Map headers;
    private Map params;
    private boolean encode;
    private Map bodyParts;

    public OkHttpRequestClient(OkHttpClient httpClient, Function<Response, T> responseParser) {
        this.httpClient = Objects.requireNonNull(httpClient);
        this.responseParser = Objects.requireNonNull(responseParser);
    }
    
    public void reset() {
        headers = null;
        params = null;
        encode = false;
        bodyParts = null;
    }

    @Override
    public RequestClient<T> headers(Map map) {
        if(headers == null) {
            headers = new HashMap<>();
        }
        headers.putAll(map);
        return this;
    }

    @Override
    public RequestClient<T> params(Map map, boolean encode) {
        if(params == null) {
            params = new HashMap<>();
        }
        params.putAll(map);
        this.encode = encode;
        return this;
    }

    @Override
    public RequestClient<T> bodyParts(Map map) {
        if(bodyParts == null) {
            bodyParts = new HashMap<>();
        }
        bodyParts.putAll(map);
        return this;
    }

    @Override
    public void execute(String method, URL url, Consumer<T> consumer) 
            throws IOException, ParseException {
        try{
            
            final Request.Builder output = new Request.Builder().url(url);

            this.addHeaders(output);

            final RequestBody body;

            if(bodyParts == null || bodyParts.isEmpty()) {

                if(params == null || params.isEmpty()) {
                    
                    body = null;

                }else{
                    
                    if(getContentType(null) == null) {
                        if(headers == null) {
                            headers = new HashMap<>();
                        }
                        headers.put("Content-Type", MultipartBody.FORM);
                    }

                    final FormBody.Builder builder = new FormBody.Builder();

                    this.addFormParams(builder);

                    body = builder.build();
                }
            }else{

                final MultipartBody.Builder builder = new MultipartBody.Builder();

                builder.setType(MultipartBody.MIXED);

                this.addMultipartParams(builder);

                this.addBodyParts(builder);

                body = builder.build();
            }

            final Request request = output.method(method, body).build();

            final Response response = httpClient.newCall(request).execute();

            final T t = responseParser.apply(response);
            
            consumer.accept(t);
            
        }finally{
            
            reset();
        }
    }
    
    
    protected Request.Builder addHeaders(final Request.Builder builder) {
        
        if(headers != null && ! headers.isEmpty()) {
            for(Object key : headers.keySet()) {
                final String name = key.toString();
                final Object val = headers.get(key);
                if(val instanceof Object[]) {
                    final Object [] arr = (Object[])val;
                    for(Object e : arr){
                        builder.addHeader(name, e.toString());
                    }
                }else if(val instanceof Collection){
                    final Collection c = (Collection)val;
                    for(Object e : c){
                        builder.addHeader(name, e.toString());
                    }
                }else{
                    builder.header(name, val.toString());
                }
            }
        }
                
        return builder;
    }

    public FormBody.Builder addFormParams(FormBody.Builder builder) {
        
        if(params != null && !params.isEmpty()) {
            
            for(Object key : params.keySet()) {

                final Object val = params.get(key);

                final String keyStr = key.toString();
                
                if(val instanceof Collection) {
                    final Collection c = (Collection)val;
                    for(Object o : c) {
                        if(encode) {
                            builder.addEncoded(keyStr, o.toString());
                        }else{
                            builder.add(keyStr, o.toString());
                        }
                    }
                }else if(val instanceof Object[]) {
                    final Object[] arr = (Object[])val;
                    for(Object o : arr) {
                        if(encode) {
                            builder.addEncoded(keyStr, o.toString());
                        }else{
                            builder.add(keyStr, o.toString());
                        }
                    }
                }else{
                    if(encode) {
                        builder.addEncoded(keyStr, val.toString());
                    }else{
                        builder.add(keyStr, val.toString());
                    }
                }
            }
        }
        
        return builder;
    }

    public MultipartBody.Builder addMultipartParams(final MultipartBody.Builder builder) {
        
        builder.setType(MultipartBody.MIXED);
        
        if(params != null && !params.isEmpty()) {
            for(Object key : params.keySet()) {

                final Object val = params.get(key);
                
                final String keyStr = key.toString();
                
                if(val instanceof Collection) {
                    final Collection c = (Collection)val;
                    for(Object o : c) {
                        builder.addFormDataPart(keyStr, o.toString());
                    }
                }else if(val instanceof Object[]) {
                    final Object[] arr = (Object[])val;
                    for(Object o : arr) {
                        builder.addFormDataPart(keyStr, o.toString());
                    }
                }else{
                    builder.addFormDataPart(keyStr, val.toString());
                }
            }
        }
        
        return builder;
    }

    public MultipartBody.Builder addBodyParts(MultipartBody.Builder builder){

        if(bodyParts != null && !bodyParts.isEmpty()) {
            
            for(Object key : bodyParts.keySet()) {
                
                final Object val = bodyParts.get(key);
                
                final String keyStr = key.toString();
                
                if(val instanceof Collection) {
                    final Collection c = (Collection)val;
                    for(Object o : c) {
                        add(builder, keyStr, o);
                    }
                }else if(val instanceof Object[]) {
                    final Object[] arr = (Object[])val;
                    for(Object o : arr) {
                        add(builder, keyStr, o);
                    }
                }else{
                    add(builder, keyStr, val);
                }
            }
        }
        
        return builder;
    }
    
    public boolean add(MultipartBody.Builder builder, String keyStr, Object val) {
        boolean added = true;
        if(val instanceof File) {
            
            final File content = (File)val;
            LOG.finer(() -> "Adding multipart File: " + keyStr + " = " + content);
            builder.addFormDataPart(keyStr, content.getName(), 
                    okhttp3.RequestBody.create(MediaType.parse("application/octet-stream"), content));
            
        }else if(val instanceof String) {
            
            final String content = (String)val;
            LOG.finer(() -> "Adding multipart String: " + keyStr + " = " + content);
            builder.addFormDataPart(keyStr, null, 
                    okhttp3.RequestBody.create(MediaType.parse(getContentTypeFor(keyStr, content)), content));
            
        }else if(val instanceof byte[]) {
            
            final byte[] content = (byte[])val;
            LOG.finer(() -> "Adding multipart Bytes: " + keyStr + " = " + new String(content));
            builder.addFormDataPart(keyStr, null, 
                    okhttp3.RequestBody.create(MediaType.parse(getContentTypeFor(keyStr, content)), content));
        }else{
            added = false;
            LOG.log(Level.WARNING, "Skipping unexpected content. {0} = {1}", new Object[]{keyStr, val});
        }
        return added;
    }
    
    public String getContentTypeFor(String key, Object val) {
        return getContentType("application/octet-stream");
    }

    public String getContentType(String outputIfNone) {
        final Object contentType = headers == null ? null : headers.get("Content-Type");
        return contentType == null ? outputIfNone : contentType.toString();
    }

    public Map getHeaders() {
        return headers;
    }

    public Map getParams() {
        return params;
    }

    public boolean isEncode() {
        return encode;
    }

    public Map getBodyParts() {
        return bodyParts;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public Function<Response, T> getResponseParser() {
        return responseParser;
    }
}
