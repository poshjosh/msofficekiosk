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

import com.bc.config.Config;
import com.bc.elmi.pu.entities.User;
import com.bc.socket.io.messaging.data.Devicedetails;
import com.looseboxes.msofficekiosk.mapper.Mapper;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.net.UrlBuilderImpl;
import com.looseboxes.msofficekiosk.net.Rest;
import com.looseboxes.msofficekiosk.net.SyncDataWithServer;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;

/**
 * @author Chinomso Bassey Ikwuagwu on May 1, 2019 1:38:09 PM
 */
public class LoginToServer implements Login{

    private static final Logger LOG = Logger.getLogger(LoginToServer.class.getName());

    private final SyncDataWithServer syncData;
    
    private final ConfigFactory configFactory;
    
    private final Supplier<Devicedetails> ddSupplier;
    
    private final Mapper mapper;
    
    private User user;
    
    public LoginToServer(
            SyncDataWithServer syncData, 
            ConfigFactory configFactory,
            Supplier<Devicedetails> ddSupplier, 
            Mapper mapper) {
        this.syncData = Objects.requireNonNull(syncData);
        this.configFactory = Objects.requireNonNull(configFactory);
        this.ddSupplier = Objects.requireNonNull(ddSupplier);
        this.mapper = Objects.requireNonNull(mapper);
    }
    
    public void preLogin(URL url, Map<String, String> params, Devicedetails dd) { }
    
    @Override
    public User login(String usr, String pwd) throws LoginException {
        try{
            
            if(usr == null || usr.isEmpty()) {
                throw new RetryLogin("Please enter a " + CredentialsSupplier.USERNAME);
            }
            if(pwd == null || pwd.isEmpty()) {
                throw new RetryLogin("Please enter a " + CredentialsSupplier.PASSWORD);
            }
            
            final Config config = configFactory.getConfig(ConfigService.APP_PROTECTED);
            final URL url = new UrlBuilderImpl(config).build(Rest.ENDPOINT_COMBO).get(0);
            LOG.log(Level.INFO, "Login URL: {0}", url);

            final Map<String, String> params = new HashMap<>();
            params.put(CredentialsSupplier.USERNAME, usr);
            params.put(CredentialsSupplier.PASSWORD, pwd);
            
            final Devicedetails dd = ddSupplier.get();
            
            LOG.log(Level.FINE, "Own device details: {0}", dd);

            this.preLogin(url, params, dd);
            
            syncData.run(url, params, dd, (response) -> {
                
                user = null;
                
                if(response.isError()) {
                
                    LOG.log(Level.WARNING, "Error: {0}", response);
                    
                }else{
                
                    final Object bodyObj = response.getBody();
                    
                    if(bodyObj instanceof Map) {
                    
                        final Map userMap = (Map)((Map)bodyObj).getOrDefault(Rest.RESULTNAME_USER, null);

                        user = userMap == null ? null : mapper.toObject(userMap, User.class);
                        
                    }else{
                    
                        LOG.log(Level.WARNING, "Unepected: {}", response);
                    }
                }
            });
        }catch(MalformedURLException e) {
            LOG.log(Level.WARNING, null, e);
            throw new LoginException("Login Server details not properly configured");
        }catch(IOException e) {
            LOG.log(Level.WARNING, null, e);
            throw new LoginException("Failed to access Login Server");
        }catch(ParseException e) {
            LOG.log(Level.WARNING, null, e);
            throw new LoginException("Error reading response from Login Server");
        }
        
        return user;
    }
}
