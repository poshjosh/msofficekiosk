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

package com.looseboxes.msofficekiosk.functions.admin;

import com.bc.socket.io.messaging.data.Devicedetails;
import com.looseboxes.msofficekiosk.Cache;
import com.looseboxes.msofficekiosk.net.Rest;
import com.looseboxes.msofficekiosk.security.PreconditionLogin;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import java.awt.Component;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.Border;

/**
 * @author Chinomso Bassey Ikwuagwu on May 8, 2019 4:24:42 AM
 */
public class GetDevicedetailsList implements Supplier<JList<Devicedetails>>{

    private static final Logger LOG = Logger.getLogger(GetDevicedetailsList.class.getName());

    //@todo make a property
    private final SimpleDateFormat df = new SimpleDateFormat("dd MMM yy HH:mm");

    private final AppUiContext uiContext;

    private final PreconditionLogin preconditionLogin;
    
    private final Cache cache;

    public GetDevicedetailsList(AppUiContext uiContext, PreconditionLogin preconditionLogin, Cache cache) {
        this.uiContext = Objects.requireNonNull(uiContext);
        this.preconditionLogin = Objects.requireNonNull(preconditionLogin);
        this.cache = Objects.requireNonNull(cache);
    }
    
    @Override
    public JList<Devicedetails> get() {
        
        preconditionLogin.validate("Login", this);
        
        final Collection<Devicedetails> devicedetailsList;
        try{
            devicedetailsList = cache.getListFromJson(Rest.RESULTNAME_DEVICEDETAILS, Devicedetails.class, Collections.EMPTY_LIST);
        }catch(ParseException | IOException e) {
            throw new RuntimeException(e);
        }

        LOG.log(Level.FINER, "Devicedetails: {0}", devicedetailsList);

        final JList<Devicedetails> list = new JList<>(devicedetailsList.toArray(new Devicedetails[0]));

        list.setCellRenderer(new DefaultListCellRenderer(){
            
            //@UI border
            private final Border border = BorderFactory.createEmptyBorder ( 8, 8, 8, 8 );
            
            @Override
            public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus){
                
                final JLabel label = (JLabel)super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                
                label.setBorder(border);
                
                if(value instanceof Devicedetails) {
                    final Devicedetails dd = (Devicedetails)value;
                    final String text = getDisplayValue(dd);
                    label.setText(text);
                }
                
                return label;
            }
        });

        list.setFont(uiContext.getAwtFont());

        return list;
    }
    
    public String getDisplayValue(Devicedetails dd) {
        final String text = dd.getUsername() + " @ [" + dd.getDesignation() + "] " + 
                dd.getIpaddress() + " last seen: " + df.format(new Date(dd.getTimestamp()));
        return text;
    }

    public String getHtmlDisplayValue(Devicedetails dd) {
        final String text = "<html>" + dd.getUsername() + " @ [" + dd.getDesignation() + "] " + 
                dd.getIpaddress() + " <small>last seen: " + df.format(new Date(dd.getTimestamp())) + "</small></html>";
        return text;
    }
}
