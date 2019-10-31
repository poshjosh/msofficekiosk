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

import com.bc.elmi.pu.entities.Test;
import com.bc.elmi.pu.entities.User;
import com.bc.socket.io.messaging.data.Devicedetails;
import com.looseboxes.msofficekiosk.Cache;
import com.looseboxes.msofficekiosk.mapper.Mapper;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 26, 2019 11:58:34 PM
 */
public class OnResponseCacheData implements Consumer<Response<Object>>{

    private static final Logger LOG = Logger.getLogger(OnResponseCacheData.class.getName());

    private final Cache cache;
    private final String key;
    private final Mapper mapper;

    public OnResponseCacheData(Cache cache, String key, Mapper mapper) {
        this.cache = Objects.requireNonNull(cache);
        this.key = Objects.requireNonNull(key);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public void accept(final Response<Object> response) {
        try{
            
            final Object bodyObj = response.getBody();
            
            if(bodyObj instanceof Map) {
                
                final Map bodyMap = (Map)bodyObj;
            
                final Object data = bodyMap.getOrDefault(key, null);

                if(data != null) {

                    LOG.log(Level.FINER, "Adding {0} data from response to cache:\n{1}", new Object[]{key, data});

                    cache.putAsJson(key, data);

                }else{

                    LOG.log(Level.FINE, "Response body doesnot contain data for: {0}", key);
                }
            }else{
            
                LOG.warning(() -> "Response body doesnot contain data for: " + key + "\nResponse: " + response);
            }
        }catch(IOException e) {
            LOG.log(Level.WARNING, null, e);
        }
    }
    
    public Class getTypeForName(String name) {
        if(name.equals(Rest.RESULTNAME_USER)) {
            return User.class;
        }else if(name.equals(Rest.RESULTNAME_TESTS)) {
            return Test.class;
        }else if(name.equals(Rest.RESULTNAME_DEVICEDETAILS)) {
            return Devicedetails.class;
        }else{
            throw new IllegalArgumentException("Unexpected name: " + name);
        }
    }
}
/**
 * 
    @Override
    public void accept(final Response response) {
        try{
            
            final Map body = (Map)response.getBody();
            
            final Object data = body == null ? null : body.getOrDefault(key, null);
            
            if(data != null && ! this.isEmpty(data)) {
                
                final Object update;

                if(data instanceof Collection) {

                    final Collection c = (Collection)data;

                    final List list = new ArrayList<>(c.size());

                    int i = 0;
                    for(Object o : c) {

                        final Map m = (Map)o;

                        final int n = i;
                        LOG.finer(() -> key + "["+n+"] = " + m);

                        final Object entity = mapper.toObject(m, this.getTypeForName(key));

                        list.add(entity);
                        
                        ++i;
                    }

                    update = list;

                }else if(data instanceof Object[]) {

                    final Object [] a = (Object[])data;

                    final Object [] arr = new Object[a.length];

                    for(int i=0; i<a.length; i++) {

                        final Map m = (Map)a[i];

                        final int n = i;
                        LOG.finer(() -> key + "["+n+"] = " + m);
                        
                        final Object entity = mapper.toObject(m, this.getTypeForName(key));

                        arr[i] = entity;
                    }

                    update = arr;

                }else if(data instanceof Map) {

                    final Map m = (Map)data;
                    
                    LOG.finer(() -> key + " = " + m);

                    update = mapper.toObject(m, this.getTypeForName(key));

                }else{

                    throw getInvalidDataTypeException(data);
                }

                LOG.log(Level.FINE, "Adding {0} data from response to cache", key);

                cache.put(key, update);

            }else{

                LOG.log(Level.FINE, "Response doesnot contain data for: {0}", key);
            }
        }catch(IOException e) {
            LOG.log(Level.WARNING, null, e);
        }
    }
    
    private UnsupportedOperationException getInvalidDataTypeException(Object data) {
        return new UnsupportedOperationException("Found: " + data.getClass().getName() + 
                ", expected any of: java.util.Collection, java.util.Map, Object[]");
    }

 * 
 */