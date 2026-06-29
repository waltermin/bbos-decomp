package net.rim.device.apps.internal.docview.gui;

final class AttachmentElementInfo {
   private String _strElementName = "";
   private byte _byState = 0;
   private String _strDOMId;
   private String _archiveIndicator;
   private short _nLastErrorTag = 0;
   private int _nPartIndex = -2;
   boolean _bDynamicPart;
   int _attachmentIndex = -1;
   int _morePartID = -1;

   AttachmentElementInfo(String strName, int nPartID, byte byState, short nServerTag) {
      this(strName, nPartID);
      this._byState = byState;
      this._nPartIndex = nPartID;
      this._nLastErrorTag = nServerTag;
   }

   AttachmentElementInfo(String strName, int nPartID) {
      this(nPartID);
      this._strElementName = strName;
   }

   AttachmentElementInfo(String strName) {
      this._strElementName = strName;
   }

   AttachmentElementInfo(int nPartID) {
      this._nPartIndex = nPartID;
      this._bDynamicPart = nPartID != -2 && nPartID != -1 && nPartID != 999;
   }

   final void setElementState(byte byState) {
      this._byState = byState;
   }

   final void setElementPartID(int partID) {
      this._nPartIndex = partID;
   }

   final void setElementDOMId(String strDOMId) {
      this._strDOMId = strDOMId;
   }

   final void setElementArchiveIndicator(String archiveIndicator) {
      this._archiveIndicator = archiveIndicator;
   }

   @Override
   public final String toString() {
      return this._strElementName;
   }

   final byte getElementState() {
      return this._byState;
   }

   final String getElementDOMId() {
      return this._strDOMId;
   }

   final String getElementArchiveIndicator() {
      return this._archiveIndicator;
   }

   final int getElementPartID() {
      return this._nPartIndex;
   }

   final void setElementName(String strElementName) {
      this._strElementName = strElementName;
   }

   final short getLastServerResponse() {
      return this._nLastErrorTag;
   }

   final void setServerResponseTag(short nServerResponseTag) {
      this._nLastErrorTag = nServerResponseTag;
   }

   final boolean matchArchiveIndicator(String archiveString) {
      if (this._archiveIndicator == null) {
         return archiveString == null;
      } else {
         return archiveString == null ? false : this._archiveIndicator.compareTo(archiveString) == 0;
      }
   }
}
