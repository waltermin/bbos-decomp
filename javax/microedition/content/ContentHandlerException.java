package javax.microedition.content;

import java.io.IOException;

public class ContentHandlerException extends IOException {
   private final int _errcode;
   public static final int NO_REGISTERED_HANDLER = 1;
   public static final int TYPE_UNKNOWN = 2;
   public static final int AMBIGUOUS = 3;

   public ContentHandlerException(String reason, int errcode) {
      super(reason);
      if (errcode >= 1 && errcode <= 3) {
         this._errcode = errcode;
      } else {
         throw new IllegalArgumentException("Illegal value for errcode");
      }
   }

   public int getErrorCode() {
      return this._errcode;
   }
}
