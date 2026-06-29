package net.rim.device.apps.internal.secureemail.cache;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.MorePartModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField;
import net.rim.device.apps.internal.secureemail.VerticalExtentIndicatorFieldManager;

public class CachedSignatureField extends CachedManager {
   protected boolean _inbound;
   protected long _creationDate;
   protected boolean _isPINMessage;
   protected boolean _moreAvailable;
   protected String _senderEmailAddress;
   protected int _besVerificationState;
   protected byte[] _besSignerCertificateHash;
   protected String _besNoVerifyReason;
   SecureEmailSignatureField _secureEmailSignatureField;

   public CachedSignatureField(int besVerificationState, byte[] besSignerCertificateHash, String besNoVerifyReason) {
      this._besVerificationState = besVerificationState;
      this._besSignerCertificateHash = besSignerCertificateHash;
      this._besNoVerifyReason = besNoVerifyReason;
   }

   @Override
   public void setEmailMessageModel(EmailMessageModel emailMessageModel, ServiceRecord serviceRecord) {
      super.setEmailMessageModel(emailMessageModel, serviceRecord);
      this._inbound = emailMessageModel.inbound();
      this._creationDate = emailMessageModel.getPayload().getCreationDate();
      this._isPINMessage = emailMessageModel.flagsSet(8192);
      MorePartModel morePartModel = EmailMoreVerb.findBodyMorePartModel(emailMessageModel);
      this._moreAvailable = morePartModel != null && morePartModel.isMoreAvailable();
      if (this._inbound) {
         String[] senderAddressAndName = new Object[2];
         int numModels = emailMessageModel.size();

         for (int i = 0; i < numModels; i++) {
            Object submember = emailMessageModel.getAt(i);
            if (submember instanceof Object) {
               EmailHeaderModel emailHeaderModel = (EmailHeaderModel)submember;
               if (emailHeaderModel.getHeaderType() == 3 || emailHeaderModel.getHeaderType() == 4) {
                  boolean result = emailHeaderModel.convert(null, senderAddressAndName);
                  if (result) {
                     this._senderEmailAddress = senderAddressAndName[0];
                     return;
                  }
               }
            }
         }
      } else if (super._serviceRecord != null) {
         this._senderEmailAddress = CMIMEUtilities.getEmailAddress(super._serviceRecord);
      }
   }

   @Override
   protected Manager createManager(Object context) {
      return ContextObject.getFlag(context, 101) ? super.createManager(context) : new VerticalExtentIndicatorFieldManager(16777215, 1152921504606846976L);
   }

   @Override
   public boolean hasHeaderFields() {
      return true;
   }

   @Override
   public void fillManagerHeader(Manager manager, Object context) {
      if (!ContextObject.getFlag(context, 101)) {
         this._secureEmailSignatureField = this.getSecureEmailSignatureField(manager, context, this._secureEmailSignatureField);
         ((VerticalExtentIndicatorFieldManager)manager).setExtentIndicatorColourRGB(this._secureEmailSignatureField.getOverallSignatureStatusColourRGB());
         manager.add(this._secureEmailSignatureField.getSignatureStatusField());
         manager.add(this._secureEmailSignatureField.getTrustStatusField());
         this._secureEmailSignatureField.verify();
      }
   }

   protected SecureEmailSignatureField getSecureEmailSignatureField(Manager _1, Object _2, SecureEmailSignatureField _3) {
      throw null;
   }
}
