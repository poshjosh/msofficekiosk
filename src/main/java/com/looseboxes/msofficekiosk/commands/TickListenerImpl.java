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

import com.looseboxes.msofficekiosk.functions.GetTimeDisplay;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import org.eclipse.swt.widgets.Display;
import com.bc.config.Config;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import java.util.Properties;
import com.looseboxes.msofficekiosk.config.ConfigService;

/**
 * @author Chinomso Bassey Ikwuagwu on May 27, 2018 7:57:17 PM
 */
public class TickListenerImpl implements RunTimerCommand.TickListener {

    private transient static final Logger LOG = Logger.getLogger(TickListenerImpl.class.getName());

    private final Display display;
    
    private final Config<Properties> config;
    
    private final Predicate<TimerState> test; 
    
    private final Function<TimerState, Long> timeProvider;
    
    private final Consumer<String> resultConsumer;

    private final GetTimeDisplay getCountText;

    public TickListenerImpl(ConfigFactory context, Display display, Predicate<TimerState> test, 
            Function<TimerState, Long> timeProvider, Consumer<String> tickResultConsumer) {
        this(context.getConfig(ConfigService.APP_PROTECTED), display, 
                test, timeProvider, tickResultConsumer);
    }
    
    public TickListenerImpl(Config<Properties> config, Display display, Predicate<TimerState> test, 
            Function<TimerState, Long> timeProvider, Consumer<String> tickResultConsumer) {
        this.config = Objects.requireNonNull(config);
        this.display = Objects.requireNonNull(display);
        this.test = Objects.requireNonNull(test);
        this.timeProvider = Objects.requireNonNull(timeProvider);
        this.resultConsumer = Objects.requireNonNull(tickResultConsumer);
        this.getCountText = new GetTimeDisplay();
    }
    
    @Override
    public void onTick(TimerState timer) {
        if(this.test.test(timer)) {
            display.asyncExec(() -> {
                final long timeElapsed = this.timeProvider.apply(timer);
                final String text = this.getCountText.apply(timeElapsed, this.config);
                LOG.finest(text);
                this.resultConsumer.accept(text);
            });
        }
    }
}
