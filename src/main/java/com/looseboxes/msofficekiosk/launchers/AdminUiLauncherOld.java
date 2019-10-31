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

package com.looseboxes.msofficekiosk.launchers;

import com.looseboxes.msofficekiosk.ui.admin.AdminUiOld;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 17, 2019 10:34:02 PM
 */
public class AdminUiLauncherOld extends AbstractUiLauncher<AdminUiOld> {

    private static final Logger LOG = Logger.getLogger(AdminUiLauncherOld.class.getName());

    private final AdminUiOld adminUI;
    
    public AdminUiLauncherOld(AdminUiOld adminUI) { 
        this.adminUI = Objects.requireNonNull(adminUI);
    }

    @Override
    public void initUi(UiInitializationCallback callback) { 
        if(java.awt.EventQueue.isDispatchThread()) {

            doInitUi(callback);

        }else{
            java.awt.EventQueue.invokeLater(() -> { 
                
                doInitUi(callback);
            });
        }
    }
    
    private void doInitUi(UiInitializationCallback callback) {
        
        LOG.info("Displaying UI");
        showUi(adminUI);
        LOG.info("Done displaying UI");

        callback.onUiInitialized(adminUI);
    }
}
