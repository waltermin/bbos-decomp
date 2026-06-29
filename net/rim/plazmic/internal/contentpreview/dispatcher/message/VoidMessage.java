package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class VoidMessage extends Model {
   public static final String rcsid;

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.voidMessage();
   }

   @Override
   final String getClassName() {
      return "VoidMessage";
   }

   @Override
   final String getProperties() {
      return "";
   }
}
