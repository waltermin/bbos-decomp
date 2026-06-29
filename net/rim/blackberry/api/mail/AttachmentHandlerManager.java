package net.rim.blackberry.api.mail;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;

public final class AttachmentHandlerManager {
   private Vector _handlers = (Vector)(new Object(5));
   private static final long ATTACHMENT_HANDLER_MANAGER_ID;

   private AttachmentHandlerManager() {
      new MailApiAttachmentViewerModelConverter();

      try {
         ApplicationRegistry.getApplicationRegistry().put(7720385540336953835L, new MailApiAttachmentViewerModelFactory());
      } finally {
         return;
      }
   }

   public static final AttachmentHandlerManager getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         AttachmentHandlerManager ahm = (AttachmentHandlerManager)ar.get(-7872671635551831164L);
         if (ahm == null) {
            ahm = new AttachmentHandlerManager();
            ar.put(-7872671635551831164L, ahm);
         }

         return ahm;
      }
   }

   public final void addAttachmentHandler(AttachmentHandler ah) {
      if (ah == null) {
         throw new Object("AttachmentHandler cannot be null");
      }

      if (!this._handlers.contains(ah)) {
         this._handlers.addElement(ah);
      }
   }

   public final void removeAttachmentHandler(AttachmentHandler ah) {
      this._handlers.removeElement(ah);
   }

   final Vector getHandlers() {
      return this._handlers;
   }
}
