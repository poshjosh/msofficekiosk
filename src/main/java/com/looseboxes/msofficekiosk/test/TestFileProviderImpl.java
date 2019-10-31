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

import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.FileNames;
import com.looseboxes.msofficekiosk.security.LoginManager;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2019 4:44:06 PM
 */
public class TestFileProviderImpl implements TestFileProvider {

    private final AppContext app;
    private final Tests tests;
    private final LoginManager loginManager;
    private final Path dir;

    public TestFileProviderImpl(AppContext app, Tests tests, LoginManager loginManager) {
        this.app = Objects.requireNonNull(app);
        this.tests = Objects.requireNonNull(tests);
        this.loginManager = Objects.requireNonNull(loginManager);
        this.dir = app.getSetup().getDir(FileNames.DIR_OUTPUT);
    }

    @Override
    public File getFile(TestDoc testDoc, String docName) {
        return getFile(tests.createTestDoc(testDoc.getTest(), docName));
    }

    @Override
    public File getFile(TestDocKey testKey, String extension) {
        return dir.resolve(testKey.getValue() + "." + extension).toFile();
    }
   
    @Override
    public TestDocKey getKey(TestDoc testDoc) {
        return new TestDocKey(app, loginManager, testDoc);
    }
}
