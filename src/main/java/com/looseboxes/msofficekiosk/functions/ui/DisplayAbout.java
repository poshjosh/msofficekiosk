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

package com.looseboxes.msofficekiosk.functions.ui;

import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import static com.looseboxes.msofficekiosk.functions.ui.DisplayURL.CONTENT_TYPE;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.ui.AppUiContext;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2018 5:35:43 PM
 */
public class DisplayAbout extends DisplayURL {

    private transient static final Logger LOG = Logger.getLogger(DisplayAbout.class.getName());
    
    public static final String ABOUT_FILENAME = "META-INF/resources/about.html";
        
    @Autowired private AppContext appContext;

    public DisplayAbout() { }
    
    @Override
    public Boolean apply(AppUiContext uiContext, Map<String, Object> params) {
        
        LOG.log(Level.FINE, "About filename: {0}", ABOUT_FILENAME);
        
        final URL url = Thread.currentThread().getContextClassLoader().getResource(ABOUT_FILENAME);
        
        LOG.log(Level.FINE, "About file URL: {0}", url);
        this.validate(url);
        
        params = new HashMap(params);
        final String title = (String)params.get(DisplayURL.TITLE);
        if(title == null) {
            params.put(DisplayURL.TITLE, "About " + this.appContext.getConfig(ConfigService.APP_INTERNAL).getString(ConfigNames.APP_NAME, "This App"));
        }
        
        params.put(CONTENT_TYPE, "text/html");
        params.put(java.net.URL.class.getName(), url);
        
        return super.apply(uiContext, params);
    }
    
    public void validate(Object val) {
        if(val == null || val.toString().isEmpty()) {
            throw new NullPointerException("About file not available");
        }
    }
}
