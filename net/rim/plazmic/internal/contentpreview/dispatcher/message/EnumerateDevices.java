package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class EnumerateDevices extends Model {
   public static final String rcsid;

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.enumerateDevices();
   }

   @Override
   final String getClassName() {
      return "EnumerateDevices";
   }

   @Override
   final String getProperties() {
      return "";
   }
}
