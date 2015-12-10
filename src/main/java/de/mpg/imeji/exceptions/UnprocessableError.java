package de.mpg.imeji.exceptions;

import java.util.HashSet;
import java.util.Set;

public class UnprocessableError extends ImejiException {
  /**
   * 
   */
  private static final long serialVersionUID = -2949658202758865427L;

  private Set<String> messages = new HashSet<>();

  public UnprocessableError(String message) {
    super(message);
    messages.add(message);
  }

  public UnprocessableError(Set<String> messages) {
    super(messages.toArray().toString());
    this.messages = messages;
  }

  public UnprocessableError(String message, Throwable e) {
    super(message, e);
  }

  public Set<String> getMessages() {
    return messages;
  }
}
