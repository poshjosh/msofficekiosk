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

package com.looseboxes.msofficekiosk.config;

import com.bc.config.Config;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 29, 2019 12:53:56 PM
 */
public class ConfigFactoryImpl implements ConfigFactory {

    private final Map<String, Config<Properties>> configs;

    private final Path homeDir;
    
    public ConfigFactoryImpl(Path homeDir) {
        homeDir = homeDir.toAbsolutePath().normalize();
        this.homeDir = Objects.requireNonNull(homeDir);
        this.configs = new HashMap<>();
    }
    
    @Override
    public void clear() {
        configs.clear();
    }
    
    @Override
    public void clear(String id) {
        this.configs.remove(id);
    }

    @Override
    public void loadConfigs() throws IOException{
        this.loadAndCacheConfig(ConfigService.APP_INTERNAL); 
        this.loadAndCacheConfig(ConfigService.APP_PROTECTED);
        this.loadAndCacheConfig(ConfigService.APP_UI); 
    }

    private Config<Properties> loadAndCacheConfig(String id) throws IOException {
        final Config<Properties> config = this.loadConfig(id);
        this.configs.put(id, config);
        return config;
    }

    @Override
    public void saveConfig(Config config, String id) throws IOException {
        this.createConfigService(id).saveConfig(config);
        this.loadAndCacheConfig(id);
    }
    
    @Override
    public Config<Properties> getConfig(String id) {
        Config<Properties> config = configs.get(id);
        if(config == null) {
            try{
                config = loadAndCacheConfig(id);
                Objects.requireNonNull(config);
            }catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
        return config;
    }
    
    @Override
    public Config<Properties> loadConfig(String id) throws IOException{
        return createConfigService(id).loadConfig();
    }
    
    @Override
    public ConfigService createConfigService(String id) {
        return new ConfigServiceImpl(homeDir, id);
    }
}
