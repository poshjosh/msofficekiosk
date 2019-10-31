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

package com.looseboxes.msofficekiosk.messaging;

import com.bc.elmi.pu.entities.Message;
import com.bc.socket.io.messaging.data.Devicedetails;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2019 12:49:50 PM
 */
public interface MessageBuilder<T> {

    default Message buildMessage(String subject, T body){
        return buildMessage(subject, body, null);
    }
    
    Message buildMessage(String subject, T body, Devicedetails dd);
}
