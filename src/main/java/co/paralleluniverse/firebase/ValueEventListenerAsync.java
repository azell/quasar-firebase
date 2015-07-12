package co.paralleluniverse.firebase;

//~--- non-JDK imports --------------------------------------------------------

import co.paralleluniverse.fibers.FiberAsync;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.ValueEventListener;

public abstract class ValueEventListenerAsync
        extends FiberAsync<DataSnapshot, FirebaseException>
        implements ValueEventListener {

  /** {@inheritDoc} */
  @Override
  public void onCancelled(FirebaseError error) {
    asyncFailed(error.toException());
  }

  /** {@inheritDoc} */
  @Override
  public void onDataChange(DataSnapshot snapshot) {
    asyncCompleted(snapshot);
  }
}
