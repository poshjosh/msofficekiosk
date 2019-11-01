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
package com.looseboxes.msofficekiosk.test;

import com.looseboxes.msofficekiosk.functions.ui.PromptUserSelect;
import com.bc.elmi.pu.entities.Test;
import com.bc.ui.UIContext;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.function.Function;

/**
 * @author Chinomso Bassey Ikwuagwu on November 1, 2019 01:29:00 AM
 */
public class PromptUserSelectTest extends PromptUserSelect<Test>{
    
    public static class TestToSelectionLabel implements Function<Test, Object>{
        private final DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm");
        @Override
        public Object apply(Test t) {
            return t.getTestid() + " - " + t.getTestname() + " (" + dateFormat.format(t.getStarttime()) + ")";
        }
    }

    public PromptUserSelectTest(UIContext uiContext, ConfigFactory configFactory) {
        super(uiContext, configFactory, new TestToSelectionLabel());
    }
}
