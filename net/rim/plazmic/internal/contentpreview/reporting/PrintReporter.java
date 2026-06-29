package net.rim.plazmic.internal.contentpreview.reporting;

import java.io.PrintStream;

public final class PrintReporter implements Reporter {
   private PrintStream _out;
   public static final String rcsid = "$Id: //depot/projects/JavaDevice/4.3.0/JavaApplications/sdk/CDK/net/rim/plazmic/internal/contentpreview/reporting/PrintReporter.java#1 $";

   public PrintReporter(PrintStream out) {
      this._out = out;
   }
}
