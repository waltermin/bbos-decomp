package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class GetSpecificSession extends Model {
   private int _pin;
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/message/GetSpecificSession.java#1 $";

   public GetSpecificSession(int pin) {
      this._pin = pin;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.getSpecificSession(this._pin);
   }

   @Override
   final String getClassName() {
      return "GetSpecificSession";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("pin", Integer.toString(this._pin));
   }
}
