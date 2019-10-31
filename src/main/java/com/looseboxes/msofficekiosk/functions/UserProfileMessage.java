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

package com.looseboxes.msofficekiosk.functions;

import com.bc.socket.io.functions.GetLocalIpAddress;
import com.looseboxes.msofficekiosk.security.LoginManager;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on May 16, 2019 7:24:21 AM
 */
public class UserProfileMessage {
    
    private final LoginManager loginManager;

    public UserProfileMessage(LoginManager loginManager) {
        this.loginManager = Objects.requireNonNull(loginManager);
    }

    public String get() {
        final String name = loginManager == null ? LoginManager.USERNAME_IF_NONE : loginManager.getLoggedInUserNameOrDefault();
        return name + " @ " + new GetLocalIpAddress().apply("");
    }
}
