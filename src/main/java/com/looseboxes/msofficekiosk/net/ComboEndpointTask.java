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

import com.bc.config.Config;
import com.bc.socket.io.messaging.data.Devicedetails;
import com.looseboxes.msofficekiosk.Cache;
import com.looseboxes.msofficekiosk.mapper.Mapper;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.security.CredentialsSupplier;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 29, 2019 11:47:15 AM
 */
public class ComboEndpointTask extends SyncDataWithServerImpl implements Runnable{

    private static final Logger LOG = Logger.getLogger(ComboEndpointTask.class.getName());
    
    private final ConfigFactory configFactory;
    
    private final CredentialsSupplier credentialsSupplier;
    
    private final Supplier<Devicedetails> devicedetailsSupplier;

    public ComboEndpointTask(ConfigFactory configFactory,
             CredentialsSupplier credentialsSupplier, Supplier<Devicedetails> devicedetailsSupplier, 
            RequestClient<Response<Object>> requestClient, Cache cache, 
            Mapper mapper, Consumer<Response<Object>> defaultResponseConsumer) {

        super(requestClient, cache, mapper, defaultResponseConsumer);

        this.configFactory = Objects.requireNonNull(configFactory);
        this.credentialsSupplier = Objects.requireNonNull(credentialsSupplier);
        this.devicedetailsSupplier = Objects.requireNonNull(devicedetailsSupplier);
    }

    @Override
    public void run() {
    
        try{
            
            final Map params = credentialsSupplier.get();
            
            final Devicedetails dd = devicedetailsSupplier.get();

            LOG.log(Level.FINE, "Own Devicedetails: {0}", dd);
            
            final String endpoint = Rest.ENDPOINT_COMBO;

            if( ! endpoint.startsWith("/")) {
            
                final URL url = new URL(endpoint);
                
                this.run(url, params, dd, (map) -> {});

            }else{
            
                final Config config = configFactory.getConfig(ConfigService.APP_PROTECTED);
                
                final List<URL> urls = new UrlBuilderImpl(config).build(endpoint);

                for(URL serverBaseUrl : urls) {

                    final URL url = new URL(serverBaseUrl, endpoint);

                    this.run(url, Collections.EMPTY_MAP, dd, (map) -> {});
                }
            }
        }catch(IOException | ParseException e) {

            LOG.log(Level.WARNING, null, e);
        }
    }
}
