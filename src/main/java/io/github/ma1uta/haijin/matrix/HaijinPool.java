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
    public void start() {
        HaijinConfig config = new HaijinConfig();

        config.setPatterns(configuration.getPatterns());
        config.setDisplayName(configuration.getDisplayName());
        config.setDeviceId(configuration.getDeviceId());
        config.setUserId(configuration.getUsername());
        config.setPassword(configuration.getPassword());
        config.setTimeout(configuration.getHttpClient().getTimeout().toMilliseconds() / 2);
        config.setSkipInitialSync(true);
        config.setDefaultCommand(configuration.getDefaultCommand());
        config.setOwner(configuration.getOwner());

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
