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

import com.bc.config.Config;
import com.looseboxes.msofficekiosk.mapper.Mapper;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigService;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import okhttp3.OkHttpClient;

/**
 * @author Chinomso Bassey Ikwuagwu on May 14, 2019 4:46:34 PM
 */
public class RequestClientProvider {
    
    private final ConfigFactory configFactory;
    private final OkHttpClient httpClient;

    public RequestClientProvider(ConfigFactory configFactory, OkHttpClient httpClient) {
        this.configFactory = Objects.requireNonNull(configFactory);
        this.httpClient = Objects.requireNonNull(httpClient);
    }

    public RequestClient<Response<Object>> forJson(Mapper mapper) {
        final JsonRequestClient output = new JsonRequestClientImpl(httpClient, mapper);
        output.headers(getDefaultHeaders("application/json"));
        return output;
    }

    public <T> RequestClient<T> get(String contentType, Function<Object, T> converter) {
        final RequestClient output = RequestClientProvider.this.get(converter);
        output.headers(getDefaultHeaders(contentType));
        return output;
    }

    public <T> RequestClient<T> get(Function<Object, T> converter) {
        return new OkHttpRequestClient(httpClient, converter);
    }
    
    public Map getDefaultHeaders(String contentType) {
        final Config config = configFactory.getConfig(ConfigService.APP_INTERNAL);
        return new DefaultHeaders(config, contentType);
    }
}
