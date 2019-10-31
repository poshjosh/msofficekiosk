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

import com.bc.functions.FindExceptionInHeirarchy;
import com.looseboxes.msofficekiosk.exceptions.HasUserMessage;
import java.net.UnknownHostException;
import java.util.function.Predicate;
import javax.security.auth.login.LoginException;
import javax.validation.ValidationException;

/**
 * @author Chinomso Bassey Ikwuagwu on May 12, 2019 8:52:45 AM
 */
public class GetUserMessage {

    public String apply(Throwable t) {
        
        return apply(t, "Encountered an unexpected problem");
    }
    
    public String apply(Throwable t, String outputIfNone) {

        final Predicate<Throwable> predicate = (ex) -> 
                ex.getClass().getName().equals("com.mysql.jdbc.exceptions.CommunicationsException") ||
                ex.getClass().getName().equals("com.mysql.cj.jdbc.exceptions.CommunicationsException");

        final String m;
        if(t instanceof HasUserMessage) {
            m = ((HasUserMessage)t).getUserMessage();
        }else if(t instanceof javax.validation.ConstraintViolationException) {
            m = "Input contains one or more invalid values";
        }else if(t instanceof ValidationException) {
            m = truncate(t.getLocalizedMessage(), 50);
        }else if(t instanceof LoginException) {
            m = truncate(t.getLocalizedMessage(), 50);
        }else if(new FindExceptionInHeirarchy().apply(t, UnknownHostException.class).isPresent()) {
            m = "Internet unavailable";
        }else if(new FindExceptionInHeirarchy().apply(t, predicate).isPresent()) {
            m = "Failed to connect to database";
        }else{
            m = null;
        }
        
        return m == null ? outputIfNone : m;
    }
    
    private String truncate(String m, int len) {
        return m == null ? null : m.length() <= len ? m : m.substring(0, len);
    }
}
