package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.MorePartModel;
import net.rim.device.apps.internal.blackberryemail.email.ProxyModel;

final class DocViewMessageListener implements CollectionListener {
   private static final byte MAX_PREFETCH_COUNT = 5;
   private static final byte MAX_ARCHIVE_PREFETCH_REQUESTS = 2;

   @Override
   public final void reset(Collection collection) {
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      if (AttachmentViewerFactory.isAttachmentViewingEnabled() && element instanceof EmailMessageModel) {
         EmailMessageModel model = (EmailMessageModel)element;
         if (model.getStatus() == 2047 && AttachmentViewerFactory.isFastAttachmentEnabled()) {
            EmailPayloadModel payload = model.getPayload();
            if (payload != null && payload.size() > 0) {
               int size = payload.size();
               DocViewAttachmentViewerModel[] attachments = new DocViewAttachmentViewerModel[0];

               for (int i = 0; i < size; i++) {
                  Object item = payload.getAt(i);
                  if (item instanceof ProxyModel) {
                     item = ((ProxyModel)item).getObject();
                  }

                  if (item instanceof DocViewAttachmentViewerModel) {
                     Arrays.add(attachments, item);
                  }
               }

               if (attachments.length > 0) {
                  new DocViewMessageListener$1(this, model, attachments).start();
               }
            }
         }
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      try {
         EmailMessageModel model = (EmailMessageModel)newElement;
         if (model.getStatus() == 1) {
            Object obj = ApplicationRegistry.getApplicationRegistry().get(-5725347163685773030L);
            LatestRequestInfo fwdScreenRequest = null;
            if (obj instanceof LatestRequestInfo) {
               fwdScreenRequest = (LatestRequestInfo)obj;
            }

            obj = ApplicationRegistry.getApplicationRegistry().get(-1519928498572343430L);
            LatestRequestInfo displayScreenRequest = null;
            if (obj instanceof LatestRequestInfo) {
               displayScreenRequest = (LatestRequestInfo)obj;
            }

            if (fwdScreenRequest != null || displayScreenRequest != null) {
               boolean msgAlreadyFailed = false;
               if (fwdScreenRequest != null) {
                  this.notifyForwardScreen(fwdScreenRequest);
                  msgAlreadyFailed = fwdScreenRequest._messageAlreadyFailed;
               }

               if (displayScreenRequest != null) {
                  this.notifyDisplayScreen(displayScreenRequest);
                  msgAlreadyFailed = displayScreenRequest._messageAlreadyFailed;
               }

               if (!msgAlreadyFailed
                  && this.wasAttachmentPartRequested(
                     model, fwdScreenRequest != null ? fwdScreenRequest._attachmentMoreID : displayScreenRequest._attachmentMoreID
                  )) {
                  this.markMessageUnread(model);
                  return;
               }
            }
         }
      } finally {
         return;
      }
   }

   private final void notifyForwardScreen(LatestRequestInfo request) {
      boolean bProcessed = DocViewAttachmentPersist.getInstance()
         .onMessageError(request._messageID, request._attachmentMoreID, request._archiveIndicatorString, request._attachmentPartID);
      if (bProcessed) {
         DocViewAttachmentPersist.commitChanges();
      }

      ForwardScreen[] fwdScreenInstances = DocViewDisplayScreenInstance.getForwardScreenInstances();
      if (fwdScreenInstances != null) {
         for (int i = 0; i < fwdScreenInstances.length; i++) {
            if (request._applicationID == 0) {
               if (fwdScreenInstances[i]._messageID == request._messageID) {
                  fwdScreenInstances[i].notifyMoreRequestFailed(request);
               }
            } else if (fwdScreenInstances[i]._messageID == request._messageID && fwdScreenInstances[i]._applicationID == request._applicationID) {
               fwdScreenInstances[i].notifyMoreRequestFailed(request);
               return;
            }
         }
      }
   }

   private final void notifyDisplayScreen(LatestRequestInfo request) {
      ActiveDisplayedPart[] displayParts = DocViewDisplayScreenInstance.getActivePartInstances();
      if (displayParts != null) {
         for (int i = 0; i < displayParts.length; i++) {
            if (request._applicationID == 0) {
               if (displayParts[i]._active && displayParts[i]._screen != null && displayParts[i]._messageID == request._messageID) {
                  displayParts[i]._screen.notifyMoreRequestFailed(request);
               }
            } else if (displayParts[i]._active
               && displayParts[i]._screen != null
               && displayParts[i]._messageID == request._messageID
               && displayParts[i]._screen._applicationID == request._applicationID) {
               displayParts[i]._screen.notifyMoreRequestFailed(request);
               return;
            }
         }
      }
   }

   private final void markMessageUnread(EmailMessageModel model) {
      boolean messageOpened = ModelViewListenerRegistry.isViewerUp(0, model, null);
      model.changeStatus(0, messageOpened ? 0 : 1, 2047, 0, true, true, false, false, null);
   }

   private final boolean wasAttachmentPartRequested(EmailMessageModel model, int morePartID) {
      try {
         EmailPayloadModelImpl payload = (EmailPayloadModelImpl)model.getPayload();
         int submembersCount = payload.size();

         for (int i = 0; i < submembersCount; i++) {
            Object submember = payload.getAt(i);
            if (submember instanceof ProxyModel) {
               Object subObject = ((ProxyModel)submember).getObject();
               if (subObject instanceof MorePartModel) {
                  MorePartModel partModel = (MorePartModel)subObject;
                  if (partModel.getMorePartID() == morePartID) {
                     boolean retValue = partModel.getMoreRequestSent();
                     partModel.clearMoreRequestSent();
                     return retValue;
                  }
               }
            }
         }
      } finally {
         return false;
      }

      return false;
   }
}
