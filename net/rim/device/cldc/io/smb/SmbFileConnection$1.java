package net.rim.device.cldc.io.smb;

import java.util.Enumeration;

final class SmbFileConnection$1 implements Enumeration {
   private int _index;
   private final SmbFileConnection this$0;

   SmbFileConnection$1(SmbFileConnection _1) {
      this.this$0 = _1;
   }

   @Override
   public final boolean hasMoreElements() {
      return this._index < this.this$0._fileNames.length;
   }

   @Override
   public final Object nextElement() {
      return this.this$0._fileNames[this._index++];
   }
}
