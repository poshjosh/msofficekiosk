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

import java.util.function.Consumer;

/**
 * @author Chinomso Bassey Ikwuagwu on May 11, 2019 9:45:21 AM
 */
public interface ResponseHandler<T> extends Consumer<Response<T>> {

    @Override
    void accept(Response<T> r);

    void acceptAdded();

    ResponseHandler add(Response<T> r);
}
