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
package com.hpcloud.mon.app.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import com.hpcloud.mon.common.model.Services;
import com.hpcloud.mon.resource.exception.Exceptions;

/**
 * Utilities for validating dimensions.
 */
public final class DimensionValidation {
  private static final Map<String, DimensionValidator> VALIDATORS;
  private static final Pattern UUID_PATTERN = Pattern.compile("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}");
  private static final Pattern VALID_DIMENSION_NAME = Pattern.compile("^[a-zA-Z0-9_\\.\\-]+$");

  private DimensionValidation() {
  }

  interface DimensionValidator {
    boolean isValidDimension(String name, String value);
  }

  static {
    VALIDATORS = new HashMap<String, DimensionValidator>();

    // Compute validator
    VALIDATORS.put(Services.COMPUTE_SERVICE, new DimensionValidator() {
      @Override
      public boolean isValidDimension(String name, String value) {
        if ("instance_id".equals(name))
          return value.length() != 36 || UUID_PATTERN.matcher(value).matches();
        if ("az".equals(name))
          return Ints.tryParse(value) != null;
        return true;
      }
    });

    // Objectstore validator
    VALIDATORS.put(Services.OBJECT_STORE_SERVICE, new DimensionValidator() {
      @Override
      public boolean isValidDimension(String name, String value) {
        if ("container".equals(name))
          return value.length() < 256 || !value.contains("/");
        return true;
      }
    });

    // Volume validator
    VALIDATORS.put(Services.VOLUME_SERVICE, new DimensionValidator() {
      @Override
      public boolean isValidDimension(String name, String value) {
        if ("instance_id".equals(name))
          return value.length() != 36 || UUID_PATTERN.matcher(value).matches();
        if ("az".equals(name))
          return Ints.tryParse(value) != null;
        return true;
      }
    });
  }

  /**
   * Normalizes dimensions by stripping whitespace.
   */
  public static Map<String, String> normalize(Map<String, String> dimensions) {
    if (dimensions == null)
      return null;
    Map<String, String> result = new HashMap<>();
    for (Map.Entry<String, String> dimension : dimensions.entrySet()) {
      String dimensionKey = null;
      if (dimension.getKey() != null) {
        dimensionKey = CharMatcher.WHITESPACE.trimFrom(dimension.getKey());
        if (dimensionKey.isEmpty())
          dimensionKey = null;
      }
      String dimensionValue = null;
      if (dimension.getValue() != null) {
        dimensionValue = CharMatcher.WHITESPACE.trimFrom(dimension.getValue());
        if (dimensionValue.isEmpty())
          dimensionValue = null;
      }
      result.put(dimensionKey, dimensionValue);
    }

    return result;
  }

  /**
   * Validates that the given {@code dimensions} are valid.
   * 
   * @throws WebApplicationException if validation fails
   */
  public static void validate(Map<String, String> dimensions, @Nullable String service) {
    // Validate dimension names and values
    for (Map.Entry<String, String> dimension : dimensions.entrySet()) {
      String name = dimension.getKey();
      String value = dimension.getValue();

      // General validations
      if (Strings.isNullOrEmpty(name))
        throw Exceptions.unprocessableEntity("Dimension name cannot be empty");
      if (Strings.isNullOrEmpty(value))
        throw Exceptions.unprocessableEntity("Dimension %s cannot have an empty value", name);
      if (name.length() > 255)
        throw Exceptions.unprocessableEntity("Dimension name %s must be 255 characters or less",
            name);
      if (value.length() > 255)
        throw Exceptions.unprocessableEntity("Dimension value %s must be 255 characters or less",
            value);
      if (!VALID_DIMENSION_NAME.matcher(name).matches())
        throw Exceptions.unprocessableEntity(
            "Dimension name %s may only contain: a-z A-Z 0-9 _ - .", name);

      // Service specific validations
      if (service != null) {
        if (!name.equals(Services.SERVICE_DIMENSION)
            && !Services.isValidDimensionName(service, name))
          throw Exceptions.unprocessableEntity("%s is not a valid dimension name for service %s",
              name, service);
        DimensionValidator validator = VALIDATORS.get(service);
        if (validator != null && !validator.isValidDimension(name, value))
          throw Exceptions.unprocessableEntity("%s is not a valid dimension value for service %s",
              value, service);
      }
    }
  }
}
