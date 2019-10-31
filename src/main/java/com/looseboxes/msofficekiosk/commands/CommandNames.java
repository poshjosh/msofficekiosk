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

package com.looseboxes.msofficekiosk.commands;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 23, 2019 4:31:36 PM
 */
public interface CommandNames {
    String ABOUT = "About";
    String CREATE_AND_OPEN_DOCUMENT = "Create and Open Document";
    String DISPOSE = "Dispose";
    String SAVE_THEN_DISPOSE_THEN_SUBMIT = "Exit";
    String SAVE_THEN_DISPOSE = "Exit Without Submitting";
    String SAVE = "Save";
    String SUBMIT = "Submit";
}
