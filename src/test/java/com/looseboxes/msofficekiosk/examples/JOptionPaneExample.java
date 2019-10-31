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

package com.looseboxes.msofficekiosk.examples;

import javax.swing.JOptionPane;

/**
 * @author Chinomso Bassey Ikwuagwu on May 6, 2019 8:30:41 PM
 */
public class JOptionPaneExample {

    public static void main(String... args) {
    
        final Object [] arr = {"Option A", "Option B", "Option C"};
        final int o = JOptionPane.showOptionDialog(null, "message", "title", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, 
                null, arr, arr[0]);
        System.out.println(o);
    }
}
