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

import com.bc.node.Node;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.tree.TreePath;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 28, 2018 9:05:46 PM
 */
public class GetFilesForTreePath implements Function<TreePath, List<File>>, Serializable {

    private final Supplier<List<File>> filesSupplier;
    
    private final Function<Object, Node<String>> getNode;
    
    private final boolean rootInclusive;

    public GetFilesForTreePath(Supplier<List<File>> filesSupplier, 
            Function<Object, Node<String>> getNode, boolean rootInclusive) {
        this.filesSupplier = Objects.requireNonNull(filesSupplier);
        this.getNode = Objects.requireNonNull(getNode);
        this.rootInclusive = rootInclusive;
    }
    
    @Override
    public List<File> apply(TreePath treePath) {
        final List<File> files = filesSupplier.get();
        final List<File> output = new ArrayList<>(files.size());
        final int offset = this.rootInclusive ? 0 : 1;
        final int pathCount = treePath.getPathCount();
        for(File file : files) {
            int found;
            for(int i=found=offset; i<pathCount; i++) {
                final String val = getNode.apply(treePath.getPathComponent(i)).getValueOrDefault("");
                if(file.getName().contains(val)) {
                    ++found;
                }else{
                    break;
                }
            }
            if(found >= pathCount) {
                output.add(file);
            }
        }
        return output;
    }
}
