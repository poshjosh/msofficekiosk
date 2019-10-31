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

import java.util.List;
import java.util.Optional;
import javax.validation.ValidationException;

/**
 * @author Chinomso Bassey Ikwuagwu on Jan 25, 2019 12:48:16 AM
 */
public interface LoginManager {
    
    //@todo replace with property
    public static final String USERNAME_IF_NONE = "Guest";
    
    default Object validateUserId() {
        return getUserId().orElseThrow(() -> new ValidationException(
            "You are not logged in\nYou must be logged in before performing the requested operation"));
    }

    Optional<Object> getUserId();
    
    void addListener(LoginListener listener);
    
    default String validateUsername() {
        return getLoggedInUserName().orElseThrow(() -> new ValidationException(
            "You are not logged in\nYou must be logged in before performing the requested operation"));
    }
    
    default String validateUsergroup() {
        final String username = validateUsername();
        return getUserGroup().orElseThrow(() -> new ValidationException("Current logged in user: " + username + 
                        ", does not belong to any unit/group.\nMust belong to a unit/group before performing the requested operation"));
    }

    default String getLoggedInUserNameOrDefault() {
        return getLoggedInUserName(LoginManager.USERNAME_IF_NONE);
    }

    default Optional<String> getLoggedInUserName(){
        return Optional.ofNullable(getLoggedInUserName(null));
    }
    
    String getLoggedInUserName(String resultIfNone);

    boolean isLoggedIn();
    
    /**
     * @param attempts
     * @return true If the user is logged in.
     */
    boolean promptUserLogin(int attempts);

    /**
     * @return true If the user is logged out.
     */
    boolean logout();
    
    boolean isUserInRole(String role);

    boolean isUserInAnyRole(String... roles);
    
    Optional<String> getUserGroup();
    
    List<String> getUserRoles();
}
