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

package com.looseboxes.msofficekiosk.mapper;

import com.bc.jpa.spring.ClassesFromFromPersistenceXmlFileSupplier;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 6, 2019 3:14:02 PM
 */
public class MapperJackson extends MapperObjectgraph {

    private static final Logger LOG = Logger.getLogger(MapperJackson.class.getName());
    
    private final ObjectMapper mapper;
    
    public MapperJackson() {
        
        mapper = new ObjectMapper();

        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));

        mapper.setSerializationInclusion(Include.NON_NULL);

        final FilterProvider filters = new SimpleFilterProvider()
                .addFilter(MapperJacksonMixInForFilter.FILTER_ID, new ObjectMapperFilter());

        mapper.setFilterProvider(filters);
        
        final List<Class> classList = new ClassesFromFromPersistenceXmlFileSupplier().get();

        for(Class cls : classList) {
            mapper.addMixIn(cls, MapperJacksonMixInForFilter.class);
        }
    }
    
    @Override
    public Map toMap(Object object) {
        try{
            return mapper.readValue(writeValueAsString(mapper, object), Map.class);
        }catch(IOException e) {
            LOG.log(Level.WARNING, "Failed to convert to Map", e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public <T> T toObject(Map map, Class<T> type) {
        try{
            return mapper.readValue(writeValueAsString(mapper, map), type);
        }catch(IOException e) {
            LOG.log(Level.WARNING, "Failed to convert Map to "+type.getSimpleName(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toJsonString(Object object) {
        return writeValueAsString(mapper, object);
    }

    public String writeValueAsString(ObjectMapper mapper, Object object) {
        try{
            return mapper.writeValueAsString(object);
        }catch(IOException e) {
            LOG.log(Level.WARNING, "Failed to format "+object.getClass().getSimpleName()+" as text", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T toObject(String json, Class<T> type) throws java.text.ParseException{
        try{
            return mapper.readValue(json, type);
        }catch(IOException e) {
            LOG.log(Level.WARNING, "Failed to parse text to type: " + type, e);
            throw new java.text.ParseException(e.toString(), 0);
        }
    }

    @Override
    public Map toMap(String json) throws java.text.ParseException{
        try{
            return mapper.readValue(json, Map.class);
        }catch(IOException e) {
            LOG.log(Level.WARNING, "Failed to parse text to Map", e);
            throw new java.text.ParseException(e.toString(), 0);
        }
    }
}
