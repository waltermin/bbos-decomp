package net.rim.device.apps.internal.docview.gui;

final class ActiveDisplayedPart {
   int _attachmentMoreID;
   int _attachmentPartID;
   int _messageID;
   String _archiveIndicator;
   DocViewDisplayScreen _screen;
   boolean _active = true;

   ActiveDisplayedPart(DocViewDisplayScreen instance, int moreID, int partID, int messageID, String archiveIndicator) {
      this._screen = instance;
      this._messageID = messageID;
      this._attachmentMoreID = moreID;
      this._attachmentPartID = partID;
      this._archiveIndicator = archiveIndicator;
   }

   final boolean compare(int msgID, AttachmentElementInfo info) {
      return this.compare(msgID, info._morePartID, info.getElementPartID(), info.getElementArchiveIndicator());
   }

   final boolean compare(int msgID, int morePartID, int partIndex, String archiveIndicator) {
      return this._attachmentPartID != partIndex ? false : this.compare(msgID, morePartID, archiveIndicator);
   }

   final boolean compare(int msgID, int morePartID, String archiveIndicator) {
      if (this._messageID != msgID) {
         return false;
      } else if (this._attachmentMoreID != morePartID) {
         return false;
      } else if (this._archiveIndicator == null && archiveIndicator != null) {
         return false;
      } else {
         return this._archiveIndicator != null && archiveIndicator == null
            ? false
            : this._archiveIndicator == null || archiveIndicator == null || this._archiveIndicator.compareTo(archiveIndicator) == 0;
      }
   }
}
