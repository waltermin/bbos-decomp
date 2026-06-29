package net.rim.wica.common;

public final class SystemMessageCodes {
   public static final int ERROR;
   public static final int HEARTBEAT;
   public static final int FLOW_CONTROL;
   public static final int RE_STATUS_REQUEST;
   public static final int RE_STATUS;
   public static final int POLICIES;
   public static final int RE_UPGRADE_AVAILABLE;
   public static final int RE_UPGRADE_REQUIRED;
   public static final int RE_UPGRADE_STATUS;
   public static final int RE_KEY_REFRESH;
   public static final int WICLET_UPGRADE;
   public static final int DESTROY_WICLET;
   public static final int QUARANTINE_WICLET;
   public static final int DELIVER_SCRIPT;
   public static final int LIFECYCLE_STATUS;
   public static final int INSTALL_WICLET;
   public static final int SUBSCRIBE_REFRESH_KEY;
   public static final int SUBSCRIBE_RE_STATUS;
   public static final int SUBSCRIBE_DESTROY_WICLET;
   public static final int SUBSCRIBE_QUARANTINE_WICLET;
   public static final int SUBSCRIBE_UPGRADE_WICLET;
   public static final int AUTHENTICATION_REQUEST;
   public static final int AUTHENTICATION_RESPONSE;
   public static final int CANCEL_AUTHENTICATION;

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
