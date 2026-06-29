package net.rim.device.apps.internal.docview.gui;

final class ForwardScreen$MoreUpdateThread extends Thread {
   private final ServerResponse _response;
   private final int _morePartID;
   private final ForwardScreen this$0;

   ForwardScreen$MoreUpdateThread(ForwardScreen _1, ServerResponse response, int morePartID) {
      this.this$0 = _1;
      this._response = response;
      this._morePartID = morePartID;
   }

   @Override
   public final void run() {
      if (this._response._errorCode == 3 || this._response._errorCode == 4) {
         ForwardScreen$PasswordInfo pwdInfo = this.this$0.getPwdInfo(this._morePartID, this._response._archiveIndicator);
         if (pwdInfo != null) {
            pwdInfo._requireDisplayPwd = true;
            pwdInfo._password = "";
         }
      } else if (this._response._errorCode == 0) {
         ForwardScreen$PasswordInfo pwdInfo = this.this$0.getPwdInfo(this._morePartID, this._response._archiveIndicator);
         if (pwdInfo != null && pwdInfo._requireDisplayPwd) {
            pwdInfo._requireDisplayPwd = false;
         }
      }

      synchronized (this.this$0._syncObject) {
         int node = this.this$0._attachmentsTree.findNode(this._morePartID, this._response._archiveIndicator, this._response._docID._partIndex);
         if (node != -1 && this.this$0.notifyStateChanged(node, true)) {
            this.this$0._appInstance.invokeLater(new ForwardScreen$MoreUpdateThread$1(this));
         }

         if (this.this$0._pendingParts == 0) {
            if (this._response._errorCode == 0) {
               if (AttachmentViewerFactory.isTypeRequestAllChunks(
                     DocViewAttachmentPersist.getInstance().getAttachmentType(this.this$0._messageID, this._morePartID, this._response._archiveIndicator)
                  )
                  || this._response._crtBlockIndex
                     == DocViewAttachmentPersist.getInstance()
                        .getStartBlockIndex(this.this$0._messageID, this._morePartID, this._response._archiveIndicator, this._response._docID._partIndex)) {
                  this.this$0.doAutoView(node);
               }
            } else {
               this.this$0._appInstance.invokeLater(new ForwardScreen$MoreUpdateThread$2(this, node), 1250, false);
            }
         }
      }
   }
}
