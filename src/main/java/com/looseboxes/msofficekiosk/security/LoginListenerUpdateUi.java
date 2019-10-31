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

package com.looseboxes.msofficekiosk.security;

import com.bc.config.Config;
import com.looseboxes.msofficekiosk.MsKioskSetup;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.UserProfileMessage;
import com.looseboxes.msofficekiosk.functions.admin.BuildAdminUiTitle;
import com.looseboxes.msofficekiosk.ui.admin.AdminUi;
import com.looseboxes.msofficekiosk.ui.exam.ExamUi;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.ApplicationContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 30, 2019 7:12:07 PM
 */
public class LoginListenerUpdateUi extends LoginListenerAdapter{

    private static final Logger LOG = Logger.getLogger(LoginListenerUpdateUi.class.getName());
    
    private final ApplicationContext spring;
    private final MsKioskSetup setup;

    public LoginListenerUpdateUi(ApplicationContext spring) {
        this.spring = Objects.requireNonNull(spring);
        this.setup = spring.getBean(MsKioskSetup.class);
    }

    @Override
    public void postLogout(LoginManager loginManager) {
        if( ! loginManager.isLoggedIn()) {
            updateTitle(loginManager); 
            updateLoginButtonText(loginManager);
        }
    }

    @Override
    public void postLogin(LoginManager loginManager) {
        if(loginManager.isLoggedIn()) {
            updateTitle(loginManager); 
            updateLoginButtonText(loginManager);
        }
    }
    
    public void updateTitle(LoginManager loginManager) {
        if(setup.isAdmin()) {
            final AdminUi adminUi = spring.getBean(AdminUi.class);
            final boolean specifyNameBecauseAppContextImplementsConfigFactoryHence2Implementations = true;
            final Config config = spring.getBean("configFactory", ConfigFactory.class).getConfig(ConfigService.APP_INTERNAL);
            final String m = spring.getBean(BuildAdminUiTitle.class).with(config);
//            adminUi.getTitleMenuItem().setText(m);
//            LOG.fine(() -> "Updated AdminFrame title to: " + m);
            adminUi.getShell().getDisplay().asyncExec(() -> {
                try{
                    adminUi.getTitleMenuItem().setText(m);
//                    adminUi.getUserProfileMenuItem().setText(m);
                    LOG.fine(() -> "Updated AdminUi User Profile Message to: " + m);
                }catch(RuntimeException e) {
                    LOG.log(Level.WARNING, null, e);
                }    
            });
        }else{
            final ExamUi examUi = spring.getBean(ExamUi.class);
            final String m = spring.getBean(UserProfileMessage.class).get();
            examUi.getShell().getDisplay().asyncExec(() -> {
                try{
                    examUi.getShellUi().getUserProfileMenuItem().setText(m);
                    LOG.fine(() -> "Updated ExamUi User Profile Message to: " + m);
                }catch(RuntimeException e) {
                    LOG.log(Level.WARNING, null, e);
                }    
            });
        }
    }
    
    public void updateLoginButtonText(LoginManager loginManager) {
        final String text = loginManager.isLoggedIn() ? "Logout" : "Login";
        if(setup.isAdmin()) {
            final AdminUi adminUi = spring.getBean(AdminUi.class);
            adminUi.getLoginMenuItem().setText(text);
            LOG.fine(() -> "Updated AdminFrame Login Button text to: " + text);
        }else{
//            final ExamUi examUi = spring.getBean(ExamUi.class);
//            LOG.fine(() -> "Updated ExamUi Login Button text to: " + text);
        }
    }
}
