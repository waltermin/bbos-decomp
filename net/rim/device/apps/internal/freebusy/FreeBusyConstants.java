package net.rim.device.apps.internal.freebusy;

public class FreeBusyConstants {
   public static final int FB_REQUESTING_DATA = 0;
   public static final int FB_REQUESTING_DATA_COLOR = 13882323;
   public static final int FB_NO_INFORMATION = 1;
   public static final int FB_NO_INFORMATION_COLOR = 16777184;
   public static final int FB_AVAILABLE = 2;
   public static final int FB_AVAILABLE_COLOR = 16777215;
   public static final int FB_TENTATIVE = 3;
   public static final int FB_TENTATIVE_COLOR = 65535;
   public static final int FB_BUSY = 4;
   public static final int FB_BUSY_COLOR = 255;
   public static final int FB_OUT_OF_OFFICE = 5;
   public static final int FB_OUT_OF_OFFICE_COLOR = 6957182;

   private FreeBusyConstants() {
   }

   public static int getColor(int FBState) {
      if (FBState == 0) {
         return 13882323;
      } else if (FBState == 1) {
         return 16777184;
      } else if (FBState == 2) {
         return 16777215;
      } else if (FBState == 3) {
         return 65535;
      } else if (FBState == 4) {
         return 255;
      } else {
         return FBState == 5 ? 6957182 : 16753920;
      }
   }
}
