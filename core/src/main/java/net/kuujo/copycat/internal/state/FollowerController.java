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
package net.kuujo.copycat.internal.state;

import net.kuujo.copycat.CopycatState;
import net.kuujo.copycat.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Follower state.<p>
 *
 * The follower state is the initial state of any replica once it
 * has been started, and for most replicas it remains the state for
 * most of their lifetime. Followers simply serve to listen for
 * synchronization requests from the cluster <code>Leader</code>
 * and dutifully maintain their logs according to those requests.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class FollowerController extends StateController {
  private static final Logger LOGGER = LoggerFactory.getLogger(FollowerController.class);
  private ScheduledFuture<Void> currentTimer;
  private boolean shutdown = true;

  @Override
  CopycatState state() {
    return CopycatState.FOLLOWER;
  }

  @Override
  Logger logger() {
    return LOGGER;
  }

  @Override
  public synchronized void init(StateContext context) {
    shutdown = false;
    super.init(context);
    LOGGER.debug("{} - Starting heartbeat timer", context.clusterManager().localNode());
    resetTimer();
  }

  /**
   * Resets the election timer.
   */
  private synchronized void resetTimer() {
    if (!shutdown) {
      // If a timer is already set, cancel the timer.
      if (currentTimer != null) {
        LOGGER.debug("{} - Reset heartbeat timeout", context.clusterManager().localNode());
        currentTimer.cancel(true);
      }

      // Reset the last voted for candidate.
      context.lastVotedFor(null);

      // Set the election timeout in a semi-random fashion with the random range
      // being somewhere between .75 * election timeout and 1.25 * election
      // timeout.
      long delay = context.config().getElectionTimeout() - (context.config().getElectionTimeout() / 4)
          + (Math.round(Math.random() * (context.config().getElectionTimeout() / 2)));
      currentTimer = context.config().getTimerStrategy().schedule(() -> {
        // If the node has not yet voted for anyone then transition to
        // candidate and start a new election.
        currentTimer = null;
        if (context.lastVotedFor() == null) {
          LOGGER.info("{} - Heartbeat timed out", context.clusterManager().localNode());
          context.transition(CandidateController.class);
        } else {
          // If the node voted for a candidate then reset the election timer.
          resetTimer();
        }
      }, delay, TimeUnit.MILLISECONDS);
    }
  }

  @Override
  public CompletableFuture<PingResponse> ping(PingRequest request) {
    resetTimer();
    return super.ping(request);
  }

  @Override
  public CompletableFuture<SyncResponse> sync(SyncRequest request) {
    resetTimer();
    return super.sync(request);
  }

  @Override
  public CompletableFuture<PollResponse> poll(PollRequest request) {
    return super.poll(request);
  }

  @Override
  public synchronized void destroy() {
    if (currentTimer != null) {
      LOGGER.debug("{} - Cancelling heartbeat timer", context.clusterManager().localNode());
      currentTimer.cancel(true);
    }
    shutdown = true;
  }

  @Override
  public String toString() {
    return String.format("FollowerController[context=%s]", context);
  }

}
