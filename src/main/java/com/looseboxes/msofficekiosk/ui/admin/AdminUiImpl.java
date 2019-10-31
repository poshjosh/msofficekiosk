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

package com.looseboxes.msofficekiosk.ui.admin;

import com.looseboxes.msofficekiosk.functions.ui.SetChildrenFont;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.ui.MessageDialog;
import com.looseboxes.msofficekiosk.ui.UiBeans;
import com.looseboxes.msofficekiosk.ui.listeners.LifeCycleListener;
import com.looseboxes.msofficekiosk.ui.listeners.LifecycleEventHandler;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 17, 2019 11:00:41 PM
 */
public class AdminUiImpl implements AdminUi{

    private static final Logger LOG = Logger.getLogger(AdminUi.class.getName());

    private final AppUiContext uiContext;
    
    private final Shell shell;

    private final Label messageLabel;
    private final OleFrame oleFrame;
    
    private final MenuItem fileMenuItem;
    private final Menu fileMenu;
    private final MenuItem aboutMenuItem;
    private final MenuItem loginMenuItem;
    private final MenuItem viewInboxMenuItem;
    private final MenuItem viewOnlineDevicesMenuItem;
    private final MenuItem openFileForMarkingMenuItem;
    private final MenuItem settingsMenuItem;
    private final MenuItem uiSettingsMenuItem;
    private final MenuItem exitMenuItem;
    
    private final MenuItem titleMenuItem;
    
    private final MenuItem userProfileMenuItem;
    
    private final Set<Resource> resources = new HashSet<>();
    private final Color messageLabelColor;
    
    private final LifecycleEventHandler lifecycleEventHandler;
    
    private final MessageDialog messageDialog;

    public AdminUiImpl(AppUiContext uiContext, Display display, 
            LifecycleEventHandler lifecycleEventHandler, 
            MessageDialog messageDialog, String title, 
            String status, UiBeans.UiConfigurer<AdminUi> configurer) {

        final boolean shellMayNotBeSubclassed = true;
        
        this.uiContext = Objects.requireNonNull(uiContext);
    
        shell = new Shell(display);
        
        this.lifecycleEventHandler = Objects.requireNonNull(lifecycleEventHandler);
        this.messageDialog = Objects.requireNonNull(messageDialog);
        
        final int columnCount = 3;
        
        shell.setLayout(new GridLayout(columnCount, false));

        GridData gridData = new GridData();

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
        
        this.oleFrame = new OleFrame(shell, SWT.NONE);
        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.verticalAlignment = SWT.FILL;
        gridData.horizontalSpan = columnCount;
        this.oleFrame.setLayoutData(gridData);

        final Menu menuBar = this.getMenuBar(true);
        this.fileMenuItem = createCascadeMenuItemWithDropDownSubmenu(menuBar, "&File");
        this.fileMenu = this.fileMenuItem.getMenu();

        this.aboutMenuItem = createCascadeMenuItem(fileMenu, "About");

        this.loginMenuItem = createCascadeMenuItem(fileMenu, "Login");
        this.viewInboxMenuItem = createCascadeMenuItem(fileMenu, "View Test Documents");
        this.viewOnlineDevicesMenuItem = createCascadeMenuItem(fileMenu, "View Online Devices");
        this.openFileForMarkingMenuItem = createCascadeMenuItem(fileMenu, "Open file for marking");
        this.settingsMenuItem = createCascadeMenuItem(fileMenu, "Settings");
        this.uiSettingsMenuItem = createCascadeMenuItem(fileMenu, "User Interface Settings"); 
        this.exitMenuItem = createCascadeMenuItem(fileMenu, "Exit");
        
        this.titleMenuItem = createCascadeMenuItem(menuBar, title);

        this.userProfileMenuItem = createCascadeMenuItem(menuBar, status);

        this.oleFrame.setFileMenus(new MenuItem[] { fileMenuItem, titleMenuItem , userProfileMenuItem});

        configurer.accept(this);
    }

    @Override
    public boolean addLifeCycleListener(LifeCycleListener listener) {
        return this.lifecycleEventHandler.addLifeCycleListener(listener);
    }

    @Override
    public boolean removeLifeCycleListener(LifeCycleListener listener) {
        return lifecycleEventHandler.removeLifeCycleListener(listener);
    }

