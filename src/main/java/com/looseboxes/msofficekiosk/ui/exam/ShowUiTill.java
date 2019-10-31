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

import com.bc.ui.UIContext;
import com.looseboxes.msofficekiosk.functions.GetTimeDisplay;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * @author Chinomso Bassey Ikwuagwu on May 2, 2019 6:44:23 PM
 */
public class ShowUiTill implements Runnable {

    private static final Logger LOG = Logger.getLogger(ShowUiTill.class.getName());
    
    private final UIContext uiContext;

    private final long targetTime;
    
    private final AtomicBoolean windowClosed = new AtomicBoolean(false);
    
    private JFrame frame;
    private JPanel panel;
    private JLabel iconLabel;
    private JLabel timerLabel;
    private JLabel textLabel;
    
    public ShowUiTill(UIContext uiContext, String text, long targetTime) {
        this.uiContext = Objects.requireNonNull(uiContext);
        this.targetTime = targetTime;
        SwingUtilities.invokeLater(() -> {
            createAndShowUi(text);
        });
    }
    
    private void createAndShowUi(String text) {
        Objects.requireNonNull(text);
        
        //@todo use config
        final Font font = Font.decode("Arial-PLAIN-20");

        frame = new JFrame();
        frame.setFont(font);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                windowClosed.compareAndSet(false, true);
            }
        });

        panel = new JPanel();
        panel.setFont(font);
        
        iconLabel = new JLabel();
        //@todo global UI configurer for icon, font etc
        uiContext.getImageIconOptional().ifPresent((imageIcon) -> {
            frame.setIconImage(imageIcon.getImage());
            iconLabel.setIcon(imageIcon);
            iconLabel.setFont(font);
            iconLabel.setOpaque(true);
            iconLabel.setBackground(Color.WHITE);
            iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        });

        timerLabel = new JLabel("<html><b>Time Left: <b/> 0</html>");
        timerLabel.setFont(font);
        timerLabel.setOpaque(true);
        timerLabel.setBackground(Color.WHITE);
        
        textLabel = new JLabel(text);
        textLabel.setFont(font);

        addComponents(panel, iconLabel, timerLabel, textLabel);
        
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }
    
    public void addComponents(JComponent parent, JLabel iconLabel, JLabel timerLabel, JLabel textLabel) {
//        panel.setBackground(Color.WHITE);
        parent.add(iconLabel);
        parent.setLayout(new GridLayout(3, 1));
        parent.add(timerLabel);
        parent.add(textLabel);
    }
    
    @Override
    public synchronized void run() {
        try{
            
            final int one = 1000;
            
            long timeLeftMillis;
            
            while((timeLeftMillis = (targetTime - System.currentTimeMillis())) > one) {
                
                if(windowClosed.get()) {
                    break;
                }

                final String text = "<html><b>Time Left: </b>" + new GetTimeDisplay()
                        .apply(timeLeftMillis, ZoneOffset.systemDefault()) + "</html>";
                LOG.finer(text);
                
                SwingUtilities.invokeLater(() -> {
                    timerLabel.setText(text);
                });
                
                wait(one);
            }
        }catch(InterruptedException e) {
            
            LOG.log(Level.WARNING, null, e);
        
            Thread.currentThread().interrupt();
            
        }finally{

            notifyAll();

            frame.dispose();
        }
    }

    public UIContext getUiContext() {
        return uiContext;
    }

    public long getTargetTime() {
        return targetTime;
    }

    public boolean isWindowClosed() {
        return windowClosed.get();
    }

    public JFrame getFrame() {
        return frame;
    }

    public JPanel getPanel() {
        return panel;
    }

    public JLabel getTimerLabel() {
        return timerLabel;
    }

    public JLabel getTextLabel() {
        return textLabel;
    }
}
