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

package com.looseboxes.msofficekiosk.ui;

import com.bc.config.Config;
import com.bc.ui.UIContext;
import com.looseboxes.msofficekiosk.functions.ui.GetAwtFontFromConfig;
import com.looseboxes.msofficekiosk.functions.ui.GetSwtFontFromConfig;
import java.util.Properties;
import org.eclipse.swt.widgets.Display;

/**
 * @author Chinomso Bassey Ikwuagwu on May 27, 2018 7:59:40 PM
 */
public interface AppUiContext extends UIContext {
    
    default MessageDialog getMessageDialog() {
        return getUi().getMessageDialog();
    }
    
    UI getUi();

    default java.awt.Font getAwtFont() {
        return new GetAwtFontFromConfig().apply(this.getConfig());
    }
    
    default org.eclipse.swt.graphics.Font getSwtFont(Display display) {
        return new GetSwtFontFromConfig().apply(display, this.getConfig());
    }
    
    Config<Properties> getConfig();
}
