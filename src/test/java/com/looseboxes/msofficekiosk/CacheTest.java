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

import com.looseboxes.msofficekiosk.Cache;
import com.bc.elmi.pu.entities.User;
import java.util.Collections;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Josh
 */
public class CacheTest extends TestBase{
    
    public CacheTest() { }
    
    @AfterClass
    public static void tearDownClass() {
        TestBase.getCacheContext().closeAndRemoveAll();
    }
    
    public Cache getInstance() {
        return TestBase.getCache();
    }

    /**
     * Test of putAsJson method, of class Cache.
     */
    @Test
    public void test() throws Exception {
        System.out.println("test");
        
        final Entities e = new Entities();
        
        final User u = e.getUser();
        
        testGetReturnsWhatWasEarlierPut(u);
        
        final List l = e.getTestList(u);
        
        final List output = testGetReturnsWhatWasEarlierPut("Tests", l, com.bc.elmi.pu.entities.Test.class);
        
        final com.bc.elmi.pu.entities.Test t = (com.bc.elmi.pu.entities.Test)output.get(0);
        
        final List<User> users = t.getUserList();
        
        final User user = users == null || users.isEmpty() ? null : users.get(0);
        
        System.out.println("User: " + user);
        
        assertEquals(u, user);
    }

    public Object testGetReturnsWhatWasEarlierPut(Object object) throws Exception {
        System.out.println("testGetReturnsWhatWasEarlierPut("+object+")");
        System.out.println("Value: " + object);
        final Cache instance = getInstance();
        final String json = instance.putAsJson(object.getClass().getSimpleName(), object);
        System.out.println(" JSON: " + json);
        final Object result = instance.getFromJson(object.getClass().getSimpleName(), object.getClass(), null);
        System.out.println("  Get: " + result);
        assertEquals(object, result);
        return result;
    }

    public List testGetReturnsWhatWasEarlierPut(String name, List list, Class elementType) throws Exception {
        System.out.println("testGetReturnsWhatWasEarlierPut("+list+")");
        System.out.println("Value: " + list);
        final Cache instance = getInstance();
        final String json = instance.putAsJson(name, list);
        System.out.println(" JSON: " + json);
        final List result = instance.getListFromJson(name, elementType, Collections.EMPTY_LIST);
        System.out.println("  Get: " + result);
        assertEquals(list, result);
        return result;
    }
}
