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

import com.bc.socket.io.messaging.data.Devicedetails;
import com.bc.util.JsonFormat;
import com.looseboxes.msofficekiosk.Cache;
import com.looseboxes.msofficekiosk.mapper.Mapper;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 26, 2019 11:40:48 PM
 */
public class SyncDataWithServerImpl implements SyncDataWithServer {

    private static final Logger LOG = Logger.getLogger(SyncDataWithServerImpl.class.getName());

    private final RequestClient<Response<Object>> requestClient;
    private final Cache cache;
    private final Mapper mapper;
    private final Consumer<Response<Object>> defaultResponseConsumer;

    public SyncDataWithServerImpl(RequestClient<Response<Object>> requestClient, Cache cache, 
            Mapper mapper, Consumer<Response<Object>> defaultResponseConsumer) {
        this.requestClient = Objects.requireNonNull(requestClient);
        this.cache = Objects.requireNonNull(cache);
        this.mapper = Objects.requireNonNull(mapper);
        this.defaultResponseConsumer = Objects.requireNonNull(defaultResponseConsumer);
    }

    @Override
    public void run(URL endpoint, Map params, 
            final Devicedetails dd, Consumer<Response<Object>> responseConsumer) 
            throws IOException, ParseException {
    
        LOG.log(Level.FINE, "Endpoint: {0}", endpoint);

        final String json = mapper.toJsonString(dd);

        requestClient.params(params)
                .bodyParts(Collections.singletonMap(Devicedetails.class.getSimpleName(), json))
                .execute("POST", endpoint, 
                (response) -> {
                    try{
                        LOG.finer(() -> 
                                "Response type: " + response.getClass() +
                                "\nResponse data: " + 
                                new JsonFormat(true, true, "  ").toJSONString(mapper.toMap(response)));

                        new OnResponseCacheData(cache, Rest.RESULTNAME_DEVICEDETAILS, mapper)
                                .andThen(new OnResponseCacheData(cache, Rest.RESULTNAME_TESTS, mapper))
                                .andThen(new OnResponseCacheData(cache, Rest.RESULTNAME_USER, mapper))
                                .andThen(defaultResponseConsumer)
                                .andThen(responseConsumer)
                                .accept(response);
                    }catch(RuntimeException e) {
                        LOG.log(Level.WARNING, null, e);
                    }
                }
        );
    }
}
