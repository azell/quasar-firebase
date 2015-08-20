package com.github.azell.firebase;

//~--- non-JDK imports --------------------------------------------------------

import co.paralleluniverse.fibers.FiberAsync;

import com.firebase.client.Firebase;
import com.firebase.client.Firebase.CompletionListener;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;

/**
 * This class is used as a method of being notified when an operation has been
 * acknowledged by the Firebase servers and can be considered complete.
 */
public abstract class CompletionListenerAsync
        extends FiberAsync<Firebase, FirebaseException>
        implements CompletionListener {

  /** {@inheritDoc} */
  @Override
  public void onComplete(FirebaseError error, Firebase ref) {
    if (error == null) {
      asyncCompleted(ref);
    } else {
      asyncFailed(error.toException());
    }
  }
}
