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

import com.bc.config.Config;
import com.bc.ui.UIContextImpl;
import java.awt.Window;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;
import javax.swing.ImageIcon;

/**
 * @author Chinomso Bassey Ikwuagwu on May 27, 2018 8:01:32 PM
 */
public class AppUiContextImpl extends UIContextImpl implements AppUiContext{

    private final Config config;
    private final Supplier<UI> uiSupplier;
    private final Supplier<Window> mainWindowSupplier;
    
    public AppUiContextImpl(Config config, Supplier<UI> uiSupplier) {
        this(config, uiSupplier, () -> null);
    }
    
    public AppUiContextImpl(Config config, Supplier<UI> uiSupplier, Supplier<Window> mainWindowSupplier) {
        super(
                null, 
                new ImageIcon(Thread.currentThread().getContextClassLoader()
                        .getResource("META-INF/resources/afcsc_logo.png"), "App Logo")
        );
        this.config = Objects.requireNonNull(config);
        this.mainWindowSupplier = Objects.requireNonNull(mainWindowSupplier);
        this.uiSupplier = Objects.requireNonNull(uiSupplier);
    }

    @Override
    public UI getUi() {
        return Objects.requireNonNull(uiSupplier.get());
    }

    @Override
    public Optional<Window> getMainWindowOptional() {
        return Optional.ofNullable(mainWindowSupplier.get());
    }

    @Override
    public Config<Properties> getConfig() {
        return config;
    }
}
