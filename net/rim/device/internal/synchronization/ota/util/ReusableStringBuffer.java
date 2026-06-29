package net.rim.device.internal.synchronization.ota.util;

public final class ReusableStringBuffer implements ReusableObject {
   private StringBuffer _stringBuffer = (StringBuffer)(new Object(64));
   public static final long POOL_GUID;

   public final StringBuffer getStringBuffer() {
      return this._stringBuffer;
   }

   @Override
   public final void reset() {
      this._stringBuffer.setLength(0);
   }
}
