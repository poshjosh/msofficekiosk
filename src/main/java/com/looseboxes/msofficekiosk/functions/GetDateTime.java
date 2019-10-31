/*
 * Copyright 2018 NUROX Ltd.
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

package com.looseboxes.msofficekiosk.functions;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import com.bc.config.Config;
import java.util.Properties;

/**
 * @author Chinomso Bassey Ikwuagwu on May 22, 2018 1:16:08 PM
 */
public class GetDateTime implements BiFunction<Long, ZoneId, ZonedDateTime>{

    public ZonedDateTime apply(Long time, Config<Properties> config) {
        return this.apply(time, new GetZoneIdFromConfig().apply(config));
    }
    
    @Override
    public ZonedDateTime apply(Long time, ZoneId zoneId) {
        final ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), zoneId);
        return zdt;
    }
}
