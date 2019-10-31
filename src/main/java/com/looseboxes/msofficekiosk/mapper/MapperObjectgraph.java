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

import com.bc.objectgraph.DateFormatterImpl;
import com.bc.objectgraph.MapBuilder;
import com.bc.objectgraph.MapBuilderImpl;
import com.bc.objectgraph.ObjectFromMapBuilder;
import com.bc.objectgraph.ObjectFromMapBuilderImpl;
import com.bc.util.JsonFormat;
import com.looseboxes.msofficekiosk.TypesToIgnore;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONValue;

/**
 * @author Chinomso Bassey Ikwuagwu on May 1, 2019 12:34:17 PM
 */
public class MapperObjectgraph implements Mapper {

    private static final Logger LOG = Logger.getLogger(MapperObjectgraph.class.getName());
    
    private final TypesToIgnore typesToIgnore = new TypesToIgnore();

    @Override
    public Map toMap(Object object) {
//        final DateTimeConverter dtc = new DateTimeConverter(null, null);
        final Map map = new MapBuilderImpl()
                .transformer(new MapBuilder.Transformer(){
                    @Override
                    public String transformKey(Object entity, String key) { 
                        return key;
                    }
                    @Override
                    public Object transformValue(Object entity, String oldKey, String newKey, Object value) {
//                        if(value instanceof Date) {
//                            final long epochMilli = ((Date)value).getTime();
//                            final ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.systemDefault());
//                            LOG.fine(() -> "Input: " + value + ", output: " + zdt);
//                            return zdt;
//                        }
                        return value;
                    }
                }).maxCollectionSize(100)
                .maxDepth(3)
                .nullsAllowed(false)
                .source(object)
                .typesToIgnore(typesToIgnore.apply(object).toArray(new Class[0]))
                .build();
        return map;
    }
    
    @Override
    public <T> T toObject(Map map, Class<T> type) {
        
        final ObjectFromMapBuilder.Formatter fmt = new ObjectFromMapBuilder.Formatter() {
            private final DateFormatterImpl dateFormatter = new DateFormatterImpl(
                    Arrays.asList("yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSSX", "EEE MMM dd kk:mm:ss z yyyy")
            );
            @Override
            public Object format(Object entity, String column, Object value, Class valueType) {
                if(value instanceof Number && ! value.getClass().equals(valueType)) {
                    return convertNumberType(value, valueType, value);
                }
                return dateFormatter.format(entity, column, value, valueType);
            }
        };

        final T object = (T)new ObjectFromMapBuilderImpl()
                .lenient(true)
                .formatter(fmt)
                .source(map)
                .targetType(type)
                .build();

        return object;
    }
    
    @Override
    public String toJsonString(Object object) {
//        return JSONValue.toJSONString(object); Doesn't quote dates
        return new JsonFormat(false, true, "").toJSONString(object);
    }

    @Override
    public <T> T toObject(String json, Class<T> type) throws java.text.ParseException{
        Object object;
        try{
            object = JSONValue.parseWithException(json);
        }catch(org.json.simple.parser.ParseException e) {
            LOG.log(Level.WARNING, null, e);
            throw new java.text.ParseException(e.toString(), e.getPosition());
        }
        final T output;
        if(object == null) {
            output = null;
        }else if(object instanceof Map){
            output = this.toObject((Map)object, type);
        }else {
            output = (T)object;
        }
        return output;
    }

    @Override
    public Map toMap(String json) throws java.text.ParseException{
        try{
            return (Map)JSONValue.parseWithException(json);
        }catch(org.json.simple.parser.ParseException e) {
            LOG.log(Level.WARNING, null, e);
            throw new java.text.ParseException(e.toString(), e.getPosition());
        }
    }

    public <T extends Number> Object convertNumberType(
            Object value, Class<T> numberType, Object outputIfNone) {
        
        Objects.requireNonNull(value);
        
        final Object output;
        
        if(numberType.isAssignableFrom(value.getClass())) {
            output = value;
        }else if((numberType == short.class || numberType == Short.class)){// && !(value instanceof Short)) {
            output = Short.valueOf(value.toString());
        }else if((numberType == int.class || numberType == Integer.class)){// && !(value instanceof Integer)) {
            output = Integer.valueOf(value.toString());
        }else if((numberType == long.class || numberType == Long.class)){// && !(value instanceof Long)) {
            output = Long.valueOf(value.toString());
        }else if((numberType == float.class || numberType == Float.class)){// && !(value instanceof Float)) {
            output = Float.valueOf(value.toString());
        }else if((numberType == double.class || numberType == Double.class)){// && !(value instanceof Double)) {
            output = Double.valueOf(value.toString());
        }else if((numberType == BigDecimal.class)){// && !(value instanceof BigDecimal)) {
            output = new BigDecimal(value.toString());
        }else{
            output = null;
        }
        
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "{0} {1} converted to {2} {3}",
                    new Object[]{value.getClass().getName(), value, numberType.getName(), output});
        }
        
        try{
            return output == null ? outputIfNone : output;
        }catch(ClassCastException e) {
            LOG.warning(() -> "Input : " + value + ", numberType: " + 
                    numberType.getSimpleName() + ", output: " + output);
            throw e;
        }
    }
}
