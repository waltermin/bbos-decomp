package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class DequeueThemeActivationRequest extends Model {
   private int _pin;

   public DequeueThemeActivationRequest(int pin) {
      this._pin = pin;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.dequeueThemeActivationRequest(this._pin);
   }

   @Override
   final String getClassName() {
      return "DequeueThemeActivationRequest";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("pin", String.valueOf(this._pin));
   }
}
