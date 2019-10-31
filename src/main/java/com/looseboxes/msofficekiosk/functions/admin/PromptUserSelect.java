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

package com.looseboxes.msofficekiosk.functions.admin;

import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.ui.selection.UiSelectionManager;
import java.awt.Component;
import java.awt.Font;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2019 10:02:51 AM
 */
public class PromptUserSelect<SELECTION_UI extends Component, SELECTED> implements Supplier<List<SELECTED>>{

    private final AppUiContext uiContext;
    
    private final UiSelectionManager<SELECTION_UI, SELECTED> uiSelectionManager;

    public PromptUserSelect(AppUiContext uiContext, 
            UiSelectionManager<SELECTION_UI, SELECTED> uiSelectionManager) {
        this.uiContext = Objects.requireNonNull(uiContext);
        this.uiSelectionManager = Objects.requireNonNull(uiSelectionManager);
    }
    
    @Override
    public List<SELECTED> get() {
        
        final Font font = uiContext.getAwtFont();
        
        final SELECTION_UI component = this.uiSelectionManager.getSelectionUi();
        component.setFont(font);
        
        final JScrollPane pane = new JScrollPane(component);
        pane.setFont(font);
        
        final ImageIcon icon = uiContext.getImageIconOptional().orElse(null);
        
        final int option = JOptionPane.showConfirmDialog(
                null, pane, "Select Connected Device", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, icon);
        
        if(option == JOptionPane.OK_OPTION) {
            return this.uiSelectionManager.getSelectedValues(component);
        }else{
            return Collections.EMPTY_LIST;
        }
    }
}

