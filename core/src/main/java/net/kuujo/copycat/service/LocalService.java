/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kuujo.copycat.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.kuujo.copycat.Copycat;

/**
 * Local service implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class LocalService extends AbstractService {

  public LocalService(Copycat copycat) {
    super(copycat);
  }

  @Override
  public CompletableFuture<Void> start() {
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<Void> stop() {
    return CompletableFuture.completedFuture(null);
  }

  /**
   * Submits a command via the service.
   *
   * @param command The command to submit.
   * @param args The command arguments.
   * @return A completable future to be completed once the result is received.
   */
  public <R> CompletableFuture<R> submitCommand(String command, List<Object> args) {
    return submit(command, args);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
