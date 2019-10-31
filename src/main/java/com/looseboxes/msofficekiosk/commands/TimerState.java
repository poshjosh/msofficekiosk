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

/**
 * @author Chinomso Bassey Ikwuagwu on May 22, 2018 1:34:39 PM
 */
public interface TimerState {

    default long getTimeElapsedMillis() {
        return System.currentTimeMillis() - this.getStartTimeMillis();
    }
    
    default long getMillisTill(long endTime) {
        return endTime - System.currentTimeMillis();
    }

    boolean isStarted();
    boolean isStopped();
    long getTickCount();
    int getTickIntervalMillis();
    long getStartTimeMillis();
}
