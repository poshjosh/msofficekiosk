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

package com.looseboxes.msofficekiosk.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Chinomso Bassey Ikwuagwu on May 14, 2018 1:27:27 PM
 */
public class SingleInputForm extends Composite {

    private static final Logger LOG = Logger.getLogger(SingleInputForm.class.getName());
    
    private final Label label;
    
    private final Text text;
    
    private final Button button;
    
    private final List<Resource> resources = new ArrayList<>();
    
    private final Color colorGray;

    public SingleInputForm(Composite parent, int parentStyle) {
        this(parent, parentStyle, null, null, "Submit", null);
    }
    
    public SingleInputForm(Composite parent, int parentStyle, 
            String labelText, String textText, String buttonText, Listener listener) {
        this(parent, parentStyle, labelText, SWT.NONE, textText, SWT.SINGLE, buttonText, SWT.PUSH, listener);
    }
    
    public SingleInputForm(Composite parent, int parentStyle, 
            String labelText, int labelStyle, 
            String textText, int textStyle, 
            String buttonText, int buttonStyle,
            Listener listener) {

        super(parent, parentStyle);
        final RowLayout rowLayout = new RowLayout();
        rowLayout.wrap = false;
        this.setLayout(rowLayout);
        
        final int height = 32;
        
        this.colorGray = new Color(parent.getDisplay(), 200, 200, 200);
        this.resources.add(colorGray);

        this.label = new Label(this, textStyle);
        if(labelText != null) {
            this.label.setText(labelText);
        }
        RowData rowData = new RowData();
        rowData.height = height;
        this.label.setLayoutData(rowData);
        
        this.text = new Text(this, textStyle);
        if(textText != null) {
            this.text.setText(textText);
        }
        if(listener != null) {
            this.text.addListener(SWT.Selection, listener);
        }
        rowData = new RowData();
        rowData.height = height;
        this.text.setLayoutData(rowData);
        
        this.button = new Button(this, buttonStyle);
        if(buttonText != null) {
            this.button.setText(buttonText);
        }
        if(listener != null) {
            this.button.addListener(SWT.Selection, listener);
        }
        
        this.setBackground(colorGray);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        this.disposeResources();
    }

    public void disposeResources() {
        for(Resource res : resources) {
            try{
                res.dispose();
            }catch(RuntimeException e) {
                LOG.log(Level.WARNING, "Failed to dispose resource: " + res, e);
            }
        }
    }
    
    public Label getLabel() {
        return label;
    }

    public Text getText() {
        return text;
    }

    public Button getButton() {
        return button;
    }
}
