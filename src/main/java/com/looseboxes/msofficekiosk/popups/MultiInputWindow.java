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

package com.looseboxes.msofficekiosk.popups;

import com.bc.config.Config;
import com.bc.selection.Selection;
import com.bc.selection.SelectionValues;
import com.bc.typeprovider.MemberValueTypeProvider;
import com.bc.util.JsonFormat;
import com.bc.ui.builder.functions.BuildMapFromUi;
import com.bc.ui.builder.functions.BuildUiFromMap;
import com.bc.ui.builder.model.impl.ComponentModelImpl;
import com.looseboxes.msofficekiosk.functions.ui.GetAwtFontFromConfig;
import com.looseboxes.msofficekiosk.ui.AppComponentProperties;
import java.awt.Container;
import java.awt.Font;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.awt.Dimension;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * @author Chinomso Bassey Ikwuagwu on May 22, 2018 9:31:04 PM
 */
public class MultiInputWindow implements Serializable, BiFunction<Map, String, Map>{

    private transient static final Logger LOG = Logger.getLogger(MultiInputWindow.class.getName());
    
    private final Font font;
    
    private final boolean displayPassword;

    public MultiInputWindow(Config<Properties> config, boolean displayPassword) { 
        this(new GetAwtFontFromConfig().apply(config), displayPassword);
    }

    public MultiInputWindow(Font font, boolean displayPassword) { 
        this.font = Objects.requireNonNull(font);
        this.displayPassword = displayPassword;
    }

    @Override
    public Map apply(Map inputMap, String title) {
        
        LOG.fine(() -> " Input keys:\n" + inputMap.keySet().toString());
        LOG.finer(() -> " Input:\n" + new JsonFormat(true, true, "  ").toJSONString(inputMap));

        final double d = title == null ? 0 : title.length() * 2.2;
        final int width = d > 150 ? (int)d : 150;
        final Dimension defaultEntrySize = new Dimension(width, 25);

        final Container ui = new BuildUiFromMap(
                new MemberValueTypeProvider(), 
                new ComponentModelImpl(
                        SelectionValues.from(Collections.EMPTY_LIST), 
                        new AppComponentProperties(font), 
                        100
                ){
                    @Override
                    public List<Selection> getSelectionValues(Class parentType, Class valueType, String name, Object value) {
                        return MultiInputWindow.this.getSelectionValues(parentType, valueType, name, value);
                    }
                    @Override
                    public boolean isPasswordName(String name) {
                        return displayPassword ? false : super.isPasswordName(name);
                    }
                },
                defaultEntrySize
        ).apply(inputMap, font);
        
        LOG.fine(() -> "Size: " + ui.getSize() + ", pref: " + ui.getPreferredSize() + ", UI: " + ui);
        
        show(ui, title);

        final Map outputMap = new BuildMapFromUi().apply(ui, inputMap);

        LOG.fine(() -> "Output keys:\n" + outputMap.keySet().toString());
        LOG.fine(() -> "Output:\n" + new JsonFormat(true, true, "  ").toJSONString(outputMap));

        return outputMap;
    }
    
    public List<Selection> getSelectionValues(Class parentType, Class valueType, String name, Object value) {
        return Collections.EMPTY_LIST;
    }

    public void show(Container message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
//        new MessageUi().waitTillDone();
    }
}
