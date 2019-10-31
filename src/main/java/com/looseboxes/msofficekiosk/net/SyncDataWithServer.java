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

import com.bc.socket.io.messaging.data.Devicedetails;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 27, 2019 1:10:40 PM
 */
public interface SyncDataWithServer {
    
    void run(URL url, Map params, final Devicedetails dd, Consumer<Response<Object>> consumer)
            throws IOException, ParseException;
}
