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

package com.looseboxes.msofficekiosk.ui.selection;

import com.bc.socket.io.messaging.data.Devicedetails;
import java.io.File;

/**
 * @author Chinomso Bassey Ikwuagwu on May 20, 2019 10:06:59 AM
 */
public class HasDisplayValueImpl implements HasDisplayValue{

    @Override
    public String getDisplayValue(Object value, int maxLen) {
        final String v;
        if(value == null) {
            v = null;
        }else if(value instanceof File) {
            v = ((File)value).getName();
        }else if(value instanceof Devicedetails) {
            final Devicedetails dd = ((Devicedetails)value);
            v = dd.getUsername() + "@" + dd.getIpaddress();
        }else{
            v = value.toString();
        }
        return v == null ? null : v.length() <= maxLen ? v : v.substring(0, maxLen);
    }
}
