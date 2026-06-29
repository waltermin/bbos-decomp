package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class InvalidDevice extends FailureModel {
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/message/InvalidDevice.java#1 $";

   public InvalidDevice(String message) {
      super(message);
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.invalidDevice(this.getMessage());
   }

   @Override
   final String getClassName() {
      return "InvalidDevice";
   }
}
