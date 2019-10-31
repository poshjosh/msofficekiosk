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

package com.looseboxes.msofficekiosk.examples;

import static java.lang.System.out;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Chinomso Bassey Ikwuagwu on May 2, 2019 5:52:34 PM
 */
public class DateFormat {
    public static void main(String... args) {
        try{
            final Collection<String> c = Arrays.asList(
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSSX", "EEE MMM dd kk:mm:ss z yyyy");    
            final SimpleDateFormat df = new SimpleDateFormat();
            for(String a : c){
                out.println("Pattern: " + a);
                df.applyPattern(a);
                try{
                    df.parse("2019-05-02T17:00:00.000+0000");
                }catch(java.text.ParseException e) {
                    out.println(e.toString());
                }
            }
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
