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

package com.looseboxes.msofficekiosk.ui.selection;

import com.bc.socket.io.messaging.data.Devicedetails;
import com.looseboxes.msofficekiosk.functions.admin.GetDevicedetailsList;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.validators.Precondition;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.swing.JList;
import org.eclipse.swt.widgets.Display;

/**
 * @author Chinomso Bassey Ikwuagwu on May 8, 2019 2:08:53 PM
 */
public class ListSelectionManagerForDevicedetails 
         extends AbstractUiSelectionManager<JList<Devicedetails>, Devicedetails>{

    private final GetDevicedetailsList getDevicedetails;
    
    public ListSelectionManagerForDevicedetails(AppUiContext uiContext, 
            Display display, GetDevicedetailsList getDevicedetails) {
    
        this(uiContext, display, getDevicedetails, (selected) -> true, Collections.EMPTY_LIST);
    }
    
    public ListSelectionManagerForDevicedetails(AppUiContext uiContext, 
            Display display, GetDevicedetailsList getDevicedetails,
            Precondition<Devicedetails> precondition, List<SelectionAction<Devicedetails>> actions) {
        super(uiContext, display, precondition, actions);
        this.getDevicedetails = Objects.requireNonNull(getDevicedetails);
    }

    @Override
    protected JList<Devicedetails> createSelectionUi() {
        return getDevicedetails.get();
    }

    @Override
    public String getDisplayValue(Devicedetails dd, int maxLen) {
        final String m = getDevicedetails.getDisplayValue(dd);
        return m.length() <= maxLen ? m : m.substring(0, maxLen);
    }

    @Override
    public String getHtmlDisplayValue(Devicedetails dd) {
        return getDevicedetails.getHtmlDisplayValue(dd);
    }

    @Override
    public List<Devicedetails> getSelectedValues(JList<Devicedetails> ui) {
        return ui.getSelectedValuesList();
    }
}
