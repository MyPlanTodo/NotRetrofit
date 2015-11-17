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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.json.JSONObject;
import org.json.JSONException;

import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import com.github.*;

import rx.Observable;
import rx.functions.*;
import java.util.Arrays;
import java.util.List;
import retrofit.client.Response;
import retrofit.RetrofitError;
import java.io.*;
import java.util.concurrent.CountDownLatch;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;
import java.util.concurrent.atomic.AtomicBoolean;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.HttpUrl;

public class MainTest {
    @Test
    public void testGetWithBaseUrl() {
        GitHub github = GitHub.create();
        List<String> contributorsWithoutAuth = github.contributorsWithoutAuth("yongjhih", "retrofit").map(new Func1<Contributor, String>() {
            @Override public String call(Contributor contributor) {
                System.out.println(contributor.login);
                return contributor.login;
            }
        }).toList().toBlocking().single();
        assertTrue(contributorsWithoutAuth.contains("JakeWharton"));
        assertTrue(contributorsWithoutAuth.size() > 1);
    }

    @Test
    public void testGetWithoutBaseUrl() {
        GitHub github = GitHub.create();
        List<String> contributorsWithoutAuth = github.contributorsWithoutBaseUrl("yongjhih", "retrofit").map(new Func1<Contributor, String>() {
            @Override public String call(Contributor contributor) {
                System.out.println(contributor.login);
                return contributor.login;
            }
        }).toList().toBlocking().single();
        assertTrue(contributorsWithoutAuth.contains("JakeWharton"));
        assertTrue(contributorsWithoutAuth.size() > 1);
    }

    @Test
    public void testGetWithUrl() {
        GitHub github = GitHub.create();

        List<String> contributors = github.contributorsDynamic("https://api.github.com/repos/yongjhih/retrofit/contributors").map(new Func1<Contributor, String>() {
            @Override public String call(Contributor contributor) {
                System.out.println(contributor.login);
                return contributor.login;
            }
        }).toList().toBlocking().single();
        assertTrue(contributors.contains("JakeWharton"));
        assertTrue(contributors.size() > 1);
    }

    /*
    //tested
    @Test
    public void testPostBody() {
        GitHub github = GitHub.create();
        Repository localRepo = new Repository();
        localRepo.name = "tmp";
        Repository repository = github.createRepository(localRepo).toBlocking().first();
        assertTrue(repository.name.equals("tmp"));
    }
    */

    /*
    //tested
    @Test
    public void testDelete() {
        GitHub github = GitHub.create();
        Repository localRepo = new Repository();
        localRepo.name = "tmp";
        Response response = github.deleteRepository("yongjhih", "tmp");
        assertTrue(response.getStatus() == 204);
    }
    */

    @Test
    public void testPut() {
    }
    @Test
    public void testPostField() {
    }

    @Test
    public void testGetFile() {
    }
    @Test
    public void testPostFile() {
    }
    @Test
    public void testPostPart() {
    }
    @Test
    public void testGetWithHeader() {
    }
    @Test
    public void testPutTypedFile() {
    }
    @Test
    public void testPutTypedFileTypedString() {
    }

    @Test
    public void testGson() {
    }
    @Test
    public void testJackson() {
    }
    @Test
    public void testMoshi() {
    }
    @Test
    public void testLoganSquare() {
    }
    @Test
    public void testAutoJson() {
    }

    /*
    //tested
    @Test
    public void testStar() {
        GitHub github = GitHub.create();
        List<String> contributorsWithoutAuth = github.star("YOUR_TOKEN_HERE", "yongjhih", "retrofit").map(new Func1<Contributor, String>() {
            @Override public String call(Contributor contributor) {
                System.out.println(contributor.login);
                return contributor.login;
            }
        }).toList().toBlocking().single();
    }

    //tested
    @Test
    public void testUnstar() {
        GitHub github = GitHub.create();
        List<String> contributorsWithoutAuth = github.unstar("YOUR_TOKEN_HERE", "yongjhih", "retrofit").map(new Func1<Contributor, String>() {
            @Override public String call(Contributor contributor) {
                System.out.println(contributor.login);
                return contributor.login;
            }
        }).toList().toBlocking().single();
    }
    */

