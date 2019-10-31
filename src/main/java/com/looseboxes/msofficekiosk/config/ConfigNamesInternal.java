/*
 * Copyright 2018 NUROX Ltd.
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

package com.looseboxes.msofficekiosk.config;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2018 4:16:34 AM
 */
public interface ConfigNamesInternal {

    String APP_NAME = "App Name";

    String SECURITY_ALGORITHM = "security.algorithm";

    String SECURITY_ENCRYPTIONKEY = "security.encryptionKey";
    
    String AUTOSAVE_INTERVAL_SECONDS = "Auto-save Interval (in seconds)";
    
    String SWT_JARFILE_32BIT = "swtJarFile.32Bit";

    String SWT_JARFILE_64BIT = "swtJarFile.64Bit";

    String HTTP_CONNECT_TIMEOUT = "HTTP Connect Timeout";

    String HTTP_READ_TIMEOUT = "HTTP Read Timeout";

    String HTTP_USER_AGENT = "HTTP User Agent";
    
    String CHARACTER_SET = "Character Set";

    String ROLES_IT_ADMIN = "roles.it_admin";

    String ROLES_ACADEMIC_STAFF = "roles.academic_staff";
    
    String ROLES_STUDENT = "roles.student";
    
    String CACHE_EACH_DEFAULT_SIZE_BYTES = "cache.each.defaultSizeBytes";

    /**
     * Tests which have up to this amount of time to their start time may be activated
     */ 
     String TESTS_ACTIVATION_LEADTIME_MINUTES = "tests.activation.leadTimeMinutes";   
     
     String EXIT_SYSTEM_ON_UI_EXIT = "exitSystemOnUiExit";
}
