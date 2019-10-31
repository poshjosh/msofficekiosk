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

package com.looseboxes.msofficekiosk.functions.ui;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.widgets.Display;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 30, 2019 5:11:25 PM
 */
public class WaitTillDisplayIsDisposed implements Consumer<Display> {

    private static final Logger LOG = Logger.getLogger(WaitTillDisplayIsDisposed.class.getName());

    private final boolean async;

    public WaitTillDisplayIsDisposed(boolean async) {
        this.async = async;
    }
    
    @Override
    public void accept(Display display) {
        if(async) {
            display.asyncExec(() -> {
                run(display);
            });
        }else{
            display.syncExec(() -> {
                run(display);
            });
        }
    }
    
    public void run(Display display) {
        try{
            while ( ! display.isDisposed()) {
                if ( ! display.readAndDispatch()) {
                    display.sleep();
                }
            }
        }catch(RuntimeException e) {
            LOG.log(Level.WARNING, "Exception waiting on display: " + display + 
                    ". Exception will be re-thrown", e);
            throw e;
        }
    }
}
