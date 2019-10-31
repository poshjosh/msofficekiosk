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

package com.looseboxes.msofficekiosk;

import com.looseboxes.msofficekiosk.mapper.Mapper;
import com.bc.diskcache.DiskLruCacheIx;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on May 11, 2019 4:57:02 PM
 */
public class CacheImpl implements Cache{

//    private static final Logger LOG = Logger.getLogger(CacheImpl.class.getName());

    private final DiskLruCacheIx delegate;
    
    private final Mapper mapper;

    public CacheImpl(DiskLruCacheIx delegate, Mapper mapper) {
        this.delegate = Objects.requireNonNull(delegate);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public String putAsJson(String key, Object value) throws IOException {
        final String json;
        if(value == null) {
            json = null;
        }else if(value instanceof String) {
            json = (String)value;
        }else{
            json = mapper.toJsonString(value);
        }
        delegate.put(key, json);
        return json;
    }

    @Override
    public <T> T getFromJson(String key, Class<T> type, T outputIfNone) 
            throws IOException, ParseException {
        
        final String json = delegate.getString(key, null);
        
        return json == null ? outputIfNone : mapper.toObject(json, type);
    }

    @Override
    public <T> List<T> getListFromJson(String key, Class<T> elementType, List<T> outputIfNone) 
            throws IOException, ParseException {
        
        final String json = delegate.getString(key, null);
        
        final List list = json == null || json.isEmpty() ? null : mapper.toObject(json, List.class);
        
        final List<T> output;
        
        if(list == null) {
            
            output = null;
            
        }else{
            
            output = new ArrayList<>(list.size());
            
            int i = 0;
            for(Object object : list) {

                output.add(this.convert(key + '[' + i + ']', object, elementType));

                ++i;
            }
        }
        
        return output == null ? outputIfNone : output;
    }

    @Override
    public <T> Collection<T> getCollectionFromJson(String key, Class<T> elementType, Collection<T> outputIfNone) 
            throws IOException, ParseException {
        
        final String json = delegate.getString(key, null);
        
        final Collection collection = json == null || json.isEmpty() ? null : mapper.toObject(json, Collection.class);
        
        final Collection<T> output;
        
        if(collection == null) {
        
            output = null;
            
        }else{
        
            output = new ArrayList<>(collection.size());
            
            int i = 0;
            for(Object object : collection) {

                output.add(this.convert(key + '[' + i + ']', object, elementType));

                ++i;
            }
        }
        
        return output;
    }

    public <T> T convert(String name, Object object, Class<T> type) 
            throws java.text.ParseException{
        final T output;
        if(object == null) {
            output = null;
        }else if(type.isAssignableFrom(object.getClass())) {
            output = (T)object;
        }else if(object instanceof Map) {
            output = mapper.toObject((Map)object, type);
        }else if(object instanceof String) {
            output = mapper.toObject((String)object, type);
        }else{
            throw new UnsupportedOperationException("Not supported. Converting cache value named: " + name + ", to type: " + type);
        }
        return output;
    }

    @Override
    public DiskLruCacheIx getDelegate() {
        return delegate;
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public boolean isClosed() {
        return delegate.isClosed();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public void clear() throws IOException {
        delegate.clear();
    }

    @Override
    public boolean remove(String key) throws IOException {
        return delegate.remove(key);
    }

    @Override
    public void delete() throws IOException {
        delegate.delete();
    }
}
/**
 * 

    public <T> T get(String key, T outputIfNone, Class<T> type) 
            throws IOException, ClassNotFoundException {
        
        final Object object = getObject(key, outputIfNone);
        
        return this.convert(key, object, type);
    }

    public <T> List<T> getList(String key, Class<T> type) 
            throws IOException, ClassNotFoundException {
        
        final Object object = getObject(key, null);
        
        final List list;
        if(object instanceof List) {
            list = (List)object;
        }else if(object instanceof String) {
            try{
                list = mapper.toObject((String)object, List.class);
            }catch(java.text.ParseException e) {
                LOG.log(Level.WARNING, null, e);
                throw new IllegalArgumentException(
                        "Failed to convert cache value named: " + key + ", to type: " + type);
            }
        }else{
            throw new UnsupportedOperationException(
                    "Not supported. Converting cache value named: " + key + ", to type: " + type);        
        }
        
        final List output = list.isEmpty() ? Collections.EMPTY_LIST : new ArrayList(list.size());
        
        for(int i=0; i<list.size(); i++) {
            
            output.add(this.convert(key+"["+i+"]", list.get(i), type));
        }
        
        return output;
    }
 * 
 */