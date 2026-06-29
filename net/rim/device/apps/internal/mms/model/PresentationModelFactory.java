package net.rim.device.apps.internal.mms.model;

import java.util.Enumeration;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.options.MMSOptions;

public final class PresentationModelFactory {
   public static final MMSPresentationModel createInstance(int type) {
      switch (type) {
         case 65535:
            throw new Object();
         case 65536:
         default:
            return new DraftPresentationModel();
      }
   }

   public static final MMSPresentationModel createInstance(MMSAttachment attachment) {
      switch (attachment.getType()) {
         case 8:
         case 20:
         case 65537:
         case 65540:
            return new BrowserPresentationModel(attachment);
         case 65536:
            return new DraftPresentationModel(attachment);
         default:
            throw new Object();
      }
   }

   public static final MMSAttachment findPresentationAttachment(Enumeration names, AttachmentDataProvider attachmentProvider) {
      if (names == null) {
         return null;
      }

      MMSAttachment draftAttachment = null;
      MMSAttachment presentationAttachment = null;
      boolean hasPIM = false;

      while (names.hasMoreElements()) {
         String name = (String)names.nextElement();
         MMSAttachment attachment = attachmentProvider.getAttachment(name);
         if (attachment != null) {
            if (name.equals("net_rim_DraftPresentation")) {
               draftAttachment = attachment;
            } else {
               switch (attachment.getType()) {
                  case 6:
                  case 7:
                     hasPIM = true;
                     break;
                  case 8:
                  case 20:
                  case 65537:
                  case 65540:
                     presentationAttachment = attachment;
                     break;
                  case 65536:
                     draftAttachment = attachment;
               }
            }
         }
      }

      if (hasPIM || MMSOptions.getInstance().getOptionFlag(32)) {
         presentationAttachment = null;
      }

      return presentationAttachment != null ? presentationAttachment : draftAttachment;
   }
}
