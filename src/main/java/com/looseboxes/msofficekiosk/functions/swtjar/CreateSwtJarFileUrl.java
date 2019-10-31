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

package com.looseboxes.msofficekiosk.functions.swtjar;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import org.eclipse.jdt.internal.jarinjarloader.RsrcURLStreamHandlerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 23, 2018 8:46:08 PM
 */
public class CreateSwtJarFileUrl 
        implements BiFunction<ClassLoader, String, URL>, Serializable {

    private static final Logger LOG = Logger.getLogger(CreateSwtJarFileUrl.class.getName());

    @Override
    public URL apply(ClassLoader classLoader, String jarFileName) {
        try{
            return this.execute(classLoader, jarFileName);
        }catch(MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public URL execute(ClassLoader classLoader, String jarFileName) 
            throws MalformedURLException {
    
        LOG.info(() -> "Creating URL from file name: " + jarFileName + ", using ClassLoader: " + classLoader);
        
        Objects.requireNonNull(jarFileName);
        
        if(!jarFileName.endsWith(".jar")) {
            throw new IllegalArgumentException("Expected file name ending with .jar, got: " + jarFileName);    
        }
        
        final String protocol = "rsrc";
        
        final URLStreamHandler ush = new RsrcURLStreamHandlerFactory(classLoader).createURLStreamHandler(protocol);

        final URL url = new URL(null, protocol + ":" + jarFileName, ush);
        
        return url;
    }
}
