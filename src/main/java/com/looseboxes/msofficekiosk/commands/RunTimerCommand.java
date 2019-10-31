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

import java.util.Objects;
import org.eclipse.swt.widgets.Display;

/**
 * @author Chinomso Bassey Ikwuagwu on May 3, 2018 1:58:20 PM
 */
public class RunTimerCommand implements Runnable, TimerState {
    
    @FunctionalInterface
    public static interface TickListener {
        void onTick(TimerState timer);
    }
    
    private boolean started;
    
    private boolean stopped;
    
    private long tickCount;
    
    private final Display display;
    
    private final long startTimeMillis;
    
    private final int tickIntervalMillis;
    
    private final TickListener tickListener;

    public RunTimerCommand(Display display, int tickIntervalMillis, TickListener listener) {
        this(display, System.currentTimeMillis(), tickIntervalMillis, listener);
    }
    
    public RunTimerCommand(Display display, long startTimeMillis, int tickIntervalMillis, TickListener listener) {
        this.display = Objects.requireNonNull(display);
        this.startTimeMillis = startTimeMillis;
        this.tickIntervalMillis = tickIntervalMillis;
        this.tickListener = listener;
    }
    
    public void start() {
        this.display.timerExec(tickIntervalMillis, this);
        this.started = true;
    }
    
    public void stop() {
        if(started && !stopped) {
            this.display.timerExec(-1, this);
            this.stopped = true;
        }
    }

    @Override
    public void run() {

        this.started = true;

        this.update();

        display.timerExec(tickIntervalMillis, this);
    }
      
    public void update() { 
        if(this.tickListener != null) {
            this.tickListener.onTick(this);
        }
    }  

    @Override
    public final boolean isStarted() {
        return started;
    }

    @Override
    public final boolean isStopped() {
        return stopped;
    }

    @Override
    public long getTickCount() {
        return tickCount;
    }

    public final Display getDisplay() {
        return display;
    }

    @Override
    public final int getTickIntervalMillis() {
        return tickIntervalMillis;
    }

    @Override
    public final long getStartTimeMillis() {
        return startTimeMillis;
    }
}
