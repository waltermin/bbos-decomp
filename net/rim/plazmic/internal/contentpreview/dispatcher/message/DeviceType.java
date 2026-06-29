package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class DeviceType extends Model {
   private String _device;
   public static final String rcsid;

   public DeviceType(String device) {
      this._device = device;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.deviceType(this._device);
   }

   @Override
   final String getClassName() {
      return "DeviceType";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("device", this._device);
   }
}
