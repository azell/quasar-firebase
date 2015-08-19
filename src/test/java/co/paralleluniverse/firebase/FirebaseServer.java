package co.paralleluniverse.firebase;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Throwables;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;

import java.util.Optional;
import java.util.logging.Logger;

public final class FirebaseServer {
  private static final String RUNNER = "server.js";
  private Optional<Process>   proc   = Optional.empty();

  public static File file(Class<?> cls, String path) {
    try {
      return new File(cls.getResource(path).toURI());
    } catch (URISyntaxException e) {
      throw Throwables.propagate(e);
    }
  }

  public boolean running() {
    return proc.isPresent();
  }

  public void start(File file) {
    try {
      File home = new File(System.getProperty("firebase.server"));

      proc = Optional.of(new ProcessBuilder("node", RUNNER,
              file.getAbsolutePath()).directory(home).inheritIO().start());
    } catch (IOException e) {
      warn(e);
    }
  }

  public void stop() throws InterruptedException {
    if (running()) {
      Process impl = proc.get();

      impl.destroyForcibly();
      impl.waitFor();
    }

    proc = Optional.empty();
  }

  private void warn(Throwable thrown) {
    Logger logger = Logger.getLogger(getClass().getName());

    logger.warning(thrown.getMessage());
  }
}
