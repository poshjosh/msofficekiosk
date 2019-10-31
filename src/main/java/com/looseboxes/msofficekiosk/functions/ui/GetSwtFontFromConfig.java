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

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.bc.config.Config;
import java.util.Properties;

/**
 * @author Chinomso Bassey Ikwuagwu on May 14, 2018 2:54:07 PM
 */
public class GetSwtFontFromConfig implements BiFunction<Display, Config<Properties>, Font> {

    private final Function<String, Integer> getStyleFromName;

    public GetSwtFontFromConfig() {
        this(new GetFontStyleFromName());
    }
    
    public GetSwtFontFromConfig(Function<String, Integer> getStyleFromName) {
        this.getStyleFromName = Objects.requireNonNull(getStyleFromName);
    }

    @Override
    public Font apply(Display display, Config<Properties> config) {
        final String name = config.getString(ConfigNames.FONT_NAME, "ARIAL");
        final Integer height = config.getInt(ConfigNames.FONT_HEIGHT, 18);
        final Integer style = getStyleFromName.apply(config.getString(ConfigNames.FONT_STYLE, "NORMAL"));
        final Font font = new Font(display, new FontData(name, height, style));
        return font;
    }
}
