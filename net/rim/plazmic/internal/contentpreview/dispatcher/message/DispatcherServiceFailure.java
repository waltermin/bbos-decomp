package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class DispatcherServiceFailure extends FailureModel {
   public static final String rcsid;

   public DispatcherServiceFailure(String message) {
      super(message);
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.dispatcherServiceFailure(this.getMessage());
   }

   @Override
   final String getClassName() {
      return "DispatcherServiceFailure";
   }
}
