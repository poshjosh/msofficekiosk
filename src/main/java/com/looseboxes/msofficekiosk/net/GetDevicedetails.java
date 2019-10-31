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

package com.looseboxes.msofficekiosk.net;

import com.bc.config.Config;
import com.bc.socket.io.messaging.data.Designations;
import com.bc.socket.io.functions.GetLocalIpAddress;
import com.bc.socket.io.messaging.data.Devicedetails;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import java.util.function.Function;
import com.looseboxes.msofficekiosk.security.LoginManager;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 2, 2019 11:11:41 AM
 */
public class GetDevicedetails implements Function<Config, Devicedetails>{

    private final LoginManager loginManager;

    public GetDevicedetails(LoginManager loginManager) {
        this.loginManager = loginManager;
    }
    
    @Override
    public Devicedetails apply(Config config) {
        final String ownIp = new GetLocalIpAddress().apply(null);
        final String serverIp = getServerIp(config);
        final Devicedetails dd = new Devicedetails();
//        dd.setDescription("");
        dd.setDesignation(serverIp.equals(ownIp) ? Designations.SERVER : Designations.CLIENT);
        dd.setIpaddress(new GetLocalIpAddress().apply(null));
        dd.setName(System.getProperty("user.name"));
        dd.setPort(getPort(config));
        final String name = loginManager == null ? 
                LoginManager.USERNAME_IF_NONE : 
                loginManager.getLoggedInUserNameOrDefault();
        dd.setUsername(name);
        return dd;
    }
    
    public String getServerIp(Config config) {
        final String output = config.getString(ConfigNames.IP_ADDRESSES_TO_SEND_DOCUMENTS, null);
        if(output == null) {
            throw new InvalidConfigurationException("Server ip not configured");
        }
        return output;
    }
    
    public int getPort(Config config) {
        final int portNumber = config.getInt(ConfigNames.SERVER_PORT_NUMBER, -1);
        if(portNumber == -1) {
            throw new InvalidConfigurationException("Port number for server not configured");
        }
        return portNumber;
    }
}
