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

package com.looseboxes.msofficekiosk.document;

import com.looseboxes.msofficekiosk.test.TestDoc;
import com.bc.elmi.pu.entities.Document;
import com.bc.elmi.pu.entities.User;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.test.ScoreFile;
import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.test.TestFileProvider;
import java.util.Date;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2019 2:08:46 PM
 */
public class TestDocumentBuilderImpl implements TestDocumentBuilder {
    
    private final TestFileProvider testFileProvider;
    
    private final LoginManager loginManager;
    
    private final ScoreFile scoreFile;

    public TestDocumentBuilderImpl(AppContext app, 
            TestFileProvider testFileProvider, LoginManager loginManager) {
        this.scoreFile = new ScoreFile(app);
        this.testFileProvider = Objects.requireNonNull(testFileProvider);
        this.loginManager = Objects.requireNonNull(loginManager);
    }

    @Override
    public Document build(Document parent, TestDoc testDoc) {
        
        final Document doc = new Document();
        final String name = loginManager.getLoggedInUserName(null);
        if(name != null) {
            final User user = new User();
            user.setUsername(name);
            doc.setAuthor(user);
        }
        final String extension = scoreFile.isScoreFilename(testDoc.getDocumentname()) ?
                scoreFile.getExtension() : testDoc.getExtension();
        doc.setDocumentname(testDoc.getDocumentname() + "." + extension);
        doc.setDatesigned(new Date());
        doc.setParentdocument(parent);
        doc.setReferencenumber(null);
        doc.setSubject(testFileProvider.getKey(testDoc).getSummary());
        
        return doc;
    }
}
