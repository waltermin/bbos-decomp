package net.rim.device.apps.internal.docview.gui;

import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

final class DocViewAttachmentViewerModel$ReceiveRunnableEx implements Runnable {
   private Object _context;
   private ServerResponse _response;
   private byte[] _data;
   private int _messageID;
   private int _partID;
   private final DocViewAttachmentViewerModel this$0;

   private DocViewAttachmentViewerModel$ReceiveRunnableEx(
      DocViewAttachmentViewerModel _1, Object context, ServerResponse response, byte[] ucsData, int messageID, int partID
   ) {
      this.this$0 = _1;
      this._context = context;
      this._response = response;
      this._data = ucsData;
      this._messageID = messageID;
      this._partID = partID;
   }

   @Override
   public final void run() {
      this.this$0.processReceiveMoreImpl(this._context, this._response, this._data, this._messageID, this._partID, false);
      Object obj = MessageLookups.get(-4420850319371185992L, this._messageID);
      if (obj instanceof EmailMessageModel) {
         EmailMessageModel message = (EmailMessageModel)obj;
         if (ModelViewListenerRegistry.isViewerUp(0, message, null)) {
            ModelViewListenerRegistry.notifyOfOpenedModelChange(message, message, this._context);
         }
      }
   }

   DocViewAttachmentViewerModel$ReceiveRunnableEx(
      DocViewAttachmentViewerModel x0, Object x1, ServerResponse x2, byte[] x3, int x4, int x5, DocViewAttachmentViewerModel$1 x6
   ) {
      this(x0, x1, x2, x3, x4, x5);
   }
}
