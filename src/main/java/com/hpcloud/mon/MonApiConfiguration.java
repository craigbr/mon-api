/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hpcloud.mon;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.hpcloud.messaging.kafka.KafkaConfiguration;
import com.hpcloud.mon.infrastructure.middleware.MiddlewareConfiguration;

public class MonApiConfiguration extends Configuration {
  @NotNull public Boolean accessedViaHttps;
  @NotEmpty public String metricsTopic = "metrics";
  @NotEmpty public String eventsTopic = "events";
  @NotEmpty public String alarmStateTransitionsTopic = "alarm-state-transitions";

  @Valid @NotNull public DataSourceFactory mysql;
  @Valid @NotNull public DataSourceFactory vertica;
  @Valid @NotNull public KafkaConfiguration kafka;
  @Valid @NotNull public MiddlewareConfiguration middleware;
}
