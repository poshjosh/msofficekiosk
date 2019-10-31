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

package com.looseboxes.msofficekiosk.ui.selection;

import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.validators.Precondition;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * @author Chinomso Bassey Ikwuagwu on May 8, 2019 2:31:13 AM
 */
public class PopupMenuSwingProviderForSelection<SELECTION_UI extends Component, SELECTED>{

    private transient static final Logger LOG = Logger.getLogger(PopupMenuSwingProviderForSelection.class.getName());

    private final AppUiContext uiContext;
    
    private final UiSelectionManager<SELECTION_UI, SELECTED> selectionManager;
    
    private final Precondition<SELECTED> precondition;
    
    private final List<SelectionAction<SELECTED>> actions;

    public PopupMenuSwingProviderForSelection(
            AppUiContext uiContext, 
            UiSelectionManager<SELECTION_UI, SELECTED> selectionManager,
            Precondition<SELECTED> precondition,
            List<SelectionAction<SELECTED>> actions) {
        this.uiContext = Objects.requireNonNull(uiContext);
        this.selectionManager = Objects.requireNonNull(selectionManager);
        this.precondition = Objects.requireNonNull(precondition);
        this.actions = Objects.requireNonNull(actions);
    }
    
    public JPopupMenu getPopupMenu(SELECTION_UI selectionSource) {
        
        LOG.finer(() -> "Creating popup menu for selection source: " + selectionSource);
                
        final JPopupMenu menu = new JPopupMenu();
        menu.setName(this.getClass().getName());
        final java.awt.Font font = uiContext.getAwtFont();
        menu.setFont(font);
        
        for(SelectionAction<SELECTED> action : actions) {
            
            LOG.log(Level.FINER, "Adding MenuItem for: {0}", action);

            final ActionExecutionThread thread = new ActionExecutionThread(
                    uiContext, selectionManager, precondition, action, selectionSource);
            
            this.addMenuItem(menu, action.getName(), font, thread);
        }
        
        return menu;
    }
    
    public JMenuItem addMenuItem(JPopupMenu menu, String text, 
            java.awt.Font font, ActionListener listener) {
        final JMenuItem item = new JMenuItem(text);
        item.setFont(font);
        item.addActionListener(listener);
        menu.add(item);
        return item;
    }
}
