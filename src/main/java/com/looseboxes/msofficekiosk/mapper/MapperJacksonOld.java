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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 23, 2019 10:59:11 PM
 */
public class MapperJacksonOld extends MapperObjectgraph {

    private static final Logger LOG = Logger.getLogger(MapperJackson.class.getName());
    
    private final ObjectMapper mapper = new ObjectMapper();
    
    public MapperJacksonOld() {
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        mapper.setSerializationInclusion(Include.NON_NULL);
    }

    @Override
    public Map toMap(Object object) {
        return super.toMap(object);
    }
    
    @Override
    public <T> T toObject(Map map, Class<T> type) {
        try{
            return mapper.readValue(writeValueAsString(map), type);
        }catch(IOException e) {
            LOG.log(Level.WARNING, "Failed to convert Map to "+type.getSimpleName(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toJsonString(Object object) {
//        try{
//            return mapper.writeValueAsString(object);
//        }catch(IOException e) {
//            LOG.log(Level.WARNING, "Failed to format "+object.getClass().getSimpleName()+" as text", e);
//            throw new RuntimeException(e);
//        }

        if(object != null && object.getClass().getAnnotation(javax.persistence.Entity.class) != null) {
        
            try{
                
                object = super.toMap(object);

                return writeValueAsString(object);
                
            }catch(RuntimeException e) {
                
                LOG.log(Level.WARNING, "Following exception may be ignored.", e);
            
                return writeValueAsString(object);
            }
        }else{
            
            return writeValueAsString(object);
        }
    }

    public String writeValueAsString(Object object) {
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
        return toObject(json, Map.class);
    }
}
