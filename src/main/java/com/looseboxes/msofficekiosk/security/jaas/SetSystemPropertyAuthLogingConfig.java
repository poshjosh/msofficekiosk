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

import java.io.Serializable;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 21, 2018 8:23:29 PM
 */
public class SetSystemPropertyAuthLogingConfig implements Runnable, Callable<URL>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(SetSystemPropertyAuthLogingConfig.class.getName());

    public SetSystemPropertyAuthLogingConfig() { }

    @Override
    public void run() {
        call();
    }
    
    @Override
    public URL call() {
        
        final String propertyName = "java.security.auth.login.config";
//-Djava.security.auth.login.config==[FULL FILE PATH]
        
        final URL url = Thread.currentThread().getContextClassLoader()
                .getResource(JaasFiles.RESOURCE_JAAS_CONFIG);
        
        LOG.log(Level.INFO, "Jaas Config URL = {0}", url);
//        try{
//        LOG.log(Level.INFO, "Jaas Config Path = {0}", Paths.get(url.toURI()));
//        }catch(Exception e) {
//            e.printStackTrace();
//        }

        final String path = url.toExternalForm();

        System.setProperty(propertyName, path);

        return url;
    }
}
