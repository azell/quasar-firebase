package co.paralleluniverse.firebase;

//~--- non-JDK imports --------------------------------------------------------

import co.paralleluniverse.fibers.SuspendExecution;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Query;

public enum FirebaseUtil {
  INSTANCE;

  public long serverTime(Firebase ref) throws SuspendExecution {
    return System.currentTimeMillis() + serverTimeOffset(ref);
  }

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
