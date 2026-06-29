package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class TimeoutExpiry extends FailureModel {
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/message/TimeoutExpiry.java#1 $";

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
