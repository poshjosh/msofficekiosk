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

package com.looseboxes.msofficekiosk.functions.ui;

import com.looseboxes.msofficekiosk.config.ConfigNames;
import java.awt.Font;
import java.util.function.Function;
import com.bc.config.Config;
import java.util.Properties;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2018 3:10:40 PM
 */
public class GetAwtFontFromConfig implements Function<Config<Properties>, Font> {

    public GetAwtFontFromConfig() { 
    }

    @Override
    public Font apply(Config<Properties> config) {
        final String name = config.getString(ConfigNames.FONT_NAME, "ARIAL");
        final Integer height = config.getInt(ConfigNames.FONT_HEIGHT, 18);
        final String sval = config.getString(ConfigNames.FONT_STYLE, "PLAIN");
        final String style = sval.replace("NORMAL", "PLAIN");
        final Font font = Font.decode(name+'-'+style+'-'+height);
        return font;
    }
}
