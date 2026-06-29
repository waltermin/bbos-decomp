package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Screen;

final class DocViewDisplayScreen$MoreParsingThread extends Thread {
   private byte _embObjType;
   private int _srcType;
   private int _crtBlockIndex;
   private int _totalRetrievedBlocks;
   private int _totalBlocksInfo;
   private String _embDomID;
   private int _partIndexInfo;
   private String _findPattern;
   private int _findIncomplete;
   private boolean _findCaseSensitive;
   private byte[] _ucs;
   private Screen _displayScreen;
   private final DocViewDisplayScreen this$0;
   private static final long WAIT_TIMEOUT = 20000L;

   DocViewDisplayScreen$MoreParsingThread(DocViewDisplayScreen _1, ServerResponse response, byte[] ucsData, int totalRetrievedBlocks, Screen displayScreen) {
      this.this$0 = _1;
      this._embObjType = response._docID._embObjType;
      this._srcType = response._docID._srcType;
      this._crtBlockIndex = response._crtBlockIndex;
      this._totalBlocksInfo = response._docID._totalBlocks;
      this._embDomID = response._docID._domID;
      this._partIndexInfo = response._docID._partIndex;
      this._findPattern = response._findPattern;
      this._findIncomplete = response._findIncomplete;
      this._findCaseSensitive = response._findCaseSensitive;
      this._ucs = ucsData;
      this._totalRetrievedBlocks = totalRetrievedBlocks;
      this._displayScreen = displayScreen;
   }

   @Override
   public final void run() {
      try {
         boolean isTypeReqAll = AttachmentViewerFactory.isTypeRequestAllChunks(this._embObjType);
         if (this._srcType == 1) {
            if (this._crtBlockIndex == 0) {
               if (!isTypeReqAll || this._totalBlocksInfo == this._totalRetrievedBlocks) {
                  this.this$0.processEmbeddedInitialChunk(this._ucs, this._totalBlocksInfo, this._embDomID, this._totalRetrievedBlocks);
               }

               return;
            }

            if (!this.this$0._isEmbScreen || this._embDomID.compareTo(this.this$0._domID) != 0) {
               if (isTypeReqAll && this._totalBlocksInfo == this._totalRetrievedBlocks && this._srcType == 1) {
                  DocViewDisplayScreen screen = this.this$0.getScreenWithRequest(this._embDomID);
                  if (screen != null) {
                     screen.embeddedObjectInitialChunkArrived(null, this._embDomID);
                  }
               }

               return;
            }
         }

         while (this.this$0._docData != null && this.this$0.moreDataProcessed(this._ucs, this._crtBlockIndex, this._totalRetrievedBlocks, false) == 2) {
            synchronized (this._displayScreen) {
               try {
                  this._displayScreen.wait(20000);
               } finally {
                  continue;
               }
            }
         }

         this.this$0.postMoreRequestProcessed(this._partIndexInfo, this._displayScreen);
         if (this._partIndexInfo == 1001) {
            this.this$0.serverFindResponse((short)0, this._findPattern, this._findIncomplete, this._findCaseSensitive);
            return;
         }
      } finally {
         return;
      }
   }
}
