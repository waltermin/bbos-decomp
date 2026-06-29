package net.rim.device.apps.internal.freebusy;

public class FreeBusyConstants {
   public static final int FB_REQUESTING_DATA;
   public static final int FB_REQUESTING_DATA_COLOR;
   public static final int FB_NO_INFORMATION;
   public static final int FB_NO_INFORMATION_COLOR;
   public static final int FB_AVAILABLE;
   public static final int FB_AVAILABLE_COLOR;
   public static final int FB_TENTATIVE;
   public static final int FB_TENTATIVE_COLOR;
   public static final int FB_BUSY;
   public static final int FB_BUSY_COLOR;
   public static final int FB_OUT_OF_OFFICE;
   public static final int FB_OUT_OF_OFFICE_COLOR;

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
