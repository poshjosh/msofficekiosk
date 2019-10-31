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

import com.looseboxes.msofficekiosk.document.DocumentStore;
import com.bc.elmi.pu.entities.Document;
import com.bc.elmi.pu.entities.Testsetting;
import com.looseboxes.msofficekiosk.TestBase;
import static com.looseboxes.msofficekiosk.TestBase.getTests;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import org.junit.Test;

/**
 *
 * @author Josh
 */
public class DocumentStoreTest extends TestBase{
    
    public DocumentStoreTest() { }

    public DocumentStore getInstance() {
        return getDocumentStore();
    }
    
    /**
     * Test of fetchFiles method, of class TestsettingsImpl.
     */
    @Test
    public void testFetch() {
        System.out.println("fetch");
        final DocumentStore instance = getInstance();
        final List<com.bc.elmi.pu.entities.Test> tList = getTests().get();
        System.out.println("Test List: " + tList);
        final com.bc.elmi.pu.entities.Test t = tList.stream().findFirst().orElse(null);
        System.out.println("Test: " + t);
        if(t == null) {
            JOptionPane.showMessageDialog(null, "0 Test Entitie(s) found in Cache\nAssociated Test will not be run", 
                    "Entity Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }
        final List<Testsetting> ttl = t.getTestsettingList();
        if(ttl == null || ttl.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Test Entity has 0 Test Settings\nAssociated Test will not be run", 
                    "No Test Settings", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final List<Document> docList = ttl.stream().map((tt) -> tt.getTestsetting()).collect(Collectors.toList());

        final Map<Document, Optional<File>> result = instance.fetch(docList);

        Desktop d = null;
        if(Desktop.isDesktopSupported()) {
            d = Desktop.getDesktop();
        }
        for(Document doc : result.keySet()) {
            final Optional<File> fileOptional = result.get(doc);
            if(doc == null) {
                JOptionPane.showMessageDialog(null, doc, 
                        "Test Setting has no Document", JOptionPane.WARNING_MESSAGE);
            }
            final Object loc = doc.getLocation();
            if(loc == null) {
                JOptionPane.showMessageDialog(null, doc, 
                        "Test Setting Document has no Location", JOptionPane.WARNING_MESSAGE);
            }
            if( ! fileOptional.isPresent()) {
                JOptionPane.showMessageDialog(null, "" + doc + "\nDocument location: " + loc, 
                        "Failed to download Test Setting", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            
            final File file = fileOptional.get();
            
            if(d == null || ! d.isSupported(Desktop.Action.OPEN)) {
                JOptionPane.showMessageDialog(null, file.toString(), "Can't open file", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            
            try{
                d.open(file);
            }catch(IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
