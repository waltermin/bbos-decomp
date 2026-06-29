package net.rim.device.apps.internal.phone.api;

public final class Out {
   private static String PARAMETERS_START = " (";
   private static final char PARAMETERS_COMMA;
   private static final char PARAMETERS_END;
   private static String PARAMETER_NULL = "null";
   public static final int DIRECT_CONNECT_EVENT;
   public static final int CALL_MANAGER_EVENT;
   public static final int LIVE_CALL_EVENT;

   public static final void p(String s) {
      System.out.println(s);
   }

   public static final void p(int msgClass, int msg) {
      printLabel(msgClass, msg);
      System.out.println();
   }

   public static final void p(int msgClass, int msg, Object a) {
      printLabel(msgClass, msg);
      System.out.print(PARAMETERS_START);
      print(a);
      System.out.println(')');
   }

   public static final void p(int msgClass, int msg, long a, boolean b) {
      printLabel(msgClass, msg);
      System.out.print(PARAMETERS_START);
      System.out.print(a);
      System.out.print(',');
      System.out.print(b);
      System.out.println(')');
   }

   public static final void p(int msgClass, int msg, long a) {
      printLabel(msgClass, msg);
      System.out.print(PARAMETERS_START);
      System.out.print(a);
      System.out.println(')');
   }

   private static final void printLabel(int msgClass, int msg) {
      char[] _buf = new char[9];

      for (int idx = 0; idx < 4; idx++) {
         _buf[idx] = (char)(msgClass >> 8 * (3 - idx) & 0xFF);
      }

      _buf[4] = '/';

      for (int idx = 0; idx < 4; idx++) {
         _buf[5 + idx] = (char)(msg >> 8 * (3 - idx) & 0xFF);
      }

      System.out.print(_buf);
   }

   private static final void print(Object a) {
      if (a != null) {
         System.out.print(a.toString());
      } else {
         System.out.print(PARAMETER_NULL);
      }
   }
}
