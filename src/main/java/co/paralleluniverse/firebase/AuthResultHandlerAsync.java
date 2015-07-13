package co.paralleluniverse.firebase;

//~--- non-JDK imports --------------------------------------------------------

import co.paralleluniverse.fibers.FiberAsync;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase.AuthResultHandler;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;

/**
 * This class is used to listen to the result of an authentication attempt.
 */
public abstract class AuthResultHandlerAsync
        extends FiberAsync<AuthData, FirebaseException>
        implements AuthResultHandler {

  /** {@inheritDoc} */
  @Override
  public void onAuthenticated(AuthData authData) {
    asyncCompleted(authData);
  }

  /** {@inheritDoc} */
  @Override
  public void onAuthenticationError(FirebaseError error) {
    asyncFailed(error.toException());
  }
}
