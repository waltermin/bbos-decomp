package net.rim.device.api.i18n;

public class Format {
   private StringBuffer _buffer = new StringBuffer();

   protected Format() {
   }

   public final String format(Object obj) {
      synchronized (this._buffer) {
         this._buffer.setLength(0);
         return this.format(obj, this._buffer, null).toString();
      }
   }

   public StringBuffer format(Object _1, StringBuffer _2, FieldPosition _3) {
      throw null;
   }

   public int[] getFields() {
      throw null;
   }
}
