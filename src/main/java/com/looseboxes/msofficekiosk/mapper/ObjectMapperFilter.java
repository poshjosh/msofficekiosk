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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.looseboxes.msofficekiosk.TypesToIgnore;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 25, 2019 7:20:39 PM
 */
public class ObjectMapperFilter extends SimpleBeanPropertyFilter{

    private static final Logger LOG = Logger.getLogger(ObjectMapperFilter.class.getName());
    
    private final TypesToIgnore typesToIgnore = new TypesToIgnore();
    
    public ObjectMapperFilter() { }
    
    @Override
    public void serializeAsElement(Object elementValue, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        final boolean ignore = elementValue != null && ignore(
                writer.getMember().getDeclaringClass(), writer.getName(), elementValue);
        if( ! ignore) {
            super.serializeAsElement(elementValue, jgen, provider, writer); 
        }
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        final Object value = getValue(pojo, writer);
        final boolean ignore = value != null && ignore(pojo.getClass(), writer.getName(), value);
        if( ! ignore) {
            super.serializeAsField(pojo, jgen, provider, writer); 
        }
    }
    
    public Object getValue(Object pojo, PropertyWriter writer) {
        try{
            final AnnotatedMember member = writer.getMember();
            if(member != null) {
                final Object value = member.getValue(pojo);
                return value;
            }
        }catch(RuntimeException e) {
            LOG.log(Level.WARNING, null, e);
        }
        return null;
    }

    public boolean ignore(Class parentType, String name, Object value) {
        
        final List<Class> tti = typesToIgnore.apply(parentType);
        
        final boolean ignore;
        if(value instanceof Collection) {
            final Collection c = (Collection)value;
            final Object first = c.isEmpty() ? null : c.stream().findFirst().orElse(null);
            ignore = first == null ? true : ignore(tti, first);
        }else{
            ignore = ignore(tti, value);
        }
        
        final Level level = ignore ? Level.FINER : Level.FINEST;
        
        LOG.log(level, () -> "Ignore: " + ignore + ", parent type: " + 
                parentType.getSimpleName() + ", " + name + " = " + value);
        
        return ignore;
    }

    public boolean ignore(List<Class> ignoreList, Object value) {
        final Class valueType = value.getClass();
        return ignoreList.stream().filter((cls) -> cls.isAssignableFrom(valueType)).findFirst().isPresent();
    }
}
