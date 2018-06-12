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

package io.github.ma1uta.haijin.command;

import io.github.ma1uta.haijin.matrix.HaijinConfig;
import io.github.ma1uta.haijin.matrix.HaijinDao;
import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.bot.BotHolder;
import io.github.ma1uta.matrix.bot.Command;
import io.github.ma1uta.matrix.bot.PersistentService;
import io.github.ma1uta.matrix.client.MatrixClient;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Find quote on the selected site.
 */
public class Quote implements Command<HaijinConfig, HaijinDao, PersistentService<HaijinDao>, Object> {

    private static final int TIMEOUT = 20 * 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(Quote.class);

    @Override
    public String name() {
        return "quote";
    }

    @Override
    public void invoke(BotHolder<HaijinConfig, HaijinDao, PersistentService<HaijinDao>, Object> holder, Event event, String arguments) {
        HaijinConfig config = holder.getConfig();
        MatrixClient matrixClient = holder.getMatrixClient();

        if (arguments == null || arguments.trim().isEmpty()) {
            matrixClient.event().sendNotice(config.getRoomId(), "Usage: " + usage());
            return;
        }

        String siteArgument = arguments.trim();
        Optional<String> siteKey = config.getProps().stringPropertyNames().stream()
            .filter(sitePattern -> sitePattern.contains(siteArgument)).findFirst();
        if (!siteKey.isPresent()) {
            matrixClient.event().sendNotice(config.getRoomId(), "Cannot found pattern for site: " + siteArgument);
            return;
        }
        String siteAddress = siteKey.get();
        String regexp = config.getProps().getProperty(siteAddress);
        Pattern pattern = Pattern.compile(regexp);

        URL url;
        try {
            url = new URL(siteAddress);
        } catch (MalformedURLException e) {
            String msg = "wrong site url: " + e.getMessage();
            LOGGER.error(msg, e);
            matrixClient.event().sendNotice(config.getRoomId(), msg);
            return;
        }

        String html;
        try {
            html = Jsoup.parse(url, TIMEOUT).html();
        } catch (IOException e) {
            String msg = "Cannot read url: " + siteArgument;
            LOGGER.error(msg, e);
            matrixClient.event().sendNotice(config.getRoomId(), msg);
            return;
        }

        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String quote = matcher.group(1);

            matrixClient.event().sendNotice(config.getRoomId(), quote);
        } else {
            matrixClient.event().sendNotice(config.getRoomId(), "Nothing.");
        }
    }

    @Override
    public String help() {
        return "find quote on the selected site.";
    }

    @Override
    public String usage() {
        return "!quote <site>";
    }
}
