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

package com.looseboxes.msofficekiosk.commands;

import java.io.File;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.widgets.Display;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2018 12:49:59 PM
 */
public class SaveCommand extends AbstractCommandWithPreCondition {

    private transient static final Logger LOG = Logger.getLogger(SaveCommand.class.getName());

    private final OleClientSite oleClientSite;
    
    private final File outputFile;
    
    private final boolean includeOleInfoInOutput;

    public SaveCommand(
            OleClientSite oleClientSite, 
            File outputFile, 
            boolean includeOleInfoInOutput,
            Predicate<Display> preCondition) {
        super(oleClientSite.getDisplay(), preCondition);
        this.oleClientSite = Objects.requireNonNull(oleClientSite);
        this.outputFile = Objects.requireNonNull(outputFile);
        this.includeOleInfoInOutput = includeOleInfoInOutput;
    }
    
    @Override
    public void run() {

        final File parent = outputFile.getParentFile();
        if(!parent.exists()) {
            parent.mkdirs();
        }

        LOG.finer(() -> "Saving to file: " + outputFile);

        this.oleClientSite.save(outputFile, this.includeOleInfoInOutput);

        LOG.fine(() -> "Saved to file: " + outputFile);
    }
    
    public final OleClientSite getOleClientSite() {
        return oleClientSite;
    }

    public final File getOutputFile() {
        return outputFile;
    }

    public final boolean isIncludeOleInfoInOutput() {
        return includeOleInfoInOutput;
    }
}
