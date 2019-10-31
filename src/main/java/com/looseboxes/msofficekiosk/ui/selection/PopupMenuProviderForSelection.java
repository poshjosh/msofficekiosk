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
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 8, 2019 2:31:13 AM
 */
public class PopupMenuProviderForSelection<SELECTION_UI extends Component, SELECTED>{

    private transient static final Logger LOG = Logger.getLogger(PopupMenuProviderForSelection.class.getName());

    private final AppUiContext uiContext;
    
    private final Display display;
    
    private final UiSelectionManager<SELECTION_UI, SELECTED> selectionManager;
    
    private final Precondition<SELECTED> precondition;
    
    private final List<SelectionAction<SELECTED>> actions;

    public PopupMenuProviderForSelection(
            AppUiContext uiContext, 
            Display display,
            UiSelectionManager<SELECTION_UI, SELECTED> selectionManager,
            Precondition<SELECTED> precondition,
            List<SelectionAction<SELECTED>> actions) {
        this.uiContext = Objects.requireNonNull(uiContext);
        this.display = Objects.requireNonNull(display);
        this.selectionManager = Objects.requireNonNull(selectionManager);
        this.precondition = Objects.requireNonNull(precondition);
        this.actions = Objects.requireNonNull(actions);
    }
    
    public Menu getPopupMenu(SELECTION_UI selectionSource) {
        
        LOG.finer(() -> "Creating popup menu for selection source: " + selectionSource);
        
        final Shell shell = new Shell(display);
        final Menu menu = new Menu(shell, SWT.POP_UP);
        final Font font = uiContext.getSwtFont(shell.getDisplay());
        shell.setFont(font);
        
        for(SelectionAction<SELECTED> action : actions) {
            
            LOG.log(Level.FINER, "Adding MenuItem for: {0}", action);

            final ActionExecutionThread thread = new ActionExecutionThread(
                    uiContext, selectionManager, precondition, action, selectionSource);
            this.addMenuItem(menu, action.getName(), new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent se) {
                    try{
                        thread.run();
                    }catch(RuntimeException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        
        return menu;
    }
    
    public final MenuItem addMenuItem(Menu menu, String text, SelectionListener listener) {
        final MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
        menuItem.setText(text);
//        menuItem.setFont(font);
        menuItem.addSelectionListener(listener);
        return menuItem;
    }
}
