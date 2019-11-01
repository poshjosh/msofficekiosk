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

package com.looseboxes.msofficekiosk.test;

import com.bc.config.Config;
import com.bc.elmi.pu.entities.Test;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.popups.MultiInputDialog;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ValidationException;

/**
 * @author Chinomso Bassey Ikwuagwu on November 1, 2019 01:13:00 AM
 */
public class EditTest implements UnaryOperator<Test>{

    private static final Logger LOG = Logger.getLogger(EditTest.class.getName());

    private final ConfigFactory configFactory;

    public EditTest(ConfigFactory configFactory) {
        this.configFactory = Objects.requireNonNull(configFactory);
    }
    
    @Override
    public Test apply(Test target) {
        
        final String timeFormat = "HH:mm";
        final String startTimeLabel = "Start Time (" + timeFormat + " e.g 09:30)"; 
        final String durationLabel = "Duration (in minutes)"; 

        final Map inputMap = new LinkedHashMap();
        if(target.getTestname() == null) {
            target.setTestname("");
        }
        inputMap.put(TestDocKey.TEST_NAME, target.getTestname());
        inputMap.put(startTimeLabel, "");
        if(target.getDurationinminutes() == null) {
            target.setDurationinminutes(60);
        }
        inputMap.put(durationLabel, target.getDurationinminutes());
        
        final Config uiConfig = configFactory.getConfig(ConfigService.APP_UI);
        
        final Map idParams = new MultiInputDialog(uiConfig).apply(inputMap, "Enter Test Details");

        final String name = (String)idParams.getOrDefault(TestDocKey.TEST_NAME, null);
        if(name != null && !name.isEmpty()) {
            
            target.setTestname(name);
            LOG.log(Level.FINE, "Updated name to: {0}", name);
        }
        
        final String timeStr = (String)idParams.getOrDefault(startTimeLabel, null);
        if(timeStr != null && !timeStr.isEmpty()) {
            final Date time;
            try{
                time = new SimpleDateFormat(timeFormat).parse(timeStr);
                
                target.setStarttime(time);
                LOG.log(Level.FINE, "Updated Starttime to: {0}", time);
                
            }catch(java.text.ParseException e) {
                throw new ValidationException("Invalid " + startTimeLabel, e);
            }
        }
        
        final Integer duration = (Integer)idParams.getOrDefault(durationLabel, null);
        if(duration != null && duration > 0) {
            target.setDurationinminutes(duration);
            LOG.log(Level.FINE, "Updated duration to: {0} minutes", target);
        }
        
        return target;
    }
}
