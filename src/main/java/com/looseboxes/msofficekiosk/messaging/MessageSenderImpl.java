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

package com.looseboxes.msofficekiosk.messaging;

import com.bc.config.Config;
import com.bc.elmi.pu.entities.Message;
import com.looseboxes.msofficekiosk.mapper.Mapper;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.net.UrlBuilderImpl;
import com.looseboxes.msofficekiosk.net.RequestClient;
import com.looseboxes.msofficekiosk.net.Response;
import com.looseboxes.msofficekiosk.net.ResponseHandler;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2019 12:54:30 PM
 */
public class MessageSenderImpl implements MessageSender {

    private static final Logger LOG = Logger.getLogger(MessageSenderImpl.class.getName());
    
    private final Mapper mapper;
    
    private final ConfigFactory configFactory;

    private final RequestClient<Response<Object>> requestClient;

    private final ResponseHandler responseHandler;

    public MessageSenderImpl(Mapper mapper, 
            ConfigFactory configFactory, 
            RequestClient<Response<Object>> requestClient,
            ResponseHandler responseHandler) {
        this.mapper = Objects.requireNonNull(mapper);
        this.configFactory = Objects.requireNonNull(configFactory);
        this.requestClient = Objects.requireNonNull(requestClient);
        this.responseHandler = Objects.requireNonNull(responseHandler);
    }
    
    @Override
    public void send(Message m, Set<String> usernames, String endpoint) 
            throws MalformedURLException, IOException, ParseException{
    
        send(m, usernames, endpoint, responseHandler);
    }

    @Override
    public void send(Message m, Set<String> usernames, 
            String endpoint, Consumer<Response<Object>> consumer) 
            throws MalformedURLException, IOException, ParseException{
    
        final Map<String, Set<String>> multipartParams = Collections.singletonMap("usernames", usernames);
        
        final Config config = configFactory.getConfig(ConfigService.APP_PROTECTED);
        
        final List<URL> urls = new UrlBuilderImpl(config).build(endpoint);
        
        send(m, multipartParams, urls, consumer);
    }

    @Override
    public void send(Message m, Map<String, Set<String>> multipartParams, Collection<URL> urls) 
            throws MalformedURLException, IOException, ParseException{
    
        LOG.finer(() -> "posting to urls: " + urls);

        final Map<String, String> bodyParts = buildRequestBody(m);
        
        for(URL url : urls) {
            
            LOG.finer(() -> "posting to URL: " + url + 
                    "\nmessage subject: " + m.getSubject() +
                    "\nmultipart params: " + multipartParams +
                    "\nbodyParts: " + bodyParts + 
                    "\nresponse handler type: " + responseHandler.getClass());
            requestClient.params(multipartParams).bodyParts(bodyParts).execute("POST", url, (response) -> {
                responseHandler.add(response);
            });
        }
        
        responseHandler.acceptAdded();
    }

    @Override
    public void send(Message m, Map<String, Set<String>> multipartParams, 
            Collection<URL> urls, Consumer<Response<Object>> consumer) 
            throws MalformedURLException, IOException, ParseException{

        LOG.finer(() -> "posting to urls: " + urls);
                
        final Map<String, String> bodyParts = buildRequestBody(m);
        
        for(URL url : urls) {
            
            LOG.finer(() -> "posting to URL: " + url + 
                    "\nmessage subject: " + m.getSubject() +
                    "\nmultipart params: " + multipartParams +
                    "\nbodyParts: " + bodyParts + 
                    "\nresponse handler type: " + consumer.getClass());

            requestClient.params(multipartParams).bodyParts(bodyParts).execute("POST", url, consumer);
        }
    }

    @Override
    public void send(Message m, Map<String, Set<String>> multipartParams, URL url) 
            throws MalformedURLException, IOException, ParseException{
    
        send(m, multipartParams, url, responseHandler);
    }

    @Override
    public void send(Message m, Map<String, Set<String>> multipartParams, 
            URL url, Consumer<Response<Object>> consumer) 
            throws MalformedURLException, IOException, ParseException{
    
        final Map<String, String> bodyParts = buildRequestBody(m);
        
        LOG.finer(() -> "posting to URL: " + url + 
                "\nmessage subject: " + m.getSubject() +
                "\nmultipart params: " + multipartParams +
                "\nbodyParts: " + bodyParts + 
                "\nresponse handler type: " + consumer.getClass());

        requestClient.params(multipartParams).bodyParts(bodyParts).execute("POST", url, consumer);
    }
    
    public Map<String, String> buildRequestBody(Message m) {
    
        final String json = mapper.toJsonString(m);
        
        LOG.log(Level.FINE, "Message json: {0}", json);
        
        return Collections.singletonMap(Message.class.getSimpleName(), json);
    }
}
