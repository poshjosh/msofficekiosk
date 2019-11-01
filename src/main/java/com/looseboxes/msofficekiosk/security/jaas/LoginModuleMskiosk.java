/*
 * Copyright 2017 NUROX Ltd.
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

package com.looseboxes.msofficekiosk.security.jaas;

import com.bc.jaas.KeyValueStoreException;
import com.bc.jaas.loginmodules.LoginModuleImpl;
import com.bc.jaas.loginmodules.SimpleCredentialsValidator;
import com.looseboxes.msofficekiosk.functions.io.CreateNewFile;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 22, 2017 7:38:07 PM
 */
public class LoginModuleMskiosk extends LoginModuleImpl {

    private transient static final Logger LOG = Logger.getLogger(LoginModuleMskiosk.class.getName());
    
    static{
        final File keyValueStoreFile = JaasFiles.PATH_KEY_VALUE_STORE.toFile();
        if( ! keyValueStoreFile.exists()) {
            try{
                new CreateNewFile().execute(keyValueStoreFile, Boolean.FALSE);
            }catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public LoginModuleMskiosk() 
            throws IOException, LoginException, GeneralSecurityException, KeyValueStoreException {
        super(new SimpleCredentialsValidator(
                        (messageSupplier) -> { LOG.fine(() -> messageSupplier.get()); },
                        new CredentialsSupplierJaas(null, "Create New User"), 
                        JaasFiles.PATH_KEY_VALUE_STORE.toFile()
                )
        );
    }
}
