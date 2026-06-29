package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.GlobalObject;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.common.ErrorCodesConstants;
import net.rim.wica.runtime.metadata.component.REError;
import net.rim.wica.runtime.metadata.component.REErrorDetails;

final class ESMDSError extends RedirectedObject {
   private REError _error;
   private static final String Code = "code";
   private static final String Description = "description";
   private static final String Data = "data";
   private static final String Name = "name";
   private static final String Category = "category";
   private static final String RequestName = "requestName";
   private static final String CategoryRuntime = "CATEGORY_MDS_RE_ERROR";
   private static final String CategoryServices = "CATEGORY_MDS_SERVICES_ERROR";
   private static final String CategoryConnector = "CATEGORY_CONNECTOR_ERROR";
   private static final String TypeMetadata = "METADATA_ERROR";
   private static final String TypeOutgoingFull = "OUT_QUEUE_FULL";
   private static final String TypeOutgoingCritical = "OUT_QUEUE_CRITICAL";
   private static final String TypeScript = "JAVA_SCRIPT_ERROR";
   private static final String TypeIncomingMessage = "INCOMING_MESSAGE_ERROR";
   private static final String TypeOutgoingMessage = "OUTGOING_MESSAGE_ERROR";
   private static final String TypeInternalServer = "SERVER_INTERNAL_ERROR";
   private static final String TypeServerMapping = "SERVER_MAPPING_ERROR";
   private static final String TypeServerTransformation = "SERVER_TRANSFORMATION_ERROR";
   private static final String TypeServerSecurity = "SERVER_SECURITY_ERROR";
   private static final String TypeBackendConnector = "BACKEND_CONNECTOR_ERROR";
   private static final String TypeLargeMessage = "MESSAGE_TOO_LARGE";
   private static final String TypeSoapAuthentication = "SOAP_AUTH_ERROR";
   private static final String TypeSoapSecurity = "SOAP_SECURITY_ERROR";
   private static final String TypeEndpointUnavailable = "SOAP_ENDPOINT_UNAVAILABLE_ERROR";
   private static final String TypeMessageInvalid = "SOAP_MESSAGE_INVALID_ERROR";
   private static final String TypeSoapBackend = "SOAP_BACKEND_ERROR";
   private static final String TypeSoapConnector = "SOAP_CONNECTOR_ERROR";
   private static final String TypeSoapNetwork = "SOAP_NETWORK_ERROR";
   private static final String TypeSubscribe = "WSE_SUBSCRIBE_ERROR";
   private static final String TypeUnsubscribe = "WSE_UNSUBSCRIBE_ERROR";
   private static final String TypeSubscriptionNotFound = "WSE_UNSUBSCRIBE_SUBSCRIPTION_NOT_FOUND_ERROR";
   private static final String[] NAMES = new String[]{
      "CATEGORY_MDS_RE_ERROR",
      "CATEGORY_MDS_SERVICES_ERROR",
      "CATEGORY_CONNECTOR_ERROR",
      "METADATA_ERROR",
      "OUT_QUEUE_FULL",
      "OUT_QUEUE_CRITICAL",
      "JAVA_SCRIPT_ERROR",
      "INCOMING_MESSAGE_ERROR",
      "OUTGOING_MESSAGE_ERROR",
      "SERVER_INTERNAL_ERROR",
      "SERVER_MAPPING_ERROR",
      "SERVER_TRANSFORMATION_ERROR",
      "SERVER_SECURITY_ERROR",
      "BACKEND_CONNECTOR_ERROR",
      "MESSAGE_TOO_LARGE",
      "SOAP_AUTH_ERROR",
      "SOAP_SECURITY_ERROR",
      "SOAP_ENDPOINT_UNAVAILABLE_ERROR",
      "SOAP_MESSAGE_INVALID_ERROR",
      "SOAP_BACKEND_ERROR",
      "SOAP_CONNECTOR_ERROR",
      "SOAP_NETWORK_ERROR",
      "WSE_SUBSCRIBE_ERROR",
      "WSE_UNSUBSCRIBE_ERROR",
      "WSE_UNSUBSCRIBE_SUBSCRIPTION_NOT_FOUND_ERROR"
   };
   private static final int[] VALUES = new int[]{
      0,
      1,
      2,
      100,
      101,
      102,
      103,
      104,
      105,
      0,
      1,
      2,
      3,
      4,
      5,
      200,
      201,
      202,
      203,
      204,
      205,
      206,
      300,
      301,
      302,
      51,
      -804651003,
      -1,
      2,
      0,
      2,
      3,
      521863424,
      1886404972,
      16827085,
      -1033101541,
      1701869908,
      712179968,
      -1975817147,
      16806977,
      -2104615050,
      16795989,
      100713909,
      100664923,
      526977907,
      524524,
      1679894024,
      -1549376448,
      1939900944,
      192310383,
      2032535552,
      638058622,
      1816493689,
      134253115,
      119610152,
      -2044196864,
      134243701,
      2037690411,
      134243696,
      1713915713,
      1091043439,
      1395197795,
      134247947,
      15820609,
      711737608,
      1816201216,
      134247210,
      -848269247,
      1769100738,
      1883310080,
      -238891664,
      1916864512,
      134248910,
      2043572801,
      2037690411,
      134243696,
      761332034,
      -1368427616,
      1107820551,
      -1609011965,
      128872307,
      1732380672,
      15035721,
      -1503182328,
      134224236,
      2121232194,
      1702200865,
      -1572730880,
      134250084,
      712380483,
      1124597771,
      1327732321,
      12806754,
      698827528,
      -1488779264,
      1697402409,
      -1371338752,
      1165258252,
      1451899756,
      2294682
   };

   ESMDSError() {
      super("Error", GlobalObject.getInstance().getObjectPrototype());
      int length = NAMES.length;
      this.setGrowthIncrement(length);

      for (int i = 0; i < length; i++) {
         this.addField(NAMES[i], 5, Value.makeIntegerValue(VALUES[i]));
      }
   }

   final void setError(REError error) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final long requestFieldValue(String name) {
      REErrorDetails ed = (REErrorDetails)this._error.getErrorDescriptor();
      if (name == "code") {
         return Value.makeIntegerValue(this._error.getCode());
      } else if (name == "data") {
         return this.getData(ed);
      } else if (name == "description") {
         return EcmaUtilities.makeStringValue(this._error.getDescription());
      } else if (name == "name") {
         return EcmaUtilities.makeStringValue(ed.getName());
      } else if (name == "category") {
         return Value.makeStringValue(ErrorCodesConstants.errorCategoryToString(ed.getCategory()));
      } else {
         return name == "requestName" ? EcmaUtilities.makeStringValue(ed.getRequestMessageName()) : Value.DEFAULT;
      }
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      return false;
   }

   private final long getData(Object data) {
      if (data == null) {
         return Value.NULL;
      } else if (!(data instanceof Object)) {
         return !(data instanceof REErrorDetails)
            ? Value.makeStringValue(data.toString())
            : EcmaUtilities.makeStringValue(((REErrorDetails)data).getData().toString());
      } else {
         return EcmaUtilities.makeStringValue(((Throwable)data).getMessage());
      }
   }
}
