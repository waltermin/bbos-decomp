package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class GetValidDevice extends Model {
   private String _candidateDevice;
   public static final String rcsid;

   public GetValidDevice(String candidateDevice) {
      this._candidateDevice = candidateDevice;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.getValidDevice(this._candidateDevice);
   }

   @Override
   final String getClassName() {
      return "GetValidDevice";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("candidateDevice", this._candidateDevice);
   }
}
