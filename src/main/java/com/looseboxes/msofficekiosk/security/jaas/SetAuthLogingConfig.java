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

package com.looseboxes.msofficekiosk.security.jaas;

import com.bc.io.CharFileIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.looseboxes.msofficekiosk.FileNames;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 21, 2018 8:23:29 PM
 */
public class SetAuthLogingConfig implements Runnable, Callable<URL>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(SetAuthLogingConfig.class.getName());

    private final File dir;
    
    public SetAuthLogingConfig(File dir) {
        this.dir = Objects.requireNonNull(dir);
    }

    @Override
    public void run() {
        try{
            call();
        }catch(URISyntaxException e) {
            LOG.log(Level.WARNING, null, e);
        }
    }
    
    @Override
    public URL call() throws URISyntaxException {
        final String propertyName = "java.security.auth.login.config";
//-Djava.security.auth.login.config==C:\Users\Josh\Documents\NetBeansProjects\bcjaas\src\main\resources\META-INF\mswordbox_jaas_config.config
        final URL url = Thread.currentThread().getContextClassLoader()
                .getResource(FileNames.RESOURCE_JAAS_CONFIG);
//        final String path = Paths.get(url.toURI()).toString();
//        final String path = new File(url.toURI()).toString();
        try{
            final String path = url.toExternalForm();
            
            System.setProperty(propertyName, path);
            
        }catch(Exception e) {
            LOG.log(Level.WARNING, "Exception resolving path from resources dir; for property: " + propertyName, e);
            final File file = dir.toPath().resolve("mswordbox_jaas_config.config").toFile();
            if(!file.exists()) {
                try(final InputStream in = url.openStream();
                    final OutputStream out = new FileOutputStream(file, false)) {
                    new CharFileIO().copyChars(in, out);
                    
                    System.setProperty(propertyName, file.getAbsolutePath());
                    
                }catch(IOException ioe) {
                    LOG.log(Level.WARNING, "Exception resolving path via app home dir; for property: " + propertyName, ioe);
                }
            }
        }
        return url;
    }
}
