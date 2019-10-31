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

import com.looseboxes.msofficekiosk.functions.LoadExternalJar;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 28, 2019 6:18:18 PM
 */
public class MsKioskSetupProd extends MsKioskSetupImpl{

    public MsKioskSetupProd(String [] args, Path homeDir) {
        this(args, homeDir, new LoadExternalJar());
    }
    
    public MsKioskSetupProd(String [] args, Path homeDir, Consumer<String> addNamedJar) {
        super(args, homeDir, addNamedJar, "META-INF/properties/logging.properties");
    }
}
