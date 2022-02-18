/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.runtime.zookeeper;

import org.apache.flink.core.testutils.CustomExtension;
import org.apache.flink.util.Preconditions;

import org.apache.curator.test.TestingServer;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import java.io.IOException;

/**
 * {@link org.junit.jupiter.api.extension.Extension} which starts a {@link
 * org.apache.zookeeper.server.ZooKeeperServer}.
 */
public class ZooKeeperExtension implements CustomExtension {
    private static final Logger LOG = LoggerFactory.getLogger(ZooKeeperExtension.class);

    @Nullable private TestingServer zooKeeperServer;

    public String getConnectString() {
        return getRunningZookeeperInstanceOrFail().getConnectString();
    }

    private TestingServer getRunningZookeeperInstanceOrFail() {
        Preconditions.checkState(zooKeeperServer != null);
        return zooKeeperServer;
    }

    @Override
    public void before(ExtensionContext context) throws Exception {
        terminateZooKeeperServer();
        zooKeeperServer = new TestingServer(true);
    }

    private void terminateZooKeeperServer() throws IOException {
        if (zooKeeperServer != null) {
            zooKeeperServer.close();
            zooKeeperServer = null;
        }
    }

    @Override
    public void after(ExtensionContext context) throws Exception {
        try {
            terminateZooKeeperServer();
        } catch (IOException e) {
            LOG.warn("Could not properly terminate the {}.", getClass().getSimpleName(), e);
        }
    }

    public void restart() throws Exception {
        getRunningZookeeperInstanceOrFail().restart();
    }

    public void stop() throws IOException {
        getRunningZookeeperInstanceOrFail().stop();
    }
}
