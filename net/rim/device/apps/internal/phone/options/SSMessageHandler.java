package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardEFListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.Out;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

class SSMessageHandler implements PhoneEventListener, SIMCardEFListener {
   int _suppressDialogs;

   public void responseMarkSMSAsRead(int status, int packetId) {
   }

   public void resetSuppressMessageDialogs() {
      this._suppressDialogs = 0;
      Out.p("SSMessageSuppression: 0");
   }

   public void suppressMessageDialogs(boolean suppress) {
      if (suppress) {
         this._suppressDialogs++;
      } else {
         this._suppressDialogs--;
      }

      Out.p(((StringBuffer)(new Object("SSMessageSuppression: "))).append(this._suppressDialogs).toString());
   }

   public boolean isCallForwardUnconditionalActive(int line) {
      return CFU.isActive(line);
   }

   public void smsEFFull() {
   }

   public void responseDeleteSMS(int status, int packetId) {
   }

   @Override
   public void phoneEventNotify(int eventId, int param1, Object param2) {
      switch (eventId) {
         case 1010:
            int[] data = (int[])param2;
            int status = data[0];
            int errorCode = data[1];
            int resourceId = -1;
            switch (status) {
               case 0:
                  break;
               case 1:
               default:
                  resourceId = 6127;
                  break;
               case 2:
                  resourceId = getSSErrorStringId(errorCode);
            }

            if (resourceId != -1) {
               this.showSSNotificationStatus(PhoneResources.getString(resourceId));
            }
            break;
         case 5000: {
            int[] params = (int[])param2;
            this.ssRequestSucceeded(params[0], params[1], params[2], params[3], params[4] != 0);
            return;
         }
         case 5001: {
            int[] params = (int[])param2;
            this.ssRequestFailed(params[0], params[1]);
            return;
         }
         case 5002:
            this.alert(PhoneResources.getString(6062));
            return;
         case 5003:
            if ((Branding.getVendorId() == 102 || Branding.getVendorId() == 101) && param1 == 1) {
               String msg = PhoneResources.getString(130);
               new SSMessageDialog(msg, "dialog_information").show();
               return;
            }
            break;
         case 5004:
            this.showSSNotificationStatus(PhoneResources.getString(6239));
            return;
         case 5005:
            this.handleSSPasswordRequest(param1);
            return;
         case 5007:
            int ssOption = param1;
            this.handleSSNotification(ssOption);
            return;
         case 100200:
            SIMCard.addListener(VoiceServices.getUiApplication(), this);

            try {
               SIMCard.requestEFInfo(50);
               return;
            } finally {
               SIMCard.removeListener(VoiceServices.getUiApplication(), this);
               return;
            }
         case 150110:
            if (PhoneUtilities.idenTypeNetwork()) {
               this.updateCFUStatus();
               return;
            }
      }
   }

   @Override
   public void responseEFRead(int code, int id, int structure, int length, int recordNumber) {
   }

   @Override
   public void responseEFWrite(int code, int id, int structure, int recordNumber) {
   }

   @Override
   public void responseEFInfo(int code, int id, int fileStatus, int structure, int fileSize, int recordLength, int numRecords) {
      if (id == 50) {
         SIMCard.removeListener(VoiceServices.getUiApplication(), this);
         if (code == 0) {
            CFU.setCFFAvailable(true);
            this.updateCFUStatus();
            return;
         }

         if (code == 3) {
            CFU.setCFFAvailable(false);
         }
      }
   }

   private static int getSSErrorStringId(int err) {
      return -1;
   }

