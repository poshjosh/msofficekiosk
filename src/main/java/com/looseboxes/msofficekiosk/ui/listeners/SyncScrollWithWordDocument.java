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

package com.looseboxes.msofficekiosk.ui.listeners;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Scrollable;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 15, 2019 2:38:39 PM
 * https://docs.microsoft.com/en-us/office/vba/api/word.selection.goto
 */
public class SyncScrollWithWordDocument extends SelectionAdapter {

    private static final Logger LOG = Logger.getLogger(SyncScrollWithWordDocument.class.getName());

    private final int factor = 14;
        
    private final OleAutomation document;
    private final Scrollable rhs;
 
    private OleAutomation application;
    private OleAutomation selection;
    
    private int wdGoto; 
    private int wdGotoWhat; 
    private int wdGotoWhich; 
    private int wdGotoCount; 
    private int wdGotoName; 
    
    private final int wdGoToLine = 3;
    private final int wdGoToAbsolute = 1;
    private final int wdGoToNext = 2;
    private final int wdGoToPrevious = 3;
    
    private final Variant[] rgvarg = new Variant[3];

    public SyncScrollWithWordDocument(OleClientSite clientSite, Scrollable rhs) {
        this(new OleAutomation(clientSite), rhs);
    }
    
    public SyncScrollWithWordDocument(OleAutomation document, Scrollable rhs) {
        this.document = Objects.requireNonNull(document);
        LOG.finer(() -> "OleAutomation: " + document);
        
        this.rhs = Objects.requireNonNull(rhs);
        rhs.getVerticalBar().addSelectionListener(SyncScrollWithWordDocument.this);

        final int[] dispIDsForApplication = document.getIDsOfNames(new String[] {"Application"}); 
        LOG.finer(() -> "OleAutomation.getIDsOfNames('Application'): " + (dispIDsForApplication==null?null:Arrays.toString(dispIDsForApplication)));

        Variant varResult = document.getProperty(dispIDsForApplication[0]); 
        LOG.log(Level.FINER, "OleAutomation.getProperty('idsOfNames'): {0}", varResult);

        if (varResult != null && varResult.getType() == COM.VT_DISPATCH) { 
        
            application = varResult.getAutomation(); 
            LOG.log(Level.FINER, "OleAutomation.getProperty('idsOfNames').getAutomation(): {0}", application);
            
            varResult.dispose(); 
            
            final int [] dispIDsForSelection = application.getIDsOfNames(new String[] {"Selection"}); 
            LOG.finer(() -> "OleAutomation.getProperty('idsOfNames').getAutomation().getIDsOfNames('Selection'): " + (dispIDsForApplication==null?null:Arrays.toString(dispIDsForApplication)));
            
            varResult = application.getProperty(dispIDsForSelection[0]); 
            
            if (varResult != null && varResult.getType() == COM.VT_DISPATCH) { 
            
                selection = varResult.getAutomation(); 
                varResult.dispose(); 

                //https://docs.microsoft.com/en-us/office/vba/api/word.selection.goto
                final int [] dispIDsForGoto = selection.getIDsOfNames(new String[]{"Goto","What","Which","Count","Name"}); 
                wdGoto = dispIDsForGoto[0];
                wdGotoWhat = dispIDsForGoto[1]; 
                wdGotoWhich = dispIDsForGoto[2]; 
                wdGotoCount = dispIDsForGoto[3]; 
                wdGotoName = dispIDsForGoto[4]; 
                
                rgvarg[0] = new Variant(wdGoToLine);   
                rgvarg[1] = new Variant(wdGoToAbsolute);   
            } 
        } 
    }
    
    public void dispose(){
        selection.dispose(); 
        application.dispose(); 
        document.dispose();     
    }
    
    @Override
    public void widgetSelected(SelectionEvent e) {
        if(e.getSource().equals(this.rhs.getVerticalBar())){
            onTreeVerticalScrollSelected(this.rhs, this.selection);
        }
//        else if(e.getSource().equals(this.selection.getVerticalBar())){
//            onTreeVerticalScrollSelected(this.selection, this.rhs);
//        }
    }
    
    private void onTreeVerticalScrollSelected(Scrollable target, OleAutomation automation){
        
        if(automation == null) {
            return;
        }
        
        final int n = target.getVerticalBar().getSelection();
        
        final int loc = n / factor;
        
        final int prevLoc = getPreviouLoc(-1);
        
        LOG.finer(() -> "VerticalBar: " + n + ", Document line. Next: " + loc + ", prev: " + prevLoc);
        
        if(loc < 1) {
            return;
        }
        
        if(loc == prevLoc) {
            return;
        }
        
        LOG.finer(() -> "Invoking WordDocument Goto to line: " + loc);
        
        rgvarg[2] = new Variant(loc); 

        final int[] rgdispidNamedArgs = new int[] {wdGotoWhat, wdGotoWhich, wdGotoCount}; 

        automation.invoke(wdGoto, rgvarg, rgdispidNamedArgs); 
    }
    
    private int getPreviouLoc(int outputIfNone) {
        final int loc = rgvarg == null ? -1 : rgvarg[2] == null ? -1 : rgvarg[2].getInt();
        return loc < 0 ? outputIfNone : loc;
    }
}
