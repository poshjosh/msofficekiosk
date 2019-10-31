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

package com.looseboxes.msofficekiosk.functions.ui;

import com.looseboxes.msofficekiosk.ui.JEditorPaneFrame;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Map;
import java.util.function.BiFunction;
import javax.swing.ImageIcon;
import com.looseboxes.msofficekiosk.ui.AppUiContext;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2018 5:22:08 PM
 */
public class DisplayURL implements Serializable, BiFunction<AppUiContext, Map<String, Object>, Boolean> {
    
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String TITLE = "title";

    public DisplayURL() { }
    
    @Override
    public Boolean apply(AppUiContext uiContext, Map<String, Object> params) {
        
        final String contentType = (String)params.get(CONTENT_TYPE);
        final URL url = (URL)params.get(java.net.URL.class.getName());
        if(url == null) {
            throw new NullPointerException(java.net.URL.class.getName());
        }
        final String title = (String)params.get(TITLE);
        
        final Font font = uiContext.getAwtFont();
        
        try{
            
            final ImageIcon imageIcon = uiContext.getImageIconOptional().orElse(null);
            final Image image = imageIcon == null ? null : imageIcon.getImage();
            
            final JEditorPaneFrame frame = new JEditorPaneFrame(contentType, title, image, font);

            frame.setAlwaysOnTop(true);
            
            frame.addDefaultHyperlinkListener();
            
            uiContext.positionFullScreen(frame.getEditorPane());
            
            frame.setPage(url);
            
            frame.pack();
            frame.setVisible(true);
            
        }catch(IOException e) {
            throw new RuntimeException(e);
        }
        
        return Boolean.TRUE;
    }
}

