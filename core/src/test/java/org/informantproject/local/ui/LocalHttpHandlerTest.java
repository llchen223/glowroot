/**
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.informantproject.local.ui;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Collection;

import org.informantproject.core.MainEntryPoint;
import org.informantproject.core.config.CoreConfig;
import org.informantproject.core.config.CoreConfigTestData;
import org.informantproject.core.util.Threads;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.Response;

/**
 * @author Trask Stalnaker
 * @since 0.5
 */
public class LocalHttpHandlerTest {

    private Collection<Thread> preExistingThreads;
    private AsyncHttpClient asyncHttpClient;

    @Before
    public void before() {
        preExistingThreads = Threads.currentThreads();
        MainEntryPoint.start("ui.port:0");
        asyncHttpClient = new AsyncHttpClient();
    }

    @After
    public void after() throws Exception {
        // asyncHttpClient is not part of the system under test, so it can be shut down
        // first before checking for non-daemon threads
        asyncHttpClient.close();
        // wait for the non-daemon thread in AsyncHttpClient to shutdown
        // (see NettyConnectionsPool.idleConnectionDetector)
        // a patch has been submitted to change this non-daemon thread to a daemon thread
        // (see https://github.com/sonatype/async-http-client/pull/35)
        // TODO remove this sleep (and comment above) once the patch is accepted
        // and a new release of async-http-client is available
        Thread.sleep(200);
        Threads.preShutdownCheck(preExistingThreads);
        MainEntryPoint.shutdown();
        Threads.postShutdownCheck(preExistingThreads);
    }

    @Test
    public void shouldUpdateAndReadBackConfig() throws Exception {
        // given
        CoreConfig randomCoreConfig = new CoreConfigTestData().getNonDefaultCoreConfig();
        BoundRequestBuilder updateRequest = asyncHttpClient.preparePost("http://localhost:"
                + MainEntryPoint.getPort() + "/config/core/properties");
        updateRequest.setBody(randomCoreConfig.getPropertiesJson());
        updateRequest.execute().get();
        BoundRequestBuilder readRequest = asyncHttpClient.prepareGet("http://localhost:"
                + MainEntryPoint.getPort() + "/config/read");
        // when
        Response response = readRequest.execute().get();
        // then
        String responseText = response.getResponseBody();
        JsonObject rootNode = new JsonParser().parse(responseText).getAsJsonObject();
        String coreConfigJson = new Gson().toJson(rootNode.get("coreProperties"));
        assertThat(coreConfigJson).isEqualTo(randomCoreConfig.getPropertiesJson());
    }
}