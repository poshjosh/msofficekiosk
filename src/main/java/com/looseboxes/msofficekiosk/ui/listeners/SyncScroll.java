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

package com.looseboxes.msofficekiosk.ui.listeners;

import java.util.Objects;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Scrollable;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 14, 2019 9:16:31 PM
 */
public class SyncScroll extends SelectionAdapter {

    private final Scrollable lhs;
    
    private final Scrollable rhs;

    public SyncScroll(Scrollable lhs, Scrollable rhs) {
        this.lhs = Objects.requireNonNull(lhs);
        lhs.getVerticalBar().addSelectionListener(SyncScroll.this);
        this.rhs = Objects.requireNonNull(rhs);
        rhs.getVerticalBar().addSelectionListener(SyncScroll.this);
    }
    
    @Override
    public void widgetSelected(SelectionEvent e) {
        if(e.getSource().equals(this.rhs.getVerticalBar())){
            onTreeVerticalScrollSelected(this.rhs, this.lhs);
        }else if(e.getSource().equals(this.lhs.getVerticalBar())){
            onTreeVerticalScrollSelected(this.lhs, this.rhs);
        }
    }

    private void onTreeVerticalScrollSelected(Scrollable target, Scrollable other){
        other.getVerticalBar().setSelection(target.getVerticalBar().getSelection());
    }
}
