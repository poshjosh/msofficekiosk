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

import javax.security.auth.login.LoginException;

/**
 * @author Chinomso Bassey Ikwuagwu on May 1, 2019 1:41:51 PM
 */
public class RetryLogin extends LoginException { 
    
    public RetryLogin() { }
    
    public RetryLogin(String msg) {
        super(msg);
    }
}
