/*
 * Copyright sablintolya@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.ma1uta.haijin.matrix;

import io.dropwizard.lifecycle.Managed;
import io.github.ma1uta.haijin.Configuration;
import io.github.ma1uta.matrix.bot.Bot;
import io.github.ma1uta.matrix.bot.BotState;
import io.github.ma1uta.matrix.bot.PersistentService;
import io.github.ma1uta.matrix.client.MatrixClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;

/**
 * Store only an one bot.
 */
public class HaijinPool implements Managed {

    private static final long TIMEOUT = 10L;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final Client client;

    private final Configuration configuration;

    public HaijinPool(Client client, Configuration configuration) {
        this.client = client;
        this.configuration = configuration;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void start() throws IOException {
        String location = configuration.getPatternLocation();
        Path path = Paths.get(location);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException(String.format("File not found: %s", location));
        }

        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException(String.format("File isn't regular: %s", location));
        }

        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException(String.format("Cannot read file: %s", location));
        }

        HaijinConfig config = new HaijinConfig();

        Properties props = new Properties();
        props.load(Files.newInputStream(path));
        config.setProps(props);

        config.setPatternLocation(configuration.getPatternLocation());
        config.setTxnId(System.currentTimeMillis());
        config.setDisplayName(configuration.getDisplayName());
        config.setDeviceId(configuration.getDeviceId());
        config.setUserId(configuration.getUsername());
        config.setPassword(configuration.getPassword());
        config.setTimeout(configuration.getHttpClient().getTimeout().toMilliseconds() / 2);
        config.setSkipInitialSync(true);


        Bot<HaijinConfig, HaijinDao, PersistentService<HaijinDao>, Object> bot = new Bot<>(
            getClient(), configuration.getHomeserverUrl(), null, false, true, false, config, new PersistentService<>(new HaijinDao()),
            configuration.getCommands());

        bot.setInitAction((holder, dao) -> {
            MatrixClient matrixClient = holder.getMatrixClient();
            holder.getConfig().setState(matrixClient.room().joinedRooms().isEmpty() ? BotState.REGISTERED : BotState.JOINED);
        });

        getExecutorService().submit(bot);
    }

    @Override
    public void stop() throws InterruptedException {
        getExecutorService().awaitTermination(TIMEOUT, TimeUnit.SECONDS);
    }
}
