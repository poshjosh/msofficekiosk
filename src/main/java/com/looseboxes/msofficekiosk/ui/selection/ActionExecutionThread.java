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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 8, 2019 8:38:10 PM
 */
public class ActionExecutionThread<SELECTION_UI, SELECTED> extends Thread
        implements ActionListener {

    private static final Logger LOG = Logger.getLogger(ActionExecutionThread.class.getName());
    
    private final AppUiContext uiContext;
    private final UiSelectionManager<SELECTION_UI, SELECTED> selectionManager;
    private final Precondition<SELECTED> preCondition;
    private final SelectionAction<SELECTED> action;
    private final SELECTION_UI selectionSource;
    
    public ActionExecutionThread(AppUiContext uiContext, 
            UiSelectionManager<SELECTION_UI, SELECTED> selectionManager, 
            Precondition<SELECTED> preCondition,     
            SelectionAction<SELECTED> action, SELECTION_UI selectionSource) {
        super(action.getName() + "ActionExecution_Thread");
        this.uiContext = Objects.requireNonNull(uiContext);
        this.selectionManager = Objects.requireNonNull(selectionManager);
        this.preCondition = Objects.requireNonNull(preCondition);
        this.action = Objects.requireNonNull(action);
        this.selectionSource = Objects.requireNonNull(selectionSource);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.start();
    }
    
    @Override
    public void run() {
        try{
            
            final List<SELECTED> selected = selectionManager.getSelectedValues(selectionSource);

            LOG.finer(() -> "Selected: " + selected);

            new SelectionActionExecutor(uiContext, selectionManager).run(action, preCondition, selected);
            
        }catch(RuntimeException e) {
            final String m = "Unexpected Exception";
            LOG.log(Level.WARNING, m, e);
        }
    }
}
