package net.rim.blackberry.api.pim;

public class FieldFullException extends RuntimeException {
   private int _field = -1;

   public FieldFullException() {
   }

   public FieldFullException(String detailMessage) {
      super(detailMessage);
   }

   public FieldFullException(String detailMessage, int field) {
      super(detailMessage);
      this._field = field;
   }

   FieldFullException(int field) {
      this._field = field;
   }

   public int getField() {
      return this._field;
   }
}
