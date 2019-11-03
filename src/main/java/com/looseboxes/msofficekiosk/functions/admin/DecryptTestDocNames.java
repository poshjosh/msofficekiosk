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

import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.test.TestDocKey;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.popups.MultiInputDialog;
import com.looseboxes.msofficekiosk.ui.selection.AbstractSelectionAction;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 26, 2018 2:59:07 PM
 */
public class DecryptTestDocNames extends AbstractSelectionAction<File>{
    
    private final AppContext context;

    public DecryptTestDocNames(AppContext context) {
        super("Decrypt Test Document name(s)");
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public Boolean apply(File file) {

        final TestDocKey id = TestDocKey.decodeFilename(context, file.getName());
        
        final Map map = new LinkedHashMap();
        map.put(TestDocKey.TEST_NAME, id.getTestname());
        map.put(TestDocKey.STUDENT_GROUP, id.getStudentgroup());
        map.put(TestDocKey.STUDENT_NAME, id.getStudentname());
        map.put(TestDocKey.DOCUMENT_NAME, id.getDocumentname());

        new MultiInputDialog(context.getConfig(ConfigService.APP_UI)).apply(map, "File Details");
        
        return Boolean.TRUE;
    }
}
