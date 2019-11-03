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

package com.looseboxes.msofficekiosk.test;

import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.bc.security.Encryption;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.functions.GetOutputFileUsernameEncryption;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.GetFilenameWithoutExtension;
import com.looseboxes.msofficekiosk.security.LoginManager;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 12, 2018 11:09:44 AM
 */
public class TestDocKey implements Serializable {

    private static final Logger LOG = Logger.getLogger(TestDocKey.class.getName());
    
    public static final String SEPARATOR = "__";
            
    public static final String TEST_NAME = "Test Name";
    public static final String STUDENT_GROUP = "Student Group";
    public static final String STUDENT_NAME = "Student Name";
    public static final String DOCUMENT_NAME = "Document Name";
    
    private final String basename;
    
    private final Object testid;
    
    private final String testname;
    
    private final String studentgroup;
    
    private final String studentname;
    
    private final String studentnameEncrypted;
    
    private final String documentname;

    private final long timecreated;
    
    private final String value;
    
    private final String summary;

    public TestDocKey(AppContext context, LoginManager loginManager, TestDoc testDoc) {
        this(new GetOutputFileUsernameEncryption().apply(context).orElse(null), 
                context.getConfigFactory(), loginManager, testDoc);
    }
    
    public TestDocKey(Encryption encryption, ConfigFactory configFactory,
            LoginManager loginManager, TestDoc testDoc) {
        this(encryption, 
                configFactory.getConfig(ConfigService.APP_INTERNAL).getString(ConfigNames.APP_NAME, "App").replaceAll("(\\p{Punct}|\\s)", ""), 
                testDoc.getTest().getTestid(),
                testDoc.getTest().getTestname(), 
                loginManager.validateUsergroup(), 
                loginManager.validateUsername(), 
                testDoc.getDocumentname(),
                testDoc.getTimecreated());
    }

    public TestDocKey(Encryption encryption, String basename, 
            Object testid, String testname, String studentgroup, 
            String studentname, String docname, long timecreated) {
        
        this(basename, testid, testname, studentgroup, studentname, 
                encrypt(encryption, studentname), 
                docname, timecreated);
    }
    
