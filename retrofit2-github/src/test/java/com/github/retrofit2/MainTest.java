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

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.functions.Func1;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
                    while(read != null) {
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
    public void testErrorHandler() { // SocketTimeoutException
        MockWebServer server = new MockWebServer();
        try {
            server.start();
        } catch (Throwable e) {
            System.out.println("server start() error: " + e);
        }
        final AtomicBoolean hasErrorHandled = new AtomicBoolean(false);
        //MockService service = MockService.create();
        MockService service = MockService.builder()
            .errorHandler(new ErrorHandler() {
                @Override public Throwable handleError(RetrofitError cause) {
                    hasErrorHandled.set(true);

                    System.out.println("ErrorHandler: " + cause);

                    Response r = cause.getResponse();

                    System.out.println("ErrorHandler: Response: " + r);
                    if (r != null) {
                        System.out.println("ErrorHandler: status: " + r.getStatus());
                        if (r.getStatus() == 401) {
                            return new RuntimeException("401", cause);
                        }
                    }

                    return cause;
                }
            })
        .build();
        String s = service.get(server.url("/").toString());
        assertTrue(hasErrorHandled.get());
    }

    @Test
    public void testHasHttpErrorHandlerResponseCode() {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("{ \"error\": \"failure msg\" }").setResponseCode(HttpURLConnection.HTTP_NOT_FOUND));
        try {
            server.start();
        } catch (Throwable e) {
            System.out.println("server start() error: " + e);
        }
        final AtomicInteger status = new AtomicInteger(-1);
        //MockService service = MockService.create();
        MockService service = MockService.builder()
            .errorHandler(new ErrorHandler() {
                @Override public Throwable handleError(RetrofitError cause) {

                    System.out.println("ErrorHandler: " + cause);

                    Response r = cause.getResponse();

                    System.out.println("ErrorHandler: Response: " + r);
                    if (r != null) {
                        System.out.println("ErrorHandler: status: " + r.getStatus());

                        status.set(r.getStatus());
                    }

                    return cause;
                }
            })
        .build();
        String s = service.get(server.url("/").toString());
        assertEquals(status.get(), HttpURLConnection.HTTP_NOT_FOUND);
    }

    @Test
    public void testBuilderHttpErrorHandlerReturnsBodyAndErrorCode() throws IOException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("SomeBody").setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED));
        server.start();

        MockService service = MockService.builder()
                .errorHandler(new ErrorHandler() {
                    @Override public Throwable handleError(RetrofitError cause) {

                        Response r = cause.getResponse();
                        assertEquals(r.getStatus(), HttpURLConnection.HTTP_UNAUTHORIZED);

                        return cause;
                    }
                })
                .build();

        String s = service.get(server.url("/").toString());
        assertNotNull(s);
        assertEquals("Body wasn't as expected: ", s, "SomeBody");

        server.shutdown();
    }

    @Test
    public void testCreatorHttpErrorHandlerReturnsBodyAndErrorCode() throws IOException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("SomeBody").setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED));
        server.start();

        // init errorhandler static vars
        MockErrorHandler.errorCode = -1;

        MockService service = MockService.create();
        String s = service.get(server.url("/").toString());
        System.out.println("get: " + s);

        // verify that the errorhandler was called
        assertEquals("ErrorHandler didn't get error code", MockErrorHandler.errorCode, HttpURLConnection.HTTP_UNAUTHORIZED);

        assertNotNull(s);
        assertEquals("Body wasn't as expected: ", s, "SomeBody");

        server.shutdown();
    }

    @Test
    public void testHttpErrorHandlerSocketTimeout() throws IOException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("SomeBody").setResponseCode(HttpURLConnection.HTTP_OK).setBodyDelay(60, TimeUnit.SECONDS));
        server.start();

        final AtomicBoolean hasErrorHandled = new AtomicBoolean(false);
        MockService service = MockService.builder()
                .errorHandler(new ErrorHandler() {
                    @Override public Throwable handleError(RetrofitError cause) {
                        hasErrorHandled.set(true);

                        // cause: retrofit.RetrofitError: timeout
                        System.out.println("ErrorHandler: cause: " + cause);
                        // cause.getCause(): java.net.SocketTimeoutException: timeout
                        System.out.println("ErrorHandler: cause.getCause(): " + cause.getCause());
                        // cause.getCause().getCause(): java.net.SocketException: Socket closed
                        System.out.println("ErrorHandler: cause.getCause().getCause(): " + cause.getCause().getCause());
                        assertTrue(cause.getCause() instanceof SocketTimeoutException);

                        Response r = cause.getResponse();

                        System.out.println("ErrorHandler: Response: " + r);
                        if (r != null) {
                            System.out.println("ErrorHandler: status: " + r.getStatus());
                        }

                        return cause;
                    }
                })
                .build();

        String s = service.get(server.url("/").toString());
        System.out.println("get: " + s);

        assertTrue("Error handler wasn't called", hasErrorHandled.get());
        assertEquals("Body should be null because of timeout", s, null);

        try {
            server.shutdown();
        } catch (IOException e) {
            // ignore...server will be stuck in timeout still
        }
    }

    @Test
    public void testErrorHandlerOnMethod() {
    }

    @Test
    public void testDynamicBaseUrl() {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("yo"));
        try {
            server.start();
        } catch (Throwable e) {
            System.out.println("server start() error: " + e);
        }
        MockService service = MockService.builder()
            .baseUrl(server.url("/").toString())
        .build();
        String s = service.get("/");

        assertEquals("yo", s);

        /*
         * static base url: @Retrofit("https://api.github.com")
         * GET "https://api.github.com" + "/"
            {"current_user_url":"https://api.github.com/user","current_user_authorizations_html_url":"https://github.com/settings/connections/applications{/client_id}","authorizations_url":"https://api.github.com/
                authorizations","code_search_url":"https://api.github.com/search/code?q={query}{&page,per_page,sort,order}","emails_url":"https://api.github.com/user/emails","emojis_url":"https://api.github.com/emojis","e
                    vents_url":"https://api.github.com/events","feeds_url":"https://api.github.com/feeds","followers_url":"https://api.github.com/user/followers","following_url":"https://api.github.com/user/following{/target}
            ","gists_url":"https://api.github.com/gists{/gist_id}","hub_url":"https://api.github.com/hub","issue_search_url":"https://api.github.com/search/issues?q={query}{&page,per_page,sort,order}","issues_url":"ht
                tps://api.github.com/issues","keys_url":"https://api.github.com/user/keys","notifications_url":"https://api.github.com/notifications","organization_repositories_url":"https://api.github.com/orgs/{org}/repo
                s{?type,page,per_page,sort}","organization_url":"https://api.github.com/orgs/{org}","public_gists_url":"https://api.github.com/gists/public","rate_limit_url":"https://api.github.com/rate_limit","repository
                _url":"https://api.github.com/repos/{owner}/{repo}","repository_search_url":"https://api.github.com/search/repositories?q={query}{&page,per_page,sort,order}","current_user_repositories_url":"https://api.gi
                thub.com/user/repos{?type,page,per_page,sort}","starred_url":"https://api.github.com/user/starred{/owner}{/repo}","starred_gists_url":"https://api.github.com/gists/starred","team_url":"https://api.github.c
                om/teams","user_url":"https://api.github.com/users/{user}","user_organizations_url":"https://api.github.com/user/orgs","user_repositories_url":"https://api.github.com/users/{user}/repos{?type,page,per_page
                ,sort}","user_search_url":"https://api.github.com/search/users?q={query}{&page,per_page,sort,order}"}
                MockErrorHandler: retrofit.RetrofitError: com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected a string but was BEGIN_OBJECT at line 1 column 2 path $
        */
    }

    @Test
    public void testLogLevel() {
    }
    @Test
    public void testConverterOnMethod() {
    }

}
