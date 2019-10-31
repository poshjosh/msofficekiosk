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

package com.looseboxes.msofficekiosk.mapper;

import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 28, 2019 1:32:06 AM
 */
public interface Mapper {

    Map toMap(Object object);
    
    String toJsonString(Object object);

    <T> T toObject(Map map, Class<T> type);

    <T> T toObject(String json, Class<T> type) throws java.text.ParseException;

    Map toMap(String json) throws java.text.ParseException;
}