   private void ssRequestFailed(int reason, int bearerService) {
      int rc;
      switch (reason) {
         case 0:
            rc = 6111;
            break;
         case 1:
            rc = 6112;
            break;
         case 2:
            rc = 6113;
            break;
         case 3:
            rc = 6114;
            break;
         case 4:
            rc = 6115;
            break;
         case 5:
            rc = 6116;
            break;
         case 6:
            rc = 6137;
            break;
         case 7:
            rc = 6125;
            break;
         case 8:
            rc = 6119;
            break;
         case 9:
            rc = 6120;
            break;
         case 10:
            rc = 6121;
            break;
         case 11:
            rc = 6122;
            break;
         case 12:
            rc = 6123;
            break;
         case 13:
            rc = 6124;
            break;
         case 14:
            rc = 6095;
            break;
         case 15:
            rc = 6096;
            break;
         case 16:
            rc = 6097;
            break;
         case 17:
            rc = 6098;
            break;
         case 18:
            rc = 6099;
            break;
         case 19:
            rc = 6100;
            break;
         case 20:
            rc = 6101;
            break;
         case 21:
            rc = 6102;
            break;
         case 22:
            rc = 6103;
            break;
         case 23:
            rc = 6104;
            break;
         case 24:
            rc = 6105;
            break;
         case 25:
            rc = 6106;
            break;
         case 26:
            rc = 6107;
            break;
         case 27:
            rc = 6108;
            break;
         case 28:
            rc = 6109;
            break;
         case 29:
            rc = 6110;
            break;
         case 30:
            rc = 6299;
            break;
         case 31:
            rc = 6308;
            break;
         case 1573:
            rc = 6131;
            break;
         default:
            rc = 6055;
      }

      String[] params = new Object[]{PhoneResources.getString(rc)};
      String msg = MessageFormat.format(PhoneResources.getString(6139), params);
      this.alert(msg);
   }

   private void handleSSNotification(int ssOption) {
      switch (ssOption) {
         case 32:
            this.showSSNotificationStatus(PhoneResources.getString(6294));
         default:
            return;
         case 33:
            this.showSSNotificationStatus(PhoneResources.getString(6088));
            return;
         case 40:
            this.showSSNotificationStatus(PhoneResources.getString(6291));
            return;
         case 41:
            this.showSSNotificationStatus(PhoneResources.getString(6089));
            return;
         case 42:
            this.showSSNotificationStatus(PhoneResources.getString(6292));
            return;
         case 43:
            this.showSSNotificationStatus(PhoneResources.getString(6293));
      }
   }

   private void showSSNotificationStatus(String status) {
      Status.show(status, Bitmap.getPredefinedBitmap(0), 1500, 33554432, false, false, -2147483647);
   }

   public static String getSsName(int ss) {
      int idx;
      switch (ss) {
         case 17:
            idx = 11;
            break;
         case 18:
            idx = 12;
            break;
         case 19:
            idx = 13;
            break;
         case 20:
            idx = 14;
            break;
         case 25:
            idx = 19;
            break;
         case 32:
            idx = 15;
            break;
         case 33:
            idx = 0;
            break;
         case 40:
            idx = 16;
            break;
         case 41:
            idx = 1;
            break;
         case 42:
            idx = 2;
            break;
         case 43:
            idx = 3;
            break;
         case 65:
            idx = 18;
            break;
         case 144:
            idx = 17;
            break;
         case 145:
            idx = 4;
            break;
         case 146:
            idx = 5;
            break;
         case 147:
            idx = 6;
            break;
         case 148:
            idx = 7;
            break;
         case 153:
            idx = 8;
            break;
         case 154:
            idx = 9;
            break;
         case 155:
            idx = 10;
            break;
         default:
            System.out.println(((StringBuffer)(new Object("Unknown Service: "))).append(Integer.toString(ss)).toString());
            return null;
      }

      return PhoneResources.getString(6059, idx);
   }

   public static String getSsActionName(int action) {
      int idx;
      switch (action) {
         case 0:
         case 7:
            if (DeviceInfo.getPlatformVersion().startsWith("1.4.")) {
               return null;
            }

            return ((StringBuffer)(new Object("Action"))).append(Integer.toString(action)).toString();
         case 1:
         default:
            idx = 0;
            break;
         case 2:
            idx = 1;
            break;
         case 3:
            idx = 2;
            break;
         case 4:
            idx = 3;
            break;
         case 5:
            idx = 4;
            break;
         case 6:
            idx = 5;
            break;
         case 8:
            idx = 6;
      }

      return PhoneResources.getString(6030, idx);
   }

   public static String getSsBearerName(int bearer) {
      int idx;
      switch (bearer) {
         case 0:
            return null;
         case 1:
         default:
            idx = 0;
            break;
         case 2:
            idx = 1;
            break;
         case 3:
            idx = 2;
            break;
         case 4:
            idx = 3;
            break;
         case 5:
            idx = 4;
            break;
         case 6:
            idx = 5;
            break;
         case 7:
            idx = 6;
            break;
         case 8:
            idx = 7;
            break;
         case 9:
            idx = 8;
            break;
         case 10:
            idx = 9;
            break;
         case 11:
            idx = 10;
      }

      return PhoneResources.getString(6058, idx);
   }

