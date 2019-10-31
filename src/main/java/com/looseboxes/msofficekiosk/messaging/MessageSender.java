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

import com.bc.elmi.pu.entities.Message;
import com.looseboxes.msofficekiosk.net.Response;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2019 6:04:29 PM
 */
public interface MessageSender {

    void send(Message m, Set<String> usernames, String endpoint) 
            throws MalformedURLException, IOException, ParseException;

    void send(Message m, Map<String, Set<String>> multipartParams, URL url) 
                throws MalformedURLException, IOException, ParseException;

    void send(Message m, Map<String, Set<String>> multipartParams, Collection<URL> urls) 
            throws MalformedURLException, IOException, ParseException;

    void send(Message m, Map<String, Set<String>> multipartParams, 
            Collection<URL> urls, Consumer<Response<Object>> consumer) 
            throws MalformedURLException, IOException, ParseException;

    void send(Message m, Map<String, Set<String>> multipartParams, 
            URL url, Consumer<Response<Object>> consumer) 
            throws MalformedURLException, IOException, ParseException;

    void send(Message m, Set<String> usernames, String endpoint, 
            Consumer<Response<Object>> consumer) 
            throws MalformedURLException, IOException, ParseException;
}
