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

package com.looseboxes.msofficekiosk;

import com.bc.mail.EmailBuilder;
import com.bc.mail.EmailBuilderImpl;
import com.bc.mail.MimeMessageBuilderImpl;
import com.bc.mail.config.MailConfig;
import com.bc.mail.config.MailConnectionProperties;
import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;

/**
 * @author Chinomso Bassey Ikwuagwu on May 15, 2018 9:51:34 AM
 */
public class SendMail implements Serializable {

    private static final Logger LOG = Logger.getLogger(SendMail.class.getName());

    private final String sender;
    private final char [] senderPass;

    private final MailConfig mailConfig;
    
    public SendMail(String sender, String senderPass) {
        this(MailConfig.builder().connectionTimeout(20_000).timeout(60_000).build(), 
                sender, 
                senderPass, 
                true);
    }
    
    public SendMail(MailConfig mailConfig, String sender, String senderPass, boolean outgoing) {
        this.mailConfig = Objects.requireNonNull(mailConfig);
        this.sender = sender.trim();
        this.senderPass = senderPass == null ? null : senderPass.trim().toCharArray();

        final boolean ssl = senderPass != null;
        final MailConnectionProperties connProps = mailConfig.getConnectionProperties(sender, ssl, outgoing);
        LOG.fine(() -> "Connection proprties: " + connProps + 
                "\nProperties: " + mailConfig.getProperties(connProps, ssl));
    }
    
    public void execute(
            String contentType, String subject, String message, 
            List<String> recipients, Collection attachments) 
            throws MessagingException {
        
        if(recipients.isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        LOG.fine(() -> "Sender: " + sender + ", content type: " + contentType + 
                "\nSubject: " + subject + "\nRecipients: " + recipients + 
                "\nAttachments: " + attachments);
        
        try{
            
            final boolean html = contentType.toLowerCase().contains("html");
            
            final MultiPartEmail email;
            
            if(html) {
                email = new HtmlEmail();
            }else{
                email = new MultiPartEmail();
            }
            
//            email.setHostName();
//            email.setSmtpPort();
//            email.setSslSmtpPort();

            final EmailBuilder messageBuilder = new EmailBuilderImpl(mailConfig);

            for(String to : recipients) {
                messageBuilder.addTo(email, to, null);
            }

            for(Object att : attachments) {
                email.attach(new File(att.toString()));
            }

            messageBuilder.build(email, sender, new String(senderPass), senderPass != null, true);

            email.setSubject(subject);
            if(html) {
                ((HtmlEmail)email).setHtmlMsg(message);
            }else{
                email.setMsg(message);
            }
            
            LOG.finer(() -> "Host name: " + email.getHostName() +
                    ", smtp port: " + email.getSmtpPort() + 
                    ", Ssl smtp port: " + email.getSslSmtpPort()); 

            final String messageId = email.send(); 

            LOG.fine(() -> "Successfully sent mail message with ID: " + messageId);            

        }catch(EmailException e) {
            throw new MessagingException(e.getLocalizedMessage(), e);
        }
    }

    public void executeOld(
            String contentType, String subject, String message, 
            List<String> recipients, Set attachments) 
            throws MessagingException {
        
        if(recipients.isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        try(final MimeMessageBuilderImpl messageBuilder = new MimeMessageBuilderImpl(mailConfig)) {
        
            for(String to : recipients) {
                messageBuilder.addTo(to, null);
            }
            
            attachments.forEach((att) -> {
                messageBuilder.addAttachment(att.toString());
            });
            
            final MimeMessage mimeMessage = messageBuilder
                    .contentType(contentType)
                    .from(sender, senderPass)
                    .message(message)
                    .subject(subject)
                    .build();
            
            Transport.send(mimeMessage);
            
            LOG.fine(() -> "Mail successfully sent.");            
        }
    }
}
