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

import com.looseboxes.msofficekiosk.validators.Precondition;
import java.util.Objects;
import javax.validation.ValidationException;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 27, 2019 4:34:26 PM
 */
public class PreconditionLogin implements Precondition{

    private final LoginManager loginManager;

    public PreconditionLogin(LoginManager loginManager) {
        this.loginManager = Objects.requireNonNull(loginManager);
    }

    @Override
    public void validate(String name, Object value) throws ValidationException {
        if( ! test(value)) {
            if(!loginManager.isLoggedIn()) {
                throw new ValidationException("Login Failed");
            }
        }
    }

    @Override
    public boolean test(Object obj) {

        if( ! loginManager.isLoggedIn()) {

            if( ! loginManager.promptUserLogin(1)) {

                return false;
            }
        }

        return true;
    }
}
