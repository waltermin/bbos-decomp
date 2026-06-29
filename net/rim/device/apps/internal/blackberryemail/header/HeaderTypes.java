package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public class HeaderTypes {
   public static final int TO = 0;
   public static final int CC = 1;
   public static final int BCC = 2;
   public static final int FROM = 3;
   public static final int SENDER = 4;
   public static final int REPLY_TO = 5;
   public static final int ORIGINAL_RECIPIENT = 6;
   public static final Integer[] _typesAsInteger = new Object[]{
      new Object(0), new Object(1), new Object(2), new Object(3), new Object(4), new Object(5), new Object(6)
   };

   public static String getStringForHeaderType(int headerType) {
      int id;
      switch (headerType) {
         case -1:
            id = 57;
            break;
         case 0:
         default:
            id = 51;
            break;
         case 1:
            id = 52;
            break;
         case 2:
            id = 53;
            break;
         case 3:
            id = 54;
            break;
         case 4:
            id = 55;
            break;
         case 5:
            id = 56;
      }

      return EmailResources.getString(id);
   }

   public static int getMenuOrdering(int headerType) {
      switch (headerType) {
         case -1:
            return 16859696;
         case 0:
         default:
            return 16859648;
         case 1:
            return 16859664;
         case 2:
            return 16859680;
      }
   }
}
