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

package com.looseboxes.msofficekiosk.functions.swtjar;

import com.bc.config.Config;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import java.io.Serializable;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 23, 2018 6:22:52 PM
 */
public class GetSwtJarFileNameForOsAch 
        implements Function<Config<Properties>, String>, Serializable {

    private static final Logger LOG = Logger.getLogger(GetSwtJarFileNameForOsAch.class.getName());

    @Override
    public String apply(Config<Properties> config) {
        final String arch = System.getProperty("os.arch");
        final String swtJarFileName;
        if(arch.contains("64")) {
            swtJarFileName = config.getString(ConfigNames.SWT_JARFILE_64BIT).trim();
        }else {
            swtJarFileName = config.getString(ConfigNames.SWT_JARFILE_32BIT).trim();
        }
        
        LOG.info(() -> "os.arch: " + arch + ", swt jar file: " + swtJarFileName);
        
        return swtJarFileName;
    }
}
