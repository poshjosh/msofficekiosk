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
import com.bc.socket.io.functions.GetLocalIpAddress;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.functions.GetServerHostsFromConfig;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.HttpUrl;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 23, 2019 7:51:05 PM
 */
public class UrlBuilderImpl implements UrlBuilder {

    private static final Logger LOG = Logger.getLogger(UrlBuilderImpl.class.getName());
    
    private final Config config;

    public UrlBuilderImpl(Config config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public List<URL> getServerBaseUrls() {
        try{
            return this.build(new String[0]);
        }catch(MalformedURLException e) {
            LOG.log(Level.WARNING, null, e);
            return Collections.EMPTY_LIST;
        }
    }
    
    @Override
    public List<URL> build(String... pathSegments) throws MalformedURLException {
    
        final String [] ipAddresses = new GetServerHostsFromConfig().apply(config);

        return this.build(ipAddresses, Collections.EMPTY_MAP, false, pathSegments);
    }

    @Override
    public List<URL> buildFromIps(String[] ipAddresses, String... pathSegments) 
            throws MalformedURLException {
    
        return this.build(ipAddresses, Collections.EMPTY_MAP, false, pathSegments);
    }

    @Override
    public List<URL> build(Map queryParams, boolean encode, String... pathSegments) 
            throws MalformedURLException {
    
        final String [] ipAddresses = new GetServerHostsFromConfig().apply(config);

        return this.build(ipAddresses, queryParams, encode, pathSegments);
    }
    
    @Override
    public List<URL> build(String[] ipAddresses, 
            Map queryParams, boolean encode, String... pathSegments) throws MalformedURLException {
    
        final List<URL> output = new ArrayList<>(ipAddresses.length);
        
        for(String ip : ipAddresses) {
        
            final URL u = this.build(ip, queryParams, encode, pathSegments);
            
            output.add(u);
        }
        
        return output;
    }

    @Override
    public URL buildFromIp(String ipAddress, String... pathSegments) throws MalformedURLException {
        return this.build(ipAddress, Collections.EMPTY_MAP, false, pathSegments);
    }
    
    @Override
    public URL build(String ipAddress, Map queryParams, boolean encode, String... pathSegments) 
            throws MalformedURLException {
    
        LOG.finer(() -> MessageFormat.format("Ipaddress: {0}, pathSegments: {1}, encode: {2}, queryParam: {3}",
                ipAddress, (pathSegments==null?null:Arrays.toString(pathSegments)), encode, queryParams));
        
        final String protocol = config.getString(ConfigNames.SERVER_PROTOCOL, null);
        if(protocol == null || protocol.isEmpty()) {
            throw new InvalidConfigurationException(ConfigNames.SERVER_PROTOCOL);
        }
            
        final int port = config.getInt(ConfigNames.SERVER_PORT_NUMBER, -1);
        if(port == -1) {
            throw new InvalidConfigurationException(ConfigNames.SERVER_PORT_NUMBER);
        }
    
        final String ownIp = new GetLocalIpAddress().apply(null);
        
        final String ip = ipAddress.equals(ownIp) ? "localhost" : ipAddress;
        
        final HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme(protocol)
                .host(ip)
                .port(port);
        
        if(pathSegments != null && pathSegments.length > 0) {
            
            for(String _v : pathSegments) {
                
                final String val = _v.replace('\\', '/');
                
                final String str = val.startsWith("/") ? val.substring(1) : val;
                
                final String [] segments = str.indexOf('/') == -1 ? new String[]{str} : str.split("/");
                
                for(String segment : segments) {
                    
                    LOG.finer(() -> "Adding pathSegment: " + segment + ", for: " + val);

                    builder.addPathSegment(segment);
                }
            }
        }
        
        for(Object key : queryParams.keySet()) {
        
            final Object val = queryParams.get(key);
            
            if(encode) {
                builder.addQueryParameter(key.toString(), val.toString());
            }else{
                builder.addEncodedQueryParameter(key.toString(), val.toString());
            }
        }
        
        final HttpUrl httpUrl = builder.build();
        
        LOG.log(Level.FINE, "URL: {0}", httpUrl);
        
        return httpUrl.url();
    }
}
