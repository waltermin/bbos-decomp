package net.rim.ecmascript.runtime;

public class ESError extends ESObject {
   protected String _message;
   protected String _type;

   public ESError(String message) {
      this("Error", message);
   }

   ESError(String type, String message) {
      this(type, message, GlobalObject.getInstance().errorPrototype);
   }

   ESError(String type, String message, ESObject prototype) {
      super("Error", prototype);
      this._message = message;
      this._type = type;
   }

   ESError(String type, String message, ESObject prototype, boolean nullProtoOK) {
      super("Error", prototype, nullProtoOK);
      this._message = message;
      this._type = type;
   }
}
