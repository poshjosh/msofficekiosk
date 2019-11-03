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

import com.looseboxes.msofficekiosk.ui.admin.AdminUiConfigurer;
import com.bc.node.Node;
import com.bc.ui.treebuilder.TreeBuilder;
import com.bc.ui.treebuilder.TreeBuilderImpl;
import com.looseboxes.msofficekiosk.test.TestDocKey;
import java.awt.Component;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.MutableTreeNode;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 28, 2018 7:38:37 PM
 */
public class SelectionTreeProviderForTestDocs implements SelectionUiProvider<JTree> {

    private transient static final Logger LOG = Logger.getLogger(SelectionTreeProviderForTestDocs.class.getName());

    private final boolean rootVisible;

    public SelectionTreeProviderForTestDocs() {
        this(AdminUiConfigurer.JTREE_ROOT_INCLUSIVE);
    }
    
    public SelectionTreeProviderForTestDocs(boolean rootVisible) {
        this.rootVisible = rootVisible;
    }
    
    @Override
    public JTree apply(List<File> files) {
        
        final Node root = createNode(null, TestDocTreeNodeLevel.root, 
                "Select to view submitted " + TestDocTreeNodeLevel.test.name() + 's');
        
        for(File file : files) {
    
            if(file.isDirectory()) {
                
                LOG.log(Level.FINE, "Ignoring dir: {0}", file);
                
                continue;
            }
            
            final String filename = file.getName();
            
            final Optional<TestDocKey> keyOptional = TestDocKey.decodePossibleFilename(filename);
            
            if(keyOptional.isPresent()) {
                
                this.addToTree(root, keyOptional.get());
                
            }else{
            
                LOG.log(Level.FINE, "Not a Test Document: {0}", filename);
            }
        }
        
        LOG.fine(() -> "Children of root: " + root.getChildren());
        
        final Function<Node, Node[]> getChildren = (node) -> 
                (Node[])node.getChildren().toArray(new Node[0]);
        
        final TreeBuilder<Node> treeBuilder = new TreeBuilderImpl<>(getChildren);
        final MutableTreeNode treeNode = treeBuilder.build(root, (node) -> true);
        
        final Function<Object, Node<String>> getNode = new GetNodeForTreeNode();
        final JTree tree = new JTree(treeNode){
            @Override
            public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                return getNode.apply(value).getValueOrDefault("");
            }
        };
        
        tree.setRootVisible(rootVisible);
    
        tree.setRowHeight(0);
        tree.setCellRenderer (new DefaultTreeCellRenderer () {
        
            //@UI border
            private final Border border = BorderFactory.createEmptyBorder ( 8, 8, 8, 8 );

            @Override
            public Component getTreeCellRendererComponent (JTree tree, Object value, boolean sel,
                                                            boolean expanded, boolean leaf, int row,
                                                            boolean hasFocus ){
                final JLabel label = (JLabel)super
                        .getTreeCellRendererComponent ( tree, value, sel, expanded, leaf, row, hasFocus);

                label.setBorder (border);

                return label;
            }
        });

        return tree;
    }
    
    public Node addToTree(Node root, TestDocKey id) {
        
        final String testName = id.getTestname();
        
        final Node test = this.find(root, TestDocTreeNodeLevel.test, testName);
        
        final String studentGroupName = id.getStudentgroup();
        
        final Node studentGroup = this.find(test, TestDocTreeNodeLevel.group, studentGroupName);
        
        final String userName = id.getStudentname();
        
        final Node user = this.find(studentGroup, TestDocTreeNodeLevel.student, userName);
        
        final String docName = id.getDocumentname();
        
        final Node doc = this.find(user, TestDocTreeNodeLevel.document, docName);
        
        return doc;
    }
    
    public <T> Node<T> find(Node<T> parent, TestDocTreeNodeLevel en, T value) {
        
        final int level = en.ordinal();
        
        final Predicate<Node<T>> nodeTest = (node) -> {
            
            final boolean nodeLevelEquals = node.getLevel() == level;
            
            final boolean success = nodeLevelEquals && 
                    Objects.equals(node.getValueOrDefault(null).toString(), value.toString());
            
            LOG.finest(() -> "Success: " + success + ", level: " + level + 
                    ", value: " + value + ", node: " + node +
                    ", level equals: " + nodeLevelEquals + "\nValues. lhs: " + node.getValueOptional() + ", rhs: " + value);
            
            return success;
        };
        
        final Optional<Node<T>> found = parent.findFirstChild(nodeTest);
        
        LOG.finer(() -> "Found: "+found.isPresent()+". Level: " + level + ", value: " + value);
       
        return found.isPresent() ? found.get() : this.createNode(parent, en, value);
    }
    
    public <T> Node<T> createNode(Node<T> parent, TestDocTreeNodeLevel en, T value) {
        final Node<T> node = Node.of(en.name(), value, parent);
        LOG.finer(() -> "Created: " + node);
        return node;
    }
}
