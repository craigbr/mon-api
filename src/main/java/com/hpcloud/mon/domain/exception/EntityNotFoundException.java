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
package com.hpcloud.mon.domain.exception;

/**
 * Indicates that a domain entity is unknown.
 */
public class EntityNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public EntityNotFoundException(Exception ex, String msg) {
    super(msg, ex);
  }

  public EntityNotFoundException(Exception ex, String msg, Object... args) {
    super(String.format(msg, args), ex);
  }

  public EntityNotFoundException(String msg) {
    super(msg);
  }

  public EntityNotFoundException(String msg, Object... args) {
    super(String.format(msg, args));
  }
}
