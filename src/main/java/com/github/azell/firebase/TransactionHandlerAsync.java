package com.github.azell.firebase;

//~--- non-JDK imports --------------------------------------------------------

import co.paralleluniverse.fibers.FiberAsync;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.Transaction;

/**
 * This class is used to run a transaction, and will be notified of the results
 * of the transaction. If there are no active listeners for the given location,
 * the first invocation of the handler will have a null value.
 */
public abstract class TransactionHandlerAsync
        extends FiberAsync<TransactionHandlerAsync.Status, FirebaseException>
        implements Transaction.Handler {

  /** {@inheritDoc} */
  @Override
  public void onComplete(FirebaseError error, boolean committed,
                         DataSnapshot currentData) {
    if (error == null) {
      asyncCompleted(new Status(committed, currentData));
    } else {
      asyncFailed(error.toException());
    }
  }

  /**
   * This class stores transaction results.
   */
  public static class Status {
    private final boolean      committed;
    private final DataSnapshot currentData;

    public Status(boolean committed, DataSnapshot currentData) {
      this.committed   = committed;
      this.currentData = currentData;
    }

    public boolean committed() {
      return committed;
    }

    public DataSnapshot currentData() {
      return currentData;
    }
  }
}
