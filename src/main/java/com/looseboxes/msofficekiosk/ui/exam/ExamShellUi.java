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

package com.looseboxes.msofficekiosk.ui.exam;

import com.looseboxes.msofficekiosk.ui.SingleInputForm;
import java.util.logging.Level;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 7, 2019 12:35:14 PM
 */
public interface ExamShellUi {

    MenuItem createCascadeMenuItem(Menu menu, String text);

    MenuItem createCascadeMenuItemWithDropDownSubmenu(Menu menu, String text);
    
    void dispose();
    
    //    public final MenuItem getSettingsMenuItem() {
    //        return settingsMenuItem;
    //    }
    MenuItem getAboutMenuItem();

    Display getDisplay();

    MenuItem getExitMenuItem();

    MenuItem getExitWithoutSubmittingMenuItem();

    Menu getFileMenu();

    MenuItem getFileMenuItem();

    Menu getMenuBar();

    Label getMessageLabel();

    Menu getOpenMenu();

    MenuItem getOpenMenuItem();

    SingleInputForm getOpenNewForm();

    MenuItem getSaveMenuItem();

    Shell getShell();
        
    MenuItem getSubmitAndCloseMenuItem();

    Label getTimerLabel();

    MenuItem getTitleMenuItem();

    MenuItem getUserProfileMenuItem();

    boolean isDisposed();

    boolean isShowing();

    void setFont(Font font);
    
    void setMessageText(String text);

    void setText(Object ui, String text);

    void setTextAndLog(Object ui, Level level, String text);

    void setTextAndLog(Object ui, Level level, String text, Exception e);

    Color getMessageLabelColor();

    OleClientSite getOleClientSite();

    OleFrame getOleFrame();

}
