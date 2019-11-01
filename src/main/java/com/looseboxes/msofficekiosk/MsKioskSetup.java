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

import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.launchers.LauncherFactory;
import java.nio.file.Path;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 27, 2019 11:25:26 PM
 */
public interface MsKioskSetup extends ApplicationContextInitializer{
    
    void launchApp(LauncherFactory.Type launchType);

    LauncherFactory.Type getLaunchType();

    String getLoggingConfigFile();

    @Override
    void initialize(ConfigurableApplicationContext applicationContext);

    boolean isAdmin();
    
    boolean isAdmin(LauncherFactory.Type launchType);

    //    @Override
    boolean isServer();

    //    @Override
    boolean isServerHost(String host);
    
    Path getDir(String name);

    ConfigFactory getConfigFactory();
}
