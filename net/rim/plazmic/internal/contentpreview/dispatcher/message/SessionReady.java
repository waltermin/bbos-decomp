package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class SessionReady extends Model {
   private int _pin;
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/message/SessionReady.java#1 $";

   public SessionReady(int pin) {
      this._pin = pin;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.sessionReady(this._pin);
   }

   @Override
   final String getClassName() {
      return "SessionReady";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("pin", Integer.toString(this._pin));
   }
}
