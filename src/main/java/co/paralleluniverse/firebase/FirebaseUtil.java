package co.paralleluniverse.firebase;

//~--- non-JDK imports --------------------------------------------------------

import co.paralleluniverse.fibers.SuspendExecution;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Query;

/**
 * This class exposes Firebase functionality that builds on top of the fiber
 * model.
 */
public enum FirebaseUtil {
  INSTANCE;

  /**
   * Retrieves the Firebase server time based off of the local server time
   * offset.
   *
   * @param  ref an authenticated Firebase reference.
   *
   * @return the Firebase server time.
   *
   * @throws SuspendExecution for internal Quasar use.
   */
  public long serverTime(Firebase ref) throws SuspendExecution {
    return System.currentTimeMillis() + serverTimeOffset(ref);
  }

  /**
   * Retrieves the local server time offset.
   *
   * @param  ref an authenticated Firebase reference.
   *
   * @return the local server time offset.
   *
   * @throws SuspendExecution for internal Quasar use.
   */
  public long serverTimeOffset(Firebase ref) throws SuspendExecution {
    DataSnapshot snap = QuasarUtil.INSTANCE.run(new ValueEventListenerAsync() {
      @Override
      protected void requestAsync() {
        Query query = ref.getRoot().child(".info/serverTimeOffset");

        query.addListenerForSingleValueEvent(this);
      }
    });

    return (Long) snap.getValue();
  }
}
