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
package net.kuujo.copycat.test;

import net.kuujo.copycat.Command;
import net.kuujo.copycat.Query;
import net.kuujo.copycat.StateMachine;

import java.util.HashMap;
import java.util.Map;

/**
 * Test state machine.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class TestStateMachine implements StateMachine {
  private String data = "Hello world!";
  private final TestStateMachineEvents events;
  private final Map<String, Runnable> listeners = new HashMap<>(10);

  public TestStateMachine() {
    events = new TestStateMachineEvents(this);
  }

  /**
   * Returns test state machine event listeners.
   */
  public TestStateMachineEvents await() {
    return events;
  }

  void addSnapshotListener(Runnable callback) {
    listeners.put("snapshot", callback);
  }

  void addInstallListener(Runnable callback) {
    listeners.put("install", callback);
  }

  void addCommandListener(Runnable callback) {
    listeners.put("command", callback);
  }

  void addQueryListener(Runnable callback) {
    listeners.put("query", callback);
  }

  /**
   * Returns the state machine data.
   */
  public String data() {
    return data;
  }

  /**
   * Sets the state machine data.
   */
  public void data(String data) {
    this.data = data;
  }

  @Override
  public byte[] takeSnapshot() {
    Runnable listener = listeners.remove("snapshot");
    if (listener != null) {
      listener.run();
    }
    return data.getBytes();
  }

  @Override
  public void installSnapshot(byte[] snapshot) {
    Runnable listener = listeners.remove("install");
    if (listener != null) {
      listener.run();
    }
    data = new String(snapshot);
  }

  @Command
  public String command(String arg) {
    Runnable listener = listeners.remove("command");
    if (listener != null) {
      listener.run();
    }
    return arg;
  }

  @Query
  public String query(String arg) {
    Runnable listener = listeners.remove("query");
    if (listener != null) {
      listener.run();
    }
    return arg;
  }

}
