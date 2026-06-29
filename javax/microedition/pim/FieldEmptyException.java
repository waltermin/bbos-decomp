package javax.microedition.pim;

public class FieldEmptyException extends RuntimeException {
   private int _field = -1;

   public FieldEmptyException() {
   }

   public FieldEmptyException(String detailMessage) {
      super(detailMessage);
   }

   public FieldEmptyException(String detailMessage, int field) {
      super(detailMessage);
      this._field = field;
   }

   public int getField() {
      return this._field;
   }
}
