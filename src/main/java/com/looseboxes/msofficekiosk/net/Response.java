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

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on May 6, 2019 4:11:44 PM
 */
public class Response<T> implements Serializable{

    private int code;
    private String message;
    private boolean error;
    private T body;

    public Response() { }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + this.code;
        hash = 71 * hash + Objects.hashCode(this.message);
        hash = 71 * hash + (this.error ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.body);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Response<?> other = (Response<?>) obj;
        if (this.code != other.code) {
            return false;
        }
        if (this.error != other.error) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        if (!Objects.equals(this.body, other.body)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Response{" + "code=" + code + ", message=" + message + ", error=" + error + ", body=" + body + '}';
    }
}
