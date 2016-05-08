package com.github.azell.firebase;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Throwables;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;

import java.util.logging.Logger;

public final class FirebaseServer {
  private static final String RUNNER = "server.js";
  private Process             proc;

  public static File file(Class<?> cls, String path) {
    try {
      return new File(cls.getResource(path).toURI());
    } catch (URISyntaxException e) {
      throw Throwables.propagate(e);
    }
  }

  public boolean running() {
    return proc != null;
  }

  public void start(File data, File rules, String secret) {
    try {
      File home = new File(System.getProperty("firebase.server"));

      proc = new ProcessBuilder("node",
                                RUNNER,
                                "--data",
                                data.getAbsolutePath(),
                                "--rules",
                                rules.getAbsolutePath(),
                                "--secret",
                                secret).directory(home)
                                       .inheritIO()
                                       .start();
    } catch (IOException e) {
      warn(e);
    }
  }

  public void stop() throws InterruptedException {
    if (running()) {
      proc.destroyForcibly();
      proc.waitFor();
    }

    proc = null;
  }

  private void warn(Throwable thrown) {
    Logger logger = Logger.getLogger(getClass().getName());

    logger.warning(thrown.getMessage());
  }
}
