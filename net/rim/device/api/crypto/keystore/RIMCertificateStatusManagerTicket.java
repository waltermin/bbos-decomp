package net.rim.device.api.crypto.keystore;

import net.rim.device.api.system.Application;

final class RIMCertificateStatusManagerTicket implements CertificateStatusManagerTicket {
   private int _processID;

   public RIMCertificateStatusManagerTicket() {
      this.prompt();
      this._processID = Application.getApplication().getProcessId();
   }

   public final boolean access() {
      return Application.getApplication().getProcessId() == this._processID;
   }

   private final void prompt() {
      KeyStoreUtilitiesInternal.getCurrentKeyStorePassword(KeyStoreResources.getString(6083), true, false);
   }
}
