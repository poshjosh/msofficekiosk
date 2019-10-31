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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 10, 2019 10:09:58 AM
 */
public abstract class AbstractUiSelectionManager<SELECTION_UI extends Component, SELECTED> 
        implements UiSelectionManager<SELECTION_UI, SELECTED>{

    private static final Logger LOG = Logger.getLogger(AbstractUiSelectionManager.class.getName());

    private final AppUiContext uiContext;
    
    private final Display display;
    
    private final Precondition<SELECTED> precondition;
    
    private final List<SelectionAction<SELECTED>> actions;
    
    public AbstractUiSelectionManager(AppUiContext uiContext, Display display) {
    
        this(uiContext, display, (selected) -> true, Collections.EMPTY_LIST);
    }
    
    public AbstractUiSelectionManager(AppUiContext uiContext, Display display,
            Precondition<SELECTED> precondition, List<SelectionAction<SELECTED>> actions) {
        this.uiContext = Objects.requireNonNull(uiContext);
        this.display = Objects.requireNonNull(display);
        this.precondition = Objects.requireNonNull(precondition);
        this.actions = Objects.requireNonNull(actions);
    }

    protected abstract SELECTION_UI createSelectionUi();
    
    @Override
    public Optional<Menu> getPopupMenuSwt(SELECTION_UI ui) {
        final Menu menu;
        if(actions == null || actions.isEmpty()) {
            menu = null;    
        }else{
            menu = new PopupMenuProviderForSelection<>(uiContext, display, this, precondition, actions).getPopupMenu(ui);
        }
        return Optional.ofNullable(menu);
    }

    @Override
    public Optional<JPopupMenu> getPopupMenuSwing(SELECTION_UI ui) {
        final JPopupMenu menu;
        if(actions == null || actions.isEmpty()) {
            menu = null;    
        }else{
            menu = new PopupMenuSwingProviderForSelection<>(uiContext, this, precondition, actions).getPopupMenu(ui);
        }
        return Optional.ofNullable(menu);
    }

    @Override
    public SELECTION_UI getSelectionUi() {
        
        final SELECTION_UI ui = createSelectionUi();
        
        ui.setFont(uiContext.getAwtFont());
        
        addRightClickMenu(ui);
        
        return ui;
    }

    public void addRightClickMenu(final SELECTION_UI selectionSource) {
        
        final UiSelectionManager<SELECTION_UI, SELECTED> uiSelectionManager = this;

        final MouseListener mouseListener = new MouseAdapter( ){
            @Override
            public void mouseClicked (MouseEvent event) {

                if(event.getClickCount() == 2) {
                    
                    final SelectionAction<SELECTED> defaultAction = actions.isEmpty() ? null: actions.get(0);

                    final SELECTION_UI source = (SELECTION_UI)event.getSource();

                    if(defaultAction != null && isValidForDoubleClick(source)) {

                        final Thread thread = new ActionExecutionThread(
                                uiContext, uiSelectionManager, precondition, defaultAction, selectionSource);

                        thread.start();
                    }
                }else if(SwingUtilities.isRightMouseButton(event)) {

                    new Thread("UiSelectionManager_PopupMenuDisplay_Thread"){
                        @Override
                        public void run() {

                            Display.getDefault().asyncExec(() -> {
                                try{
                                    
                                    final Optional<Menu> menuOptional = getPopupMenuSwt(selectionSource);

                                    if( ! menuOptional.isPresent()) {
                                        return;
                                    }

                                    final Menu menu = menuOptional.get();

                                    final int x = event.getXOnScreen();
                                    final int y = event.getYOnScreen();

                                    final Shell shell = menu.getShell();
                                    final Display display = shell.getDisplay();

                                    menu.setLocation(x, y);
                                    menu.setVisible(true);

                                    while ( ! shell.isDisposed()) {
                                        if ( ! display.readAndDispatch()) {
                                            display.sleep();
                                        }
                                    }
                                }catch(RuntimeException e) {
                                    LOG.log(Level.WARNING, null, e);
                                }
                            });
                        }
                    }.start();

                    if(LOG.isLoggable(Level.FINER)) {
                        LOG.log(Level.FINER, "Showing menu at[{0}:{1}]", 
                                new Object[]{event.getX(), event.getY()});
                    }
                }    
            }
        };

        selectionSource.addMouseListener(mouseListener);
    }
    
    public boolean isValidForDoubleClick(SELECTION_UI source) {

        final List<SELECTED> selectedValues = getSelectedValues(source);
        
        return selectedValues.size() == 1;
    }

    public boolean isValidForDoubleClickWithUserPrompt(String actionName, SELECTION_UI source) {

        final List<SELECTED> selectedValues = getSelectedValues(source);
        
        final boolean output;
        
        if(selectedValues.size() > 1) {

            final String msg = "Are you sure you want to '" + 
                            actionName + "' " + selectedValues.size() + " files?";

            final int selection = JOptionPane.showConfirmDialog(null, msg, "Proceed?", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            output = selection == JOptionPane.YES_OPTION;
            
        }else{
            
            output = selectedValues.size() == 1;
        }
        
        return output;
    }

    public AppUiContext getUiContext() {
        return uiContext;
    }

    public Precondition<SELECTED> getPrecondition() {
        return precondition;
    }

    public List<SelectionAction<SELECTED>> getActions() {
        return actions;
    }
}
