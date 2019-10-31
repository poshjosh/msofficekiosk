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

package com.looseboxes.msofficekiosk.ui.exam;

import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.test.TestDoc;
import com.looseboxes.msofficekiosk.ui.UI;
import java.io.File;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 23, 2019 4:51:23 AM
 */
public interface ExamUi extends UI{

    AppContext getContext();
    
    Display getDisplay();

    long getDisplayDurationMillis();

    ZonedDateTime getDisplayTime();

    TestDoc getTestDoc();

    OleClientSite getOleClientSite();

    OleFrame getOleFrame();
    
    File getOutputFile();

    Shell getShell();
    
    ExamShellUi getShellUi();

    ExamUi openDocument(String documentName);

    void show(ZonedDateTime timeOffset);

    void show(long timeout, TimeUnit timeoutUnit);

    void show(ZonedDateTime timeOffset, long timeout, TimeUnit timeoutUnit);

    Font getFont();
}
