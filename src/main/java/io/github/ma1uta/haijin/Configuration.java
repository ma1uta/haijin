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

package io.github.ma1uta.haijin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;
import io.github.ma1uta.haijin.matrix.HaijinConfig;
import io.github.ma1uta.haijin.matrix.HaijinDao;
import io.github.ma1uta.matrix.bot.Command;
import io.github.ma1uta.matrix.bot.PersistentService;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * BotApplication configuration.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration extends io.dropwizard.Configuration {

    @Valid
    @NotNull
    @JsonProperty("httpClient")
    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

    @NotEmpty
    @URL
    private String homeserverUrl;

    private String displayName;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String patternLocation;

    private String deviceId;

    @NotBlank
    private String initialRoom;

    private List<Class<? extends Command<HaijinConfig, HaijinDao, PersistentService<HaijinDao>, Object>>> commands = new ArrayList<>();

    public JerseyClientConfiguration getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(JerseyClientConfiguration httpClient) {
        this.httpClient = httpClient;
    }

    public String getHomeserverUrl() {
        return homeserverUrl;
    }

    public void setHomeserverUrl(String homeserverUrl) {
        this.homeserverUrl = homeserverUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPatternLocation() {
        return patternLocation;
    }

    public void setPatternLocation(String patternLocation) {
        this.patternLocation = patternLocation;
    }

    public List<Class<? extends Command<HaijinConfig, HaijinDao, PersistentService<HaijinDao>, Object>>> getCommands() {
        return commands;
    }

    public void setCommands(
        List<Class<? extends Command<HaijinConfig, HaijinDao, PersistentService<HaijinDao>, Object>>> commands) {
        this.commands = commands;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getInitialRoom() {
        return initialRoom;
    }

    public void setInitialRoom(String initialRoom) {
        this.initialRoom = initialRoom;
    }
}
