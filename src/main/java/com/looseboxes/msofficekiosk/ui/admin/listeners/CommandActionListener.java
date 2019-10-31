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

package com.looseboxes.msofficekiosk.ui.admin.listeners;

import com.looseboxes.msofficekiosk.ui.UI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2018 1:24:36 PM
 */
public class CommandActionListener<R> implements ActionListener, Serializable {

    private transient static final Logger LOG = Logger.getLogger(CommandActionListener.class.getName());
    
    private final UI ui;
    
    private final Predicate<ActionEvent> precondition;
    
    private final Callable<R> command;

    public CommandActionListener(UI ui, Callable<R> command) { 
        this(ui, (ae) -> true, command);
    }
    
    public CommandActionListener(UI ui, Predicate<ActionEvent> precondition, Callable<R> command) {
        this.ui = Objects.requireNonNull(ui);
        this.precondition = Objects.requireNonNull(precondition);
        this.command = Objects.requireNonNull(command);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        
        LOG.entering(this.getClass().getName(), "actionPerformed(ActionEvent)");
        
        final String commandName = command.getClass().getSimpleName().toUpperCase();
        try{
            if(this.precondition.test(event)) {
                LOG.fine(() -> "Executing command: " + commandName);
                command.call();
            }else{
                LOG.fine(() -> "Precondition failed. Will not be executed, command: " + commandName);
            }
        }catch(Exception e) {
            final String message = "Failed to execute Command: " + commandName;
            LOG.log(Level.WARNING, message, e);
            ui.getMessageDialog().showWarningMessage(message);
        }
    }
}
