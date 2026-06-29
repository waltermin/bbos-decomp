package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class GetServerVersion extends Model {
   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.getServerVersion();
   }

   @Override
   final String getClassName() {
      return "GetServerVersion";
   }

   @Override
   final String getProperties() {
      return "";
   }
}
