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
package net.kuujo.copycat.protocol;

/**
 * Submit command response.<p>
 *
 * Submit responses are sent back to the forwarding node upon
 * successful submission of a command.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class SubmitResponse extends Response {
  private static final long serialVersionUID = -2137570252386650195L;
  private Object result;

  public SubmitResponse() {
  }

  public SubmitResponse(Object id, Object result) {
    super(id, Status.OK);
    this.result = result;
  }

  public SubmitResponse(Object id, Throwable t) {
    super(id, Status.ERROR, t);
  }

  public SubmitResponse(Object id, String message) {
    super(id, Status.ERROR, message);
  }

  /**
   * Returns the command result.
   *
   * @return The command execution result.
   */
  public Object result() {
    return result;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof SubmitResponse) {
      SubmitResponse response = (SubmitResponse) object;
      return response.id().equals(id()) && response.status().equals(status()) && response.result.equals(result);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hashCode = 23;
    hashCode = 37 * hashCode + id().hashCode();
    hashCode = 37 * hashCode + status().hashCode();
    hashCode = 37 * hashCode + result.hashCode();
    return hashCode;
  }

  @Override
  public String toString() {
    return String.format("%s[id=%s, result=%s]", getClass().getSimpleName(), id(), result);
  }

}
