package net.rim.plazmic.internal.contentpreview.reporting;

import java.io.PrintStream;

public final class PrintReporter implements Reporter {
   private PrintStream _out;
   public static final String rcsid;

   public PrintReporter(PrintStream out) {
      this._out = out;
   }
}
