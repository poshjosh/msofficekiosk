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
import com.looseboxes.msofficekiosk.config.ConfigNames;
import java.util.concurrent.TimeUnit;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 29, 2019 12:26:31 PM
 */
public class OkHttpClientProvider {
    public OkHttpClient apply(Config config, CookieJar cookieJar) {
        return new OkHttpClient.Builder()
                .connectTimeout(config.getInt(ConfigNames.HTTP_CONNECT_TIMEOUT), TimeUnit.MILLISECONDS)
                .readTimeout(config.getInt(ConfigNames.HTTP_READ_TIMEOUT), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true) 
                .cookieJar(cookieJar).build();    
    }
}
