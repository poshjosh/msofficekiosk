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

package com.looseboxes.msofficekiosk.functions;

import com.bc.config.Config;
import com.bc.socket.io.functions.FindInetAddresses;
import com.bc.socket.io.functions.GetIpAddressFromInetAddress;
import com.bc.socket.io.functions.IsAddressReachable;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.net.InvalidConfigurationException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 22, 2018 12:36:36 PM
 */
public class GetServerHostsFromConfig implements Function<Config<Properties>, String[]>, Serializable {
    
    private final int timeout;

    public GetServerHostsFromConfig() {
        this(126_000);
    }
    
    public GetServerHostsFromConfig(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String[] apply(Config<Properties> config) {
        
        final String val = config.getString(ConfigNames.IP_ADDRESSES_TO_SEND_DOCUMENTS, null);
        
        final int timeoutPerAddress = timeout / 254 < 100 ? 100 : timeout / 254;
            
        final Predicate<InetAddress> test = new IsAddressReachable(timeoutPerAddress);

        final Function<InetAddress, String> format = new GetIpAddressFromInetAddress();

        final String [] ipAddresses;

        if(val == null || val.isEmpty()) {
            ipAddresses = null;
        }else if("*.*".equals(val) || "**".equals(val)) {
            
            final List<String> output = new ArrayList(255);
            
            final Consumer<InetAddress> consumer = (ia) -> output.add(format.apply(ia));
            
            final FindInetAddresses search = new FindInetAddresses(test, consumer);
            
            search.run();
            
            ipAddresses = output.stream().collect(Collectors.toList()).toArray(new String[0]);
            
        }else{
            final String [] ips = config.getArray(ConfigNames.IP_ADDRESSES_TO_SEND_DOCUMENTS, (String[])null);
            final List<String> addresses = new ArrayList<>();
            if(ips != null && ips.length > 0) {
                for(int i=0; i<ips.length; i++) {
                    try{
                        InetAddress.getByName(ips[i]);
                        addresses.add(ips[i]);
                    }catch(Exception e) {
                        final String msg = "Server hosts not properly configured";
                        throw new InvalidConfigurationException(msg, e);
                    }
                }
            }
            ipAddresses = addresses.toArray(new String[0]);
        }
        
        if(ipAddresses == null || ipAddresses.length == 0) {
            final String msg = "Server hosts not configured";
            throw new InvalidConfigurationException(msg);
        }
        
        return ipAddresses;
    }
}
