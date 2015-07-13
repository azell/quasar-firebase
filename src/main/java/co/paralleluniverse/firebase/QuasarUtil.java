package co.paralleluniverse.firebase;

//~--- non-JDK imports --------------------------------------------------------

import co.paralleluniverse.fibers.DefaultFiberScheduler;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableCallable;

import com.google.common.base.Throwables;

//~--- JDK imports ------------------------------------------------------------

import java.util.concurrent.ExecutionException;

/**
 * This class attempts to simplify the Quasar API.
 */
public enum QuasarUtil {
  INSTANCE;

  /**
   * Runs the asynchronous operation in a new fiber, blocks until it completes
   * and returns its result. Throws an exception if the operation has failed.
   *
   * @param  target the asynchronous operation.
   *
   * @return the result of the async operation.
   */
  public <V> V runInFiber(SuspendableCallable<V> target) {
    FiberScheduler scheduler = DefaultFiberScheduler.getInstance();

    try {
      return new Fiber<V>(scheduler, target).start().get();
    } catch (ExecutionException | InterruptedException e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * Runs the asynchronous operation, blocks until it completes and returns its
   * result. Throws an exception if the operation has failed.
   *
   * @param  target the asynchronous operation.
   *
   * @return the result of the async operation.
   */
  public <V, E extends Throwable> V run(FiberAsync<V, E> target)
          throws E, SuspendExecution {
    try {
      return target.run();
    } catch (InterruptedException e) {
      throw Throwables.propagate(e);
    }
  }
}
