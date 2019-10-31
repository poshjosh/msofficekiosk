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

package com.looseboxes.msofficekiosk;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Chinomso Bassey Ikwuagwu on May 14, 2019 3:28:42 PM
 */
public class UrlTest {
    
    public static void main(String[] args) {
    
        new UrlTest().test();
    }

    public void test() {
        try{
            final Object m = "http://192.168.43.142:8080/downloadFile/2019\\05\\2018 - Brief for House of Rep Ctee on  Defence.docx";
            final URL base = new URL("http://192.168.43.142:8080");
            System.out.println("Base: " + base);
            final String a = URLEncoder.encode("/downloadFile", "UTF-8");
            final String b = URLEncoder.encode("2019\\05\\2018 - Brief for House of Rep Ctee on  Defence.docx".replace('\\', '/'), "UTF-8");
            final URI uri = URI.create(a).resolve(b);
            System.out.println("URI: " + uri);
            System.out.println("URI.toASCIIString(): " + uri.toASCIIString());
//            System.out.println("URI.toURL(): " + uri.toURL());
            System.out.println("URL: " + new URL(base, uri.toASCIIString()));
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
