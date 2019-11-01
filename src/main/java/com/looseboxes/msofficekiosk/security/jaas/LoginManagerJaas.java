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

package com.looseboxes.msofficekiosk.security.jaas;

import com.bc.jaas.DisplayUserPromptMessageHandler;
import com.bc.jaas.LoginManagerImpl;
import com.bc.jaas.callbacks.CallbackHandlerImpl;
import com.looseboxes.msofficekiosk.security.LoginListener;
import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.ui.UI;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.security.auth.login.LoginException;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 21, 2018 9:07:19 PM
 */
public class LoginManagerJaas extends LoginManagerImpl implements LoginManager {

//    private transient static final Logger LOG = Logger.getLogger(LoginManagerJaas.class.getName());
    
    private final List<LoginListener> loginListeners;
    
    private final Supplier<UI> uiSupplier;
    
    public LoginManagerJaas(Supplier<UI> uiSupplier, Component parent) 
            throws LoginException, SecurityException {
        
        this(uiSupplier, parent, "Enter Login Details");
    }
    
    public LoginManagerJaas(Supplier<UI> uiSupplier, Component parent, String title) 
            throws LoginException {
        
        super("MsofficekioskJaasConfig", 
                new CallbackHandlerImpl(
                        new DisplayUserPromptMessageHandler(parent), 
                        new CredentialsSupplierJaas(parent, title)
                ), 
                new DisplayUserPromptMessageHandler(parent));
        
        this.loginListeners = new ArrayList<>();
        this.uiSupplier = Objects.requireNonNull(uiSupplier);
    }

    @Override
    public void addListener(LoginListener listener) {
        this.loginListeners.add(listener);
    }

    @Override
    public List<String> getUserRoles() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public boolean isUserInAnyRole(String... roles) {
        return false;
    }

    @Override
    public Optional<Object> getUserId() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getUserGroup() {
        return Optional.empty();
    }

    @Override
    public String getLoggedInUserName(String resultIfNone) {
        return super.getLoggedInUserName(resultIfNone);
    }

    @Override
    public boolean isLoggedIn() {
        return super.getLoggedInUserName(null) == null;
    }
    
    @Override
    public boolean promptUserLogin(int attempts) {
      
        if(this.isLoggedIn()) {
            
            final String msg = "You are already logged in";
            
            uiSupplier.get().getMessageDialog().showWarningMessage(msg);
            
            return true;
            
        }else{

            for(LoginListener l : loginListeners) {
                l.preLogin(this);
            }
            
            final boolean loggedIn = super.promptUserLogin(attempts);
  
            for(LoginListener l : loginListeners) {
                l.postLogin(this);
            }
            
            return loggedIn;
        }
    }
    
    @Override
    public boolean logout() {
        
        if( ! this.isLoggedIn()) {
            
            final String msg = "You are already logged out";
            
            uiSupplier.get().getMessageDialog().showWarningMessage(msg);
            
            return true;
            
        }else{
            
            for(LoginListener l : loginListeners) {
                l.preLogout(this);
            }

            final boolean loggedOut = super.logout();

            for(LoginListener l : loginListeners) {
                l.postLogout(this);
            }

            return loggedOut;
        }
    }
}

