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

public enum QuasarUtil {
  INSTANCE;

  public <V> V runInFiber(SuspendableCallable<V> target) {
    FiberScheduler scheduler = DefaultFiberScheduler.getInstance();

    try {
      return new Fiber<V>(scheduler, target).start().get();
    } catch (ExecutionException | InterruptedException e) {
      throw Throwables.propagate(e);
    }
  }

  public <V, E extends Throwable> V run(FiberAsync<V, E> target)
          throws E, SuspendExecution {
    try {
      return target.run();
    } catch (InterruptedException e) {
      throw Throwables.propagate(e);
    }
  }
}
