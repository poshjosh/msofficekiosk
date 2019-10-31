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

package com.looseboxes.msofficekiosk.net;

import com.bc.config.Config;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * @author Chinomso Bassey Ikwuagwu on May 15, 2019 7:47:01 PM
 */
public class DefaultHeaders extends HashMap{

    public DefaultHeaders(Config config) {
        this(config, null);
    }
    
    public DefaultHeaders(Config config, String contentType) {
        this(Charset.forName(config.get(ConfigNames.CHARACTER_SET).trim()), 
                config.get(ConfigNames.HTTP_USER_AGENT),
                contentType);
    }
    
    public DefaultHeaders(Charset charset, String userAgent) {
        this(charset, userAgent, null);
    }
    
    public DefaultHeaders(Charset charset, String userAgent, String contentType) {
        put("User-Agent", userAgent);
        put("Accept", contentType);
        put("Accept-Charset", charset.name());
        if(contentType != null) {
            put("Content-Type", contentType);
        }
    }
}
