/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kuujo.copycat.functional;

import java.util.Set;

import net.kuujo.copycat.AsyncCopycat;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Submit command test.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@Test
public class SubmitTest extends FunctionalTest {
  private Set<AsyncCopycat> copycats;

  @BeforeMethod
  protected void beforeMethod() throws Throwable {
    copycats = startCluster(3);
  }

  @AfterMethod
  protected void stopCluster() throws Throwable {
    stopCluster(copycats);
  }

  public void shouldSubmitSingleCommand() throws Throwable {
    expectResume();
    final AsyncCopycat copycat = copycats.iterator().next();
    copycat.submit("set", "foo", "bar").whenComplete((result, error) -> {
      threadAssertNull(error);
      //resume();
    });

    await(30000);
  }
  
  public void shouldSubmitChainOfCommands() throws Throwable {
    Set<AsyncCopycat> copycats = startCluster(3);
    final AsyncCopycat copycat = copycats.iterator().next();
    copycat.on().leaderElect().run((event) -> {
      copycat.submit("set", "foo", "bar").thenRun(() -> {
        copycat.submit("set", "bar", "baz").thenRun(() -> {
          copycat.submit("get", "foo").whenComplete((result, error) -> {
            threadAssertNull(error);
            threadAssertEquals("bar", result);
            resume();
          });
        });
      });
    });

    await(10000);
  }
}
