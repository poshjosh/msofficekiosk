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

package com.looseboxes.msofficekiosk.launchers;

import com.bc.elmi.pu.entities.Document;
import com.looseboxes.msofficekiosk.exceptions.StartupException;
import com.bc.elmi.pu.entities.Test;
import com.bc.elmi.pu.entities.Unit;
import com.bc.ui.UIContext;
import com.looseboxes.msofficekiosk.MsKioskSetup;
import com.looseboxes.msofficekiosk.test.Tests;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.GetTimeDisplay;
import com.looseboxes.msofficekiosk.functions.admin.OpenFilesNative;
import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.document.DocumentStore;
import com.looseboxes.msofficekiosk.ui.exam.ShowUiTill;
import com.looseboxes.msofficekiosk.validators.Precondition;
import java.awt.GridLayout;
import java.io.File;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.swing.border.EmptyBorder;

/**
 * @author Chinomso Bassey Ikwuagwu on May 3, 2019 8:56:07 PM
 */
public class PreconditionForExamUiLaunch implements Precondition<MsKioskSetup>{

    private static final Logger LOG = Logger.getLogger(PreconditionForExamUiLaunch.class.getName());

    private final UIContext uiContext;
    
    private final ConfigFactory configFactory;
        
    private final LoginManager loginManager;
    
    private final Tests tests;
    
    private final DocumentStore documentStore;
    
    private final Function<List<Test>, Optional<Test>> promptUserSelectTest;
    
    private final UnaryOperator<Test> editTest;
    
    private String error;
    
    public PreconditionForExamUiLaunch(
            UIContext uiContext, ConfigFactory configFactory, 
            LoginManager loginManager,
            Tests tests, DocumentStore documentStore, 
            Function<List<Test>, Optional<Test>> promptUserSelectTest, 
            UnaryOperator<Test> editTest) {
        this.uiContext = Objects.requireNonNull(uiContext);
        this.configFactory = Objects.requireNonNull(configFactory);
        this.loginManager = Objects.requireNonNull(loginManager);
        this.tests = Objects.requireNonNull(tests);
        this.documentStore = Objects.requireNonNull(documentStore);
        this.promptUserSelectTest = Objects.requireNonNull(promptUserSelectTest);
        this.editTest = Objects.requireNonNull(editTest);
    }

    @Override
    public void validate(String name, MsKioskSetup value) throws ValidationException {
        if( ! test(value)) {
            try{
                throw new StartupException(error != null ? error : name != null ? "Invalid " + name : "Not validated");
            }finally{
                error = null;
            }
        }
    }

    @Override
    public boolean test(MsKioskSetup setup) {
        
        error = null;
        
        final int activationLeadTimeMinutes = configFactory.getConfig(ConfigService.APP_INTERNAL)
                .getInt(ConfigNames.TESTS_ACTIVATION_LEADTIME_MINUTES);

        final TimeUnit activationLeadTimeUnit = TimeUnit.MINUTES;

        final Optional<Test> testOptional = tests.getCurrentOrPendingTest(
                activationLeadTimeMinutes, activationLeadTimeUnit);

        if(testOptional.isPresent()) {

            final Test test = testOptional.get();

            final String text = "<html><br/><b>Test Name: </b>" + test.getTestname() + 
                    "<br/><br/><b>Duration: </b>" + test.getDurationinminutes() + " minutes</html>";

            final Date date = test.getStarttime();
            LOG.log(Level.FINE, "Current time: {0}, Start time: {1}", new Object[]{new Date(), date});

            final long starttime = date.getTime();
            
            final Map<Document, Optional<File>> settingsMap = documentStore.fetchTestsettings(test);
            
            final List<File> files = settingsMap.values().stream()
                    .filter((fileOptional) -> fileOptional.isPresent())
                    .map((fileOptional) -> fileOptional.get())
                    .collect(Collectors.toList());
            
            new OpenFilesNative(true).run(files);

            final List<Document> yetToBeDownloaded = new ArrayList<>(); 
            settingsMap.forEach((testsetting, fileOptional) -> {
                if( ! fileOptional.isPresent()) {
                    yetToBeDownloaded.add(testsetting);
                }
            });

            showUiTill(text, starttime, activationLeadTimeMinutes, activationLeadTimeUnit, files, yetToBeDownloaded);
            
            final Object timeLeft = new GetTimeDisplay()
                .apply(timeTill(starttime), ZoneOffset.systemDefault());
            
            if(withinTimeToDisplay(starttime)) {
            
                LOG.log(Level.FINE, "Done waiting for Exam start, time left: {0}", timeLeft);
                
                return true;
                
            }else{
            
                error = "You terminated process of waiting for next Test\n\nNext Test: '" + 
                        test.getTestname()+"' is due in: " + timeLeft;
                
                return false;
            }
        }else{

//            if(true) {
//                error = "Try again later.\n\nNo pending Test within the next " + 
//                        activationLeadTimeMinutes + " " + activationLeadTimeUnit.name().toLowerCase();
//                return false;
//            }
            
            final ImageIcon icon = uiContext.getImageIconOptional().orElse(null);
            
            final String edit = "Edit an exiting Test";
            final String create = "Create new Test";
            final String quit = "Quit";
            
            final List<Test> testList = tests.get();
            
            final String [] options = ! testList.isEmpty() ? 
                    new String[]{edit, create, quit} :
                    new String[]{create, quit};
            
            final String m = "No pending Test within the next " + 
                    activationLeadTimeMinutes + " " + activationLeadTimeUnit.name().toLowerCase() + 
//                    ".\nYou may wish to edit an existing Test to make it due.\n";
                    ".\nHere are your options.\n";
            
            final String _opt = (String)JOptionPane.showInputDialog(null, m, "No Pending Test", 
                    JOptionPane.PLAIN_MESSAGE, icon, options, options[0]);
            LOG.log(Level.FINE, "User selection: {0}", _opt);
            
            final String option = _opt == null ? quit : _opt;
            
//            final int index = JOptionPane.showOptionDialog(null, m, "No Pending Test", 
//                    JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, icon, options, options[0]);
            switch(option) {
                case edit: 
                    final Optional<Test> testOpt = promptUserSelectTest.apply(testList);
                    if(testOpt.isPresent()) {
                        final Test test = editTest.apply(testOpt.get());
                        tests.setUserCreatedTest(test);
                        tests.activateTest(test, loginManager);
                        return test(setup);
                    }else{
                        error = "You did not select any test!";
                        return false;
                    }
                case quit:
                    error = "Quitting based on user decision";
                    return false;
                case create:
                default:
                    final Test test = editTest.apply(new Test());

                    final Optional<String> unitnameOpt = loginManager.getUserGroup();
                    final Unit unit = new Unit((short)0);
                    if(unitnameOpt.isPresent()) {
                        unit.setUnitname(unitnameOpt.get());
                    }
                    unit.setTestList(new ArrayList(Arrays.asList(test)));
                    test.setUnitList(new ArrayList(Arrays.asList(unit)));

                    tests.setUserCreatedTest(test);
                    tests.activateTest(test, loginManager);
                    return test(setup);
            }
        }
    }
    
