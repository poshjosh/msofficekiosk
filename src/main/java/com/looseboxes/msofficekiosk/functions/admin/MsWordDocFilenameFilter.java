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

package com.looseboxes.msofficekiosk.functions.admin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 26, 2018 5:35:06 PM
 */
public class MsWordDocFilenameFilter implements FilenameFilter, Serializable {

    @Override
    public boolean accept(File dir, String name) {
        final String s = name.toLowerCase();
        return s.endsWith(".docx") || s.endsWith(".doc");
    }
}
