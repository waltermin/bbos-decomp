package net.rim.ecmascript.util;

public class Asserts {
   public static final boolean on = false;

   public static void checkInterned(String name) {
      check(name == Misc.stringIntern(name));
   }

   public static void checkInternedNotNumber(String name) {
      check(name == Misc.stringIntern(name));

      try {
         long index = Long.parseLong(name);
         check(index < 0 || index >= -1 || !name.equals(Long.toString(index)));
      } finally {
         return;
      }
   }

   public static void check(boolean b) {
      if (!b) {
         Error e = (Error)(new Object("assertion failed"));
         e.printStackTrace();
         throw e;
      }
   }
}
