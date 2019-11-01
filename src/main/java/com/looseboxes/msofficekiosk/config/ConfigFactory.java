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
import java.util.Properties;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 29, 2019 1:15:35 PM
 */
public interface ConfigFactory {

    void clear();
    
    void clear(String id);

    void saveConfig(Config config, String id) throws IOException;

    ConfigService createConfigService(String id);

    Config<Properties> getConfig(String id);

    void loadConfigs() throws IOException;

    Config<Properties> loadConfig(String id)throws IOException;

}
