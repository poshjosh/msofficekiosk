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

package com.looseboxes.msofficekiosk.ui.admin;

import com.looseboxes.msofficekiosk.Cache;
import com.looseboxes.msofficekiosk.MsKioskConfiguration;
import com.looseboxes.msofficekiosk.functions.UserProfileMessage;
import com.looseboxes.msofficekiosk.functions.admin.BuildAdminUiTitle;
import com.looseboxes.msofficekiosk.functions.admin.GetDevicedetailsList;
import com.looseboxes.msofficekiosk.ui.listeners.LifecycleEventHandler;
import com.looseboxes.msofficekiosk.ui.UiBeans;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.security.PreconditionLogin;
import com.looseboxes.msofficekiosk.ui.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 30, 2019 12:26:14 AM
 */
@Lazy
@Configuration
public class AdminUiConfiguration {
    
    @Bean public AdminUiOld adminUiOld(UiBeans.UiConfigurer uiConfigurer,
            LifecycleEventHandler lifeCycleEventHandler, MessageDialog messageDialog) {
        return new AdminUiOld(uiConfigurer, lifeCycleEventHandler, messageDialog);
    }
    
    @Bean public AdminUi adminUi(AppUiContext uiContext, Display display, 
            LifecycleEventHandler lifecycleEventHandler, 
            MessageDialog messageDialog, 
            UiBeans.UiConfigurer<AdminUi> configurer) {
    
        return new AdminUiImpl(uiContext, display, lifecycleEventHandler, 
                messageDialog, "title", "", configurer);
    }
    
    @Bean @Scope("prototype") public BuildAdminUiTitle buildAdminUiTitle(UserProfileMessage userProfileMessage) {
        return new BuildAdminUiTitle(userProfileMessage);
    }

    @Bean @Scope("prototype") public GetDevicedetailsList getDevicedetailsList(
            AppUiContext uiContext,
            PreconditionLogin preconditionLogin,
            @Qualifier(MsKioskConfiguration.DEFAULT_CACHE_NAME) Cache cache) {
        return new GetDevicedetailsList(uiContext, preconditionLogin, cache);
    }
}
