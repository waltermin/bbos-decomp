package net.rim.device.apps.internal.docview.gui;

final class DocViewSheetData$ArznCellTextHandler {
   DocViewTextContentHandler _tcHandler;
   DocViewTextContentHandler _trackChangesHandler;
   private boolean _complete;
   private DocViewTextContentHandler _parkTcHandler;
   private DocViewTextContentHandler _parkTrackChangesHandler;
   private int _chunkIndexWhenComplete = -1;

   DocViewSheetData$ArznCellTextHandler(boolean isComplete, int currentChunkIndex) {
      this.setComplete(isComplete, currentChunkIndex);
   }

   private final void setComplete(boolean isCompleteCell, int currentChunkIndex) {
      if (!this._complete) {
         if (isCompleteCell) {
            this._complete = true;
            this._chunkIndexWhenComplete = currentChunkIndex;
            return;
         }
      } else if (!isCompleteCell) {
         this._parkTcHandler = this._tcHandler;
         this._parkTrackChangesHandler = this._trackChangesHandler;
         this._tcHandler = null;
         this._trackChangesHandler = null;
         this._complete = false;
      }
   }

   private final void endCellParsing(int currentChunkIndex) {
      if (this._parkTcHandler != null && currentChunkIndex == this._chunkIndexWhenComplete - 1) {
         if (this._tcHandler != null) {
            this._tcHandler.append(this._parkTcHandler);
            if (this._parkTrackChangesHandler != null) {
               if (this._trackChangesHandler == null) {
                  this._trackChangesHandler = new DocViewTextContentHandler(this._tcHandler, true);
               }

               this._trackChangesHandler.append(this._parkTrackChangesHandler);
            }
         } else {
            this._tcHandler = this._parkTcHandler;
            this._trackChangesHandler = this._parkTrackChangesHandler;
         }

         this._parkTcHandler = null;
         this._parkTrackChangesHandler = null;
      }
   }
}
