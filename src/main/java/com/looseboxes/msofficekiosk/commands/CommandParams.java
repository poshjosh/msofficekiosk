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

package com.looseboxes.msofficekiosk.commands;

import java.io.File;
import org.eclipse.swt.ole.win32.OleClientSite;
import com.looseboxes.msofficekiosk.ui.exam.ExamUi;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 23, 2019 3:16:14 PM
 */
public interface CommandParams {
    String APP_UI = ExamUi.class.getName();
    String DOCUMENT_NAME = "DOCUMENT_NAME";
    String FILE = File.class.getName();
    String OLECLIENTSITE = OleClientSite.class.getName();
}
