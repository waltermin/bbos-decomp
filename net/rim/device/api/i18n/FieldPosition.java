package net.rim.device.api.i18n;

public class FieldPosition {
   private int _field;
   private int _beginindex;
   private int _endindex;

   public FieldPosition(int field) {
      this._field = field;
   }

   public int getBeginIndex() {
      return this._beginindex;
   }

   public int getEndIndex() {
      return this._endindex;
   }

   public int getField() {
      return this._field;
   }

   public void setField(int field) {
      this._field = field;
   }

   public void setBeginIndex(int beginindex) {
      this._beginindex = beginindex;
   }

   public void setEndIndex(int endindex) {
      this._endindex = endindex;
   }
}
