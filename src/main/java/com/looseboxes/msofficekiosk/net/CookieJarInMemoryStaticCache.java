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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 25, 2019 12:37:14 PM
 */
public class CookieJarInMemoryStaticCache implements CookieJar {

    private static final Logger LOG = Logger.getLogger(CookieJarInMemoryStaticCache.class.getName());

    private static final Map<String, Set<Cookie>> cache = new HashMap<>();
    
    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        
        if(cookies == null || cookies.isEmpty()) {
            return;
        }
        
        final String key = getKey(url);
        
        Set<Cookie> set = cache.getOrDefault(key, null);
        
        if(set == null) {
            set = new HashSet<>();
            cache.put(key, set);
        }

        LOG.log(Level.FINER, "Adding cookies:: {0}", 
                cookies.stream().map((cookie) -> cookie.toString()).collect(Collectors.joining("\n")));
        
        set.addAll(cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {

        final String key = getKey(url);
        
        final Set<Cookie> set = cache.getOrDefault(key, null);
        
        final List<Cookie> cookies = set == null || set.isEmpty() ? Collections.EMPTY_LIST :
                new ArrayList(set);
                
        LOG.log(Level.FINER, "Returning cookies:: {0}", 
                cookies.stream().map((cookie) -> cookie.toString()).collect(Collectors.joining("\n")));
        
        return cookies;
    }
    
    public String getKey(HttpUrl url) {
        return url.host();
    }
}
