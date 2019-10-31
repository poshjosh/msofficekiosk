/*
 * Copyright 2018 NUROX Ltd.
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

package com.looseboxes.msofficekiosk.functions;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 23, 2018 8:34:35 PM
 */
public class AddUrlToClassLoader implements BiConsumer<URLClassLoader, URL>, Serializable {

    private static final Logger LOG = Logger.getLogger(AddUrlToClassLoader.class.getName());

    @Override
    public void accept(URLClassLoader classLoader, URL url) {
        try{

            LOG.info(() -> "Adding url: " + url + ", to ClassLoader: " + classLoader);
                    
            final Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class); 
            
            addUrlMethod.setAccessible(true);         
            
            addUrlMethod.invoke(classLoader, url); 
            
        }catch(NoSuchMethodException | SecurityException | IllegalAccessException | 
                IllegalArgumentException | InvocationTargetException e) {
            
            throw new RuntimeException(e);
        }
    }
}
