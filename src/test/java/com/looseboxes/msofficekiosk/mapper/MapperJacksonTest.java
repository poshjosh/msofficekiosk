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

import com.looseboxes.msofficekiosk.mapper.Mapper;
import com.looseboxes.msofficekiosk.mapper.MapperJackson;
import com.bc.elmi.pu.entities.User;
import com.looseboxes.msofficekiosk.Entities;
import com.looseboxes.msofficekiosk.TestBase;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.junit.Test;

/**
 *
 * @author Josh
 */
public class MapperJacksonTest extends TestBase{
    
    public MapperJacksonTest() { }
    
    /**
     * Test of toMap method, of class MapperJackson.
     */
    @Test
    public void testToMap_Object() {
        try{
            System.out.println("toMap_Object");
            final Mapper instance = getInstance();
            final Object object = getObject();
            final String json = instance.toJsonString(object);
            System.out.println("    Json: " + json);
            final Map expResult = instance.toMap(json);
            final Map result = instance.toMap(object);
            System.out.println("Expected: " + new TreeMap(expResult));
            System.out.println("   Found: " + new TreeMap(result));
//            assertEquals(expResult, result);
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Test of toObject method, of class MapperJackson.
     */
    @Test
    public void testToObject_Map_Class() {
        try{
            System.out.println("toObject_Map_Class");
            final Mapper instance = getInstance();
            final Object object = getObject();
            final Map map = instance.toMap(object);;
            final Object expResult = object;
            final Object result = instance.toObject(map, object.getClass());
            System.out.println("Expected: " + expResult);
            System.out.println("   Found: " + result);
//            assertEquals(expResult, result);
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Test of toJsonString method, of class MapperJackson.
     */
    @Test
    public void testToJsonString() {
        testToJsonString(getObject());
        testToJsonString(getList());
//        final List list = getList();
//        for(Object e : list) {
//            testToJsonString(e);
//        }
    }

    public void testToJsonString(Object object) {
        try{
            System.out.println("toJsonString("+object+")");
            final Mapper instance = getInstance();
            System.out.println("  Object: " + object);
            final String expResult = instance.toJsonString(object);
            System.out.println("Expected: " + expResult);
            String result = instance.toJsonString(object);
            System.out.println("   Found: " + result);
            final Object o = instance.toObject(result, object.getClass());
            result = instance.toJsonString(o);
            System.out.println("   Found: " + result);
//            assertEquals(expResult, result);
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Test of toObject method, of class MapperJackson.
     */
    @Test
    public void testToObject_String_Class() {
        testToObject_String_Class(getObject());
        testToObject_String_Class(getList());
//        final List list = getList();
//        for(Object e : list) {
//            testToObject_String_Class(e);
//        }
    }

    public void testToObject_String_Class(Object object) {
        try{
            System.out.println("toObject_String_Class("+object+")");
            final Mapper instance = getInstance();
            final String json = instance.toJsonString(object);
            System.out.println("   Json: " + json);
            final Object expResult = object;
            final Object result = instance.toObject(json, object.getClass());
            System.out.println("Expected: " + expResult);
            System.out.println("   Found: " + result);
//            assertEquals(expResult, result);
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Test of toMap method, of class MapperJackson.
     */
    @Test
    public void testToMap_String() throws Exception {
        try{
            System.out.println("toMap_String");
            final Mapper instance = getInstance();
            final Object object = getObject();
            System.out.println("  Object: " + object);
            final Map expResult = instance.toMap(object);
            System.out.println("    Map: " + expResult);
            final String json = instance.toJsonString(object);
            System.out.println("    Json: " + json);
            final Map result = instance.toMap(json);
            System.out.println("Expected: " + new TreeMap(expResult));
            System.out.println("   Found: " + new TreeMap(result));
//            assertEquals(expResult, result);
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }

    public Object getObject() {
        User u;
        try{
            u = TestBase.getJpa().getDao().findAndClose(User.class, 5);
        }catch(Exception e) {
            System.err.println("Database access failed");
            System.err.println(e);
            u = new Entities().getUser();
        }
        Objects.requireNonNull(u);
        return u;
    }
    
    public List getList() {
        List<com.bc.elmi.pu.entities.Test> output;
        try{
            output = TestBase.getJpa().getDaoForSelect(com.bc.elmi.pu.entities.Test.class)
                        .findAllAndClose(com.bc.elmi.pu.entities.Test.class);
        }catch(Exception e) {
            System.err.println("Database access failed");
            System.err.println(e);
            output = new Entities().getTestList();
        }
        return output;
    }
    
    public Mapper getInstance() {
        return new MapperJackson();
    }
}
