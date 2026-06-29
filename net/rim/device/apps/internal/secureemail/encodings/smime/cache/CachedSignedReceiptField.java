package net.rim.device.apps.internal.secureemail.encodings.smime.cache;

import net.rim.device.api.crypto.cms.CMSReceiptData;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.secureemail.cache.CachedField;
import net.rim.device.apps.internal.secureemail.encodings.smime.SMIMEResources;
import net.rim.device.apps.internal.secureemail.encodings.smime.SignedReceiptUserData;
import net.rim.device.internal.ui.RichTextFieldUtilities;

public class CachedSignedReceiptField extends CachedField {
   private CMSReceiptData _receiptData;

   public CachedSignedReceiptField(CMSReceiptData receiptData) {
      this._receiptData = receiptData;
   }

   @Override
   public void fillManager(Manager manager, Object context) {
      if (!ContextObject.getFlag(context, 101)) {
         if (this._receiptData == null) {
            manager.add(new RichTextField(SMIMEResources.getString(2032)));
         } else {
            manager.add(new RichTextField(SMIMEResources.getString(2031)));
            SignedReceiptUserData userData = (SignedReceiptUserData)this._receiptData.getUserData();
            this.addBoldLabelValueField(manager, SMIMEResources.getString(2090), userData.getTo());
            this.addBoldLabelValueField(manager, SMIMEResources.getString(2091), userData.getCC());
            this.addBoldLabelValueField(manager, SMIMEResources.getString(2092), userData.getBCC());
            this.addBoldLabelValueField(manager, SMIMEResources.getString(2093), userData.getSubject());
            DateFormat format = DateFormat.getInstance(54);
            this.addBoldLabelValueField(manager, SMIMEResources.getString(2088), format.formatLocal(userData.getSent()));
         }
      }
   }

   private void addBoldLabelValueField(Manager manager, String labelValuePattern, String value) {
      if (value != null) {
         String labelValue = MessageFormat.format(labelValuePattern, new String[]{value});
         manager.add(RichTextFieldUtilities.getBoldFormattedRichTextField(labelValue, 27021597764222976L));
      }
   }
}
