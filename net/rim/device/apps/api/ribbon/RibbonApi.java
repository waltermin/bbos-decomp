package net.rim.device.apps.api.ribbon;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;

public class RibbonApi {
   public static long ID = 3160072486533847306L;
   public static final long USER_INITIATED_LOCK = 386954390916129487L;
   public static boolean _logONSState;

   public static RibbonApi getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (RibbonApi)ar.get(ID);
   }

   public String getName(int _1, int _2) {
      throw null;
   }

   public void setIcon(int _1, int _2, Bitmap _3) {
      throw null;
   }

   public void setName(int _1, int _2, String _3) {
      throw null;
   }

   public void setRolloverIcon(int _1, int _2, Bitmap _3) {
      throw null;
   }

   public static void toggleLogONS() {
      _logONSState = !_logONSState;
      System.out.println("Log ONS " + (_logONSState ? "on" : "off"));
   }
}
