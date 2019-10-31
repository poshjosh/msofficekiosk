/*
 * Copyright 2018 NUROX Ltd.
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

package com.looseboxes.msofficekiosk.ui.admin;

import com.looseboxes.msofficekiosk.ui.MessageDialog;
import com.looseboxes.msofficekiosk.ui.UI;
import com.looseboxes.msofficekiosk.ui.listeners.LifeCycleListener;
import com.looseboxes.msofficekiosk.ui.listeners.LifecycleEventHandler;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 28, 2018 12:01:14 PM
 */
public interface AdminUi extends UI{

    boolean addLifeCycleListener(LifeCycleListener listener);

    void dispose();

    MenuItem getAboutMenuItem();

    //    @Override
    Display getDisplay();

    MenuItem getExitMenuItem();

    Menu getFileMenu();

    MenuItem getFileMenuItem();

    //    @Override
    Font getFont();

    LifecycleEventHandler getLifecycleEventHandler();

    MenuItem getLoginMenuItem();

    Menu getMenuBar(boolean createIfNone);

    //    @Override
    Menu getMenuBar();

    MessageDialog getMessageDialog();

    Label getMessageLabel();

    Color getMessageLabelColor();

    OleFrame getOleFrame();

    MenuItem getOpenFileForMarkingMenuItem();
    
    Set<Resource> getResources();

    MenuItem getSettingsMenuItem();

    Shell getShell();

    MenuItem getTitleMenuItem();

    MenuItem getUiSettingsMenuItem();
    
    MenuItem getUserProfileMenuItem();
    
    MenuItem getViewInboxMenuItem();
    
    MenuItem getViewOnlineDevicesMenuItem();

    //    @Override
    boolean isDisposed();

    //    @Override
    boolean isShowing();

    boolean removeLifeCycleListener(LifeCycleListener listener);

    //    @Override
    void setFont(Font font);

    //    @Override
    void setMessageText(String text);

    //    @Override
    void setText(Object ui, String text);

    //    @Override
    void setTextAndLog(Object ui, Level level, String text);

    //    @Override
    void setTextAndLog(Object ui, Level level, String text, Exception e);

    void show();
}
