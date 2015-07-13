package co.paralleluniverse.firebase;

//~--- non-JDK imports --------------------------------------------------------

import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.firebase.TransactionHandlerAsync.Status;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

//~--- JDK imports ------------------------------------------------------------

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class TransactionHandlerCachedAsync
        extends FiberAsync<Status, FirebaseException>
        implements Transaction.Handler, ValueEventListener {
  private final AtomicBoolean flag = new AtomicBoolean();

  /** {@inheritDoc} */
  @Override
  public void onComplete(FirebaseError error, boolean committed,
                         DataSnapshot currentData) {
    currentData.getRef().removeEventListener(this);

    if (error == null) {
      asyncCompleted(new Status(committed, currentData));
    } else {
      asyncFailed(error.toException());
    }
  }

  /** {@inheritDoc} */
  @Override
  public void onCancelled(FirebaseError error) {
    asyncFailed(error.toException());
  }

  /** {@inheritDoc} */
  @Override
  public void onDataChange(DataSnapshot snapshot) {
    if (flag.compareAndSet(false, true)) {
      snapshot.getRef().runTransaction(this, false);
    }
  }
}
