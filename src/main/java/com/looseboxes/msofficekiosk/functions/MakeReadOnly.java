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

package com.looseboxes.msofficekiosk.functions;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiConsumer;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.Variant;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2018 9:11:55 PM
 */
public class MakeReadOnly implements BiConsumer<OleClientSite, Boolean>, Serializable {

    /**
     * Sets a boolean that indicates whether a document is final (read only)
     * <p>http://msdn.microsoft.com/en-us/library/office/ff838930(v=office.15).aspx</p>
     * 
     * IMPORTANT: Call after OleClientSite.doVerb(), otherwise Application.ActiveDocument is not initialized
     *
     * @param clientSite
     * @param readOnly
     * @see https://stackoverflow.com/questions/22654487/how-to-open-a-word-document-in-read-only-mode-into-swt-shell
     */
    @Override
    public void accept(OleClientSite clientSite, Boolean readOnly) {
        Objects.requireNonNull(clientSite);
        Objects.requireNonNull(readOnly);
        OleAutomation oleAutomation = new OleAutomation(clientSite);
        int[] ids = oleAutomation.getIDsOfNames(new String[] { "Application" }); //$NON-NLS-1$
        if (ids != null) {
            Variant variant = oleAutomation.getProperty(ids[0]);
            if (variant != null) {
                OleAutomation application = variant.getAutomation();
                ids = application.getIDsOfNames(new String[] { "ActiveDocument" }); //$NON-NLS-1$
                if (ids != null) {
                    variant = application.getProperty(ids[0]);
                    if (variant != null) {
                        OleAutomation activeDocument = variant.getAutomation();
                        ids = activeDocument.getIDsOfNames(new String[] { "Final" }); //$NON-NLS-1$
                        if (ids != null) {
                            activeDocument.setProperty(ids[0], new Variant(readOnly));
                        }
                        activeDocument.dispose();
                    }
                }
                application.dispose();
            }
        }
        oleAutomation.dispose();
    }
}
