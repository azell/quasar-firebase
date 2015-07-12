package co.paralleluniverse.firebase;

//~--- non-JDK imports --------------------------------------------------------

import co.paralleluniverse.fibers.FiberAsync;

import com.firebase.client.Firebase;
import com.firebase.client.Firebase.CompletionListener;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;

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
