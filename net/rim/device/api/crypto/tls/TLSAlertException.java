package net.rim.device.api.crypto.tls;

import net.rim.device.cldc.io.ssl.TLSException;

public class TLSAlertException extends TLSException {
   private byte _alertLevel;
   private byte _alertDescription;
   private static final boolean DEBUG = false;

   public TLSAlertException(byte alertLevel, byte alertDescription) {
      super((String)null);
      this._alertLevel = alertLevel;
      this._alertDescription = alertDescription;
   }

   public TLSAlertException(byte alertLevel, byte alertDescription, String msg) {
      super(msg);
      this._alertLevel = alertLevel;
      this._alertDescription = alertDescription;
   }

   public byte getAlertLevel() {
      return this._alertLevel;
   }

   public byte getAlertDescription() {
      return this._alertDescription;
   }
}
