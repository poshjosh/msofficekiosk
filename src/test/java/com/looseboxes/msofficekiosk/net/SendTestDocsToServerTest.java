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
package com.looseboxes.msofficekiosk.net;

import com.looseboxes.msofficekiosk.net.SendTestDocsToServer;
import com.looseboxes.msofficekiosk.net.SendTestDocs;
import com.bc.io.FileIO;
import com.looseboxes.msofficekiosk.TestBase;
import com.looseboxes.msofficekiosk.TestUtil;
import com.looseboxes.msofficekiosk.test.TestDoc;
import com.looseboxes.msofficekiosk.test.TestDocImpl;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.BeforeClass;

/**
 * @author Josh
 */
public class SendTestDocsToServerTest extends TestBase{
    
    private static SendTestDocs delegate;
    
    public SendTestDocsToServerTest() { }
    
    @BeforeClass
    public static void setUpClass() { 
        
        TestBase.setUpClass();
        
        delegate = new SendTestDocsToServer(
            TestBase.getHttpClient(), 
            TestBase.getConfigFactory(),
            TestBase.getDocumentBuilder(), 
            TestBase.getCredentialsSupplier(),
            TestBase.getMapper(),
            TestBase.getUiContext());        
    }
    
//    public void testSendDirectly() { }

    /**
     * Test of apply method, of class SendTestDocsToServer.
     */
//    @Test
    public void testApply() {
        try{
            
            System.out.println("apply");

            final File [] arr = TestUtil.selectFiles(extension);

            final Map<TestDoc, File> attachments = buildPayload(arr);

            final Boolean result = delegate.apply(attachments);
            
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }
    
    private Map<TestDoc, File> buildPayload(File [] arr) throws IOException {
        final Map<TestDoc, File> output = new HashMap<>(arr.length, 1.0f);
        final com.bc.elmi.pu.entities.Test test = new com.bc.elmi.pu.entities.Test();
        test.setTestid(22);
        test.setTestname("RIC Written Brief");
        test.setStarttime(new Date()); // 05/04/2019 05:53 AM
        test.setDurationinminutes(2);
        for(File file : arr) {
            final TestDoc td = new TestDocImpl(test, file.getName());
            final File f = getTestFileProvider().getFile(td);
            new FileIO().copy(false, file, f, false);
            output.put(td, f);
        }
        return output;
    }
}
