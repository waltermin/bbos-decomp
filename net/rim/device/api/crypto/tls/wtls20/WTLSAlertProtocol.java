package net.rim.device.api.crypto.tls.wtls20;

import net.rim.device.api.crypto.tls.AlertProtocolMethods;
import net.rim.device.api.crypto.tls.SessionResumption;
import net.rim.device.api.crypto.tls.TLSAlertException;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;

final class WTLSAlertProtocol implements AlertProtocolMethods {
   private WTLSRecordProtocol _recordProtocol;

   public WTLSAlertProtocol(WTLSRecordProtocol recordProtocol) {
      this._recordProtocol = recordProtocol;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void processAlertMessage(DataBuffer buffer) {
      try {
         byte level = buffer.readByte();
         byte description = buffer.readByte();
         byte[] receivedChecksum = new byte[4];
         buffer.readFully(receivedChecksum);
         byte[] computedChecksum = this.computeChecksum(this._recordProtocol.getLastWrittenCipherTextMessage());
         if (Arrays.equals(receivedChecksum, computedChecksum)) {
            SessionResumption resumption = new SessionResumption();
            resumption.removeSession(this._recordProtocol.getDomainName(), this._recordProtocol.getProtocol());
            throw new TLSAlertException(level, description);
         }
      } catch (Throwable var8) {
         throw new Object(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void sendAlertMessage(byte alertLevel, byte alertDescription) {
      try {
         byte[] checksum = this.computeChecksum(this._recordProtocol.getLastReadCipherTextMessage());
         alertDescription = this.convertAlertDescription(alertDescription);
         byte[] finalMessage = new byte[]{alertLevel, alertDescription, checksum[0], checksum[1], checksum[2], checksum[3]};
         this._recordProtocol.write(2, finalMessage);
      } catch (Throwable var6) {
         throw new Object(e);
      }
   }

   @Override
   public final void sendCloseNotify(boolean session) {
      if (session) {
         this.sendAlertMessage((byte)1, (byte)1);
      } else {
         this.sendAlertMessage((byte)1, (byte)0);
      }
   }

   private final byte[] computeChecksum(DataBuffer buffer) {
      if (buffer == null) {
         buffer = (DataBuffer)(new Object(0, true));
      }

      int length = buffer.getLength();
      if (length % 4 != 0) {
         buffer.setPosition(length);
         int pad = length % 4;
         byte[] bytes = new byte[4 - pad];
         buffer.write(bytes);
      }

      byte[] data = buffer.getArray();
      byte[] checksum = new byte[4];

      for (int i = 0; i < length; i += 4) {
         checksum[0] ^= data[i + 0];
         checksum[1] ^= data[i + 1];
         checksum[2] ^= data[i + 2];
         checksum[3] ^= data[i + 3];
      }

      return checksum;
   }

   @Override
   public final byte convertAlertDescription(byte description) {
      switch (description) {
         case 0:
         case 1:
         case 5:
         case 10:
         case 11:
         case 20:
         case 21:
         case 22:
         case 30:
         case 40:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 60:
         case 70:
         case 71:
         case 80:
         case 90:
         case 100:
            return description;
         default:
            return 80;
      }
   }
}
