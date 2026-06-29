package net.rim.device.apps.internal.secureemail.cache;

import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.apps.internal.secureemail.CertificateHarvesterManager;

public final class CachedOpenedMessageData {
   private boolean _showShortForm;
   private SymmetricKey _symmetricKey;
   private CertificateHarvesterManager _certificateHarvesterManager;

   public final boolean getShowShortForm() {
      return this._showShortForm;
   }

   public final void setShowShortForm(boolean showShortForm) {
      this._showShortForm = showShortForm;
   }

   public final SymmetricKey getSymmetricKey() {
      return this._symmetricKey;
   }

   public final void setSymmetricKey(SymmetricKey symmetricKey) {
      this._symmetricKey = symmetricKey;
   }

   public final CertificateHarvesterManager getCertificateHarvesterManager() {
      return this._certificateHarvesterManager;
   }

   public final void setCertificateHarvesterManager(CertificateHarvesterManager certificateHarvesterManager) {
      this._certificateHarvesterManager = certificateHarvesterManager;
   }
}
