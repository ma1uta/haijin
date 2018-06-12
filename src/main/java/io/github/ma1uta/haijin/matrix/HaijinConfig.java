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

import io.github.ma1uta.matrix.bot.BotConfig;

import java.util.Properties;

/**
 * All settings store in the properties files.
 */
public class HaijinConfig extends BotConfig {

    /**
     * Map &lt;site&gt; - &lt;regexp&gt;.
     */
    private Properties props;

    /**
     * Path to properties file.
     */
    private String patternLocation;

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public String getPatternLocation() {
        return patternLocation;
    }

    public void setPatternLocation(String patternLocation) {
        this.patternLocation = patternLocation;
    }
}
