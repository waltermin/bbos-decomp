package net.rim.device.cldc.io.sync.command;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.sync.SyncCommand;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;

public final class GetSyncConfiguration extends SyncCommand {
   private int _deviceCapabilities;

   public GetSyncConfiguration() {
      this.setTag(9);
   }

   @Override
   public final boolean isValid() {
      return true;
   }

   public final void setDeviceCapabilities(int deviceCapabilities) {
      this._deviceCapabilities = deviceCapabilities;
   }

   @Override
   public final void writeParametersTo(DataBuffer douts) {
      if (this._deviceCapabilities != 0) {
         TypeLengthEncoding.writeInt(douts, 102, this._deviceCapabilities);
      }
   }

   @Override
   public final void reset() {
      super.reset();
      this._deviceCapabilities = 0;
   }
}
