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

package com.looseboxes.msofficekiosk.ui.exam;

import com.bc.elmi.pu.entities.Test;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.test.Tests;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.ui.listeners.LifecycleEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.test.TestDoc;
import com.looseboxes.msofficekiosk.functions.UserProfileMessage;
import com.looseboxes.msofficekiosk.test.EditTest;
import com.looseboxes.msofficekiosk.test.PromptUserSelectTest;
import com.looseboxes.msofficekiosk.test.TestDocImpl;
import static com.looseboxes.msofficekiosk.test.Tests.MAIN_DOCUS_DEFAULT_NAME;
import com.looseboxes.msofficekiosk.ui.MessageDialog;
import com.looseboxes.msofficekiosk.ui.UiBeans;
import java.util.concurrent.TimeUnit;
import org.eclipse.swt.widgets.Display;
import org.springframework.context.ApplicationContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 30, 2019 12:27:00 AM
 */
@Lazy
@Configuration
public class ExamUiConfiguration {

    @Bean public ExamUi examUi(AppContext context, AppUiContext uiContext, 
            ConfigFactory configFactory,
            Tests tests, LifecycleEventHandler lifecycleEventHandler, Display display, 
            MessageDialog messageDialog, ExamUiConfigurer uiConfigurer, 
            UserProfileMessage userProfileMessage, LoginManager loginManager) {
        
        final int activationLeadTimeMinutes = configFactory.getConfig(ConfigService.APP_INTERNAL)
                .getInt(ConfigNames.TESTS_ACTIVATION_LEADTIME_MINUTES);

        final Test test = tests.activateTest(loginManager, activationLeadTimeMinutes, TimeUnit.MINUTES);

        final TestDoc testDoc = new TestDocImpl(test, MAIN_DOCUS_DEFAULT_NAME);
        
        return new ExamUiImpl(context, uiContext, tests, testDoc, lifecycleEventHandler, 
            display, messageDialog, uiConfigurer, userProfileMessage, loginManager);
    }
    
    @Bean @Scope("prototype") public ExamUiConfigurer examUiConfigurer(UiBeans.UiConfigurer configurer) {
        return (ExamUiConfigurer)configurer;
    }

    @Bean @Scope("prototype") EditTest editTest(ApplicationContext spring) {
        return new EditTest(spring);
    }

    @Bean @Scope("prototype") PromptUserSelectTest promptUserSelectTest(
            AppUiContext uiContext, ConfigFactory configFactory) {
        return new PromptUserSelectTest(uiContext, configFactory);
    }
}
