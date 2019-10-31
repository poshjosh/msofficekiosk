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
import com.bc.ui.UIContext;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.ui.GetAwtFontFromConfig;
import com.looseboxes.msofficekiosk.popups.MultiInputDialog;
import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.test.Tests;
import com.looseboxes.msofficekiosk.test.Tests;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.validation.ValidationException;

/**
 * @author Chinomso Bassey Ikwuagwu on May 3, 2019 9:39:24 PM
 */
public class EditCachedTest {

    private static final Logger LOG = Logger.getLogger(EditCachedTest.class.getName());

    private final Tests tests;
    
    private final UIContext uiContext;
            
    private final LoginManager loginManager;
    
    private final ConfigFactory configFactory;

    public EditCachedTest(Tests tests, UIContext uiContext, LoginManager loginManager, ConfigFactory configFactory) {
        this.tests = Objects.requireNonNull(tests);
        this.uiContext = Objects.requireNonNull(uiContext);
        this.loginManager = Objects.requireNonNull(loginManager);
        this.configFactory = Objects.requireNonNull(configFactory);
    }
    
    public Test call() {
        return this.promptUserEditTest();
    }

    public Test promptUserEditTest() {
        
        final DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm");
        final Function<Test, Object> to = (t) -> t.getTestid() + " - " + t.getTestname() + " (" + dateFormat.format(t.getStarttime()) + ")";
        final List<Test> tt = tests.get();
        final Object [] arr = tt.stream().map(to).toArray();
        
        final JList jList = new JList(arr);
        final Font font = new GetAwtFontFromConfig().apply(configFactory.getConfig(ConfigService.APP_UI));
        jList.setFont(font);
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final JScrollPane pane = new JScrollPane(jList);

        final ImageIcon icon = uiContext.getImageIconOptional().orElse(null);
        JOptionPane.showMessageDialog(null, pane, "Select Test to Edit", JOptionPane.PLAIN_MESSAGE, icon);

        final List selections =  jList.getSelectedValuesList();
        
        Test selected = null;
        for(Object o : selections) {
            for(Test t : tt) {
                if(to.apply(t).equals(o)) {
                    selected = t;
                    break;
                }
            }
        }
        
        if(selected == null) {
            return null;
        }
        
        final String unitname = loginManager.validateUsergroup();
        
        final String timeFormat = "HH:mm";
        final String startTimeLabel = "Start Time (" + timeFormat + " e.g 09:30)"; 
        final String durationLabel = "Duration (in minutes)"; 

        final Map inputMap = new LinkedHashMap();
        inputMap.put(TestDocKey.TEST_NAME, selected.getTestname());
        inputMap.put(startTimeLabel, "");
        inputMap.put(durationLabel, selected.getDurationinminutes());
        
        final Config uiConfig = configFactory.getConfig(ConfigService.APP_UI);
        
        final Map idParams = new MultiInputDialog(uiConfig).apply(inputMap, "Enter Test Details");

        final String name = (String)idParams.getOrDefault(TestDocKey.TEST_NAME, null);
        if(name != null && !name.isEmpty()) {
            
            selected.setTestname(name);
            LOG.log(Level.FINE, "Updated name to: {0}", name);
        }
        
        final String timeStr = (String)idParams.getOrDefault(startTimeLabel, null);
        if(timeStr != null && !timeStr.isEmpty()) {
            final Date time;
            try{
                time = new SimpleDateFormat(timeFormat).parse(timeStr);
                
                selected.setStarttime(time);
                LOG.log(Level.FINE, "Updated Starttime to: {0}", time);
                
            }catch(java.text.ParseException e) {
                throw new ValidationException("Invalid " + startTimeLabel, e);
            }
        }
        
        final Integer duration = (Integer)idParams.getOrDefault(durationLabel, null);
        if(duration != null && duration > 0) {
            selected.setDurationinminutes(duration);
            LOG.log(Level.FINE, "Updated duration to: {0} minutes", selected);
        }
        
        tests.setUserCreatedTest(selected);
        
        return selected;
    }
}
