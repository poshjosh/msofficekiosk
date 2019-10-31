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

package com.looseboxes.msofficekiosk.popups;

import java.util.Objects;
import java.util.function.BiConsumer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on Jan 24, 2019 9:50:32 PM
 */
public class ShowWarningMessageViaShell implements BiConsumer<Shell, String> {

    private final Display display;

    public ShowWarningMessageViaShell(Display display) {
        this.display = Objects.requireNonNull(display);
    }
    
    @Override
    public void accept(Shell sh, String message) {
        if(!sh.isDisposed()) {
            new ShowWarningMessage(sh).apply(message);
        }else{
            final Shell msgShell = new Shell(display);
            try{
                new ShowWarningMessage(msgShell).apply(message);
            }finally{
                msgShell.dispose();
            }
        }
    }
}
