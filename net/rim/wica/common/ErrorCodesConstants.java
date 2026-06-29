package net.rim.wica.common;

public class ErrorCodesConstants {
   public static final int CATEGORY_MDS_RE_ERROR = 0;
   public static final int CATEGORY_MDS_SERVICES_ERROR = 1;
   public static final int CATEGORY_CONNECTOR_ERROR = 2;
   public static final int AG_INTERNAL_SERVER_ERROR = 0;
   public static final int AG_MAPPING_ERROR = 1;
   public static final int AG_TRANFORMATION_ERROR = 2;
   public static final int AG_SECURITY_ERROR = 3;
   public static final int AG_BACKEND_CONNECTOR_ERROR = 4;
   public static final int AG_MESSAGE_TOO_LARGE_ERROR = 5;
   public static final int RE_METADATA_ERROR = 100;
   public static final int RE_OUT_QUEUE_FULL = 101;
   public static final int RE_OUT_QUEUE_CRITICAL = 102;
   public static final int RE_JAVA_SCRIPT_ERROR = 103;
   public static final int RE_INCOMING_MESSAGE_ERROR = 104;
   public static final int RE_OUTGOING_MESSAGE_ERROR = 105;
   public static final int RE_SECURITY_ERROR = 106;
   public static final int RE_PERSISTENT_CONTENT_ERROR = 107;
   public static final int SOAP_AUTH_ERROR = 200;
   public static final int SOAP_SECURITY_ERROR = 201;
   public static final int SOAP_ENDPOINT_UNAVAILABLE_ERROR = 202;
   public static final int SOAP_MESSAGE_INVALID_ERROR = 203;
   public static final int SOAP_BACKEND_ERROR = 204;
   public static final int SOAP_CONNECTOR_ERROR = 205;
   public static final int SOAP_NETWORK_ERROR = 206;
   public static final int WSE_SUBSCRIBE_ERROR = 300;
   public static final int WSE_UNSUBSCRIBE_ERROR = 301;
   public static final int WSE_UNSUBSCRIBE_SUBSCRIPTION_NOT_FOUND_ERROR = 302;

   public static String errorCategoryToString(int errorCategory) {
      switch (errorCategory) {
         case -1: {
            String errorCategoryAsString = "UNKNOWN";
            return errorCategoryAsString;
         }
         case 0:
         default: {
            String errorCategoryAsString = "MDS_RE_ERROR";
            return errorCategoryAsString;
         }
         case 1: {
            String errorCategoryAsString = "MDS_SERVICES_ERROR";
            return errorCategoryAsString;
         }
         case 2:
            return "CONNECTOR_ERROR";
      }
   }

   public static String errorCodeToString(int errorCode) {
      switch (errorCode) {
         case 0: {
            String errorCodeAsString = "INTERNAL_ERROR";
            return errorCodeAsString;
         }
         case 1: {
            String errorCodeAsString = "MAPPING_ERROR";
            return errorCodeAsString;
         }
         case 2: {
            String errorCodeAsString = "TRANSFORMATION_ERROR";
            return errorCodeAsString;
         }
         case 3: {
            String errorCodeAsString = "SECURITY_ERROR";
            return errorCodeAsString;
         }
         case 4: {
            String errorCodeAsString = "BACKEND_CONNECTOR_ERROR";
            return errorCodeAsString;
         }
         case 5: {
            String errorCodeAsString = "MSG_TOO_LARGE_ERROR";
            return errorCodeAsString;
         }
         case 100: {
            String errorCodeAsString = "MSG_TOO_LARGE_ERROR";
            return errorCodeAsString;
         }
         case 101: {
            String errorCodeAsString = "OUT_QUEUE_FULL";
            return errorCodeAsString;
         }
         case 102: {
            String errorCodeAsString = "OUT_QUEUE_CRITICAL";
            return errorCodeAsString;
         }
         case 103: {
            String errorCodeAsString = "JAVA_SCRIPT_ERROR";
            return errorCodeAsString;
         }
         case 104: {
            String errorCodeAsString = "IN_MSG_ERROR";
            return errorCodeAsString;
         }
         case 105: {
            String errorCodeAsString = "OUT_MSG_ERROR";
            return errorCodeAsString;
         }
         case 106: {
            String errorCodeAsString = "SECURITY_ERROR";
            return errorCodeAsString;
         }
         case 200: {
            String errorCodeAsString = "SOAP_AUTH_ERROR";
            return errorCodeAsString;
         }
         case 201: {
            String errorCodeAsString = "SOAP_SECURITY_ERROR";
            return errorCodeAsString;
         }
         case 202: {
            String errorCodeAsString = "SOAP_ENDPOINT_UNAVAILABLE_ERROR";
            return errorCodeAsString;
         }
         case 203: {
            String errorCodeAsString = "SOAP_MESSAGE_INVALID_ERROR";
            return errorCodeAsString;
         }
         case 204: {
            String errorCodeAsString = "SOAP_BACKEND_ERROR";
            return errorCodeAsString;
         }
         case 205: {
            String errorCodeAsString = "SOAP_CONNECTOR_ERROR";
            return errorCodeAsString;
         }
         default:
            return "UNKNOWN";
      }
   }
}
