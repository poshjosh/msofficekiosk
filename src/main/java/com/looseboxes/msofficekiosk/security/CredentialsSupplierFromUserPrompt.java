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
import com.looseboxes.msofficekiosk.functions.ui.GetAwtFontFromConfig;
import com.looseboxes.msofficekiosk.popups.MultiInputWindow;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 29, 2019 2:25:36 PM
 */
public class CredentialsSupplierFromUserPrompt implements CredentialsSupplier{

    private final Font font;
    
    private final boolean displayPassword;

    public CredentialsSupplierFromUserPrompt(Config<Properties> config, boolean displayPassword) { 
        this(new GetAwtFontFromConfig().apply(config), displayPassword);
    }

    public CredentialsSupplierFromUserPrompt(Font font, boolean displayPassword) { 
        this.font = Objects.requireNonNull(font);
        this.displayPassword = displayPassword;
    }
    
    @Override
    public Map get() {

        Map loginData = new LinkedHashMap();

        loginData.put(CredentialsSupplier.USERNAME, "");
        loginData.put(CredentialsSupplier.PASSWORD, "");

        return new MultiInputWindow(font, displayPassword).apply(loginData, "Login");
    }
}
