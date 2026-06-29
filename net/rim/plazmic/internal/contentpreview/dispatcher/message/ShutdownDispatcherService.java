package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class ShutdownDispatcherService extends Model {
   public static final String rcsid;

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.shutdownDispatcherService();
   }

   @Override
   final String getClassName() {
      return "ShutdownDispatcherService";
   }

   @Override
   final String getProperties() {
      return "";
   }
}
