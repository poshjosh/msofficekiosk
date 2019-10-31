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

package com.looseboxes.msofficekiosk.misc;

import com.looseboxes.msofficekiosk.commands.RunTimerCommand;
import com.looseboxes.msofficekiosk.commands.TickListenerImpl;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.LoginException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * @author Chinomso Bassey Ikwuagwu on May 3, 2018 1:45:03 PM
 */
public class TimerDisplayTest {
    
    @Lazy @Autowired private ConfigFactory configFactory;
    
    @Lazy @Autowired private Display display;

//    @Test
    public void testTimerCountdown() throws IOException, LoginException {

        final Shell shell = new Shell(display);
        shell.setLayout(new RowLayout());
        final Button stopButton = new Button(shell, SWT.PUSH);
        stopButton.setText("Stop Timer");
        stopButton.setSize(100, 18);
        final Label label = new Label(shell, SWT.BORDER);
        final Font font = new Font(display, new FontData( "Arial", 14, SWT.BOLD ) );
        label.setFont(font);
        final Color colorWhite = display.getSystemColor(SWT.COLOR_WHITE);
        label.setBackground(colorWhite);
        label.setSize(100, 18);
        final int tickIntervalMillis = 1000;
        final long endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(180);
        final RunTimerCommand.TickListener tickListener = new TickListenerImpl(
                configFactory, 
                display,
                (timer) -> !label.isDisposed() && label.isVisible(), 
                (timer) -> timer.getMillisTill(endTime), 
                (text) -> {
                    label.setText(text);
                    System.out.println(text);
                }
        );
        final RunTimerCommand timer = new RunTimerCommand(display, tickIntervalMillis, tickListener);

        timer.start();

        stopButton.addListener(SWT.Selection, (Event event) -> {
            timer.stop();
        });
    
        stopButton.pack();
        label.setLayoutData(new RowData(stopButton.getSize()));
        
        shell.pack();
        shell.open();
        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }    
        }
        display.dispose();
    }
}
