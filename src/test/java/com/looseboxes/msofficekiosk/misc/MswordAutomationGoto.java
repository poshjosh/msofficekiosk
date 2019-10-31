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

package com.looseboxes.msofficekiosk.misc;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 15, 2019 10:52:11 AM
 * @see https://docs.microsoft.com/en-us/office/vba/api/word.selection.goto
 */
public class MswordAutomationGoto {

    public static void main(String [] args) {
        
        final File file = Paths.get(System.getProperty("user.home"), "Desktop", "8.docx").toFile();

        final Point point = new Point(1000, 750);

        final Display display = new Display();
        final Shell shell = new Shell(display);

        shell.setSize(point);

        shell.setLayout(new FillLayout());

        final OleFrame oleFrame = new OleFrame(shell, SWT.NONE);
        oleFrame.setSize(point);
        final OleClientSite clientSite;
        if(file.exists()) {
//            clientSite = new OleClientSite(oleFrame, SWT.NONE, "Word.Document", file);
            clientSite = new OleClientSite(oleFrame, SWT.NULL, file);
        }else{
            clientSite = new OleClientSite(oleFrame, SWT.NONE, "Word.Document");
        }
        clientSite.setSize(point);

//        clientSite.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);

        final boolean withoutSettingFocusClientSiteDidNotDisplay = true;

        if(withoutSettingFocusClientSiteDidNotDisplay) {
//                    clientSite.setFocus();
        }

        shell.pack();
        shell.open();
        
        a(clientSite); 
        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }    
        }
        
        display.dispose();
    }
    
    private static void a(OleClientSite clientSite) {

        final OleAutomation document = new OleAutomation(clientSite); 
        System.out.println("OleAutomation: " + document);
        int[] dispIDs = document.getIDsOfNames(new String[] {"Application"}); 
        System.out.println("OleAutomation.getIDsOfNames('Application'): " + (dispIDs==null?null:Arrays.toString(dispIDs)));
        Variant varResult = document.getProperty(dispIDs[0]); 
        System.out.println("OleAutomation.getProperty('idsOfNames'): " + varResult);
        if (varResult != null && varResult.getType() == COM.VT_DISPATCH) { 
            OleAutomation application = varResult.getAutomation(); 
            System.out.println("OleAutomation.getProperty('idsOfNames').getAutomation(): " + application);
            varResult.dispose(); 
            dispIDs = application.getIDsOfNames(new String[] {"Selection"}); 
            System.out.println("OleAutomation.getProperty('idsOfNames').getAutomation().getIDsOfNames('Selection'): " + (dispIDs==null?null:Arrays.toString(dispIDs)));
            varResult = application.getProperty(dispIDs[0]); 
            if (varResult != null && varResult.getType() == COM.VT_DISPATCH) { 
                OleAutomation selection = varResult.getAutomation(); 
                varResult.dispose(); 
                //@see https://docs.microsoft.com/en-us/office/vba/api/word.selection.goto
                //What,Which,Count,Name
                dispIDs = selection.getIDsOfNames(new String[]{"Goto","What","Which","Count"}); 
                Variant[] rgvarg = new Variant[3]; 
                rgvarg[0] = new Variant(3); //  What:: wdGoToLine = 3
                rgvarg[1] = new Variant(1); // Which:: wdGoToAbsolute = 1, wdGoToNext = 2, wdGoToPreviou = 3
                rgvarg[2] = new Variant(5); // Count::
                int[] rgdispidNamedArgs = new int[] {dispIDs[1], dispIDs[2], dispIDs[3]}; 
                selection.invoke(dispIDs[0], rgvarg, rgdispidNamedArgs); 

                selection.dispose(); 
            } 
            application.dispose(); 
        } 
        document.dispose();     
    }
}
