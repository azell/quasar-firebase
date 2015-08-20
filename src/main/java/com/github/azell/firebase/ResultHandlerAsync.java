package com.github.azell.firebase;

//~--- non-JDK imports --------------------------------------------------------

import co.paralleluniverse.fibers.FiberAsync;

import com.firebase.client.Firebase.ResultHandler;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;

/**
 * This class is used to handle the outcome of an operation.
 */
public abstract class ResultHandlerAsync
        extends FiberAsync<Void, FirebaseException> implements ResultHandler {

  /** {@inheritDoc} */
  @Override
  public void onError(FirebaseError error) {
    asyncFailed(error.toException());
  }

  /** {@inheritDoc} */
  @Override
  public void onSuccess() {
    asyncCompleted(null);
  }
}
