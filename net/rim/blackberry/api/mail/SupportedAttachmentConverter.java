package net.rim.blackberry.api.mail;

import net.rim.blackberry.api.mail.rim.MailConverter;
import net.rim.blackberry.api.mail.rim.MailConverterManager;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.MergedCollection;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.MorePartModel;
import net.rim.device.apps.internal.blackberryemail.email.ProxyModel;

class SupportedAttachmentConverter implements MailConverter {
   private MailApiPersistentProxyModelStore _store = MailApiPersistentProxyModelStore.getInstance();

   private SupportedAttachmentConverter() {
      MergedCollection mc = (MergedCollection)FolderMerge.getMergeCollection(-5581791943352753293L);
      this._store.addCollectionEventSource(mc);
   }

   public static void register() {
      MailConverterManager.getInstance().register(new SupportedAttachmentConverter());
   }

   @Override
   public boolean canConvert(Object o) {
      if (o instanceof Object) {
         ProxyModel pm = (ProxyModel)o;
         o = pm.getObject();
      }

      return o instanceof MailApiAttachmentViewerModel || o instanceof SupportedAttachmentPart;
   }

   @Override
   public Object convert(Object o, Object context) {
      if (o instanceof Object) {
         ProxyModel m = (ProxyModel)o;
         o = m.getObject();
      }

      if (o instanceof MailApiAttachmentViewerModel && context instanceof Multipart) {
         MailApiAttachmentViewerModel maavm = (MailApiAttachmentViewerModel)o;
         Multipart parent = (Multipart)context;
         SupportedAttachmentPart sap = new SupportedAttachmentPart(parent, maavm);
         if (maavm instanceof Object) {
            MorePartModel mpm = maavm;
            Message m = (Message)parent.getParent();
            EmailMessageModel emm = m.getEmailMessageModel();
            sap.setInternalModel(emm, mpm.getMorePartID());
         }

         return sap;
      } else if (o instanceof SupportedAttachmentPart && context instanceof Object) {
         SupportedAttachmentPart sap = (SupportedAttachmentPart)o;
         int handle = this._store.add(sap._data);
         return new Object(this._store.getId(), handle);
      } else {
         return null;
      }
   }
}
