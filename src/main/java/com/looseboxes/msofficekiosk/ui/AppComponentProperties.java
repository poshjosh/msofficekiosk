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

package com.looseboxes.msofficekiosk.ui;

import com.bc.ui.builder.model.impl.ComponentPropertiesImpl;
import java.awt.Component;
import java.awt.Font;
import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.JTextArea;

/**
 * @author Chinomso Bassey Ikwuagwu on Jan 24, 2019 10:45:34 PM
 */
public class AppComponentProperties extends ComponentPropertiesImpl 
        implements Serializable {

    private transient static final Logger LOG = Logger.getLogger(AppComponentProperties.class.getName());

    private final java.awt.Font font;
    
    public AppComponentProperties(java.awt.Font font) { 
        this.font = Objects.requireNonNull(font);
    }

    @Override
    public Font getFont(Component component) {
        final Font result = this.font;
        this.log(component, "Font", result);
        return result;
    }
    
    @Override
    public int getHeight(Component component) {
        final int factor = 2;
        final int n = (this.getFontSize(component) + 2);
        final int result;
        if(component instanceof JTextArea || component instanceof JTable) {
            result = n * factor * 3;
        }else{
            result = n * factor; 
        }
        this.log(component, "Height", result);
        return result;
    }
    
    @Override
    public int getWidth(Component component) {
        final int result = -1;
        this.log(component, "Width", result);
        return result;
    }
    
    @Override
    public int getFontSize(Component component) {
        final int result = this.getFont(component).getSize();
        this.log(component, "Font-size", result);
        return result;
    }
    
    private void log(Component component, String name, Object value) {
        LOG.finer(() -> name + ": " + value +
                ", type: " + component.getClass().getSimpleName() + 
                ", name: " + component.getName());
    }
}
