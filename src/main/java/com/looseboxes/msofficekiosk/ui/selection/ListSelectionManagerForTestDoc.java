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

package com.looseboxes.msofficekiosk.ui.selection;

import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.validators.Precondition;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import javax.swing.JList;
import org.eclipse.swt.widgets.Display;

/**
 * @author Chinomso Bassey Ikwuagwu on May 8, 2019 1:47:31 AM
 */
public class ListSelectionManagerForTestDoc extends AbstractUiSelectionManager<JList<File>, File> {

    private static final Logger LOG = Logger.getLogger(ListSelectionManagerForTestDoc.class.getName());
    
    private final File dir;
    
    private final FilenameFilter filenameFilter;

    public ListSelectionManagerForTestDoc(AppUiContext uiContext, Display display,
            File dir, FilenameFilter filenameFilter) {
        
        this(uiContext, display, dir, filenameFilter, (selected) -> true, Collections.EMPTY_LIST);
    }
    
    public ListSelectionManagerForTestDoc(AppUiContext uiContext, Display display,
            File dir, FilenameFilter filenameFilter, 
            Precondition<File> precondition, List<SelectionAction<File>> actions) {
        super(uiContext, display, precondition, actions);
        this.dir = Objects.requireNonNull(dir);
        this.filenameFilter = Objects.requireNonNull(filenameFilter);
    }

    @Override
    public JList<File> createSelectionUi() {
        return new JList(dir.listFiles(filenameFilter));
    }

    @Override
    public String getHtmlDisplayValue(File value) {
        return value.getName();
    }

    @Override
    public String getDisplayValue(File value, int maxLen) {
        final String name = value.getName();
        return name.length() <= maxLen ? name : name.substring(0, maxLen);
    }
    
    @Override
    public List<File> getSelectedValues(JList<File> list){
    
        final List<File> selectedValues = list.getSelectedValuesList();
        
        LOG.fine(() -> "Selection: " + selectedValues);
        
        return selectedValues;
    } 
}
