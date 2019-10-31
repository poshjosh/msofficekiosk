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

import java.io.Closeable;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

/**
 * @author Chinomso Bassey Ikwuagwu on May 11, 2019 4:52:22 PM
 */
public interface Cache extends Closeable{

    String putAsJson(String key, Object value) throws IOException;

    <T> T getFromJson(String key, Class<T> type, T outputIfNone) 
            throws IOException, ParseException;

    <T> List<T> getListFromJson(String key, Class<T> elementType, List<T> outputIfNone) 
            throws IOException, ParseException;

    <T> Collection<T> getCollectionFromJson(String key, Class<T> elementType, Collection<T> outputIfNone) 
            throws IOException, ParseException;

    void flush() throws IOException;

    boolean isClosed();

    @Override
    void close() throws IOException;

    /**
     * User should be sure there are no outstanding operations.
     * @throws IOException
     */
    void clear() throws IOException;

    boolean remove(String key) throws IOException;

    void delete() throws IOException;
    
    Object getDelegate();
}
