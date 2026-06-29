package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class GetRecentSession extends Model {
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/message/GetRecentSession.java#1 $";

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.getRecentSession();
   }

   @Override
   final String getClassName() {
      return "GetRecentSession";
   }

   @Override
   final String getProperties() {
      return "";
   }
}
