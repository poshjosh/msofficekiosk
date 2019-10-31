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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 23, 2019 5:36:46 PM
 */
public class GetHttpURLConnection {
    
    private final String method;

    public GetHttpURLConnection(String method) {
        this.method = Objects.requireNonNull(method);
    }

    public HttpURLConnection apply(URL url, boolean doOutput) 
            throws MalformedURLException, ProtocolException, IOException {
        
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(doOutput);
        conn.setConnectTimeout(15_000);
        conn.setReadTimeout(45_000);
        conn.setInstanceFollowRedirects(true);
        if(method != null) {
            conn.setRequestMethod(method);
        }
        return conn;
    }
}
