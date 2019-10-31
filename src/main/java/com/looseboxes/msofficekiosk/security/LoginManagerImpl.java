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

import com.bc.elmi.pu.entities.User;
import com.looseboxes.msofficekiosk.ui.MessageDialog;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 27, 2019 12:14:30 PM
 */
public class LoginManagerImpl implements Login, LoginManager {

    private static final Logger LOG = Logger.getLogger(LoginManagerImpl.class.getName());

    private final Login login;
    
    private final CredentialsSupplier credentialsSupplier;
    
    private final MessageDialog messageDialog;
    
    private final List<LoginListener> listeners;

    private static User user;
    
    private static transient volatile char [] rawPassword;
    
    public LoginManagerImpl(
            Login login, 
            CredentialsSupplier credentialsSupplier,
            MessageDialog messageDialog) {
        this.login = Objects.requireNonNull(login);
        this.credentialsSupplier = Objects.requireNonNull(credentialsSupplier);
        this.listeners = new ArrayList<>();
        this.messageDialog = Objects.requireNonNull(messageDialog);
    }
    
    @Override
    public void addListener(LoginListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public List<String> getUserRoles() {
        List<String> output;
        if(user == null || user.getRoleList() == null) {
            output = Collections.EMPTY_LIST;
        }else{
            output = user.getRoleList().stream()
                    .map((role) -> role.getRolename()).collect(Collectors.toList());
        }
        return output;
    }

    @Override
    public Optional<Object> getUserId() {
        Object output;
        if(user == null) {
            output = null;
        }else{
            output = user.getUserid() == null ? null : user.getUserid();
        }
        return Optional.ofNullable(output);
    }

    @Override
    public Optional<String> getUserGroup() {
        String output;
        if(user == null) {
            output = null;
        }else{
            output = user.getUnit() == null ? null : user.getUnit().getUnitname();
        }
        return Optional.ofNullable(output);
    }

    @Override
    public boolean isUserInRole(String role) {
        return user == null || user.getRoleList() == null ? false : 
                user.getRoleList().stream()
                .filter((roleObj) -> role.equals(roleObj.getRolename()))
                .findFirst().isPresent();
    }

    @Override
    public boolean isUserInAnyRole(String... roles) {
        return user == null ? false : Arrays.asList(roles).stream()
                .filter((role) -> isUserInRole(role)).findFirst().isPresent();
    }

    @Override
    public String getLoggedInUserName(String outputIfNone) {
        final String output = user == null ? null : user.getUsername();
        return output == null ? outputIfNone : output;
    }

    @Override
    public boolean isLoggedIn() {
        return this.user != null;
    }

    @Override
    public boolean promptUserLogin(int n) {
        
        if(this.isLoggedIn()) {
            
            this.messageDialog.showInformationMessage("You are already logged in");
        
            return true;
            
        }else{
        
            boolean loggedIn = false;
            
            try{
                
                for(LoginListener l : listeners) {
                    try{
                        l.preLogin(this);
                    }catch(RuntimeException e) {
                        LOG.log(Level.WARNING, null, e);
                    }
                }
                
                final Map loginData = credentialsSupplier.get();
                
                final String pwd = getPwd(loginData.get(CredentialsSupplier.PASSWORD));
                if(pwd != null && pwd.length() > 0) {
                    rawPassword = pwd.toCharArray();
                }
                
                user = login((String)loginData.get(CredentialsSupplier.USERNAME), pwd);
                
                loggedIn = this.isLoggedIn();
                
                for(LoginListener l : listeners) {
                    try{
                        l.postLogin(this);
                    }catch(RuntimeException e) {
                        LOG.log(Level.WARNING, null, e);
                    }
                }

                if(loggedIn) {

                    this.messageDialog.showInformationMessage("Login Successful");
                    
                }else if(n > 1){
                
                    promptUserLogin(n - 1);
                }
            }catch(LoginException e) {
                
                this.messageDialog.showInformationMessage(e.getLocalizedMessage());
                
                if(n > 1 && e instanceof RetryLogin) {
                    
                    LOG.warning(e.toString());
                    
                    promptUserLogin(n - 1);
                    
                }else{
                    
                    LOG.log(Level.WARNING, null, e);
                }
            }

            return loggedIn;
        }
    }
    
    @Override
    public User login(String usr, String pwd) throws LoginException {
    
        try{
            user = login.login(usr, pwd);
        }catch(LoginException | RuntimeException e) {
            throw e;    
        }finally{
            this.cleanupAfterLogin();
        }
        
        return user;
    }
    
    private void cleanupAfterLogin() {
        if(user == null) {
            rawPassword = null;
        }
    }
    
    @Override
    public boolean logout() {
        for(LoginListener l : listeners) {
            try{
                l.preLogout(this);
            }catch(RuntimeException e) {
                LOG.log(Level.WARNING, null, e);
            }
        }
        this.user = null;
        final boolean loggedOut = true;
        for(LoginListener l : listeners) {
            try{
                l.postLogout(this);
            }catch(RuntimeException e) {
                LOG.log(Level.WARNING, null, e);
            }
        }
        return loggedOut;
    }
    
    private String getPwd(Object obj) {
        if(obj instanceof char[]) {
            return new String((char[])obj);
        }else{
            return obj == null ? null : obj.toString();
        }
    }
    
    public void setUser(User u) {
        user = u;
    }
    
    public User getUser() {
        return user;
    }
    
    public char [] getRawPassword() {
        return rawPassword;
    }

    public Login getLogin() {
        return login;
    }

    public CredentialsSupplier getCredentialsSupplier() {
        return credentialsSupplier;
    }

    public MessageDialog getMessageDialog() {
        return messageDialog;
    }

    public List<LoginListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }
}
