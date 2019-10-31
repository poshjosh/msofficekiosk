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

import com.looseboxes.msofficekiosk.config.ConfigNames;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.function.Function;
import com.bc.config.Config;
import java.util.Properties;

/**
 * @author Chinomso Bassey Ikwuagwu on May 22, 2018 1:23:16 PM
 */
public class GetZoneIdFromConfig implements Function<Config<Properties>, ZoneId> {

    @Override
    public ZoneId apply(Config<Properties> config) {
        final String zoneOffset = config.getString(ConfigNames.ZONE_OFFSET, ZoneOffset.systemDefault().toString());
        final ZoneId zoneId = ZoneId.of(zoneOffset);
        return zoneId;
    }
}
