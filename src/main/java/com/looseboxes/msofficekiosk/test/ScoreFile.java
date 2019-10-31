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
import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 16, 2019 10:36:20 AM
 */
public class ScoreFile {

    private static final Logger LOG = Logger.getLogger(ScoreFile.class.getName());
    
    private final AppContext app;

    public ScoreFile(AppContext app) {
        this.app = Objects.requireNonNull(app);
    }
    
    public String getExtension() {
        return TestDoc.SCORE_EXTENSION;
    }

    public File toScoreFile(File testFile) {
        
        final String fname = toScoreFile(testFile.getName());

        return new File(app.getSetup().getDir(FileNames.DIR_OUTPUT).toFile(), fname);
    }
    
    public String toScoreFile(String testFilename) {
        
        if(this.isScoreFilename(testFilename)) {
            throw new IllegalArgumentException(testFilename);
        }
        
        final TestDocKey testDocKey = TestDocKey.decodeFilename(app, testFilename);
        
        final TestDocKey scoreDocKey = testDocKey.withDocumentnameSuffix(TestDoc.SCORE_SUFFIX);
        
        final String fname = scoreDocKey.getValue() + '.' + this.getExtension();
        
        LOG.fine(() -> "test file: " + testFilename + ", score file: " + fname);
        
        return fname;
    }

    public String toTestFile(String scoreFilename) {
        
        if( ! this.isScoreFilename(scoreFilename)) {
            throw new IllegalArgumentException(scoreFilename);
        }
        
        final TestDocKey scoreDocKey = TestDocKey.decodeFilename(app, scoreFilename);
        
        final TestDocKey testDocKey = scoreDocKey.withoutDocumentnameSuffix(TestDoc.SCORE_SUFFIX);
        
        final String fname = testDocKey.getValue() + '.' + TestDoc.EXTENSION;
        
        LOG.fine(() -> "score file: " + scoreFilename + ", test file: " + fname);
        
        return fname;
    }

    public boolean isScoreFilename(String name) {
        final Optional<TestDocKey> opt = TestDocKey.decodePossibleFilename(app, name);
        if(!opt.isPresent()) {
            return false;
        }
        final String docname = opt.get().getDocumentname();
        final boolean output = (docname.endsWith(TestDoc.SCORE_SUFFIX + '.' + this.getExtension()) ||
                docname.endsWith(TestDoc.SCORE_SUFFIX));
        LOG.fine(() -> "Is score file name: " + output + ", name: " + name);
        return output;
    }
}
