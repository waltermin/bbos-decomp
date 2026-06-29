package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class EnumerateDevices extends Model {
   public static final String rcsid = "$Id: //depot/projects/JavaDevice/4.3.0/JavaApplications/sdk/CDK/net/rim/plazmic/internal/contentpreview/dispatcher/message/EnumerateDevices.java#1 $";

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
