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
package com.looseboxes.msofficekiosk.misc;

import com.looseboxes.msofficekiosk.SendMail;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Josh
 */
public class SendMailTest {
    
    public SendMailTest() { }

    /**
     * Test of execute method, of class SendMail.
     */
//    @Test
    public void testExecute() throws Exception {

        System.out.println("execute");
        
        final String contentType = "text/html";
        
        final String subject = "This is the Subject of the message sent on: " + 
                ZonedDateTime.now() + " by: " + System.getProperty("user.name");
        
        final List<String> recipients = Arrays.asList("posh.bc@gmail.com");
        
        final Path firstTextFile = Files.walk(Paths.get(System.getProperty("user.home")))
                .filter((path) -> path.toString().endsWith(".txt")).findAny().orElse(null);
        
        final Set<String> attachments = firstTextFile == null ? 
                Collections.EMPTY_SET : Collections.singleton(firstTextFile.toString());
        
        final String message = "<h2>Jesus is Lord of all</h2><p>Message content type: " + contentType + "</p>" + 
                "<p>Message has attachments: " + attachments + "</p>";
        
        final SendMail instance = new SendMail("amex@looseboxes.com", "4xT-eeSw");
        
        instance.execute(contentType, subject, message, recipients, attachments);
    }
}
