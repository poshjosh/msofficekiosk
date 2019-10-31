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

package com.looseboxes.msofficekiosk.commands;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.logging.Logger;
import org.eclipse.swt.widgets.Display;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 23, 2019 2:36:15 PM
 */
public abstract class AbstractCommandWithPreCondition implements Callable<Boolean>, Runnable {

    private static final Logger LOG = Logger.getLogger(AbstractCommandWithPreCondition.class.getName());

    private final Display display;
    
    private final Predicate<Display> preCondition;
    
    public AbstractCommandWithPreCondition(Display display) {
        this(display, (displayArg) -> true);
    }
    
    public AbstractCommandWithPreCondition(Display display, Predicate<Display> preCondition) {
        this.display = Objects.requireNonNull(display);
        this.preCondition = Objects.requireNonNull(preCondition);
    }

    @Override
    public Boolean call() {
        
        LOG.entering(this.getClass().getName(), "call()");

        if(display == null || preCondition.test(display)) {
      
            run();
            
            return Boolean.TRUE;
            
        }else{
            
            return Boolean.FALSE;
        }
    }  
    
    public Display getDisplay() {
        return display;
    }
}
