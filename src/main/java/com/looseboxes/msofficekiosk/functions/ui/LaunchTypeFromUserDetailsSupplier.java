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

package com.looseboxes.msofficekiosk.functions.ui;

import com.bc.config.Config;
import com.looseboxes.msofficekiosk.config.ConfigFactoryImpl;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.launchers.LauncherFactory;
import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.security.OneOffLoginManagerProvider;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 29, 2019 5:23:26 PM
 */
public class LaunchTypeFromUserDetailsSupplier implements Supplier<LauncherFactory.Type>{

    private static final Logger LOG = Logger.getLogger(LaunchTypeFromUserDetailsSupplier.class.getName());
    
    private final Path homeDir;

    public LaunchTypeFromUserDetailsSupplier(Path homeDir) {
        homeDir = homeDir.toAbsolutePath().normalize();
        this.homeDir = Objects.requireNonNull(homeDir);
    }

    @Override
    public LauncherFactory.Type get() {
        
        final LauncherFactory.Type launchType;
        
        final LoginManager loginManager = new OneOffLoginManagerProvider().get(homeDir);
        
        if(loginManager.promptUserLogin(2)) {
        
            final Config config = new ConfigFactoryImpl(homeDir).getConfig(ConfigService.APP_INTERNAL);

            if(loginManager.isUserInAnyRole(config.getArray(ConfigNames.ROLES_ACADEMIC_STAFF))) {
                launchType = LauncherFactory.Type.Academic_Staff;
            }else if(loginManager.isUserInAnyRole(config.getArray(ConfigNames.ROLES_IT_ADMIN))){
                launchType = LauncherFactory.Type.IT_Admin;
            }else if(loginManager.isUserInAnyRole(config.getArray(ConfigNames.ROLES_STUDENT))){
                launchType = LauncherFactory.Type.Student;
            }else{
                
                final String username = loginManager.getLoggedInUserNameOrDefault();
                
                final List roles = loginManager.getUserRoles();
                
                LOG.log(Level.WARNING, "For user: {0}, unexpected user roles: {1}", 
                        new Object[]{username, roles});
                
                throw new UnsupportedOperationException(
                        "Not configured to display User Interface for current user: " + 
                                username);
            }
        }else{
            launchType = LauncherFactory.Type.None;
        }
        
        return launchType;
    }
}
