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

import com.bc.config.Config;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.commands.RunTimerCommand;
import com.looseboxes.msofficekiosk.commands.TickListenerImpl;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.MakeReadOnly;
import com.looseboxes.msofficekiosk.test.ScoreFile;
import com.looseboxes.msofficekiosk.ui.SwtMessageDialog;
import com.looseboxes.msofficekiosk.ui.listeners.SyncScrollWithWordDocument;
import com.looseboxes.msofficekiosk.ui.selection.SelectionAction;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2018 11:27:17 PM
 */
public class MarkingUi implements SelectionListener {

    private static final Logger LOG = Logger.getLogger(MarkingUi.class.getName());
    
    private final AppContext app;
    
    private final Shell shell;
    
    private final SashForm sashForm;
    
    private final OleFrame oleFrame;
    
    private final OleClientSite oleClientSite;
    
    private final StyledText styledText;
    
    private final boolean internalDisplay;

    private final SyncScrollWithWordDocument synchronizedScroll;
    
    private final MenuItem fileMenuItem;
    private final Menu fileMenu;
    private final MenuItem saveSubmitAndExitMenuItem;
    private final MenuItem saveMenuItem;
    private final MenuItem saveAndExitMenuItem;
    
    private final SelectionAction<File> submitAction;
    
    private final File testFile;

    private final File scoreFile;
    
    private RunTimerCommand periodSaveTask;
    
    private final String charset;

    public MarkingUi(AppContext app, int [] splitFormRatios, 
            SelectionAction<File> submitAction, File file) {
        this(app, new Shell(new Display()), splitFormRatios, submitAction, file, true);
    }

    public MarkingUi(AppContext app, Shell shell, int [] splitFormRatios, 
            SelectionAction<File> submitAction, File file) {
        this(app, shell, splitFormRatios, submitAction, file, false);
    }
    
