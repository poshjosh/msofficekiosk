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
package com.looseboxes.msofficekiosk.commands;

import com.bc.config.Config;
import com.bc.elmi.pu.entities.Test;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.test.Tests;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import java.io.File;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import org.eclipse.swt.widgets.Display;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import java.io.IOException;
import java.util.logging.Level;
import com.looseboxes.msofficekiosk.FileNames;
import com.looseboxes.msofficekiosk.functions.io.CopyToDir;
import com.looseboxes.msofficekiosk.net.TestDocSenderFactory;
import com.looseboxes.msofficekiosk.test.OpenedFileManager;
import com.looseboxes.msofficekiosk.test.TestDoc;
import com.looseboxes.msofficekiosk.test.TestDocKey;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 23, 2019 4:37:19 AM
 */
public class SubmitCommand extends AbstractCommandWithPreCondition {

    private static final Logger LOG = Logger.getLogger(SubmitCommand.class.getName());

    private final AppContext context;
    
    private final AppUiContext uiContext;
    
    private final Tests tests;
    
    private final Test activatedTest;
    
    private final TestDocSenderFactory testDocSenderFactory;
    
    public SubmitCommand(AppContext context, AppUiContext uiContext, Display display, 
            Tests tests, TestDocSenderFactory testDocSenderFactory) {
        this(context, uiContext, display, tests, testDocSenderFactory, (displayArg) -> true);
    }
    
    public SubmitCommand(AppContext context, AppUiContext uiContext, 
            Display display, Tests tests, TestDocSenderFactory testDocSenderFactory, 
            Predicate<Display> preCondition) {
        super(display, preCondition);
        this.context = Objects.requireNonNull(context);
        this.uiContext = Objects.requireNonNull(uiContext);
        this.tests = Objects.requireNonNull(tests);
        this.activatedTest = tests.validateActivatedTest();
        this.testDocSenderFactory = Objects.requireNonNull(testDocSenderFactory);
    }

    @Override
    public void run() {

        final OpenedFileManager ofm = context.getOpenedFileManager();
        
        final Set<TestDoc> openedDocs = ofm.getOpenedFiles();
        
        LOG.fine(() -> "Open documents: " + openedDocs);
        
        final TestDocKey key = openedDocs.stream().map((td) -> ofm.getKey(td)).findFirst().orElse(null);
        
        LOG.fine(() -> "Sample: " + key);
        
        final String subject = activatedTest.getTestname() + '_' + key.getSyndicate() + '_' + key.getUsernameEncrypted();
        
        final Map<TestDoc, File> attachments = new HashMap(openedDocs.size());
        
        final Path outboxDir = context.getSetup().getDir(FileNames.DIR_OUTBOX);
        
        for(TestDoc testDoc : openedDocs) {
            
            final File outputFile = ofm.getFile(testDoc);
                    
            final File outputDir = outputFile.getParentFile();

            try{
                
                final File outboxFile = new CopyToDir().execute(outputFile, outboxDir);

                if( ! outputFile.delete()) {

                    outputFile.deleteOnExit();
                }

                attachments.put(testDoc, outboxFile);

            }catch(IOException e) {
                
                LOG.log(Level.WARNING, "Failed to transfer: "+outputFile.getName()+
                        ", from: " + outputDir + " to: " + outboxDir, e);
                
                attachments.put(testDoc, outputFile);
            }
        }

        final boolean submitted = submit(subject, attachments);
    }    

    public boolean submit(String subject, Map<TestDoc, File> attachments) {
        
        boolean submitted = false;
        
        if(testDocSenderFactory.isSupported(TestDocSenderFactory.SEND_TO_SERVER) &&
                testDocSenderFactory.get(TestDocSenderFactory.SEND_TO_SERVER).apply(attachments)) {
        
            submitted = true;
            
        }else if(testDocSenderFactory.isSupported(TestDocSenderFactory.SEND_TO_EMAIL) &&
                testDocSenderFactory.get(TestDocSenderFactory.SEND_TO_EMAIL).apply(attachments)) {
            
            submitted = true;
        }
        
        if(submitted) {
            
            tests.delete(activatedTest);
            
            final Config<Properties> cfg = context.getConfig(ConfigService.APP_PROTECTED);
            final boolean delete = cfg.getBoolean(ConfigNames.DELETE_DOCUMENTS_AFTER_SUBMIT, false);

            if(delete) {
                delete(attachments.values());
            }
        }
        
        return submitted;
    }

    public void delete(Collection<File> attachments) {
        LOG.fine(() -> "Deleting " + attachments.size() + attachments);
        attachments.forEach((f) -> {
            if(f.exists()) {
                if(!f.delete()) {
                    f.deleteOnExit();
                }
            }
        });
        LOG.fine(() -> "Deleted " + attachments.size() + attachments);
    }

    public AppContext getContext() {
        return context;
    }

    public AppUiContext getUiContext() {
        return uiContext;
    }
}
