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

package com.looseboxes.msofficekiosk.net;

import com.looseboxes.msofficekiosk.ui.MessageDialog;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on May 10, 2019 3:49:08 PM
 */
public class SimpleResponseHandler<T> implements ResponseHandler<T>{

    private final MessageDialog messageDialog;
    
    private Response combo;

    public SimpleResponseHandler(MessageDialog messageDialog) {
        this.messageDialog = Objects.requireNonNull(messageDialog);
    }
    
    @Override
    public ResponseHandler add(Response<T> r) {
        if(combo == null) {
            combo = new Response();
        }
        combo.setCode(Math.max(combo.getCode(), r == null ? 0 : r.getCode()));
        combo.setError(combo.isError() || (r == null ? true : r.isError()));
        combo.setMessage(
                (combo.getMessage() == null ? "" : combo.getMessage() + "\n") + 
                (r == null ? this.getNoResponseMessage() : r.getMessage()));
//@todo        
//        combo.setBody(???);
        return this;
    }
    
    @Override
    public void acceptAdded() {
        try{
            accept(combo);
        }finally{
            combo = null;
        }
    }
    
    @Override
    public void accept(Response r) {
        
        String m;
        if(r == null) {
            m = getNoResponseMessage();
        }else{
            m = r.getMessage();
//            m = r.isError() ? getDefaultErrorMessage() : getDefaultSuccessMessage();
        }

        if(r == null || r.isError()) {
            messageDialog.showWarningMessage(m);
        }else{
            messageDialog.showInformationMessage(m);
        }
    }
    
    public String getNoResponseMessage() {
        return "No Response from Server";
    }

    public String getDefaultErrorMessage() {
        return "Error";
    }
    public String getDefaultSuccessMessage() {
        return "Success";
    }
}
