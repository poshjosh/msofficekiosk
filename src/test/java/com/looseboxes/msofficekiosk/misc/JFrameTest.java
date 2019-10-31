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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 23, 2018 12:37:39 PM
 */
public class JFrameTest {

    public static void main(String [] args) {
        
        final JFrame frame = new JFrame("Hallelujah");
        frame.setAlwaysOnTop(true);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//        frame.setState(Frame.NORMAL);
        frame.setResizable(false);
//        frame.setType(Window.Type.NORMAL);

        final JTextArea textArea = new JTextArea();
        textArea.setText("Jesus is Lord");

        positionFullScreen(textArea);
        frame.getContentPane().add(textArea);
        positionFullScreen(frame);

        try{
            
            frame.pack();
            frame.setVisible(true);

            for(int i=0; i<15; i++) {
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }finally{
        
            frame.setVisible(false);
            frame.dispose();
        }
    }

    public static boolean positionFullScreen(Component c) {
        try{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension custom = new Dimension(screenSize.width, screenSize.height);
            c.setLocation(0, 0);
            c.setSize(custom);
            c.setPreferredSize(custom);
            return true;
        }catch(Exception ignored) { 
            return false;
        }
    }
}
