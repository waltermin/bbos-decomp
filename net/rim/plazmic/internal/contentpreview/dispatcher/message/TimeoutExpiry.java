package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class TimeoutExpiry extends FailureModel {
   public static final String rcsid;

   public TimeoutExpiry(String message) {
      super(message);
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.timeoutExpiry(this.getMessage());
   }

   @Override
   final String getClassName() {
      return "TimeoutExpiry";
   }
}
