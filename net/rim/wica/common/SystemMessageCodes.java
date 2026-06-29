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
         case 0: {
            String msgCodeAsString = "ERROR";
            return msgCodeAsString;
         }
         case 1: {
            String msgCodeAsString = "HEARTBEAT";
            return msgCodeAsString;
         }
         case 2: {
            String msgCodeAsString = "FLOW_CONTROL";
            return msgCodeAsString;
         }
         case 3: {
            String msgCodeAsString = "RE_STATUS_REQUEST";
            return msgCodeAsString;
         }
         case 4: {
            String msgCodeAsString = "RE_STATUS";
            return msgCodeAsString;
         }
         case 5: {
            String msgCodeAsString = "POLICIES";
            return msgCodeAsString;
         }
         case 6: {
            String msgCodeAsString = "RE_UPGRADE_AVAILABLE";
            return msgCodeAsString;
         }
         case 7: {
            String msgCodeAsString = "RE_UPGRADE_REQUIRED";
            return msgCodeAsString;
         }
         case 8: {
            String msgCodeAsString = "RE_UPGRADE_STATUS";
            return msgCodeAsString;
         }
         case 9: {
            String msgCodeAsString = "RE_KEY_REFRESH";
            return msgCodeAsString;
         }
         case 128: {
            String msgCodeAsString = "WICLET_UPGRADE";
            return msgCodeAsString;
         }
         case 129: {
            String msgCodeAsString = "DESTROY_WICLET";
            return msgCodeAsString;
         }
         case 130: {
            String msgCodeAsString = "QUARANTINE_WICLET";
            return msgCodeAsString;
         }
         case 131: {
            String msgCodeAsString = "DELIVER_SCRIPT";
            return msgCodeAsString;
         }
         case 132: {
            String msgCodeAsString = "LIFECYCLE_STATUS";
            return msgCodeAsString;
         }
         case 133: {
            String msgCodeAsString = "INSTALL_WICLET";
            return msgCodeAsString;
         }
         case 256: {
            String msgCodeAsString = "SUBSCRIBE_REFRESH_KEY";
            return msgCodeAsString;
         }
         case 257: {
            String msgCodeAsString = "SUBSCRIBE_RE_STATUS";
            return msgCodeAsString;
         }
         case 258: {
            String msgCodeAsString = "SUBSCRIBE_DESTROY_WICLET";
            return msgCodeAsString;
         }
         case 259: {
            String msgCodeAsString = "SUBSCRIBE_QUARANTINE_WICLET";
            return msgCodeAsString;
         }
         case 260: {
            String msgCodeAsString = "SUBSCRIBE_UPGRADE_WICLET";
            return msgCodeAsString;
         }
         case 512: {
            String msgCodeAsString = "AUTHENTICATION_REQUEST";
            return msgCodeAsString;
         }
         case 513: {
            String msgCodeAsString = "AUTHENTICATION_RESPONSE";
            return msgCodeAsString;
         }
         case 514: {
            String msgCodeAsString = "CANCEL_AUTHENTICATION";
            return msgCodeAsString;
         }
         default:
            return ((StringBuffer)(new Object())).append(msgCode).append("(NO DESC AVAIL)").toString();
      }
   }
}
