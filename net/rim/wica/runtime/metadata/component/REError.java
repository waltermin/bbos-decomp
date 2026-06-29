package net.rim.wica.runtime.metadata.component;

public class REError {
   private int _code;
   private String _description;
   private Object _errorDescriptor;

   public REError(int code, String description, Object descriptor) {
      this._code = code;
      if (description == null) {
         this._description = "";
      } else {
         this._description = description;
      }

      this._errorDescriptor = descriptor;
   }

   public int getCode() {
      return this._code;
   }

   public String getDescription() {
      return this._description;
   }

   public Object getErrorDescriptor() {
      return this._errorDescriptor;
   }
}
