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

import com.looseboxes.msofficekiosk.functions.io.CreateNewFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 21, 2018 8:37:30 PM
 */
public class InitLoggingConfig implements Runnable, Callable, Serializable {

    private transient static final Logger LOG = Logger.getLogger(InitLoggingConfig.class.getName());
    
    private final Path logsDir;
    private final String loggingConfigFile;

    public InitLoggingConfig(final Path logsDir, final String loggingConfigFile) {
        this.logsDir = Objects.requireNonNull(logsDir);
        this.loggingConfigFile = Objects.requireNonNull(loggingConfigFile);
    }
    
    @Override
    public void run() {
        try{
            this.call();
        }catch(IOException e) {
            LOG.log(Level.WARNING, null, e);
        }
    }

    @Override
    public URL call() throws IOException {
        
        new CreateNewFile().apply(logsDir.toFile(), Boolean.TRUE);
        
        LOG.info(() -> "Created logs dir: " + logsDir);
        
        final URL url = Thread.currentThread().getContextClassLoader().getResource(loggingConfigFile);
        
        final LogManager logManager = LogManager.getLogManager();
        
        try(final InputStream in = url.openStream()) {
            
            LOG.info(() -> "Reading logging config: " + url);
            
            logManager.readConfiguration(in);
        }
        
        return url;
    }
}
