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

import java.util.function.Consumer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

/**
 * @author Chinomso Bassey Ikwuagwu on May 22, 2018 5:17:53 PM
 */
public class CenterOnScreen implements Consumer<Control> {

    @Override
    public void accept(Control control) {
        final Rectangle bounds = control.getDisplay().getMonitors()[0].getBounds();
        this.accept(control, bounds);
    }
    
    public void accept(Control control, Rectangle bounds) {
        final Point size = control.computeSize(-1, -1);
        control.setBounds((bounds.width-size.x)/2, (bounds.height-size.y)/2, size.x, size.y);
    }
}
