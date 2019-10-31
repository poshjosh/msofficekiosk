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

import com.looseboxes.msofficekiosk.functions.GetUserMessage;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.validators.Precondition;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * @author Chinomso Bassey Ikwuagwu on May 8, 2019 2:55:25 PM
 */
public class ActionListenerForSelection <SELECTION_SOURCE extends Component, SELECTED> 
        implements ActionListener, SelectionListener {

    private transient static final Logger LOG = Logger.getLogger(ActionListenerForSelection.class.getName());

    private final AppUiContext uiContext;
    
    private final String frameTitle;
    
    private final Precondition precondition;
    
    private final UiSelectionManager<SELECTION_SOURCE, SELECTED> uiSelectionManager;
    
    public ActionListenerForSelection(
            AppUiContext uiContext,
            String frameTitle,
            Precondition precondition,
            UiSelectionManager<SELECTION_SOURCE, SELECTED> uiSelectionManager) {
        this.uiContext = Objects.requireNonNull(uiContext);
        this.frameTitle = Objects.requireNonNull(frameTitle);
        this.precondition = Objects.requireNonNull(precondition);
        this.uiSelectionManager = Objects.requireNonNull(uiSelectionManager);
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        this.buildAndDisplaySelectionSource();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) { }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(java.awt.EventQueue.isDispatchThread()) {
            this.buildAndDisplaySelectionSource();
        }else{
            java.awt.EventQueue.invokeLater(() -> {
                buildAndDisplaySelectionSource();
            });
        }
    }
    
    public void buildAndDisplaySelectionSource() {
        try{
           
            //@todo Get a name ??? - arg 0
            precondition.validate("", this);

            final SELECTION_SOURCE selectionSource = uiSelectionManager.getSelectionUi();

            uiContext.positionHalfScreenRight(selectionSource);

            final JScrollPane scrolls = new JScrollPane(selectionSource);

            final JFrame frame = new JFrame(frameTitle);

            uiContext.getImageIconOptional().ifPresent((imageIcon) -> {
                frame.setIconImage(imageIcon.getImage());
            });

            uiContext.positionHalfScreenRight(frame);

            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            frame.getContentPane().add(scrolls);

            frame.pack();

            frame.setVisible(true);
            
        }catch(RuntimeException e) {
            
            final String m = "Unexpected Exception";
            
            LOG.log(Level.WARNING, m, e);
            
            uiContext.getMessageDialog().showWarningMessage(new GetUserMessage().apply(e));
        }
    }
}