    protected MarkingUi(AppContext app, Shell shell, 
            int [] splitFormRatios, SelectionAction<File> submitAction, 
            File file, boolean internalDisplay) {
        this.app = Objects.requireNonNull(app);
        this.internalDisplay = internalDisplay;
        this.submitAction = Objects.requireNonNull(submitAction);
        this.shell = Objects.requireNonNull(shell);
        this.shell.setText(" Marking -> " + file.getName());
	this.shell.setLayout(new FillLayout());

	this.sashForm = new SashForm(this.shell, SWT.HORIZONTAL);
	this.sashForm.setLayout(new FillLayout());

        this.oleFrame = new OleFrame(sashForm, SWT.NONE);
//        this.oleFrame = new OleFrame(sashForm, SWT.V_SCROLL | SWT.H_SCROLL);
        this.oleClientSite = new OleClientSite(oleFrame, SWT.NULL, file);
//        this.oleClientSite = new OleClientSite(oleFrame, SWT.V_SCROLL | SWT.H_SCROLL, file);

        oleClientSite.setLayout(new FillLayout());
        oleClientSite.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
        new MakeReadOnly().accept(oleClientSite, Boolean.TRUE);

        this.styledText = new StyledText(sashForm, 
                SWT.WRAP | SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        
        styledText.setLayout(new FillLayout());
        
	sashForm.setWeights(splitFormRatios);
        
//        new SynchronizedScroll(oleFrame, styledText);
        synchronizedScroll = new SyncScrollWithWordDocument(oleClientSite, styledText);
        
        this.testFile = Objects.requireNonNull(file);
        final ScoreFile scoreFile = new ScoreFile(app);
        this.scoreFile = scoreFile.isScoreFilename(file.getName()) ? file : scoreFile.toScoreFile(file);
        final Config config = app.getConfig(ConfigService.APP_INTERNAL);
        this.charset = config.getString(ConfigNames.CHARACTER_SET);
        
        this.load();

        final Menu menuBar = this.getMenuBar(true);
        this.fileMenuItem = createCascadeMenuItemWithDropDownSubmenu(menuBar, "&File");
        this.fileMenu = this.fileMenuItem.getMenu();

        this.saveMenuItem = createCascadeMenuItem(fileMenu, "Save");

        this.saveSubmitAndExitMenuItem = createCascadeMenuItem(fileMenu, "Submit");

        this.saveAndExitMenuItem = createCascadeMenuItem(fileMenu, "Exit");
        
        this.oleFrame.setFileMenus(new MenuItem[] { fileMenuItem, saveMenuItem,
                saveSubmitAndExitMenuItem, saveAndExitMenuItem});
        
        configure();
    }
    
    public final MenuItem createCascadeMenuItem(Menu menu, String text) {
        final MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
        menuItem.setText(text);
        return menuItem;
    }
    
    public final MenuItem createCascadeMenuItemWithDropDownSubmenu(Menu menu, String text) {
        final MenuItem menuItem = this.createCascadeMenuItem(menu, text);
        final Menu subMenu = new Menu(menu.getShell(), SWT.DROP_DOWN);
        menuItem.setMenu(subMenu);
        return menuItem;
    }

    private RunTimerCommand schedulePeriodicAutoSave() {
        
        final Config config = app.getConfig(ConfigService.APP_INTERNAL);
        final long _valueInSeconds = config.getLong(ConfigNames.AUTOSAVE_INTERVAL_SECONDS, 10);
        final int autoSaveIntervalMillis = (int)TimeUnit.SECONDS.toMillis(_valueInSeconds);
        final String charset = config.getString(ConfigNames.CHARACTER_SET);
        final RunTimerCommand.TickListener tickListener = new TickListenerImpl(
                app.getConfigFactory(), this.shell.getDisplay(),
                (timer) -> ! this.shell.getDisplay().isDisposed(), 
                (timer) -> timer.getTimeElapsedMillis(), 
                (text) -> {
                    try{
                        
                        LOG.finest(() -> "Saving after: " + text);
                    
                        save();
                    
                    }catch(RuntimeException e) {
                        LOG.log(Level.WARNING, "Exception executing PERIODIC_SAVE Command", e);
                    }
                }
        );
        
        final RunTimerCommand periodSaveCommand = new RunTimerCommand(
                this.shell.getDisplay(), autoSaveIntervalMillis, tickListener);
        
        periodSaveCommand.start();
        
        return periodSaveCommand;
    }
    
    public void load() {
        if(this.scoreFile.exists() && this.scoreFile.length() > 0) {
            try{
                final byte [] bytes = Files.readAllBytes(this.scoreFile.toPath());
                styledText.setText(new String(bytes, charset));
            }catch(IOException e) {
                LOG.log(Level.WARNING, null, e);
                try{
                    new SwtMessageDialog(shell.getDisplay()).showWarningMessage("Failed to load existing Score Card for this File");
                }catch(Throwable t) {
                    LOG.log(Level.WARNING, null, t);
                }
            }
        }
    }
    
    public void save() {
        try{

            final String toSave = styledText.getText();
            if(toSave == null || toSave.isEmpty()) {
                return;
            }

            Files.write(scoreFile.toPath(), toSave.getBytes(charset), 
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);

        }catch(IOException e) {
            LOG.log(Level.WARNING, "Exception executing PERIODIC_SAVE Command", e);
        }
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if(e.getSource().equals(this.styledText.getVerticalBar())){
            onTreeVerticalScrollSelected(this.styledText, this.oleClientSite);
        }else if(e.getSource().equals(this.oleClientSite.getVerticalBar())){
            onTreeVerticalScrollSelected(this.oleClientSite, this.styledText);
        }
    }

    private void onTreeVerticalScrollSelected(Scrollable target, Scrollable other){
        other.getVerticalBar().setSelection(target.getVerticalBar().getSelection());
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) { }
    
    public void show() {
        
        final Display display = shell.getDisplay();

        this.periodSaveTask = schedulePeriodicAutoSave();

        shell.open();

        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch()) {
                display.sleep ();
            }
        }
    }
    
    public void dispose() {
        try{
            if(internalDisplay) {
                final Display display = shell.getDisplay();
                if(!display.isDisposed()) {
                    display.close();
                    display.dispose();
                }
            }else{
                if(!shell.isDisposed()) {
                    shell.close();
                    shell.dispose();
                }
            }
        }catch(RuntimeException e) {
            LOG.log(Level.WARNING, null, e);
        }finally{
            synchronizedScroll.dispose();
        }
        if(this.periodSaveTask != null) {
            try{
                this.periodSaveTask.stop();
            }catch(RuntimeException e) {
                LOG.log(Level.WARNING, null, e);
            }
        }
    }
    
    public void configure() {
    
        this.saveMenuItem.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent se) {
                try{
                    save();
                }catch(RuntimeException e) {
                    LOG.log(Level.WARNING, null, e);
                }
            }
        });
        
        this.saveAndExitMenuItem.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent se) {
                try{
                    save();
                }catch(RuntimeException e) {
                    LOG.log(Level.WARNING, null, e);
                }
                try{
                    dispose();
                }catch(RuntimeException e) {
                    LOG.log(Level.WARNING, null, e);
                }
            }
        });

        this.saveSubmitAndExitMenuItem.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent se) {
                try{
                    save();
                }catch(RuntimeException e) {
                    LOG.log(Level.WARNING, null, e);
                }
                try{
                    submitAction.run(Collections.singletonList(scoreFile));
                }catch(RuntimeException e) {
                    LOG.log(Level.WARNING, null, e);
                }
                try{
                    dispose();
                }catch(RuntimeException e) {
                    LOG.log(Level.WARNING, null, e);
                }
            }
        });
        
        shell.addShellListener(new ShellAdapter(){
            @Override
            public void shellClosed(ShellEvent se) {
                try{
                    save();
                }catch(RuntimeException e) {
                    LOG.log(Level.WARNING, null, e);
                }
//                try{
//                    dispose();
//                }catch(RuntimeException e) {
//                    LOG.log(Level.WARNING, null, e);
//                }
            }
        });
    }
    
    public Menu getMenuBar(boolean createIfNone) {
        Menu menuBar = shell.getMenuBar();
        if (menuBar == null && createIfNone) {
            menuBar = new Menu(shell, SWT.BAR);
            shell.setMenuBar(menuBar);
        }
        return menuBar;
    }

    public OleFrame getLeft() {
        return this.oleFrame;
    }
    
    public StyledText getRight() {
        return this.styledText;
    }

    public Shell getShell() {
        return shell;
    }

    public SashForm getSashForm() {
        return sashForm;
    }

    public OleFrame getOleFrame() {
        return oleFrame;
    }

    public OleClientSite getOleClientSite() {
        return oleClientSite;
    }

    public StyledText getStyledText() {
        return styledText;
    }

    public boolean isInternalDisplay() {
        return internalDisplay;
    }

    public SyncScrollWithWordDocument getSynchronizedScroll() {
        return synchronizedScroll;
    }

    public File getTestFile() {
        return testFile;
    }

    public File getScoreFile() {
        return scoreFile;
    }

    public MenuItem getFileMenuItem() {
        return fileMenuItem;
    }

    public Menu getFileMenu() {
        return fileMenu;
    }

    public MenuItem getSaveSubmitAndExitMenuItem() {
        return saveSubmitAndExitMenuItem;
    }

    public MenuItem getSaveMenuItem() {
        return saveMenuItem;
    }

    public MenuItem getSaveAndExitMenuItem() {
        return saveAndExitMenuItem;
    }
}
/**
 * 
        
	this.open ();
        
	while (!this.isDisposed ()) {
            if (!display.readAndDispatch()) {
                display.sleep ();
            }
	}
        
	display.dispose ();    
 */