package com.github.azell.firebase;

//~--- non-JDK imports --------------------------------------------------------

import co.paralleluniverse.fibers.FiberAsync;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import com.github.azell.firebase.TransactionHandlerAsync.Status;

//~--- JDK imports ------------------------------------------------------------

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is used to run a transaction, and will be notified of the results
 * of the transaction. To prevent the handler from receiving a null value when
 * the location has not been cached, implementations should install a listener
 * in the <code>requestAsync</code> method which invokes
 * <code>Query.addValueEventListener</code>. The listener will be removed when
 * the transaction has completed.
 */
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
