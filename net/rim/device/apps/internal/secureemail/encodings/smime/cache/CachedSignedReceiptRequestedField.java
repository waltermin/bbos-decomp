package net.rim.device.apps.internal.secureemail.encodings.smime.cache;

import net.rim.device.api.crypto.cms.CMSSignedDataInputStream;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.secureemail.cache.CachedField;
import net.rim.device.apps.internal.secureemail.encodings.smime.SignedReceiptRequestedField;

public class CachedSignedReceiptRequestedField extends CachedField {
   private boolean _inbound;
   private CMSSignedDataInputStream _cmsStream;

   public CachedSignedReceiptRequestedField(CMSSignedDataInputStream cmsStream) {
      this._cmsStream = cmsStream;
   }

   @Override
   public void fillManager(Manager manager, Object context) {
      if (!ContextObject.getFlag(context, 101)) {
         manager.add(new SignedReceiptRequestedField(this._cmsStream, this._inbound, context));
      }
   }

   @Override
   public void setEmailMessageModel(EmailMessageModel emailMessageModel, ServiceRecord serviceRecord) {
      this._inbound = emailMessageModel.inbound();
      super.setEmailMessageModel(emailMessageModel, serviceRecord);
   }
}
