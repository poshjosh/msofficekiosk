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

package com.looseboxes.msofficekiosk.ui.exam;

import com.looseboxes.msofficekiosk.commands.CommandContext;
import com.looseboxes.msofficekiosk.ui.UiBeans;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 28, 2019 2:25:01 PM
 */
public interface ExamUiConfigurer extends UiBeans.UiConfigurer<ExamUi> {

    @Override
    void accept(ExamUi ui);

    SelectionListener addSelectionListener(ExamUi ui, MenuItem menuItem, String commandId);

    SelectionListener createSelectionListener(String commandId, Map<String, Object> commandParameters);

    SelectionListener createSelectionListener(Callable command);

    Callable getCommand(ExamUi ui, String commandId);
    
    Callable getCommand(ExamUi ui, String commandId, Predicate<Display> preCondition);

    Map<String, Object> getCommandParameters(ExamUi ui, String... commands);
    
    CommandContext getCommandContext();
}
