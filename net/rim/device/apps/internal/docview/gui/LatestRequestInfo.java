package net.rim.device.apps.internal.docview.gui;

final class LatestRequestInfo {
   int _attachmentMoreID;
   String _archiveIndicatorString;
   int _attachmentPartID;
   boolean _messageAlreadyFailed;
   int _applicationID;
   String _embeddedDomID;
   int _messageID;

   LatestRequestInfo(int messageID, int attMoreID, String archiveIndicatorString, int attPartID, boolean messageAlreadyFailed, int applicationID) {
      this._messageID = messageID;
      this._attachmentMoreID = attMoreID;
      this._archiveIndicatorString = archiveIndicatorString;
      this._attachmentPartID = attPartID;
      this._messageAlreadyFailed = messageAlreadyFailed;
      this._applicationID = applicationID;
   }
}
