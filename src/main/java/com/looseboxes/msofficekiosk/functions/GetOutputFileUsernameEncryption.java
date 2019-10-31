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

package com.looseboxes.msofficekiosk.functions;

import com.bc.security.Encryption;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import java.util.Optional;
import java.util.function.Function;
import com.bc.config.Config;
import java.util.Properties;
import com.looseboxes.msofficekiosk.config.ConfigService;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2018 4:43:01 AM
 */
public class GetOutputFileUsernameEncryption implements Function<AppContext, Optional<Encryption>> {

    @Override
    public Optional<Encryption> apply(AppContext context) {
        final Config<Properties> appConfig = context.getConfig(ConfigService.APP_PROTECTED);
        final boolean encrypt = appConfig.getBoolean(ConfigNames.OUTPUTFILE_ENCRYPT_USERNAME, false);
        final Encryption output;
        if(encrypt) {
            output = new GetEncryption().apply(context);
        }else{
            output = null;
        }
        return Optional.ofNullable(output);
    }
}
