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

import com.looseboxes.msofficekiosk.functions.LoadInternalJar;
import com.looseboxes.msofficekiosk.launchers.LauncherFactory;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 28, 2019 6:13:00 PM
 */
public class MsKioskSetupDev extends MsKioskSetupImpl {

    public MsKioskSetupDev(Path homeDir) {
        this(homeDir, new LoadInternalJar());
    }

    public MsKioskSetupDev(Path homeDir, Supplier<LauncherFactory.Type> launchTypeSupplier) {
        super(homeDir, new LoadInternalJar(), "META-INF/properties/logging_devmode.properties", launchTypeSupplier);
    }
    
    public MsKioskSetupDev(Path homeDir, Consumer<String> addNamedJar) {
        super(homeDir, addNamedJar, "META-INF/properties/logging_devmode.properties");
    }
}
