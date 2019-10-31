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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on May 20, 2019 4:25:52 PM
 */
public interface UrlBuilder {

    List<URL> build(String... pathSegments) throws MalformedURLException;

    List<URL> build(Map queryParams, boolean encode, String... pathSegments) throws MalformedURLException;

    List<URL> build(String[] ipAddresses, Map queryParams, boolean encode, String... pathSegments) throws MalformedURLException;

    URL build(String ipAddress, Map queryParams, boolean encode, String... pathSegments) throws MalformedURLException;

    URL buildFromIp(String ipAddress, String... pathSegments) throws MalformedURLException;

    List<URL> buildFromIps(String[] ipAddresses, String... pathSegments) throws MalformedURLException;

    List<URL> getServerBaseUrls();

}
