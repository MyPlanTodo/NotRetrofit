/*
 * Copyright (C) 2015 8tory, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.retrofit2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MockErrorHandler implements ErrorHandler {
    public static int errorCode;

    @Override public Throwable handleError(RetrofitError cause) {
        System.out.println("MockErrorHandler: " + cause);
        Response r = cause.getResponse();
        System.out.println("MockErrorHandler: Response: " + r);
        if (r != null) {
            errorCode = r.getStatus();

            System.out.println("MockErrorHandler: status: " + r.getStatus());
            if (r.getStatus() == 401) {
                return new RuntimeException("401", cause);
            }
        }
        return cause;
    }
}
