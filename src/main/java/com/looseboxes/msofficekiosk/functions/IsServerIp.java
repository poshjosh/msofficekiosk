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

import com.bc.config.Config;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.net.InvalidConfigurationException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on May 21, 2019 11:20:15 AM
 */
public class IsServerIp implements Predicate<String> {
    
    private final Config config;

    public IsServerIp(Config config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public boolean test(String host) {
        final String [] hosts = getServerHosts(config);
        return Arrays.asList(hosts).contains(host);
    }

    public String [] getServerHosts(Config config) {
        final String [] arr = config.getArray(ConfigNames.IP_ADDRESSES_TO_SEND_DOCUMENTS);
        if(arr == null || arr.length == 0) {
            throw new InvalidConfigurationException("Server hosts not configured");
        }
        return arr;
    }
}
