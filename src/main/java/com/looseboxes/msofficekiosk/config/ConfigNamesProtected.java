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

package com.looseboxes.msofficekiosk.config;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2018 4:18:27 AM
 */
public interface ConfigNamesProtected {
    
    String ZONE_OFFSET = "Time Zone";
    
    String LOCALE = "Locale";
    
    String OUTPUTFILE_ENCRYPT_USERNAME = "Encrypt username in filename";

    String OUTPUTFILE_INCLUDEOLEINFO = "Include meta-data in output files";

    String TIMER_INTERVAL_SECONDS = "Timer Interval (in seconds)";
    
    String CONVERT_TO_PDF_BEFORE_SUBMIT = "Convert to PDF before submitting";
    
    String ZIP_BEFORE_SUBMIT = "Zip multiple files before submitting";

    String DELETE_DOCUMENTS_AFTER_SUBMIT = "Delete documents after submitting";

    String OUTPUTFILE_SEND_TO_EMAIL_LIST = "Email addresses to send documents";

    String EMAIL_SENDER_ADDRESS = "Email sender address";

    String EMAIL_SENDER_PASSWORD = "Email sender password";
    
    String IP_ADDRESSES_TO_SEND_DOCUMENTS = "IP Addresses to send documents";
    
    String SOCKET_PORT_NUMBER = "Socket Port Number";
    
    String SERVER_PROTOCOL = "Server Protocol";

    String SERVER_PORT_NUMBER = "Server Port Number";

    String BEEP_ON_INCOMING_MESSAGE_RECEIVED = "Beep on incoming message received";
}