    private synchronized boolean showUiTill(String text, long starttime, 
            int activationLeadtime, TimeUnit activationLeadTimeUnit, 
            List<File> files, List<Document> testsettings) {
        
        
        if(withinTimeToDisplay(starttime)) {

            return true;

        }else{
            
            final List<Document> yetToBeDownloaded = new ArrayList<>(testsettings);
            
            final JButton viewDownloaded = new JButton("View " + files.size() + " Test Settings");
            viewDownloaded.setBorder(new EmptyBorder(0, 0, 0, 0));
            viewDownloaded.addActionListener((ae) -> {
                try{
                    new OpenFilesNative(true).run(files);
                }catch(RuntimeException e) {
                    LOG.log(Level.WARNING, null, e);
                }
            });
            final int size = yetToBeDownloaded.size();
            final JButton download = new JButton("Download " + size + " remaining Test Settings");
            download.setBorder(new EmptyBorder(0, 0, 0, 0));
            download.addActionListener((ae) -> {
                new Thread("DownloadRemainginTestSettings_Thread"){
                    @Override
                    public void run() {
                        try{
                            
                            final AtomicInteger attempted = new AtomicInteger(0);        
                            
                            final Iterator<Document> iter = yetToBeDownloaded.iterator();
                            
                            uiContext.showProgressBar("..Downloading", 0, 0, size);
                            
                            while(iter.hasNext()) {
                                
                                LOG.finer(() -> "Attempted: " + attempted.get() + "/" + size);
                                
                                final Document doc = iter.next();
                                
                                attempted.incrementAndGet();

                                final Optional<File> fileOptional = documentStore.fetchRemote(Collections.singletonList(doc)).get(doc);
                                
                                fileOptional.ifPresent((file) -> {
                                    try{

                                        new OpenFilesNative(true).apply(file);

                                        iter.remove();

                                    }catch(RuntimeException e) {
                                        LOG.log(Level.WARNING, null, e);
                                    }
                                });
                                
                                uiContext.showProgressBar("..Downloading", 0, attempted.get(), size);
                            }
                        }catch(RuntimeException e) {
                            LOG.log(Level.WARNING, null, e);
                        }finally{
                            uiContext.showProgressBarPercent(100);
                            if(yetToBeDownloaded.isEmpty()) {
                                download.setEnabled(false);
                            }
                        }
                    }
                }.start();
            });
            
            final ShowUiTill waitForTest = new ShowUiTill(
                    uiContext, text, starttime - Tests.ADVANCE_TIME_MILLIS){
                @Override
                public void addComponents(JComponent parent, JLabel iconLabel, 
                        JLabel timerLabel, JLabel textLabel) {
            //        panel.setBackground(Color.WHITE);
                    final List<JComponent> children = new ArrayList<>();
                    children.add(iconLabel);
                    children.add(timerLabel);
                    children.add(textLabel);
                    if( ! files.isEmpty()) {
                        children.add(viewDownloaded);
                    }
                    if( ! yetToBeDownloaded.isEmpty()) {
                        children.add(download);
                    }
                    parent.setLayout(new GridLayout(children.size(), 1));
                    for(JComponent comp : children) {
                        parent.add(comp);
                    }
                }
            };

            final String threadName = this.getClass().getSimpleName() + "_WaitForTest_Thread";
            final Thread waitForTestThread = new Thread(waitForTest, threadName);
            waitForTestThread.setDaemon(false);

            waitForTestThread.start();

            try{
                
                waitForTestThread.join(activationLeadTimeUnit.toMillis(activationLeadtime));
                
                return true;
                
            }catch(InterruptedException e) {
                
                LOG.log(Level.WARNING, "", e);
                
                return withinTimeToDisplay(starttime);
            }
        }
    }
    
    private boolean withinTimeToDisplay(long targetTime) {
        return timeTill(targetTime) <= Tests.ADVANCE_TIME_MILLIS + 1000;
    }
    
    private long timeTill(long targetTime) {
        return targetTime - System.currentTimeMillis();
    }
}
