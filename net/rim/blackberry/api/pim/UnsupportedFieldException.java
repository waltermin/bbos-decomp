package net.rim.blackberry.api.pim;

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

   UnsupportedFieldException(int field) {
      this(((StringBuffer)(new Object("Field "))).append(field).append(" not supported.").toString(), field);
   }

   public int getField() {
      return this._field;
   }
}
