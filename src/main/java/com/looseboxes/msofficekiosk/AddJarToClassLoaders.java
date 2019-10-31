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

package com.looseboxes.msofficekiosk;

import com.looseboxes.msofficekiosk.functions.AddUrlToClassLoader;
import com.looseboxes.msofficekiosk.functions.swtjar.CreateSwtJarFileUrl;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 23, 2018 8:51:56 PM
 */
public class AddJarToClassLoaders {

    private transient static final Logger LOG = Logger.getLogger(AddJarToClassLoaders.class.getName());

    private final List<ClassLoader> classLoaders;

    public AddJarToClassLoaders() {
        this(Arrays.asList(
                ClassLoader.getSystemClassLoader(), 
                Thread.currentThread().getContextClassLoader()
            )    
        );
    }
    
    public AddJarToClassLoaders(List<ClassLoader> classLoaders) {
        this.classLoaders = Objects.requireNonNull(classLoaders);
    }
            
    public List<URLClassLoader> apply(Object obj) {

        final List<URLClassLoader> urlClassLoaders = new ArrayList();
        
        for(ClassLoader classLoader : classLoaders) {
            
            final URLClassLoader clsLoader = toUrlClassLoader(classLoader, null);

            if(urlClassLoaders.contains(clsLoader)) {
                continue;
            }
            
            URL url = null;
            try{
                url = clsLoader == null ? null : this.toUrl(clsLoader, obj);
            }catch(MalformedURLException e) {
                LOG.log(Level.WARNING, "Failed to resolve URL for: " + obj, e);
            }
            
            if(url != null) {
                if(this.addUrlToClassLoader(clsLoader, url)) {
                    urlClassLoaders.add(clsLoader);
                }
            }
        }
        
        return urlClassLoaders;
    }

    public URLClassLoader toUrlClassLoader(ClassLoader classLoader, URLClassLoader outputIfNone) {
        try{
            return (URLClassLoader)classLoader;
        }catch(ClassCastException e) {
            LOG.warning(() -> "Not an instanceof URLClassLoader. Instance type: " + classLoader.getClass().getName());
            return outputIfNone;
        }
    }

    public URL toUrl(ClassLoader classLoader, Object obj) throws MalformedURLException {
        URL url;
        if(obj instanceof File) {
            url = ((File)obj).toURI().toURL();
        }else{
            final String sval = obj.toString();
            url = new CreateSwtJarFileUrl().execute(classLoader, sval);
        }
        return url;
    }

    public boolean addUrlToClassLoader(URLClassLoader classLoader, URL url) {
        try{
            new AddUrlToClassLoader().accept(classLoader, url);
            LOG.info(() -> "Successfully added URL: " + url + ", to " + classLoader);
            return true;
        }catch(Throwable t) {
            LOG.log(Level.WARNING, "Failed to add URL: " + url + ", to " + classLoader, t);
            return false;
        }
    }
}
