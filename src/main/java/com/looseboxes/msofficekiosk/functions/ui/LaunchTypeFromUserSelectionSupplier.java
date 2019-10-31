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

package com.looseboxes.msofficekiosk.functions.ui;

import com.looseboxes.msofficekiosk.launchers.LauncherFactory;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 30, 2019 5:02:54 PM
 */
public class LaunchTypeFromUserSelectionSupplier implements Supplier<LauncherFactory.Type>{

    private static final Logger LOG = Logger.getLogger(LaunchTypeFromUserSelectionSupplier.class.getName());

    @Override
    public LauncherFactory.Type get() {
        
        final LauncherFactory.Type [] values = LauncherFactory.Type.values();

        final LauncherFactory.Type selected = (LauncherFactory.Type)JOptionPane.showInputDialog(
                null, "Select user type", "Select", 
                JOptionPane.INFORMATION_MESSAGE, null, values, values[0]);
        
        LOG.log(Level.INFO, "Selected: {0}", selected);
        
        return selected == null ? LauncherFactory.Type.None : selected;
    }
}
