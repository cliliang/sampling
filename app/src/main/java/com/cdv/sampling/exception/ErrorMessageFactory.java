/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cdv.sampling.exception;

import com.cdv.sampling.BuildConfig;
import com.cdv.sampling.R;
import com.cdv.sampling.SamplingApplication;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

/**
 * Factory used to create error messages from an Exception as a condition.
 */
public class ErrorMessageFactory {

    private ErrorMessageFactory() {
        //empty
    }

    public static String create(Throwable exception) {
        String message = "未知错误！";
        if (exception instanceof SocketTimeoutException || exception instanceof SocketException || exception instanceof UnknownHostException || exception instanceof UnknownServiceException){
            message = SamplingApplication.getInstance().getString(R.string.error_tip_no_network);
        }else if (exception instanceof NetworkConnectionException) {
            message = SamplingApplication.getInstance().getString(R.string.error_tip_no_network);
        } else if (exception instanceof RequestIllegalException) {
            message = exception.getMessage();
        }else if (exception instanceof ErrorBundle){
            message = ((ErrorBundle) exception).getErrorMessage();
        }else if (exception instanceof DataFormatException){
            message = exception.getMessage();
        }else if (exception instanceof FormException){
            message = exception.getMessage();
        }
        if (BuildConfig.DEBUG){
            exception.printStackTrace();
        }
        return message;
    }
}
