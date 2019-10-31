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

import com.looseboxes.msofficekiosk.ui.admin.AdminUi;
import com.looseboxes.msofficekiosk.ui.exam.ExamUi;
import com.looseboxes.msofficekiosk.ui.listeners.LifeCycleAdapter;
import com.looseboxes.msofficekiosk.ui.listeners.LifeCycleListener;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 28, 2019 5:18:22 PM
 */
public class AdminUiLauncher extends AbstractUiLauncher<AdminUi> {

    private static final Logger LOG = Logger.getLogger(AdminUiLauncher.class.getName());

    private final AdminUi adminUI;
    
    public AdminUiLauncher(AdminUi adminUI) { 
        this.adminUI = Objects.requireNonNull(adminUI);
    }

    /**
     * If the SWT is not displayed immediately, the program exits.
     * Once the SWT shell is displayed the UI loops waiting for the shell to be 
     * disposed, hence no action after displaying the shell will be called till 
     * the shell is disposed. Hence we configure the SWT user interface and also
     * trigger the callback after displaying the SWT user interface.
     * @param callback 
     */
    @Override
    public void initUi(UiInitializationCallback callback) { 
//        adminUI.addLifeCycleListener(new ConfigureUiOnShow(uiConfigurer, display, examUi){
//            @Override
//            public void onShown(LifeCycleListener.Event event) {
//                super.onShown(event);
//                callback.onUiInitialized(examUi);
//            }
//        });
        adminUI.addLifeCycleListener(new LifeCycleAdapter<ExamUi>(){
            @Override
            public void onShown(LifeCycleListener.Event<ExamUi> event) {
                try{
                    LOG.info("Done displaying UI");

                    callback.onUiInitialized(adminUI);

                }catch(RuntimeException e) {
                    onLaunchFailed(e);
                }
            }
        });

        try{

            LOG.info("Displaying UI");
            showUi(adminUI);

        }finally{
            shutdownAndExit();
        }
    }
}
