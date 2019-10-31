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

import com.bc.config.Config;
import com.looseboxes.msofficekiosk.config.ConfigNamesUI;
import com.looseboxes.msofficekiosk.functions.ui.SetChildrenFont;
import com.looseboxes.msofficekiosk.ui.SingleInputForm;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 7, 2019 12:28:05 PM
 */
public class ExamShellUiImpl implements ExamShellUi{

    private static final Logger LOG = Logger.getLogger(ExamShellUiImpl.class.getName());

    private final SingleInputForm openNewForm;
    private final Label messageLabel;
    private final Label timerLabel;
    private final OleFrame oleFrame;
    private final OleClientSite oleClientSite;
    
    private final MenuItem fileMenuItem;
    private final Menu fileMenu;
    private final MenuItem submitAndCloseMenuItem;
    private final MenuItem saveMenuItem;
    private final MenuItem openMenuItem;
    private final Menu openMenu;
    private final MenuItem aboutMenuItem;
    private final MenuItem exitMenuItem;
    private final MenuItem exitWithoutSubmittingMenuItem;
    
    private final MenuItem titleMenuItem;
    
    private final MenuItem userProfileMenuItem;
    
    private final Set<Resource> resources = new HashSet<>();
    private final Color messageLabelColor;
    
    private final Shell shell;

    public ExamShellUiImpl(Display display, String title, Config config, 
            File outputFile, Listener openDocListener, String status) {
        
        final boolean shellMayNotBeSubclassed = true;
        
        final boolean prevent = config.getBoolean(ConfigNamesUI.PREVENT_USER_FROM_CLOSING_WINDOW, false);

        shell = new Shell(display, prevent ? SWT.ON_TOP : SWT.NONE);
        
        final int columnCount = 3;
        
        final boolean maximize = prevent ? true : config.getBoolean(ConfigNamesUI.MAXIMIZE_WINDOW, true);
        shell.setMaximized(maximize);
        shell.setLayout(new GridLayout(columnCount, false));

        this.openNewForm = new SingleInputForm(shell, SWT.NONE, "Enter file name: ", null, 
                "Create New", openDocListener);
        this.openNewForm.getText().setMessage("Enter name of file to create");
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.LEFT;
        gridData.verticalAlignment = SWT.FILL;
        gridData.horizontalSpan = 1;
        this.openNewForm.setLayoutData(gridData);

        this.messageLabel = new Label(shell, SWT.BORDER);
        this.messageLabelColor = new Color(display, 255, 255, 255);//new Color(display, 240, 230, 200);
        this.resources.add(this.messageLabelColor);
        this.messageLabel.setBackground(this.messageLabelColor);
        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.verticalAlignment = SWT.FILL;
        gridData.horizontalSpan = 1;
        this.messageLabel.setLayoutData(gridData);
        
        this.timerLabel = new Label(shell, SWT.NONE);
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.RIGHT;
        gridData.verticalAlignment = SWT.FILL;
        gridData.horizontalSpan = 1;
        this.timerLabel.setLayoutData(gridData);

        this.oleFrame = new OleFrame(shell, SWT.NONE);
        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.verticalAlignment = SWT.FILL;
        gridData.horizontalSpan = columnCount;
        this.oleFrame.setLayoutData(gridData);

        if(outputFile.exists()) {
            this.oleClientSite = new OleClientSite(oleFrame, SWT.NONE, "Word.Document", outputFile);
        }else{
            this.oleClientSite = new OleClientSite(oleFrame, SWT.NONE, "Word.Document");
        }

        final boolean withoutSettingFocusClientSiteDidNotDisplay = true;

        if(withoutSettingFocusClientSiteDidNotDisplay) {
            this.oleClientSite.setFocus();
        }

        final Menu menuBar = this.getMenuBar(true);
        this.fileMenuItem = createCascadeMenuItemWithDropDownSubmenu(menuBar, "&File");
        this.fileMenu = this.fileMenuItem.getMenu();

        this.submitAndCloseMenuItem = createCascadeMenuItem(fileMenu, "Submit");

        this.saveMenuItem = createCascadeMenuItem(fileMenu, "Save");

        this.openMenuItem = createCascadeMenuItemWithDropDownSubmenu(fileMenu, "Open");
        this.openMenu = this.openMenuItem.getMenu();
        
        this.aboutMenuItem = createCascadeMenuItem(fileMenu, "About");

        this.exitMenuItem = createCascadeMenuItem(fileMenu, "Exit");
        
        this.exitWithoutSubmittingMenuItem = createCascadeMenuItem(fileMenu, "Exit without submitting");

        this.titleMenuItem = createCascadeMenuItem(menuBar, title);

        this.userProfileMenuItem = createCascadeMenuItem(menuBar, status);

        this.oleFrame.setFileMenus(new MenuItem[] { fileMenuItem, titleMenuItem , userProfileMenuItem});
    }

