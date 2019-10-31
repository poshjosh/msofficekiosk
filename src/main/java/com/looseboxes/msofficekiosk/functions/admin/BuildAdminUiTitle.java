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

package com.looseboxes.msofficekiosk.functions.admin;

import com.bc.config.Config;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.functions.UserProfileMessage;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 30, 2019 7:21:03 PM
 */
public class BuildAdminUiTitle implements UnaryOperator<String>{

    private static final Logger LOG = Logger.getLogger(BuildAdminUiTitle.class.getName());
    
    private final UserProfileMessage userProfileMessage;

    public BuildAdminUiTitle(UserProfileMessage userProfileMessage) {
        this.userProfileMessage = Objects.requireNonNull(userProfileMessage);
    }
    
    public String with(Config config) {
        final String appName = config.getString(ConfigNames.APP_NAME, "App");
        return apply(appName);
    }

    @Override
    public String apply(String appName) {
        final String usrStat = userProfileMessage.get();
        final String title = appName + " - AMIN CONSOLE (" + usrStat + ')';
        LOG.fine(() -> "Built AdminFrame title: " + title);
        return title;
    }
}
