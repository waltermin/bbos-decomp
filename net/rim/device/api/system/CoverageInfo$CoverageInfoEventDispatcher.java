package net.rim.device.api.system;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class CoverageInfo$CoverageInfoEventDispatcher extends EventDispatcher {
   private CoverageInfo$CoverageInfoEventDispatcher() {
   }

   @Override
   public final void dispatch(Message message, Object listener) {
      int event = message.getEvent();
      CoverageStatusListener csListener = (CoverageStatusListener)listener;
      switch (event) {
         case 0:
            int coverage = message.getSubMessage();
            csListener.coverageStatusChanged(coverage);
      }
   }

   CoverageInfo$CoverageInfoEventDispatcher(CoverageInfo$1 x0) {
      this();
   }
}
