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

package com.looseboxes.msofficekiosk.test;

import com.bc.elmi.pu.entities.Test;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.security.LoginManager;
import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 23, 2019 12:01:04 PM
 */
public class OpenedFileManagerImpl extends TestFileProviderImpl implements OpenedFileManager{

    private transient static final Logger LOG = Logger.getLogger(OpenedFileManagerImpl.class.getName());

    private final Set<String> openedFileNames = new LinkedHashSet<>();
    
    private final Set<String> currentlyOpenFileNames = new LinkedHashSet<>();
    
    private final Tests tests;
    
    public OpenedFileManagerImpl(AppContext context, Tests tests,
            LoginManager loginManager) {
        super(context, tests, loginManager);
        this.tests = Objects.requireNonNull(tests);
    }

    @Override
    public Set<TestDoc> getOpenedFiles() {
        return getOpenedFiles(tests.validateActivatedTest());
    }

    @Override
    public Set<TestDoc> getOpenedFiles(Test test) {
        
        final Set<TestDoc> result = new LinkedHashSet<>();
        
        for(String docName : this.openedFileNames) {

            final TestDoc testDoc = tests.createTestDoc(test, docName);

            final File file = getFile(testDoc);
            
            if(file.exists()) {

                result.add(testDoc);
            }
        }
        
        return result;
    }

    @Override
    public boolean isLastOpen() {
        final boolean lastOpen = currentlyOpenFileNames.size() < 2;
        return lastOpen;
    }
    
    @Override
    public boolean addOpened(String documentName) {
        LOG.finer(() -> "Adding: " + documentName + " to: " + openedFileNames);
        currentlyOpenFileNames.add(documentName);
        return openedFileNames.add(documentName);
    }
    
    @Override
    public int removeFromCurrentlyOpen(String documentName) {
        LOG.finer(() -> "Removing: " + documentName + " from currenlty open files: " + currentlyOpenFileNames);
        currentlyOpenFileNames.remove(documentName);
        return currentlyOpenFileNames.size();
    }
    
    @Override
    public boolean isNoneOpen() {
        return currentlyOpenFileNames.isEmpty();
    }

    @Override
    public Set<String> getOpenedFileNames() {
        return Collections.unmodifiableSet(openedFileNames);
    }

    @Override
    public Set<String> getCurrentlyOpenFileNames() {
        return Collections.unmodifiableSet(currentlyOpenFileNames);
    }
}
