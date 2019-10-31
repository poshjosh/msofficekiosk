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

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Chinomso Bassey Ikwuagwu on May 6, 2019 4:08:57 PM
 */
public interface RequestClient<R> {
    
    /**
     * Each key may have multiple values
     * @param map The request headers. e.g Content-Type=application/json
     * @return (this) the calling instance
     */
    RequestClient<R> headers(Map map);

    /**
     * Each key may have multiple values
     * @param map The request parameters. e.g username=JohnDoe
     * @return (this) the calling instance
     */
    default RequestClient<R> params(Map map) {
        return params(map, false);
    }

    /**
     * Each key may have multiple values
     * @param map The request parameters. e.g username=JohnDoe
     * @param encode If true the parameters will be encoded before adding to the request
     * @return (this) the calling instance
     */
    RequestClient<R> params(Map map, boolean encode);
    
    /**
     * Supported body parts are:
     * <ul>
     *   <li>{@link java.io.File File}</li>
     *   <li>{@link java.lang.String String}</li>
     *   <li>byte []</li>
     * </ul>
     * Each key may have multiple values
     * @param map The request body parts
     * @return (this) the calling instance
     */
    RequestClient<R> bodyParts(Map map);

    void execute(String method, URL url, Consumer<R> consumer) 
            throws IOException, ParseException;
}
/**
 * 
    default void executeRequest(String method, URL url, 
            String name, File value, Consumer<R> consumer) 
            throws IOException, ParseException{
    
        executeRequestFiles(method, url, 
                Collections.singletonMap(name, value),
                Collections.EMPTY_MAP, consumer);
    }

    void executeRequestFiles(String method, URL url, Map<String, File> bodyParts, 
            Map<String, String> multipartParams, Consumer<R> consumer) 
            throws IOException, ParseException;

    default void executeRequest(String method, URL url, 
            String name, String value, Consumer<R> consumer) 
            throws IOException, ParseException{
    
        executeRequestString(method, url, 
                Collections.singletonMap(name, value),
                Collections.EMPTY_MAP, consumer);
    }

    void executeRequestString(String method, URL url, Map<String, String> bodyParts, 
            Map<String, String> multipartParams, Consumer<R> consumer) 
            throws IOException, ParseException;
    
    default void executeRequest(String method, URL url, 
            String name, byte[] value, Consumer<R> consumer) 
            throws IOException, ParseException{
    
        executeRequest(method, url, 
                Collections.singletonMap(name, value),
                Collections.EMPTY_MAP, consumer);
    }

    void executeRequest(String method, URL url, Map<String, byte[]> bodyParts, 
            Map<String, String> multipartParams, Consumer<R> consumer) 
            throws IOException, ParseException;

    void executeRequest(String method, URL url, Consumer<R> consumer) 
            throws IOException, ParseException;
    
 * 
 */
