package javax.microedition.pim;

public class UnsupportedFieldException extends RuntimeException {
   int _field = -1;

   public UnsupportedFieldException() {
   }

   public UnsupportedFieldException(String detailMessage) {
      super(detailMessage);
   }

   public UnsupportedFieldException(String detailMessage, int field) {
      super(detailMessage);
      this._field = field;
   }

   public int getField() {
      return this._field;
   }
}
