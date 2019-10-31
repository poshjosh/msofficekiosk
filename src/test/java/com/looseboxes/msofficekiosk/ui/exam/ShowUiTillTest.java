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
package com.looseboxes.msofficekiosk.ui.exam;

import com.looseboxes.msofficekiosk.ui.exam.ShowUiTill;
import com.bc.config.Config;
import com.bc.ui.UIContext;
import com.looseboxes.msofficekiosk.Main;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigFactoryImpl;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.ui.AppUiContextImpl;
import com.looseboxes.msofficekiosk.ui.UiImpl;
import org.junit.Test;

/**
 * @author Josh
 */
public class ShowUiTillTest {
    
    public ShowUiTillTest() { }
    
    /**
     * Test of run method, of class ShowUiTill.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        try{
            final int advanceMilli = 5_000;
            final String string = "<html><br/><b>Test Name: </b>RIC International Relations." +
                    "<br/><br/><b>Duration: </b> 120 minutes</html>";
            final ConfigFactory configFactory = new ConfigFactoryImpl(Main.DIR_HOME);
            final Config config = configFactory.getConfig(ConfigService.APP_UI);
            final UIContext uiContext = new AppUiContextImpl(config, () -> new UiImpl());
            final ShowUiTill instance = new ShowUiTill(uiContext, string, (System.currentTimeMillis() + advanceMilli));
            final String threadName = this.getClass().getSimpleName() + "_Thread";
            final Thread thread = new Thread(instance, threadName);
            thread.setDaemon(false);

            System.out.println("Starting");
            thread.start();

            System.out.println("Waiting");
            thread.join(advanceMilli);
            
            final int oneMilli = 1000;
            for(int i=0; i < (advanceMilli / oneMilli); i++) {
                Thread.sleep(oneMilli);
            }
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
