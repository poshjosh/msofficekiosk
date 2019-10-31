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

import com.looseboxes.msofficekiosk.ui.AppUiContext;
import java.util.Collection;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2019 9:20:50 PM
 */
public interface TestDocSenderFactory {
    
    String SEND_TO_SERVER = "Send to Server";
//    String SEND_TO_SOCKET = "Send to Socket";
    String SEND_TO_EMAIL = "Send to Email";

    SendTestDocs get(String id);

    Collection<String> getSupported();

    boolean isSupported(String id);

    AppUiContext getUiContext();
}
