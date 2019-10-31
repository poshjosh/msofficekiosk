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

package com.looseboxes.msofficekiosk.ui.selection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 28, 2018 7:03:05 PM
 */
public class TreeSelectedValuesProviderImpl<V> 
        implements TreeSelectedValuesProvider<V>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(TreeSelectedValuesProviderImpl.class.getName());

    private final Function<TreePath, List<V>> getTreePathValues;

    public TreeSelectedValuesProviderImpl(Function<TreePath, List<V>> getTreePathValues) {
        this.getTreePathValues = Objects.requireNonNull(getTreePathValues);
    }
    
    @Override
    public List<V> apply(JTree tree) {
        
        List<V> selectionList;
        
        final int [] selectionRows = tree.getSelectionRows();
        
        LOG.finer(() -> "Selection rows: "+(selectionRows==null?null:Arrays.toString(selectionRows)));

        if(selectionRows != null && selectionRows.length > 0) {
            
            selectionList = new ArrayList<>(selectionRows.length);
            
            for(int selectionRow : selectionRows) {
                
                final TreePath treePath = tree.getPathForRow(selectionRow);

                LOG.finer(() -> "Row: "+selectionRow+", treePath: "+(treePath));

                final List<V> selection = this.getTreePathValues.apply(treePath);
                
                selectionList.addAll(selection);
            }
        }else{
            
            selectionList = Collections.EMPTY_LIST;
        }
        
        LOG.fine(() -> "Selection: " + selectionList);
        
        return selectionList;
    }
}
