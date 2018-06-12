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

import io.github.ma1uta.matrix.bot.BotDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Persist only props and txnid.
 */
public class HaijinDao implements BotDao<HaijinConfig> {

    /**
     * Transaction id.
     */
    public static final String TXN_ID = "0.txnid";

    /**
     * Next batch.
     */
    public static final String NEXT_BATCH = "0.next_batch";

    /**
     * Room where bot is live.
     */
    public static final String ROOM = "0.room";

    private static final Logger LOGGER = LoggerFactory.getLogger(HaijinDao.class);

    @Override
    public List<HaijinConfig> findAll() {
        return Collections.emptyList();
    }

    @Override
    public boolean user(String userId) {
        return false;
    }

    @Override
    public HaijinConfig save(HaijinConfig data) {
        Path path = Paths.get(data.getPatternLocation());
        if (Files.exists(path) && Files.isRegularFile(path) && Files.isWritable(path)) {
            Properties props = data.getProps();
            Properties propertiesToSave = new Properties(props);
            props.stringPropertyNames().forEach(propName -> propertiesToSave.setProperty(propName, props.getProperty(propName)));
            propertiesToSave.setProperty(TXN_ID, Long.toString(data.getTxnId()));
            propertiesToSave.setProperty(NEXT_BATCH, data.getNextBatch());
            propertiesToSave.setProperty(ROOM, data.getRoomId());
            try {
                propertiesToSave.store(Files.newOutputStream(path), "haijin patterns");
            } catch (IOException e) {
                LOGGER.error("Cannot store props.");
            }
        }
        return data;
    }

    @Override
    public void delete(HaijinConfig data) {
    }
}