    @Override
    public void show() {
        
        try{
           
            this.lifecycleEventHandler.onWillShow(this);

            final Font font = getFont();
            this.shell.setFont(font);
            
            this.shell.getShell().open();
            
        }catch(Throwable e){
            
            LOG.log(Level.WARNING, null, e);
            
            throw e;

        }finally{

            try{
                this.lifecycleEventHandler.onShown(this);
            }finally{
                
                final Display display = shell.getDisplay();
                
                while ( ! shell.isDisposed()) {
                    if ( ! display.readAndDispatch()) {
                        display.sleep();
                    }
                }
            }
        }
    }

//    @Override
    @Override
    public Font getFont() {
        final Font font = uiContext.getSwtFont(shell.getDisplay());
        return font;
    }
    
    @Override
    public MessageDialog getMessageDialog() {
        return messageDialog;
    }

    @Override
    public void dispose() {
        shell.dispose(); 
        this.disposeResources();
    }
    
//    @Override
    @Override
    public void setFont(org.eclipse.swt.graphics.Font font) {
        this.resources.add(font);
        shell.setFont(font);
        new SetChildrenFont().apply(this.shell, font);
    }
    
//    @Override
    public final MenuItem createCascadeMenuItem(Menu menu, String text) {
        final MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
        menuItem.setText(text);
        return menuItem;
    }
    
//    @Override
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

//    @Override
    @Override
    public void setMessageText(String text) {
        this.setText(this.messageLabel, text);
    }
    
//    @Override
    @Override
    public void setTextAndLog(Object ui, Level level, String text) {
        this.setTextAndLog(ui, level, text, null);
    }
    
//    @Override
    @Override
    public void setTextAndLog(Object ui, Level level, String text, Exception e) {
        this.setText(ui, text);
        if(e == null) {
            LOG.log(level, text);
        }else{
            LOG.log(level, text, e);
        }
    }

//    @Override
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

    @Override
    public Menu getMenuBar(boolean createIfNone) {
        Menu menuBar = shell.getMenuBar();
        if (menuBar == null && createIfNone) {
            menuBar = new Menu(shell, SWT.BAR);
            shell.setMenuBar(menuBar);
        }
        return menuBar;
    }

//    @Override
    @Override
    public final boolean isDisposed() {
        return shell.isDisposed();
    }

//    @Override
    @Override
    public final boolean isShowing() {
        return shell.isVisible();
    }

//    @Override
    @Override
    public Display getDisplay() {
        return shell.getDisplay();
    }
    
//    @Override
    @Override
    public final Menu getMenuBar() {
        return shell.getMenuBar();
    }

    @Override
    public Label getMessageLabel() {
        return messageLabel;
    }

    @Override
    public OleFrame getOleFrame() {
        return oleFrame;
    }

    @Override
    public MenuItem getFileMenuItem() {
        return fileMenuItem;
    }

    @Override
    public Menu getFileMenu() {
        return fileMenu;
    }

    @Override
    public MenuItem getAboutMenuItem() {
        return aboutMenuItem;
    }

    @Override
    public MenuItem getLoginMenuItem() {
        return loginMenuItem;
    }

    @Override
    public MenuItem getViewInboxMenuItem() {
        return viewInboxMenuItem;
    }

    @Override
    public MenuItem getViewOnlineDevicesMenuItem() {
        return viewOnlineDevicesMenuItem;
    }

    @Override
    public MenuItem getOpenFileForMarkingMenuItem() {
        return openFileForMarkingMenuItem;
    }

    @Override
    public MenuItem getSettingsMenuItem() {
        return settingsMenuItem;
    }

    @Override
    public MenuItem getUiSettingsMenuItem() {
        return uiSettingsMenuItem;
    }

    @Override
    public MenuItem getExitMenuItem() {
        return exitMenuItem;
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
    public Set<Resource> getResources() {
        return resources;
    }

    @Override
    public Color getMessageLabelColor() {
        return messageLabelColor;
    }

    @Override
    public Shell getShell() {
        return shell;
    }

    public AppUiContext getUiContext() {
        return uiContext;
    }

    @Override
    public LifecycleEventHandler getLifecycleEventHandler() {
        return lifecycleEventHandler;
    }
}
