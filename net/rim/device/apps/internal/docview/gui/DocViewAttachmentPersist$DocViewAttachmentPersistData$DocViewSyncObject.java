package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;

final class DocViewAttachmentPersist$DocViewAttachmentPersistData$DocViewSyncObject implements SyncObject, BackupProvider {
   private int _messageID;
   private int _morePartID;
   private AttachmentInfo _attachInfo;
   private final DocViewAttachmentPersist$DocViewAttachmentPersistData this$0;

   DocViewAttachmentPersist$DocViewAttachmentPersistData$DocViewSyncObject(
      DocViewAttachmentPersist$DocViewAttachmentPersistData _1, AttachmentInfo aInfo, int msgID, int morePartID
   ) {
      this.this$0 = _1;
      this._attachInfo = aInfo;
      this._messageID = msgID;
      this._morePartID = morePartID;
   }

   @Override
   public final int getUID() {
      return 3;
   }

   @Override
   public final void serializeData(DataBuffer buffer, int param) {
      synchronized (this.this$0._msgMap) {
         ConverterUtilities.writeInt(buffer, 0, this._messageID);
         ConverterUtilities.writeInt(buffer, 1, this._morePartID);
         this._attachInfo.serializeData(buffer, -1);
      }
   }
}
