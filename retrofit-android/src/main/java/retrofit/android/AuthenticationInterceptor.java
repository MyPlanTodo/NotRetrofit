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
package retrofit.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Singleton;

@Singleton
public abstract class AuthenticationInterceptor extends retrofit.http.Retrofit.SimpleRequestInterceptor {
    String token;
    Activity activity;
    Context context;

    public abstract String accountType();
    public abstract String authTokenType();
    public abstract void intercept(String token, RequestFacade request);

    public Context context() {
        return context;
    }

    public Activity activity() {
        return activity;
    }

    @Override
    public void intercept(Object context, RequestFacade request) {
        this.activity = (Activity) context;
        this.context = (Context) context;
        if (token == null) token = getAuthToken(activity, accountType(), authTokenType());
        intercept(token, request);
    }

    public String getAuthToken(Activity activity, String accountType, String authTokenType) {
        Log.d("retrogithub", "getAuthToken");
        AccountManager accountManager = AccountManager.get(activity);
        Account account = getAccount(accountManager, accountType);
        Log.d("retrogithub", "account: " + account);
        AccountManagerFuture<Bundle> bundleTask = null;
        if (account == null) {
            Log.d("retrogithub", "addAccount");
            bundleTask = accountManager.addAccount(accountType, authTokenType, null, null, activity, null, null);
        } else {
            Log.d("retrogithub", "getAuthToken");
            bundleTask = accountManager.getAuthToken(account, authTokenType, null, activity, null, null);
        }

        if (bundleTask == null) {
            Log.d("retrogithub", "bundleTask: " + bundleTask);
            return null;
        }

        final AccountManagerFuture<Bundle> finalBundleTask = bundleTask;
        Bundle tokenBundle = null;
        try {
            Log.d("retrogithub", "tokenBundle");
            tokenBundle = finalBundleTask.getResult();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("retrogithub", "tokenBundle: " + tokenBundle);
        if (tokenBundle == null) return null;

        Log.d("retrogithub", "AccountManager.KEY_AUTHTOKEN");
        return tokenBundle.getString(AccountManager.KEY_AUTHTOKEN);
    }

    public Account getAccount(AccountManager accountManager, String accountType) {
        Log.d("retrogithub", "getAccount");
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length > 0) return accounts[0];
        return null; // not found
    }
}
