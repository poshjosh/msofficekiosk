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
import com.bc.elmi.pu.entities.Unit;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.StudentGroupListSupplier;
import com.looseboxes.msofficekiosk.popups.MultiInputDialog;
import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.ui.MessageDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ValidationException;
import org.springframework.context.ApplicationContext;

/**
 * @author Chinomso Bassey Ikwuagwu on November 1, 2019 01:13:00 AM
 */
public class EditTest implements UnaryOperator<Test>{

    private static final Logger LOG = Logger.getLogger(EditTest.class.getName());

    private final ApplicationContext spring;

    public EditTest(ApplicationContext spring) {
        this.spring = Objects.requireNonNull(spring);
    }
    
    @Override
    public Test apply(Test target) {

        final String testIdLabel = "Test ID (Numbers only e.g 15)";
        final String timeFormat = "HH:mm";
        final String startTimeLabel = "Start Time (" + timeFormat + " e.g 09:30)"; 
        final String durationLabel = "Duration (in minutes e.g 120)"; 
        
        final boolean createNotEdit = target.getTestid() == null;

        final Map inputMap = new LinkedHashMap();
        if(createNotEdit) {
            final Comparator<Test> comparator = (l, r) -> l.getTestid().compareTo(r.getTestid());
            final List<Test> tests = spring.getBean(Tests.class).get();
            final Test latest = tests == null || tests.isEmpty() ? null : tests.stream().max(comparator).orElse(null);
            inputMap.put(testIdLabel, latest == null ? 0 : latest.getTestid() + 1);
        }
        
        String studentGroup = null;
        
        final List<Unit> unitList;
        if(createNotEdit) {
            unitList = spring.getBean(StudentGroupListSupplier.class).get();
            if(unitList == null || unitList.isEmpty()) {
                inputMap.put(TestDocKey.STUDENT_GROUP, "");    
            }else{
                studentGroup = spring.getBean(LoginManager.class).getUserGroup().orElse(null);
                if(studentGroup == null || studentGroup.isEmpty()) {
//                    final List<Selection> selectionValues = SelectionValues.from(unitList).getSelectionValues("");
                    inputMap.put(TestDocKey.STUDENT_GROUP, unitList.stream()
                        .map((u) -> getValueForUnit(u)).toArray());
                }
            }
        }else{
            unitList = Collections.EMPTY_LIST;
        }
        
        final String testname = target.getTestname() == null ? "" : target.getTestname();
        inputMap.put(TestDocKey.TEST_NAME, testname);
        
        inputMap.put(startTimeLabel, "");

        final Integer dur = target.getDurationinminutes() == null ? 
                60 : target.getDurationinminutes();
        inputMap.put(durationLabel, dur);
        
        final Config uiConfig = spring.getBean(ConfigFactory.class).getConfig(ConfigService.APP_UI);
        
        final Map outputMap = new MultiInputDialog(uiConfig).apply(inputMap, "Enter Test Details");
        
        final Integer testId = (Integer)outputMap.getOrDefault(testIdLabel, null);
        if(testId != null) {
            final List<Test> tests = spring.getBean(Tests.class).get();
            if(tests != null && !tests.isEmpty()) {
                final boolean exists = tests.stream()
                        .filter((test) -> testId.equals(test.getTestid())).findFirst().isPresent();
                if(exists) {
                    spring.getBean(MessageDialog.class).showWarningMessage(
                            "Please re-try.\n\nA Test already exists with the entered test ID: " + testId);
                    return apply(target);
                }
            }
            target.setTestid(Objects.requireNonNull(testId));
            LOG.log(Level.FINE, "Updated test ID to: {0}", testId);
        }

        final String name = (String)outputMap.getOrDefault(TestDocKey.TEST_NAME, null);
        target.setTestname(this.requireNonNullOrEmpty(TestDocKey.TEST_NAME, name));
        LOG.log(Level.FINE, "Updated test name to: {0}", name);
        
        final String timeStr = (String)outputMap.getOrDefault(startTimeLabel, null);
        requireNonNullOrEmpty(startTimeLabel, timeStr);
        final Date time;
        try{
            time = new SimpleDateFormat(timeFormat).parse(timeStr);

            target.setStarttime(time);
            LOG.log(Level.FINE, "Updated Starttime to: {0}", time);

        }catch(java.text.ParseException e) {
            throw new ValidationException("Invalid " + startTimeLabel, e);
        }
        
        final Integer duration = (Integer)outputMap.getOrDefault(durationLabel, null);
        target.setDurationinminutes(Objects.requireNonNull(duration));
        LOG.log(Level.FINE, "Updated duration to: {0} minutes", duration);
        
        if(createNotEdit) {
            if(studentGroup == null || studentGroup.isEmpty()) { 
                studentGroup = (String)outputMap.getOrDefault(TestDocKey.STUDENT_GROUP, null);
                LOG.log(Level.FINE, "Selected student group: {0}", studentGroup);
            }else{
                LOG.log(Level.FINE, "Current user's student group: {1}", studentGroup);
            }        
            if(studentGroup != null && !studentGroup.isEmpty()) {
                final Unit unit;
                if(unitList == null || unitList.isEmpty()) {
                    unit = new Unit((short)0);
                    unit.setUnitname(studentGroup);
                }else{
                    final String sg = studentGroup;
                    unit = unitList.stream().filter((u) -> sg.equals(getValueForUnit(u)))
                            .findFirst().orElseThrow(() -> new ValidationException(
                                    "Invalid " + TestDocKey.STUDENT_GROUP + " selected"));
                }
                LOG.log(Level.FINE, "Updated test unit to: {0}", unit.getUnitname());
                target.setUnitList(new ArrayList(Collections.singletonList(unit)));
                // No need to add the Test to Unit.getTestList(). We didn't create the
                // unit, we shouldn't edit it arbitrarily.
            }
        }
        
        return target;
    }
    
    private String getValueForUnit(Unit u) {
        return u.getUnitname();
    }
    
    private String requireNonNullOrEmpty(String key, String val) {
        Objects.requireNonNull(val);
        if(val.isEmpty()) {
            throw new ValidationException("Empty text is invalid for input: " + key); 
        }
        return val;
    }
}
