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

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * @author Chinomso Bassey Ikwuagwu on May 1, 2018 12:31:29 PM
 */
public class MSWordFileFilter extends FileFilter {
    
    @Override
    public boolean accept(File f) {
        final String nameLower = f.getName().toLowerCase();
        return nameLower.endsWith(".doc") || nameLower.endsWith(".docx");
    }

    @Override
    public String getDescription() {
        return "MS Word Documents";
    }
}
