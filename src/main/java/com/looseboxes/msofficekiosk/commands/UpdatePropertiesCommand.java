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

package com.looseboxes.msofficekiosk.commands;

import com.looseboxes.msofficekiosk.popups.MultiInputWindow;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import com.bc.config.Config;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigService;
import java.util.LinkedHashMap;

import java.util.Properties;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on May 11, 2018 7:00:28 PM
 */
public class UpdatePropertiesCommand implements Serializable, Callable<Boolean> {

    private transient static final Logger LOG = Logger.getLogger(UpdatePropertiesCommand.class.getName());
    
    private final ConfigFactory configFactory;
    
    private final String propertiesId;
    
    private final Predicate precondition;
    
    private final Consumer<Map<String, String>> updateListener;
    
    public UpdatePropertiesCommand(ConfigFactory context, String propertiesId) {
        this(context, propertiesId, (obj) -> true, (update) -> {});
    }
    
    public UpdatePropertiesCommand(ConfigFactory context, String propertiesId, 
            Predicate precondition, Consumer<Map<String, String>> updateListener) {
        this.configFactory = Objects.requireNonNull(context);
        this.propertiesId = Objects.requireNonNull(propertiesId);
        this.precondition = Objects.requireNonNull(precondition);
        this.updateListener = Objects.requireNonNull(updateListener);
    }
    
    @Override
    public Boolean call() throws IOException {

        if( ! this.precondition.test(this)) {
            
            return Boolean.FALSE;
        }
        
        final Config<Properties> config = this.configFactory.getConfig(propertiesId);

        final Map<String, Object> inputMap = config.toMap();
        
        final Config<Properties> uiConfig = this.configFactory.getConfig(ConfigService.APP_UI);
        
        final Map outputMap = new MultiInputWindow(uiConfig, true).apply(inputMap, "Update Settings");

        final Map<String, String> update = new LinkedHashMap<>();
        
        outputMap.forEach((k, v) -> {
            LOG.finest(() -> "Checking: " + k + '=' + v);
            if(k != null && v != null) {
                final String key = k.toString();
                final String val = v.toString();
                final String existing = config.get(key);
                if(!Objects.equals(existing, val)) {
                    LOG.fine(("Updating: " + key + '=' + val + " in " + propertiesId));
                    config.set(key, val);
                    update.put(key, val);
                }
            }
        });
        
        configFactory.saveConfig(config, propertiesId);
        
        this.updateListener.accept(update);

        return Boolean.TRUE;
    }
}
