package net.rim.wica.runtime.lifecycle.internal;

import java.util.Vector;
import net.rim.wica.runtime.ui.HomeScreenEntry;
import net.rim.wica.runtime.ui.HomeScreenUtilities;

class LifecycleServiceImpl$1 implements Runnable {
   private final Vector val$entries;
   private final LifecycleServiceImpl this$0;

   LifecycleServiceImpl$1(LifecycleServiceImpl this$0, Vector val$entries) {
      this.this$0 = this$0;
      this.val$entries = val$entries;
   }

   @Override
   public void run() {
      int size = this.val$entries.size();

      for (int i = 0; i < size; i++) {
         HomeScreenUtilities.registerEntry((HomeScreenEntry)this.val$entries.elementAt(i));
      }
   }
}
