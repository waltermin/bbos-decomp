package net.rim.vm;

import java.io.PrintStream;

public final class TraceBack {
   public static final long GUID = -7188635411275533160L;
   public static final int IMMEDIATE = 0;
   public static final int OUTSIDE_MODULE = 1;
   public static final int OUTSIDE_SIBLINGS = 2;
   public static final int FIRST_3RD_PARTY = 3;

   private TraceBack() {
   }

   public static final native String getMessage(Object var0, int var1);

   public static final native Object getTraceBack();

   public static final native int getCallingModule(int var0);

   public static final native String getCallingModuleName(int var0);

   public static final native int[] getCallingModules();

   public static final native Class[] getCallingClasses();

   public static final void printStackTrace(PrintStream out, Object tb) {
      int i = 0;

      while (true) {
         String msg = getMessage(tb, i);
         if (msg == null) {
            return;
         }

         out.println(msg);
         i++;
      }
   }

   public static final void printStackTrace(PrintStream out) {
      printStackTrace(out, getTraceBack());
   }
}
