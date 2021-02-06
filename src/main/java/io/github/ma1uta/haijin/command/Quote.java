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

import static java.util.stream.Collectors.toList;

import io.github.ma1uta.haijin.PatternConfig;
import io.github.ma1uta.haijin.matrix.HaijinConfig;
import io.github.ma1uta.haijin.matrix.HaijinDao;
import io.github.ma1uta.matrix.bot.Command;
import io.github.ma1uta.matrix.bot.Context;
import io.github.ma1uta.matrix.bot.PersistentService;
import io.github.ma1uta.matrix.client.MatrixClient;
import io.github.ma1uta.matrix.event.RoomEvent;
import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
    public boolean invoke(Context<HaijinConfig, HaijinDao, PersistentService<HaijinDao>, Object> context, String roomId, RoomEvent event,
                          String arguments) {
        HaijinConfig config = context.getConfig();
        MatrixClient matrixClient = context.getMatrixClient();

        if (arguments == null || arguments.trim().isEmpty()) {
            matrixClient.event().sendNotice(roomId, "Usage: " + usage());
            return false;
        }

        String alias = arguments.trim();
        Optional<PatternConfig> patternConfig = config.getPatterns().stream().filter(pc -> pc.getAlias().equals(alias)).findFirst();

        if (!patternConfig.isPresent()) {
            return false;
        }

        long minIndex = config.getMinIndex();
        long maxIndex = config.getMaxIndex();
        long poetryIndex = RandomUtils.nextLong(minIndex, maxIndex + 1);

        String siteAddress = patternConfig.get().getUrl();
        if (!siteAddress.endsWith("/")) {
            siteAddress += "/";
        }
        URL url;
        try {
            url = new URL(siteAddress + poetryIndex);
        } catch (MalformedURLException e) {
            String msg = "wrong site url: " + e.getMessage();
            LOGGER.error(msg, e);
            matrixClient.event().sendNotice(roomId, msg);
            return true;
        }

        Elements elements;
        try {
            elements = Jsoup.parse(url, TIMEOUT).select(patternConfig.get().getSelector());
        } catch (IOException e) {
            String msg = "Cannot read url: " + alias;
            LOGGER.error(msg, e);
            matrixClient.event().sendNotice(roomId, msg);
            return true;
        }

        if (!elements.isEmpty()) {
            Element element = elements.first();
            Elements links = element.select("a");
            for (Element link : links) {
                link.remove();
            }
            String[] origins = Jsoup.clean(element.html(), new Whitelist().addTags("br")).split("<br>");
            List<String> lines = Arrays.stream(origins).filter(Objects::nonNull).filter(line -> !line.trim().isEmpty()).map(String::trim)
                .collect(toList());
            String htmlText = String.join("<br>", lines);
            String plainText = String.join("\n", lines);
            matrixClient.event().sendFormattedNotice(roomId, plainText, htmlText);
        }
        return true;
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
