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

package com.looseboxes.msofficekiosk;

import com.looseboxes.msofficekiosk.commands.CommandConfiguration;
import com.looseboxes.msofficekiosk.launchers.LauncherFactory;
import com.looseboxes.msofficekiosk.ui.UiConfiguration;
import com.looseboxes.msofficekiosk.ui.admin.AdminUiConfiguration;
import com.looseboxes.msofficekiosk.ui.exam.ExamUiConfiguration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 30, 2019 4:48:30 PM
 */
public class SpringConfigClassesProviderImpl implements SpringConfigClassesProvider{

    private static final Logger LOG = Logger.getLogger(SpringConfigClassesProviderImpl.class.getName());
    
    @Override
    public boolean isAdmin(Class<?>[] configurationClassess) {
        
        final List list = this.getTypes(configurationClassess);
        
        return list.contains(LauncherFactory.Type.Academic_Staff) ||
                list.contains(LauncherFactory.Type.IT_Admin) ||
                list.contains(LauncherFactory.Type.Background);
    }
    
    @Override
    public List<LauncherFactory.Type> getTypes(Class<?>[] configurationClassess) {
        
        final List list = Arrays.asList(configurationClassess);
        
        if(list.contains(ExamUiConfiguration.class)) {
            return Arrays.asList(LauncherFactory.Type.Student);
        }else if(list.contains(AdminUiConfiguration.class)) {
            return Arrays.asList(LauncherFactory.Type.Academic_Staff, 
                    LauncherFactory.Type.IT_Admin, 
                    LauncherFactory.Type.Background);
        }else{
            return Collections.EMPTY_LIST;
        }
    }
    
    @Override
    public Class<?>[] apply(LauncherFactory.Type type) {
        
        final Class<?>[] result;
        
        switch(type) {
            case Student:
                result = new Class[]{MsKioskConfiguration.class, CommandConfiguration.class, 
                    UiConfiguration.class, ExamUiConfiguration.class};
                break;
            case Academic_Staff:
            case IT_Admin:
            case Background:
                result = new Class[]{MsKioskConfiguration.class, CommandConfiguration.class, 
                    UiConfiguration.class, AdminUiConfiguration.class};
                break;
            default:
                throw getInvalidTypeException(type);
        }
        
        LOG.info(() -> "Type: " + type + ", Spring configuration classes: " + Arrays.toString(result));
        
        return result;
    }
    
    public RuntimeException getInvalidTypeException(LauncherFactory.Type type) {
        return new IllegalArgumentException("Unexpected type: " + type + 
                ", supported types: " + Arrays.toString(LauncherFactory.Type.values()));
    }
}
