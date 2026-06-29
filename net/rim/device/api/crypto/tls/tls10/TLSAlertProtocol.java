package net.rim.device.api.crypto.tls.tls10;

import net.rim.device.api.crypto.tls.AlertProtocolMethods;
import net.rim.device.api.crypto.tls.SessionResumption;
import net.rim.device.api.crypto.tls.TLSAlertException;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.ssl.TLSException;

public final class TLSAlertProtocol implements AlertProtocolMethods {
   protected TLSRecordProtocol _recordProtocol;
   private boolean _sentCloseNotify;

   public TLSAlertProtocol(TLSRecordProtocol recordProtocol) {
      this._recordProtocol = recordProtocol;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void processAlertMessage(DataBuffer buffer) throws TLSException {
      try {
         byte level = buffer.readByte();
         byte description = buffer.readByte();
         if (level == 1 && description == 0) {
            this._recordProtocol.closeInput(true);
            this.sendCloseNotify(true);
         }

         if (level == 2 || level == 3) {
            SessionResumption resumption = new SessionResumption();
            resumption.removeSession(this._recordProtocol.getDomainName(), this._recordProtocol.getProtocol());
            throw new TLSAlertException(level, description);
         }
      } catch (Throwable var6) {
         throw new TLSException(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void sendAlertMessage(byte alertLevel, byte alertDescription) throws TLSException {
      try {
         alertDescription = this.convertAlertDescription(alertDescription);
         if (alertLevel == 3 || alertLevel == 2) {
            alertLevel = 2;
         }

         byte[] message = new byte[]{alertLevel, alertDescription};
         this._recordProtocol.write(21, message);
         this._recordProtocol.flush();
      } catch (Throwable var5) {
         throw new TLSException(e);
      }
   }

   @Override
   public final void sendCloseNotify(boolean session) {
      if (!this._sentCloseNotify) {
         this._sentCloseNotify = true;
         this.sendAlertMessage((byte)1, (byte)0);
         this._recordProtocol.closeOutput();
      }
   }

   @Override
   public final byte convertAlertDescription(byte description) {
      switch (description) {
         case 0:
         case 10:
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
