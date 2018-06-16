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

import io.github.ma1uta.haijin.PatternConfig;
import io.github.ma1uta.haijin.matrix.HaijinConfig;
import io.github.ma1uta.haijin.matrix.HaijinDao;
import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.bot.BotHolder;
import io.github.ma1uta.matrix.bot.Command;
import io.github.ma1uta.matrix.bot.PersistentService;
import io.github.ma1uta.matrix.client.MatrixClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

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
    public void invoke(BotHolder<HaijinConfig, HaijinDao, PersistentService<HaijinDao>, Object> holder, String roomId, Event event,
                       String arguments) {
        HaijinConfig config = holder.getConfig();
        MatrixClient matrixClient = holder.getMatrixClient();

        if (arguments == null || arguments.trim().isEmpty()) {
            matrixClient.event().sendNotice(roomId, "Usage: " + usage());
            return;
        }

        String alias = arguments.trim();
        Optional<PatternConfig> patternConfig = config.getPatterns().stream().filter(pc -> pc.getAlias().equals(alias)).findFirst();

        if (!patternConfig.isPresent()) {
            return;
        }

        String siteAddress = patternConfig.get().getUrl();
        URL url;
        try {
            url = new URL(siteAddress);
        } catch (MalformedURLException e) {
            String msg = "wrong site url: " + e.getMessage();
            LOGGER.error(msg, e);
            matrixClient.event().sendNotice(roomId, msg);
            return;
        }

        Elements elements;
        try {
            elements = Jsoup.parse(url, TIMEOUT).select(patternConfig.get().getSelector());
        } catch (IOException e) {
            String msg = "Cannot read url: " + alias;
            LOGGER.error(msg, e);
            matrixClient.event().sendNotice(roomId, msg);
            return;
        }

        if (!elements.isEmpty()) {
            Element element = elements.first();
            Elements links = element.select("a");
            for (Element link : links) {
                link.remove();
            }
            String htmlText = element.html();
            String plainText = element.text();
            matrixClient.event().sendFormattedNotice(roomId, plainText, htmlText);
        }
    }

    @Override
    public String help() {
        return "find quote on the selected site.";
    }

    @Override
    public String usage() {
        return "quote <site>";
    }
}
