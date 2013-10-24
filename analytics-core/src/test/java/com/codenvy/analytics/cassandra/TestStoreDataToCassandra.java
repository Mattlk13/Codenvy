/*
 *
 * CODENVY CONFIDENTIAL
 * ________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.analytics.cassandra;

import com.codenvy.analytics.BaseTest;
import com.codenvy.analytics.metrics.Parameters;
import com.codenvy.analytics.scripts.ScriptType;
import com.codenvy.analytics.scripts.executor.pig.PigServer;
import com.codenvy.analytics.scripts.util.Event;
import com.codenvy.analytics.scripts.util.LogGenerator;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a> */
public class TestStoreDataToCassandra extends BaseTest {

    private Map<String, String> params = new HashMap<>();

    @BeforeClass
    public void setUp() throws IOException {
        List<Event> events = new ArrayList<>();
        events.add(Event.Builder.createTenantCreatedEvent("ws1", "user1").withDate("2013-01-01").build());
        events.add(Event.Builder.createTenantCreatedEvent("ws2", "user1").withDate("2013-01-01").build());

        File log = LogGenerator.generateLog(events);

        Parameters.FROM_DATE.put(params, "20130101");
        Parameters.TO_DATE.put(params, "20130101");
        Parameters.USER.put(params, Parameters.USER_TYPES.REGISTERED.name());
        Parameters.WS.put(params, Parameters.WS_TYPES.PERSISTENT.name());
        Parameters.CASSANDRA_STORAGE.put(params, CASSANDRA_URL);
        Parameters.METRIC.put(params, CASSANDRA_KEY_SPACE);
        Parameters.LOG.put(params, log.getAbsolutePath());
    }

    @Test
    public void testStore() throws Exception {
        PigServer.executeAndReturn(ScriptType.TEST_CASSANDRA_STORE, params);
    }
}
