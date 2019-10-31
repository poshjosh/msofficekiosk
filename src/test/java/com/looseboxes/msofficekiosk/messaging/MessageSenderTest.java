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
package com.looseboxes.msofficekiosk.messaging;

import com.looseboxes.msofficekiosk.messaging.MessageSender;
import com.bc.elmi.pu.entities.Message;
import com.bc.elmi.pu.entities.User;
import com.bc.elmi.pu.enums.MimetypeEnum;
import com.looseboxes.msofficekiosk.TestBase;
import com.looseboxes.msofficekiosk.net.Rest;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Josh
 */
public class MessageSenderTest extends TestBase{
    
    public MessageSenderTest() { }
    
    public MessageSender getInstance() {
        return getMessageSender();
    }
    
    public Message getMessage() {
        final Message m = new Message();
        m.setContent("This is the content of the first test message dated: " + new Date());
        m.setMimetype(MimetypeEnum.text_html.getEntity());

        final Object id = getLoginManager().validateUserId();
        final User sender = new User((Integer)id);
        m.setSender(sender);

        m.setSubject("First Test Message dated: " + new Date());
        return m;
    }

    /**
     * Test of send method, of class MessageSender.
     */
//    @Test
    public void testSend_3args_1() throws Exception {
        System.out.println("send");
        final Message m = getMessage();
        System.out.println("Sending:: " + getMapper().toJsonString(m));
        final Set<String> usernames = new HashSet<>(Arrays.asList(USR));
        final String endpoint = Rest.ENDPOINT_TRANSFER_MESSAGE;
        final MessageSender instance = getInstance();
        
        instance.send(m, usernames, endpoint, (r) -> {
            System.out.println("Received:: " + r);
        });
    }
}
