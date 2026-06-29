package net.rim.device.cldc.io.sync.command;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.sync.SyncCommand;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;
import net.rim.vm.Array;

public class SyncConfiguration extends SyncCommand {
   protected byte[] _configuration;

   public SyncConfiguration() {
      this.setTag(10);
      this._configuration = new byte[0];
   }

   @Override
   public boolean isValid() {
      return this._configuration != null && this._configuration.length != 0;
   }

   public void setConfiguration(byte[] configuration) {
      this._configuration = configuration;
   }

   public byte[] getConfiguration() {
      return this._configuration;
   }

   @Override
   public void readParametersFrom(DataBuffer din) {
      try {
         while (din.available() > 0) {
            int xTag = TypeLengthEncoding.readTag(din);
            if (xTag == 64) {
               TypeLengthEncoding.readBytes(din, this._configuration);
            } else {
               TypeLengthEncoding.skipValue(din);
            }
         }
      } finally {
         return;
      }
   }

   @Override
   public void writeParametersTo(DataBuffer outs) {
      TypeLengthEncoding.writeBytes(outs, 64, this._configuration);
   }

   @Override
   public void reset() {
      super.reset();
      Array.resize(this._configuration, 0);
   }
}
