package net.rim.wica.common;

public final class SystemMessageCodes {
   public static final int ERROR = 0;
   public static final int HEARTBEAT = 1;
   public static final int FLOW_CONTROL = 2;
   public static final int RE_STATUS_REQUEST = 3;
   public static final int RE_STATUS = 4;
   public static final int POLICIES = 5;
   public static final int RE_UPGRADE_AVAILABLE = 6;
   public static final int RE_UPGRADE_REQUIRED = 7;
   public static final int RE_UPGRADE_STATUS = 8;
   public static final int RE_KEY_REFRESH = 9;
   public static final int WICLET_UPGRADE = 128;
   public static final int DESTROY_WICLET = 129;
   public static final int QUARANTINE_WICLET = 130;
   public static final int DELIVER_SCRIPT = 131;
   public static final int LIFECYCLE_STATUS = 132;
   public static final int INSTALL_WICLET = 133;
   public static final int SUBSCRIBE_REFRESH_KEY = 256;
   public static final int SUBSCRIBE_RE_STATUS = 257;
   public static final int SUBSCRIBE_DESTROY_WICLET = 258;
   public static final int SUBSCRIBE_QUARANTINE_WICLET = 259;
   public static final int SUBSCRIBE_UPGRADE_WICLET = 260;
   public static final int AUTHENTICATION_REQUEST = 512;
   public static final int AUTHENTICATION_RESPONSE = 513;
   public static final int CANCEL_AUTHENTICATION = 514;

   public static final String toString(int msgCode) {
      switch (msgCode) {
         case 0:
            return "ERROR";
         case 1:
            return "HEARTBEAT";
         case 2:
            return "FLOW_CONTROL";
         case 3:
            return "RE_STATUS_REQUEST";
         case 4:
            return "RE_STATUS";
         case 5:
            return "POLICIES";
         case 6:
            return "RE_UPGRADE_AVAILABLE";
         case 7:
            return "RE_UPGRADE_REQUIRED";
         case 8:
            return "RE_UPGRADE_STATUS";
         case 9:
            return "RE_KEY_REFRESH";
         case 128:
            return "WICLET_UPGRADE";
         case 129:
            return "DESTROY_WICLET";
         case 130:
            return "QUARANTINE_WICLET";
         case 131:
            return "DELIVER_SCRIPT";
         case 132:
            return "LIFECYCLE_STATUS";
         case 133:
            return "INSTALL_WICLET";
         case 256:
            return "SUBSCRIBE_REFRESH_KEY";
         case 257:
            return "SUBSCRIBE_RE_STATUS";
         case 258:
            return "SUBSCRIBE_DESTROY_WICLET";
         case 259:
            return "SUBSCRIBE_QUARANTINE_WICLET";
         case 260:
            return "SUBSCRIBE_UPGRADE_WICLET";
         case 512:
            return "AUTHENTICATION_REQUEST";
         case 513:
            return "AUTHENTICATION_RESPONSE";
         case 514:
            return "CANCEL_AUTHENTICATION";
         default:
            return msgCode + "(NO DESC AVAIL)";
      }
   }
}
