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

package com.looseboxes.msofficekiosk.net;

import com.bc.config.Config;
import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.config.ConfigNames;
import com.looseboxes.msofficekiosk.SendMail;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.test.OpenedFileManager;
import com.looseboxes.msofficekiosk.test.TestDoc;
import com.looseboxes.msofficekiosk.test.TestDocKey;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 22, 2018 8:01:29 AM
 */
public class SendTestDocsByEmail implements SendTestDocs{

    private transient static final Logger LOG = Logger.getLogger(SendTestDocsByEmail.class.getName());
    
    private final AppContext context;
    
    private final AppUiContext uiContext;
    
    public SendTestDocsByEmail(AppContext context, AppUiContext uiContext) {
        this.context = Objects.requireNonNull(context);
        this.uiContext = Objects.requireNonNull(uiContext);
    }
    
    public String getSubject(Map<TestDoc, File> attachments) {
        final OpenedFileManager ofm = context.getOpenedFileManager();
        final TestDocKey key = attachments.keySet().stream().limit(1).map((td) -> ofm.getKey(td)).findFirst().orElse(null);
        final String subject = key.getSummary();
        return subject;
    }

    @Override
    public Boolean apply(Map<TestDoc, File> attachments) {
        try{
            
            if(attachments.isEmpty()) {
                return Boolean.FALSE;
            }
            
            send(attachments);
            
            final String msg = "Done emailing "+attachments.size()+" documents";
            LOG.fine(msg);
            uiContext.getMessageDialog().showInformationMessage(msg);

            return Boolean.TRUE;
            
        }catch(MessagingException | InvalidConfigurationException e) {
            final String msg = "Failed to submit document(s) via email. Reason: " + e.getLocalizedMessage();
            LOG.log(Level.WARNING, msg, e);
            uiContext.getMessageDialog().showWarningMessage(msg);
            return Boolean.FALSE;
        }
    }
    
    @Override
    public void send(Map<TestDoc, File> attachments) 
            throws MessagingException, InvalidConfigurationException{

        try{
            
            uiContext.showProgressBarPercent("..Emailing docus", -1);
            
            final Config config = context.getConfig(ConfigService.APP_PROTECTED);
            final String sender = config.getString(ConfigNames.EMAIL_SENDER_ADDRESS);
            final String senderPass = config.getString(ConfigNames.EMAIL_SENDER_PASSWORD);

            LOG.finer(() -> "Email sender: " + sender);

            final String [] recipientsArr = config.getArray(ConfigNames.OUTPUTFILE_SEND_TO_EMAIL_LIST);

            if(sender == null || sender.isEmpty() || senderPass == null || senderPass.isEmpty() ||
                    recipientsArr == null || recipientsArr.length < 1) {

                final String msg = "File transfer by email not configured properly";

                throw new InvalidConfigurationException(msg);
            }

            this.sendEmail(sender, senderPass, getSubject(attachments), attachments, recipientsArr);
            
        }finally{
        
            uiContext.showProgressBarPercent(100);
        }
    }

    private void sendEmail(String sender, String senderPass, String subject, 
            Map<TestDoc, File> attachments, String [] recipientsArr) 
            throws MessagingException {

        final SendMail sendMail = new SendMail(sender, senderPass);

        sendMail.execute(
                "text/plain", 
                subject, 
                "Attached herewith, please find " + attachments.size() + 
                        " documents with respect to the above subject matter", 
                Arrays.asList(recipientsArr), 
                attachments.values());
    }
}
