package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class DequeueThemeRegistrationRequest extends Model {
   private int _pin;

   public DequeueThemeRegistrationRequest(int pin) {
      this._pin = pin;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.dequeueThemeRegistrationRequest(this._pin);
   }

   @Override
   final String getClassName() {
      return "DequeueThemeRegistrationRequest";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("pin", String.valueOf(this._pin));
   }
}
