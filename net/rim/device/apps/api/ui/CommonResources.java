package net.rim.device.apps.api.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public final class CommonResources {
   private static ResourceBundleFamily _resources = ResourceBundle.getBundle(-8414468493733347764L, "net.rim.device.apps.internal.resource.Common");

   public static final ResourceBundleFamily getResourceBundle() {
      return _resources;
   }

   public static final String getString(int id) {
      return _resources.getString(id);
   }

   public static final String[] getStringArray(int id) {
      return _resources.getStringArray(id);
   }

   public static final String[] getYesNoArray(int yesIndex) {
      String[] yes_no = new String[2];
      yesIndex &= 1;
      yes_no[yesIndex] = getString(100);
      yes_no[yesIndex ^ 1] = getString(101);
      return yes_no;
   }

   public static final String getCallFailedMessage(int errorCode) {
      int stringID = 9009;
      switch (errorCode & 65535) {
         case 0:
         case 28:
            break;
         case 1:
         default:
            stringID = 9001;
            break;
         case 2:
            stringID = 9002;
            break;
         case 3:
            stringID = 9003;
            break;
         case 4:
            stringID = 9004;
            break;
         case 5:
            stringID = 9005;
            break;
         case 6:
            stringID = 9006;
            break;
         case 7:
            stringID = 9007;
            break;
         case 8:
            stringID = 9008;
            break;
         case 9:
            stringID = 9009;
            break;
         case 10:
            stringID = 9010;
            break;
         case 11:
            stringID = 9011;
            break;
         case 12:
            stringID = 9012;
            break;
         case 13:
            stringID = 9013;
            break;
         case 14:
            stringID = 9014;
            break;
         case 15:
            stringID = 9015;
            break;
         case 16:
            stringID = 9016;
            break;
         case 17:
            stringID = 9047;
            break;
         case 18:
            stringID = 9048;
            break;
         case 19:
            stringID = 9049;
            break;
         case 20:
            stringID = 0;
            break;
         case 21:
            stringID = 1;
            break;
         case 22:
            stringID = 2;
            break;
         case 23:
            stringID = 3;
            break;
         case 24:
            stringID = 4;
            break;
         case 25:
            stringID = 5;
            break;
         case 26:
            stringID = 6;
            break;
         case 27:
            stringID = 7;
            break;
         case 29:
            stringID = 9170;
            break;
         case 30:
            stringID = 9181;
            break;
         case 31:
            stringID = 6;
            break;
         case 32:
            stringID = 9173;
            break;
         case 33:
            stringID = 9172;
            break;
         case 34:
            stringID = 9171;
            break;
         case 35:
            stringID = 9187;
            break;
         case 36:
            stringID = 9188;
      }

      return getString(stringID);
   }
}
