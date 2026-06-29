package net.rim.device.apps.internal.docview.gui;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.blackberryemail.email.ProxyModel;

final class DocViewPackageManager$EmailViewerScreenVerbFactory implements VerbFactory {
   @Override
   public final Verb[] getVerbs(Object context) {
      if (!AttachmentViewerFactory.isAttachmentViewingEnabled()) {
         return null;
      }

      EmailMessageModel emailMessageModel = (EmailMessageModel)ContextObject.get(context, -7450314121582082994L);
      if (ContextObject.getFlag(context, 43) && emailMessageModel != null) {
         Object selectedItem = ContextObject.get(context, 250);
         if (selectedItem instanceof DocViewAttachmentViewerModel) {
            return new Verb[]{new OpenAttachmentVerb(emailMessageModel, (DocViewAttachmentViewerModel)selectedItem)};
         }

         if (selectedItem instanceof ProxyModel) {
            Object item = ((ProxyModel)selectedItem).getObject();
            if (item instanceof DocViewAttachmentViewerModel) {
               return new Verb[]{new OpenAttachmentVerb(emailMessageModel, (DocViewAttachmentViewerModel)item)};
            }
         }

         EmailPayloadModel payload = emailMessageModel.getPayload();

         for (int i = payload.size() - 1; i >= 0; i--) {
            Object item = payload.getAt(i);
            if (item instanceof ProxyModel) {
               item = ((ProxyModel)item).getObject();
            }

            if (item instanceof DocViewAttachmentViewerModel) {
               return new Verb[]{new OpenAttachmentVerb(emailMessageModel, null)};
            }
         }

         return null;
      } else {
         return null;
      }
   }
}
