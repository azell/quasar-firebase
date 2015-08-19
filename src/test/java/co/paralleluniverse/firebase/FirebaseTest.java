package co.paralleluniverse.firebase;

//~--- non-JDK imports --------------------------------------------------------

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;

import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import co.paralleluniverse.firebase.TransactionHandlerAsync.Status;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;

import java.util.concurrent.atomic.AtomicInteger;

@Test
public class FirebaseTest {
  private final FirebaseServer server = new FirebaseServer();
  private Firebase             ref;

  @BeforeClass
  public void start() {
    File file = FirebaseServer.file(getClass(), "db.json");

    server.start(file);

    if (server.running()) {
      ref = new Firebase("ws://127.0.0.1:5000");
    }
  }

  @AfterClass
  public void stop() throws InterruptedException {
    Firebase.goOffline();

    server.stop();
  }

  @BeforeMethod
  public void verify() {
    if (ref == null) {
      throw new SkipException("Firebase reference not set");
    }
  }

  public void shouldRetrieveServerTime() {
    QuasarUtil.INSTANCE.runInFiber(() ->
      FirebaseUtil.INSTANCE.serverTime(ref));
  }

  public void shouldSetValue() {
    Firebase lhs = ref.child("foo");
    Firebase rhs = QuasarUtil.INSTANCE.runInFiber(() -> {
      return new CompletionListenerAsync() {
        @Override
        protected void requestAsync() {
          lhs.setValue("bar", this);
        }
      }.run();
    });

    assertEquals(lhs, rhs);
  }

  public void shouldGetValueOnce() {
    Firebase     child = ref.child("states/CA");
    DataSnapshot snap  = QuasarUtil.INSTANCE.runInFiber(() -> {
      return new ValueEventListenerAsync() {
        @Override
        protected void requestAsync() {
          child.addListenerForSingleValueEvent(this);
        }
      }.run();
    });

    assertEquals(snap.getRef(), child);
    assertEquals(snap.getValue(), "California");
  }

  public void shouldRunCachedTransaction() {
    Firebase child  = ref.child("states/NY");
    Status   status = QuasarUtil.INSTANCE.runInFiber(() -> {
      return new TransactionHandlerCachedAsync() {
        @Override
        protected void requestAsync() {
          child.addValueEventListener(this);
        }

        @Override
        public Transaction.Result doTransaction(MutableData snap) {
          snap.setValue("New York");

          return Transaction.success(snap);
        }
      }.run();
    });

    verify(status, child, "New York");
  }

  public void shouldRunUncachedTransaction() {
    AtomicInteger count  = new AtomicInteger(0);

    Firebase      child  = ref.child("states/AL");
    Status        status = QuasarUtil.INSTANCE.runInFiber(() -> {
      return new TransactionHandlerAsync() {
        @Override
        protected void requestAsync() {
          child.runTransaction(this, false);
        }

        @Override
        public Transaction.Result doTransaction(MutableData snap) {
          Object obj = snap.getValue();

          if (count.getAndIncrement() == 0) {
            assertNull(obj);
          } else {
            assertNotNull(obj);

            snap.setValue("Roll Tide");
          }

          return Transaction.success(snap);
        }
      }.run();
    });

    verify(status, child, "Roll Tide");
  }

  private void verify(Status status, Firebase child, Object value) {
    assertTrue(status.committed());

    DataSnapshot snap = status.currentData();

    assertEquals(snap.getRef(), child);
    assertEquals(snap.getValue(), value);
  }
}
