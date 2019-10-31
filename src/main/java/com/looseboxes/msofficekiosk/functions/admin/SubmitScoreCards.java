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

package com.looseboxes.msofficekiosk.functions.admin;

import com.looseboxes.msofficekiosk.test.ScoreFile;
import com.bc.elmi.pu.entities.Test;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.net.SendTestDocs;
import com.looseboxes.msofficekiosk.test.TestDoc;
import com.looseboxes.msofficekiosk.test.TestDocImpl;
import com.looseboxes.msofficekiosk.test.TestDocKey;
import com.looseboxes.msofficekiosk.test.Tests;
import com.looseboxes.msofficekiosk.ui.selection.AbstractSelectionAction;
import java.io.File;
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 16, 2019 11:05:15 AM
 */
public class SubmitScoreCards extends AbstractSelectionAction<File> {

    private static final Logger LOG = Logger.getLogger(SubmitScoreCards.class.getName());

    private final AppContext app;
    
    private final Tests tests;
    
    private final SendTestDocs sendTestDocs;

    public SubmitScoreCards(AppContext app, Tests tests, SendTestDocs sendTestDocs) {
        super("Submit Score Card(s)");
        this.app = Objects.requireNonNull(app);
        this.tests = Objects.requireNonNull(tests);
        this.sendTestDocs = Objects.requireNonNull(sendTestDocs);
    }

    @Override
    public Boolean apply(File file) {
        
        final ScoreFile ab = new ScoreFile(app);
        
        final File scoreFile = ab.isScoreFilename(file.getName()) ?
                file : ab.toScoreFile(file);
        
        LOG.fine(() -> "Input: " + file + "\nOutput: " + scoreFile);
        
        final TestDoc testDoc = buildTestDoc(file);
        
        //@todo move to outbox
        
        return sendTestDocs.apply(Collections.singletonMap(testDoc, scoreFile));
    }

    public TestDoc buildTestDoc(File scoreFile) {
        
        final TestDocKey scoreKey = TestDocKey.decodeFilename(app, scoreFile.getName());
        
//        final Test test = tests.get(scoreKey.getTestid()).orElseThrow(() -> 
//                new NullPointerException("Test with ID: " + scoreKey.getTestid()));

        final Test test = new Test(Integer.parseInt(scoreKey.getTestid().toString()));
        test.setTestname(scoreKey.getTestname());
        
        final TestDoc testDoc = new TestDocImpl(test, scoreKey.getDocumentname());
        
        return testDoc;
    }
}
