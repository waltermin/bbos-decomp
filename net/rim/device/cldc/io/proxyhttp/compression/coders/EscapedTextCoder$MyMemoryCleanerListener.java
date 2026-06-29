package net.rim.device.cldc.io.proxyhttp.compression.coders;

import net.rim.device.api.memorycleaner.MemoryCleanerListener;

class EscapedTextCoder$MyMemoryCleanerListener implements MemoryCleanerListener {
   private final EscapedTextCoder this$0;

   private EscapedTextCoder$MyMemoryCleanerListener(EscapedTextCoder _1) {
      this.this$0 = _1;
   }

   @Override
   public boolean cleanNow(int event) {
      return event == 10 && this.this$0.clearCacheData2();
   }

   @Override
   public String getDescription() {
      return null;
   }

   EscapedTextCoder$MyMemoryCleanerListener(EscapedTextCoder x0, EscapedTextCoder$1 x1) {
      this(x0);
   }
}
