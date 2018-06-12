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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.sslreload.SslReloadBundle;
import io.github.ma1uta.haijin.matrix.HaijinPool;

import javax.ws.rs.client.Client;

/**
 * Bot.
 */
public class Bot extends Application<Configuration> {

    /**
     * Entry point.
     *
     * @param args arguments.
     * @throws Exception never throws.
     */
    public static void main(String[] args) throws Exception {
        new Bot().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) {
        matrixBot(configuration, environment);
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
            new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
        bootstrap.addBundle(new SslReloadBundle());

        bootstrap.getObjectMapper().enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
    }

    @SuppressWarnings("unchecked")
    private void matrixBot(Configuration configuration, Environment environment) {
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        environment.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        Client client = new JerseyClientBuilder(environment).using(configuration.getHttpClient()).build("client");

        environment.lifecycle().manage(new HaijinPool(client, configuration));
    }
}