    @Override
    public void dispose() {
        shell.dispose(); 
        this.openNewForm.dispose();
        this.disposeResources();
    }
    
    @Override
    public void setFont(Font font) {
        this.resources.add(font);
        shell.setFont(font);
        new SetChildrenFont().apply(this.shell, font);
    }
    
    @Override
    public final MenuItem createCascadeMenuItem(Menu menu, String text) {
        final MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
        menuItem.setText(text);
        return menuItem;
    }
    
    @Override
    public final MenuItem createCascadeMenuItemWithDropDownSubmenu(Menu menu, String text) {
        final MenuItem menuItem = this.createCascadeMenuItem(menu, text);
        final Menu subMenu = new Menu(menu.getShell(), SWT.DROP_DOWN);
        menuItem.setMenu(subMenu);
        return menuItem;
    }
    
    public void disposeResources() {
        for(Resource res : resources) {
            try{
                res.dispose();
            }catch(RuntimeException e) {
                LOG.log(Level.WARNING, "Failed to dispose resource: " + res, e);
            }
        }
    }

    @Override
    public void setMessageText(String text) {
        this.setText(this.messageLabel, text);
    }
    
    @Override
    public void setTextAndLog(Object ui, Level level, String text) {
        this.setTextAndLog(ui, level, text, null);
    }
    
    @Override
    public void setTextAndLog(Object ui, Level level, String text, Exception e) {
        this.setText(ui, text);
        if(e == null) {
            LOG.log(level, text);
        }else{
            LOG.log(level, text, e);
        }
    }

    @Override
    public void setText(Object ui, String text) {
        if( ! this.isDisposed(ui, true)) {
            this.getDisplay().asyncExec(() -> {
                if(!this.isDisposed(ui, true)) {
                    doSetText(ui, text);
                }
            });
        }
    }

    private boolean isDisposed(Object ui, boolean outputIfNone) {
        if(ui instanceof Label) {
            return ((Label)ui).isDisposed();
        }else if(ui instanceof MenuItem) {
            return ((MenuItem)ui).isDisposed();
        }else{
            LOG.warning(() -> "Updating text not yet supported for type: " + ui.getClass().getName());
            return outputIfNone;
        }
    }

    private boolean doSetText(Object ui, String text) {
        if(ui instanceof Label) {
            ((Label)ui).setText(text);
            return true;
        }else if(ui instanceof MenuItem) {
            ((MenuItem)ui).setText(text);
            return true;
        }else{
            LOG.warning(() -> "Updating text not yet supported for type: " + ui.getClass().getName());
            return false;
        }
    }

    public Menu getMenuBar(boolean createIfNone) {
        Menu menuBar = shell.getMenuBar();
        if (menuBar == null && createIfNone) {
            menuBar = new Menu(shell, SWT.BAR);
            shell.setMenuBar(menuBar);
        }
        return menuBar;
    }

    @Override
    public final boolean isDisposed() {
        return shell.isDisposed();
    }

    @Override
    public final boolean isShowing() {
        return shell.isVisible();
    }

    @Override
    public Display getDisplay() {
        return shell.getDisplay();
    }
    
    @Override
    public final Menu getMenuBar() {
        return shell.getMenuBar();
    }

    @Override
    public final MenuItem getFileMenuItem() {
        return fileMenuItem;
    }

    @Override
    public final MenuItem getSubmitAndCloseMenuItem() {
        return submitAndCloseMenuItem;
    }

    @Override
    public final MenuItem getSaveMenuItem() {
        return saveMenuItem;
    }

    @Override
    public final MenuItem getOpenMenuItem() {
        return openMenuItem;
    }

//    public final MenuItem getSettingsMenuItem() {
//        return settingsMenuItem;
//    }

    @Override
    public MenuItem getAboutMenuItem() {
        return aboutMenuItem;
    }

    @Override
    public MenuItem getTitleMenuItem() {
        return titleMenuItem;
    }

    @Override
    public MenuItem getUserProfileMenuItem() {
        return userProfileMenuItem;
    }

    @Override
    public Label getTimerLabel() {
        return timerLabel;
    }

    @Override
    public Menu getFileMenu() {
        return fileMenu;
    }

    @Override
    public Menu getOpenMenu() {
        return openMenu;
    }

    @Override
    public SingleInputForm getOpenNewForm() {
        return openNewForm;
    }

    @Override
    public Label getMessageLabel() {
        return messageLabel;
    }

    @Override
    public MenuItem getExitMenuItem() {
        return exitMenuItem;
    }

    @Override
    public MenuItem getExitWithoutSubmittingMenuItem() {
        return exitWithoutSubmittingMenuItem;
    }

    @Override
    public OleFrame getOleFrame() {
        return oleFrame;
    }

    @Override
    public OleClientSite getOleClientSite() {
        return oleClientSite;
    }

    @Override
    public Color getMessageLabelColor() {
        return messageLabelColor;
    }

    @Override
    public Shell getShell() {
        return shell;
    }
}
