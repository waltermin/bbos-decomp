package net.rim.tid.data;

public class LearningData {
   private byte[] _record;
   private Object _recordEncoding;

   public LearningData() {
   }

   public LearningData(byte[] data) {
      this._record = data;
   }

   public LearningData(byte[] data, Object encodedData) {
      this._record = data;
      this._recordEncoding = encodedData;
   }

   public void setData(byte[] data) {
      this._record = data;
   }

   public void setEncodedData(Object data) {
      this._recordEncoding = data;
   }

   public byte[] getData() {
      return this._record;
   }

   public Object getEncodedData() {
      return this._recordEncoding;
   }

   public boolean isDataLocked() {
      return this._recordEncoding != null && this._record == null;
   }
}
