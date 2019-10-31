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

import com.looseboxes.msofficekiosk.mapper.Mapper;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 28, 2019 12:13:47 AM
 */
public class JsonRequestClientImpl extends OkHttpRequestClient<Response<Object>> implements JsonRequestClient {

    private static final Logger LOG = Logger.getLogger(JsonRequestClientImpl.class.getName());
    
    public static class ResponseParserImpl implements Function<okhttp3.Response, Response<Object>>{
        private final Mapper mapper;
        public ResponseParserImpl(Mapper mapper) {
            this.mapper = Objects.requireNonNull(mapper);
        }
        @Override
        public Response<Object> apply(okhttp3.Response response) {
            
            final HttpUrl url = response.request().url();

            String content = null;
            try{
                content = getContentAndCloseBody(response).orElse(null);
            }catch(IOException e) {
                final String m = "Exception reading response from: " + url + 
                        ", code: " + response.code() + ", message: " + response.message();
                LOG.log(Level.WARNING, m, e);
            }
            
            Response<Object> output = null;
            
            try{

                output = content == null ? 
                        new Response() : mapper.toObject(content, Response.class);
                
            }catch(java.text.ParseException e) {

                final String msg = "Exception parsing response from: " + url + 
                        ", code: " + response.code() + ", message: " + response.message();

                LOG.log(Level.WARNING, msg, e);
                
                final int pos = e.getErrorOffset();

                if(content != null && content.length() > pos) {
                    LOG.log(Level.WARNING, "Printing response body from error position:\n{0}", content.substring(pos));
                }
            }
            
            if(output == null) {
                output = new Response();
            }
            
            if(output.getCode() < 1) {
                output.setCode(response.code());
            }

            if(output.getMessage() == null || output.getMessage().isEmpty()) {
                output.setMessage(response.message());
            }

            return output;
        }
        
        public  Optional<String> getContentAndCloseBody(okhttp3.Response response) throws IOException {

            try(final ResponseBody body = response.body()) {

                final String content = body.string();

                return Optional.ofNullable(content);

            }
        }
    }
    
    public JsonRequestClientImpl(OkHttpClient httpClient, Mapper mapper) {
        super(httpClient, new ResponseParserImpl(mapper));
    }
}