    private static String encrypt(Encryption encryption, String username) {
        try{
            return encryption == null ? username : encryption.encrypt(username.toCharArray());
        }catch(GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    } 
    
    protected TestDocKey(String basename, 
            Object testid, String testname, String studentgroup, 
            String studentname, String usernameEncrypted,
            String docname, long timecreated) {

        this.basename = validate(basename);
        this.testid = validate(testid.toString());
        this.testname = validate(testname);
        this.studentgroup = validate(studentgroup);
        this.studentname = validate(studentname);
        this.studentnameEncrypted = validate(usernameEncrypted);
        this.documentname = Objects.requireNonNull(docname);
        this.timecreated = Objects.requireNonNull(timecreated);
        
        final List arr = Arrays.asList(basename, testid, testname, studentgroup, usernameEncrypted,
                docname == null || docname.isEmpty() ? "" : new GetFilenameWithoutExtension().apply(docname),
                timecreated);

        this.value = buildValue(arr);

        this.summary = buildValue(arr, arr.size() - 1);
    }
    
    public TestDocKey withoutDocumentnameSuffix(String suffix) {
        
        final String [] nameAndExt = new GetFilenameWithoutExtension().splitToNameAndExtension(documentname);
        
        final int end = nameAndExt[0].indexOf(suffix);
        
        if(end == -1) {
            throw new IllegalArgumentException();
        }
        
        final String newName = nameAndExt[0].substring(0, end) + '.' + nameAndExt[1];
        
        LOG.fine(() -> "Input: " + documentname + ", Output: " + newName);
        
        return new TestDocKey(basename, testid, testname, 
                studentgroup, studentname, studentnameEncrypted, newName, timecreated);
    }

    public TestDocKey withDocumentnameSuffix(String suffix) {
        
        final String [] nameAndExt = new GetFilenameWithoutExtension().splitToNameAndExtension(documentname);
        
        final String newName = nameAndExt[0] + suffix + '.' + nameAndExt[1];
        
        LOG.fine(() -> "Input: " + documentname + ", Output: " + newName);
        
        return new TestDocKey(basename, testid, testname, 
                studentgroup, studentname, studentnameEncrypted, newName, timecreated);
    }
    
    private String buildValue(List arr) {
        return buildValue(arr, arr.size());
    }
    
    private String buildValue(List arr, int limit) {
        final StringBuilder b = new StringBuilder();
        int i = 0;
        for(Object e : arr) {
            if(i > 0) {
                b.append(SEPARATOR);
            }
            b.append(e);
            if(++i >= limit) {
                break;
            }
        }
        return format(b.toString());
    }
    
    public static TestDocKey decodeFilename(AppContext context, String value) {
        return decodePossibleFilename(context, value).orElseThrow(() -> new IllegalArgumentException());
    }
    public static Optional<TestDocKey> decodePossibleFilename(AppContext context, String value) {
        final Encryption encryption = new GetOutputFileUsernameEncryption().apply(context).orElse(null); 
        return decode(encryption, value);
    }
    
    public static TestDocKey decodeFilename(String value) {
        return decodePossibleFilename(value).orElseThrow(() -> new IllegalArgumentException());
    }
    public static Optional<TestDocKey> decodePossibleFilename(String value) {
        return decode(null, value);
    }

    private static Optional<TestDocKey> decode(Encryption encryption, String value) {
        LOG.log(Level.FINE, "Filename to decode: {0}", value);
        value = new GetFilenameWithoutExtension().apply(value);
        LOG.log(Level.FINE, "Value to decode: {0}", value);
        final String [] partArr = value.split(SEPARATOR);
        final List<String> partList = Arrays.asList(partArr).stream()
                .filter((s) -> s != null && !s.trim().isEmpty()).collect(Collectors.toList());
        LOG.log(Level.FINE, "Part to decode: {0}", partList);
        final TestDocKey output;
        if(partList.size() < 6) {
            output = null;
        }else{
            final String basename = partList.get(0);
            final String testid = partList.get(1);
            final String testname = partList.get(2);
            final String studentgroup = partList.get(3);
            final String studentnameEncrypted = partList.get(4);
            final String studentname;
            try{
                studentname = encryption == null ? studentnameEncrypted : new String(encryption.decrypt(studentnameEncrypted));
            }catch(Exception e) {
                throw new RuntimeException(e);
            }    
            final String docname;
            final String timecreatedStr;
            if(partList.size() == 6) {
                docname = "";
                timecreatedStr = partList.get(5);
            }else{
                docname = partList.get(5);
                timecreatedStr = partList.get(6);
            }
            
            final long timecreated = Long.parseLong(timecreatedStr);
            
            output = new TestDocKey(encryption, basename, testid, testname, 
                    studentgroup, studentname, docname, timecreated);
        }
        return Optional.ofNullable(output);
    }
    
    public static String format(String s) throws IllegalArgumentException {
        final String output = s.replaceAll("[\\p{Punct}&&[^"+Pattern.quote("_")+"]]", "");
        LOG.fine(() -> s + " formatted to: " + output);
        return output;
    }

    public static String validate(String s) throws IllegalArgumentException {
        if(s.isEmpty()) {
            throw new IllegalArgumentException("Expected some text. Found empty text."); 
        }
        if(s.contains(SEPARATOR)) {
            throw new IllegalArgumentException("Text cannot contain: " + SEPARATOR);
        }
        return s;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.timecreated);
        hash = 59 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestDocKey other = (TestDocKey) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.timecreated, other.timecreated)) {
            return false;
        }
        return true;
    }

    public String getBasename() {
        return basename;
    }

    public String getStudentgroup() {
        return studentgroup;
    }

    public String getStudentname() {
        return studentname;
    }

    public String getStudentnameEncrypted() {
        return studentnameEncrypted;
    }

    public Object getTestid() {
        return testid;
    }

    public String getTestname() {
        return testname;
    }

    public String getDocumentname() {
        return documentname;
    }

    public final long getTimecreated() {
        return timecreated;
    }
    
    public final String getValue() {
        return this.value;
    }

    @Override
    public final String toString() {
        return this.value;
    }

    public String getSummary() {
        return summary;
    }
}
