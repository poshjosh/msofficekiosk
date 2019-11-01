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
package com.looseboxes.msofficekiosk.functions.ui;

import com.bc.ui.UIContext;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.ui.GetAwtFontFromConfig;
import java.awt.Font;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * @author Chinomso Bassey Ikwuagwu on November 1, 2019 01:09:00 AM
 */
public class PromptUserSelect<T> implements Function<List<T>, Optional<T>>{
    
    private static final Logger LOG = Logger.getLogger(PromptUserSelect.class.getName());

    private final UIContext uiContext;
            
    private final ConfigFactory configFactory;
    
    private final Function<T, Object> toSelectionLabel;

    public PromptUserSelect(UIContext uiContext, 
            ConfigFactory configFactory, 
            Function<T, Object> toSelectionLabel) {
        this.uiContext = Objects.requireNonNull(uiContext);
        this.configFactory = Objects.requireNonNull(configFactory);
        this.toSelectionLabel = Objects.requireNonNull(toSelectionLabel);
    }
    
    @Override
    public Optional<T> apply(List<T> tt) {
        
        if(tt.isEmpty()) {
            return Optional.empty();
        }
        
        final Object [] arr = tt.stream().map(toSelectionLabel).toArray();
        
        final JList jList = new JList(arr);
        final Font font = new GetAwtFontFromConfig().apply(configFactory.getConfig(ConfigService.APP_UI));
        jList.setFont(font);
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final JScrollPane pane = new JScrollPane(jList);

        final ImageIcon icon = uiContext.getImageIconOptional().orElse(null);
        JOptionPane.showMessageDialog(null, pane, "Select Test to Edit", JOptionPane.PLAIN_MESSAGE, icon);

        final List selections =  jList.getSelectedValuesList();
        
        T selected = null;
        for(Object o : selections) {
            for(T t : tt) {
                if(toSelectionLabel.apply(t).equals(o)) {
                    selected = t;
                    break;
                }
            }
        }
        
        return Optional.ofNullable(selected);
    }
}
