/*
 * Copyright 2018 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance withDocumentName the License.
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

package com.looseboxes.msofficekiosk.commands;

import java.util.Objects;
import java.util.function.Predicate;
import org.eclipse.swt.widgets.Display;
import com.looseboxes.msofficekiosk.ui.exam.ExamUi;

/**
 * @author Chinomso Bassey Ikwuagwu on May 14, 2018 8:07:59 PM
 */
public class OpenDocumentCommand extends AbstractCommandWithPreCondition {

    private final ExamUi appUi;
    private final String documentName;    

    public OpenDocumentCommand(ExamUi appUi, String documentName) {
        this(appUi, documentName, (display) -> true);
    }
    
    public OpenDocumentCommand(ExamUi appUi, String documentName, Predicate<Display> preCondition) {
        super(appUi.getDisplay(), preCondition);
        this.appUi = Objects.requireNonNull(appUi);
        this.documentName = Objects.requireNonNull(documentName);
    }

    @Override
    public void run() {

        this.getDisplay().syncExec(() -> {
            appUi.openDocument(documentName);
        });
    }
}
