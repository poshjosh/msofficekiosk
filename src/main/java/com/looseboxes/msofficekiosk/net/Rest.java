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

import com.bc.elmi.pu.entities.Test;
import com.bc.elmi.pu.entities.User;
import com.bc.socket.io.messaging.data.Devicedetails;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 25, 2019 5:50:59 PM
 */
public interface Rest {
    String ENDPOINT_GET_DEVICEDETAILS = "/get/Devicedetails";
    String ENDPOINT_PUT_DEVICEDETAILS = "/put/Devicedetails";
    String UPDATE_DEVICE_DETAILS = "/update/Devicedetails";
    String ENDPOINT_GET_TEST = "/get/Test";
    String ENDPOINT_GET_TESTS = "/get/Tests";
    String ENDPOINT_COMBO = "/combo";
    String ENDPOINT_TRANSFER_MESSAGE = "/transfer/Message";
    String ENDPOINT_SEND_MESSAGE = "/send/Message";
    String ENDPOINT_OPEN_MESSAGE = "/open/Message";
    String ENDPOINT_UPLOAD_DOCUMENT = "/upload/Document";
    String ENDPOINT_DOWNLOAD_DOCUMENT = "/download/Document";
    String ENDPOINT_OPEN_FILE = "/open/File";
    String ENDPOINT_MARK_FILE = "/mark/File";
    
    String RESULTNAME_USER = User.class.getSimpleName();
    String RESULTNAME_DEVICEDETAILS = Devicedetails.class.getSimpleName();
    String RESULTNAME_TESTS = Test.class.getSimpleName() + "s";
}
