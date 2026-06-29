package net.rim.device.api.crypto.tls;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.ssl.TLSException;

public final class ChangeCipherSpecProtocol {
   private RecordProtocol _recordProtocol;

   public ChangeCipherSpecProtocol(RecordProtocol recordProtocol) {
      this._recordProtocol = recordProtocol;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void processChangeCipherSpecMessage(DataBuffer buffer) throws TLSException {
      try {
         if (buffer.readUnsignedByte() == 1) {
            this._recordProtocol.changeCipherSpec();
         } else {
            throw new TLSException();
         }
      } catch (Throwable var4) {
         throw new TLSException(e);
      }
   }

   public final void sendChangeCipherSpecMessage() {
      this._recordProtocol.write(this._recordProtocol.getChangeCipherSpecConstant(), new byte[]{1});
   }
}
