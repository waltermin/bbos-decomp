package net.rim.device.apps.internal.mms.service;

import java.util.Enumeration;

class MMSProtocolDataUnit$1 implements Enumeration {
   private int _idx;
   private final MMSProtocolDataUnit this$0;

   MMSProtocolDataUnit$1(MMSProtocolDataUnit _1) {
      this.this$0 = _1;
   }

   @Override
   public boolean hasMoreElements() {
      int count = this.this$0.getAttachmentCount();
      this.advanceIfNull(count);
      return this._idx < count;
   }

   @Override
   public Object nextElement() {
      int count = this.this$0.getAttachmentCount();
      this.advanceIfNull(count);
      String name = this.this$0.getAttachmentName(this._idx++, 0);
      return MMSProtocolDataUnit.trimAttachmentName(name);
   }

   private void advanceIfNull(int max) {
      while (this._idx < max && !this.this$0.hasAttachmentName(this._idx)) {
         this._idx++;
      }
   }
}