   public static String getSsResultName(int result, int action) {
      String txt = null;
      if (action == 5) {
         if (result != 0) {
            int[] bitflags = new int[]{1, 2, 4, 8, -804651004, 2, 3, 0, 1, 51, -804651007, 100, -804651004, 220, 6009, 6010};

            for (int idx = 0; idx < bitflags.length; idx++) {
               if ((result & bitflags[idx]) != 0) {
                  String flagTxt = PhoneResources.getString(6060, idx);
                  if (txt == null) {
                     txt = flagTxt;
                  } else {
                     txt = ((StringBuffer)(new Object())).append(txt).append(", ").append(flagTxt).toString();
                  }
               }
            }
         } else {
            txt = PhoneResources.getString(6279);
         }
      }

      if (txt == null) {
         txt = PhoneResources.getString(6064);
      }

      return txt;
   }

   private void handleSSPasswordRequest(int inputType) {
      int type = inputType;
      UiApplication voiceApp = VoiceServices.getUiApplication();
      voiceApp.invokeLater(new SSMessageHandler$1(this, type));
   }

   private void updateCFUStatus() {
      CFU.update();
      VoiceServices.broadcastEvent(150120);
   }

   private void alert(String msg) {
      Out.p(msg);
      if (this._suppressDialogs <= 0) {
         new SSMessageDialog(msg, "dialog_exclamation").show();
      }
   }

   private void inform(String msg) {
      Out.p(msg);
      if (this._suppressDialogs <= 0) {
         new SSMessageDialog(msg, "dialog_information").show();
      }
   }

   private void ssRequestSucceeded(int ss, int action, int result, int bearerService, boolean forwardingNumberAvailable) {
      if (!PhoneUtilities.cdmaWAFActive() || ss != 0 && ss != 1 || action != 0) {
         if (PhoneUtilities.gsmWAFActive() && ss == 33 || ss == 32 || ss == 0) {
            this.updateCFUStatus();
         }

         Phone phone = Phone.getInstance();
         if (PhoneUtilities.gsmWAFActive()) {
            label287:
            try {
               result = phone.querySSOptionResult(ss, bearerService);
            } finally {
               break label287;
            }
         }

         if ((action == 1 || (result & 2) != 0) && action != 3) {
            switch (ss) {
               case 33:
               case 41:
               case 42:
               case 43:
                  forwardingNumberAvailable = true;
            }
         }

         String strSs = getSsName(ss);
         String strAction = getSsActionName(action);
         String strBearer = getSsBearerName(bearerService);
         String strResult = getSsResultName(result, action);
         String fwdnumber = null;
         if (forwardingNumberAvailable && strSs != null) {
            if (strBearer != null) {
               label274:
               try {
                  fwdnumber = phone.getForwardingNumberForService(ss, bearerService);
               } finally {
                  break label274;
               }
            }

            if (fwdnumber == null || fwdnumber.length() == 0) {
               label268:
               try {
                  fwdnumber = phone.getCallForwardingNumber(ss);
               } finally {
                  break label268;
               }
            }
         }

         if (fwdnumber != null && fwdnumber.length() > 0 && strSs != null) {
            String[] params;
            if (strBearer != null) {
               params = new Object[]{((StringBuffer)(new Object())).append(strSs).append(" : ").append(strBearer).toString(), fwdnumber};
            } else {
               params = new Object[]{strSs, fwdnumber};
            }

            String fmtString = PhoneResources.getString(6065);
            String msg = MessageFormat.format(fmtString, params);
            this.inform(msg);
         } else if (strSs != null && strAction != null) {
            String[] params;
            if (strBearer != null) {
               params = new Object[]{strAction, ((StringBuffer)(new Object())).append(strSs).append(" : ").append(strBearer).toString(), strResult};
            } else {
               params = new Object[]{strAction, strSs, strResult};
            }

            String fmtString = PhoneResources.getString(6061);
            String msg = MessageFormat.format(fmtString, params);
            this.inform(msg);
         } else {
            if (strSs == null && strAction != null) {
               String msg;
               if (strBearer != null) {
                  String[] params = new Object[]{strAction, strBearer, strResult};
                  String fmtString = PhoneResources.getString(6061);
                  msg = MessageFormat.format(fmtString, params);
               } else {
                  String[] params = new Object[]{strAction, strResult};
                  String fmtString = PhoneResources.getString(6277);
                  msg = MessageFormat.format(fmtString, params);
               }

               this.inform(msg);
            }
         }
      }
   }
}