    @Test
    public void testObservableResponse() {
        GitHub github = GitHub.create();
        String string = github.contributorResponse("yongjhih", "retrofit").map(new Func1<Response, String>() {
            @Override public String call(Response response) {
                StringBuilder sb = new StringBuilder();
                try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                String read = null;

                    read = reader.readLine();
                    while (read != null) {
                        sb.append(read);
                        read = reader.readLine();
                    }
                } catch (IOException e) {
                }

                return sb.toString();
            }
        }).toBlocking().single();
        System.out.println(string);
        assertTrue(string.contains("JakeWharton"));
    }

    @Test
    public void testCallbackResponse() {
        final CountDownLatch signal = new CountDownLatch(1);

        GitHub github = GitHub.create();
        github.contributorResponse("yongjhih", "retrofit", new retrofit.Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                StringBuilder sb = new StringBuilder();
                try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                String read = null;

                    read = reader.readLine();
                    while (read != null) {
                        sb.append(read);
                        read = reader.readLine();
                    }
                } catch (IOException e) {
                }

                String string = sb.toString();
                System.out.println(string);
                assertTrue(string.contains("JakeWharton"));
                signal.countDown();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                assertTrue(false);
                signal.countDown();
            }
        });
        try {
            signal.await();
        } catch (InterruptedException e) {
            assertTrue(false);
        }

    }

    @Test
    public void testCallbackList() {
        final CountDownLatch signal = new CountDownLatch(1);

        GitHub github = GitHub.create();
        github.contributorList("yongjhih", "retrofit", new retrofit.Callback<List<Contributor>>() {
            @Override
            public void success(List<Contributor> list, Response response) {
                boolean contains = false;
                for (Contributor c : list) {
                    System.out.println(c.login);
                    if (!c.login.equals("yongjhih")) continue;
                    contains = true;
                }
                assertTrue(contains);
                signal.countDown();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                assertTrue(false);
                signal.countDown();
            }
        });
        try {
            signal.await();
        } catch (InterruptedException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testBlockingList() {
        GitHub github = GitHub.create();
        List<Contributor> list = github.contributorListBlocking("yongjhih", "retrofit");
        boolean contains = false;
        for (Contributor c : list) {
            System.out.println(c.login);
            if (!c.login.equals("yongjhih")) continue;
            contains = true;
        }
        assertTrue(contains);
    }

    @Test
    public void testBlockingResponse() {
        GitHub github = GitHub.create();
        Response response = github.contributorResponseBlocking("yongjhih", "retrofit");

        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
            String read = null;

            read = reader.readLine();
            while (read != null) {
                sb.append(read);
                read = reader.readLine();
            }
        } catch (IOException e) {
        }

        String string = sb.toString();
        System.out.println(string);
        assertTrue(string.contains("JakeWharton"));
    }

    @Test
    public void testMethodGson() {
        GitHub github = GitHub.create();
        List<String> contributorsWithoutAuth = github.contributorsWithGson("yongjhih", "retrofit").map(new Func1<Contributor, String>() {
            @Override public String call(Contributor contributor) {
                System.out.println(contributor.login);
                return contributor.login;
            }
        }).toList().toBlocking().single();
        assertTrue(contributorsWithoutAuth.contains("JakeWharton"));
        assertTrue(contributorsWithoutAuth.size() > 1);
    }

    @Test
    public void testMethodDateGson() {
        GitHub github = GitHub.create();
        List<String> contributorsWithoutAuth = github.contributorsWithDateGson("yongjhih", "retrofit").map(new Func1<Contributor, String>() {
            @Override public String call(Contributor contributor) {
                System.out.println(contributor.login);
                return contributor.login;
            }
        }).toList().toBlocking().single();
        assertTrue(contributorsWithoutAuth.contains("JakeWharton"));
        assertTrue(contributorsWithoutAuth.size() > 1);
    }

    @Test
    public void testAndroidAuthenticationRequestInterceptor() {
    }
    @Test
    public void testGlobalHeaders() {
    }
    @Test
    public void testRetryHeaders() {
    }
    @Test
    public void testRequestInterceptor() {
    }
    @Test
    public void testRequestInterceptorOnMethod() {
    }
    @Test
    public void testErrorHandler() {
        MockWebServer server = new MockWebServer();
        try {
            server.start();
        } catch (Throwable e) {}
        final AtomicBoolean hasErrorHandled = new AtomicBoolean(false);
        MockService service = MockService.builder()
            .errorHandler(new ErrorHandler() {
                @Override public Throwable handleError(RetrofitError cause) {
                    System.out.println("handled!!");
                    hasErrorHandled.set(true);
                    Response r = cause.getResponse();
                    if (r != null && r.getStatus() == 401) {
                        return new RuntimeException("401", cause);
                    }
                    return cause;
                }
            })
        .build();
        try {
            String s = service.get(server.url("/").toString());
        } catch (Throwable e) {
        }
        assertTrue(hasErrorHandled.get());
    }

    @Test
    public void testErrorHandlerOnMethod() {
    }
    @Test
    public void testLogLevel() {
    }
    @Test
    public void testConverterOnMethod() {
    }

}
