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

import com.bc.node.Node;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.ui.admin.AdminUiConfigurer;
import com.looseboxes.msofficekiosk.validators.Precondition;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.eclipse.swt.widgets.Display;

/**
 * @author Chinomso Bassey Ikwuagwu on May 8, 2019 2:47:14 AM
 */
public class TreeSelectionManagerForDirFiles extends AbstractUiSelectionManager<JTree, File>{

//    private static final Logger LOG = Logger.getLogger(TreeSelectionManagerForDirFiles.class.getName());
    
    private final Supplier<List<File>> filesSupplier;
    
    public TreeSelectionManagerForDirFiles(AppUiContext uiContext, Display display,
            Supplier<List<File>> filesSupplier) {
        
        this(uiContext, display, filesSupplier, (selected) -> true, Collections.EMPTY_LIST);
    }
    
    public TreeSelectionManagerForDirFiles(AppUiContext uiContext, Display display,
            Supplier<List<File>> filesSupplier, 
            Precondition<File> precondition, List<SelectionAction<File>> actions) {
        super(uiContext, display, precondition, actions);
        this.filesSupplier = Objects.requireNonNull(filesSupplier);
    }

    @Override
    protected JTree createSelectionUi() {
        return new SelectionTreeProviderForTestDocs().apply(filesSupplier.get());
    }

    @Override
    public String getHtmlDisplayValue(File value) {
        return value.getName();
    }

    @Override
    public String getDisplayValue(File value, int maxLen) {
        final String output = value.getName();
        return output.length() <= maxLen ? output : output.substring(0, maxLen);
    }

    @Override
    public List<File> getSelectedValues(JTree tree) {

        final Function<Object, Node<String>> getNode = new GetNodeForTreeNode();
        
        final Function<TreePath, List<File>> getFilenames = new GetFilesForTreePath(
                filesSupplier, getNode, AdminUiConfigurer.JTREE_ROOT_INCLUSIVE);

        final TreeSelectedValuesProvider<File> valuesProvider = new TreeSelectedValuesProviderImpl<>(getFilenames);
        
        return valuesProvider.apply(tree);
    }
}
