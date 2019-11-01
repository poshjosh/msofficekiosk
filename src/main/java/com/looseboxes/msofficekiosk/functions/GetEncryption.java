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
import com.bc.security.SecurityProvider;
import com.looseboxes.msofficekiosk.AppContext;
import java.security.GeneralSecurityException;
import java.util.function.Function;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.bc.config.Config;
import java.util.Properties;
import com.looseboxes.msofficekiosk.config.ConfigService;

/**
 * @author Chinomso Bassey Ikwuagwu on May 12, 2018 8:57:56 PM
 */
public class GetEncryption implements Function<AppContext, Encryption> {

    @Override
    public Encryption apply(AppContext context) {
        try{
            return this.getOrException(context);
        }catch(GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public Encryption get(AppContext context, Encryption outputIfNone) {
        try{
            return this.getOrException(context);
        }catch(GeneralSecurityException ignored) {
            return outputIfNone;
        }
    }
    
    public Encryption getOrException(AppContext context) throws GeneralSecurityException {
        final Config<Properties> config = context.getConfigFactory().getConfig(ConfigService.APP_INTERNAL);
        final String algorithm = config.getString(ConfigNames.SECURITY_ALGORITHM);
        final String encryptionKey = config.getString(ConfigNames.SECURITY_ENCRYPTIONKEY);
        final Encryption encryption = SecurityProvider.DEFAULT.getEncryption(algorithm, encryptionKey);
        return encryption;
    }
}
