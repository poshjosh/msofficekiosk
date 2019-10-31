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

import java.util.function.Function;
import org.eclipse.swt.SWT;

/**
 * @author Chinomso Bassey Ikwuagwu on May 14, 2018 3:02:05 PM
 */
public class GetFontStyleFromName implements Function<String, Integer> {

    @Override
    public Integer apply(String name) {
        switch(name) {
            case "BOLD": return SWT.BOLD;
            case "ITALIC": return SWT.ITALIC;
            case "BOLD-ITALIC": return SWT.BOLD | SWT.ITALIC;
            case "ITALIC-BOLD": return SWT.ITALIC | SWT.BOLD;
            default: return SWT.NORMAL;
        }
    }
}
