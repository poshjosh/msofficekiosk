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

import com.looseboxes.msofficekiosk.test.OpenedFileManager;
import com.bc.socket.io.BcSocketServer;
import com.bc.socket.io.BcSocketClient;
import com.bc.socket.io.messaging.data.Devicedetails;
import com.looseboxes.msofficekiosk.config.ConfigFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2018 4:28:34 AM
 */
public interface AppContext extends ConfigFactory{

    MsKioskSetup getSetup();
    
    Devicedetails getDevicedetails();

    boolean isAdmin();
            
    boolean isShutdown();
        
    void shutdown();
    
    void refresh();
    
    OpenedFileManager getOpenedFileManager();

    void scheduleDataUpdate();
    
    BcSocketServer startSocketServerAsync();
        
    BcSocketServer getSocketServer();
    
    BcSocketClient getSocketClient();

    boolean isServer();

    boolean isServerHost(String ip);
}
