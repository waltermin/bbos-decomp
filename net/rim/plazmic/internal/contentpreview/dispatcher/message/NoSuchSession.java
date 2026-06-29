package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class NoSuchSession extends FailureModel {
   public static final String rcsid;

   public NoSuchSession(String message) {
      super(message);
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.noSuchSession(this.getMessage());
   }

   @Override
   final String getClassName() {
      return "NoSuchSession";
   }
}
