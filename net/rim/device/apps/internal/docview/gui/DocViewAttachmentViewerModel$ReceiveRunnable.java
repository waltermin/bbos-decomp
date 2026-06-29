package net.rim.device.apps.internal.docview.gui;

import net.rim.device.apps.api.transmission.rim.RIMMessagingMoreMessage;

final class DocViewAttachmentViewerModel$ReceiveRunnable implements Runnable {
   private Object _context;
   private String _descriptor;
   private byte[] _data;
   private int _messageID;
   private int _partID;
   private final DocViewAttachmentViewerModel this$0;

   private DocViewAttachmentViewerModel$ReceiveRunnable(DocViewAttachmentViewerModel _1, Object context, RIMMessagingMoreMessage moreObject) {
      this.this$0 = _1;
      this._context = context;
      byte[] objectDescriptor = moreObject.getObjectDescriptor();
      if (objectDescriptor != null) {
         this._descriptor = new String(objectDescriptor);
      }

      this._data = moreObject.getRawBytes();
      this._messageID = moreObject.getReferenceIdentifier();
      this._partID = moreObject.getPartIdentifier();
   }

   @Override
   public final void run() {
      if (this._descriptor != null) {
         this.this$0.processReceiveMore(this._context, this._descriptor, this._data, this._messageID, this._partID);
      }
   }

   DocViewAttachmentViewerModel$ReceiveRunnable(DocViewAttachmentViewerModel x0, Object x1, RIMMessagingMoreMessage x2, DocViewAttachmentViewerModel$1 x3) {
      this(x0, x1, x2);
   }
}
