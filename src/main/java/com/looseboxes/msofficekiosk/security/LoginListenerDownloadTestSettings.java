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

import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.net.DownloadTestsettings;
import com.looseboxes.msofficekiosk.document.DocumentStore;
import com.looseboxes.msofficekiosk.test.Tests;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Chinomso Bassey Ikwuagwu on May 13, 2019 8:33:37 PM
 */
public class LoginListenerDownloadTestSettings implements LoginListener{

//    private static final Logger LOG = Logger.getLogger(LoginListenerDownloadTestSettings.class.getName());

    private final DownloadTestsettings downloadTestSettings;

    public LoginListenerDownloadTestSettings(Supplier<Tests> tests, 
            Supplier<DocumentStore> documentStore, ConfigFactory configFactory) {
        this(new DownloadTestsettings(tests, documentStore, configFactory));
    }

    public LoginListenerDownloadTestSettings(DownloadTestsettings downloadTestSettings) {
        this.downloadTestSettings = Objects.requireNonNull(downloadTestSettings);
    }
    
    @Override
    public void postLogin(LoginManager loginManager) {
        new Thread(downloadTestSettings, this.getClass().getSimpleName() + "_Thread").start();
    }
}
