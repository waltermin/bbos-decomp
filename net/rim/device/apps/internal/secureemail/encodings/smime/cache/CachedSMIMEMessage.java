package net.rim.device.apps.internal.secureemail.encodings.smime.cache;

import net.rim.device.api.crypto.cms.CMSSignedDataInputStream;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.secureemail.cache.CachedMessage;
import net.rim.device.apps.internal.secureemail.encodings.smime.SMIMEFactory;

public final class CachedSMIMEMessage extends CachedMessage {
   private CachedSignedReceiptRequestedField _cachedSignedReceiptRequestedField;

   public CachedSMIMEMessage() {
      super(SMIMEFactory.getInstance());
   }

   @Override
   public final boolean hasFooterFields() {
      return this._cachedSignedReceiptRequestedField != null || super.hasFooterFields();
   }

   @Override
   public final void fillManagerFooter(Manager manager, Object context) {
      if (this._cachedSignedReceiptRequestedField != null) {
         this._cachedSignedReceiptRequestedField.fillManager(manager, context);
      }

      super.fillManagerFooter(manager, context);
   }

   @Override
   public final synchronized void setEmailMessageModel(EmailMessageModel emailMessageModel, ServiceRecord serviceRecord) {
      if (this._cachedSignedReceiptRequestedField != null) {
         this._cachedSignedReceiptRequestedField.setEmailMessageModel(emailMessageModel, serviceRecord);
      }

      super.setEmailMessageModel(emailMessageModel, serviceRecord);
   }

   public final void addSignedReceiptRequestedField(CMSSignedDataInputStream cmsStream) {
      this._cachedSignedReceiptRequestedField = new CachedSignedReceiptRequestedField(cmsStream);
   }
}
