package net.rim.device.apps.internal.docview.gui;

final class DocViewParserObj {
   private DocViewDisplayScreen _viewer;
   private DocViewParser _coreData;
   private int _retrievedBlocks;
   private int _currentBlockIndex;
   private int _themeScreenBgColor;
   private int _themeScreenForeColor;
   private int _messageID;
   private int _morePartID;
   private String _titleString;
   private Object _context;
   private int _totalBlocks;
   private int _applicationID;
   private boolean _displayImageAsSlideshow;
   private byte _presentationValue;
   private DocViewNotify _notifyObject;
   private DocViewParseNotify _notifyObj;

   DocViewParserObj(DocViewParseNotify notifyObj) {
      this._notifyObj = notifyObj;
      if (this._notifyObj == null) {
         this._notifyObj = new DocViewParserObj$DocViewParseNotifyImpl(this);
      }
   }

   final void init(
      DocViewParser coreData,
      int currentBlockIndex,
      Object context,
      int retrievedBlocks,
      int totalBlocks,
      int messageID,
      int morePartID,
      String titleString,
      int themeScreenBgColor,
      int themeScreenForeColor,
      int applicationID,
      DocViewNotify notifyObject,
      boolean displayImageAsSlideshow,
      byte presentationValue
   ) {
      this._coreData = coreData;
      this._retrievedBlocks = retrievedBlocks;
      this._currentBlockIndex = currentBlockIndex;
      this._themeScreenBgColor = themeScreenBgColor;
      this._themeScreenForeColor = themeScreenForeColor;
      this._messageID = messageID;
      this._morePartID = morePartID;
      this._titleString = titleString;
      this._context = context;
      this._totalBlocks = totalBlocks;
      this._notifyObject = notifyObject;
      this._applicationID = applicationID;
      this._displayImageAsSlideshow = displayImageAsSlideshow;
      this._presentationValue = presentationValue;
   }

   final void parseHasSucceeded(int blockIndex, boolean lastBlock) {
      if (this._viewer == null && (!this._displayImageAsSlideshow || this._coreData.getParsingData().getDocumentType() != 2)) {
         this._viewer = AttachmentViewerFactory.getDisplayScreen(
            this._coreData,
            this._titleString,
            this._context,
            this._currentBlockIndex,
            this._totalBlocks,
            this._morePartID,
            false,
            this._coreData.getParsingData().getDocumentType(),
            this._themeScreenBgColor,
            this._themeScreenForeColor,
            0,
            this._applicationID,
            this._presentationValue,
            false
         );
         if (this._viewer != null && this._viewer._displayField != null) {
            this._viewer._displayField.setNotifyObject(this._notifyObject);
         }
      }

      if (this._viewer != null) {
         this._viewer.moreDataProcessed(null, this._currentBlockIndex + blockIndex - 1, this._retrievedBlocks, true);
      }

      if (lastBlock) {
         if (this._viewer != null) {
            DocViewDisplayField fld = this._viewer._displayField;
            if (fld != null && fld.init()) {
               if (!(this._notifyObj instanceof DocViewParserObj$DocViewParseNotifyImpl)) {
                  fld.setDataProvider(null);
                  fld.setGUIProvider(null);
                  this._viewer.releaseRefs();
                  this._viewer = null;
               }

               this._notifyObj.parseSucceeded(fld);
               return;
            }
         } else if (this._displayImageAsSlideshow && this._coreData.getParsingData().getDocumentType() == 2) {
            DocViewSlideshowField fld = new DocViewSlideshowField(this._coreData, this._presentationValue == 1);
            fld.setNotifyObject(this._notifyObject);
            this._notifyObj.parseSucceeded(fld);
            return;
         }

         this.parseHasFailed((byte)-1);
      }
   }

   final void parseHasFailed(byte errorCode) {
      if (this._viewer != null) {
         this._viewer.releaseRefs();
         this._viewer = null;
      }

      label22:
      try {
         DocViewDisplayScreenInstance.getForwardScreenInstance(this._messageID, this._applicationID).onDocDisplayEnd();
      } finally {
         break label22;
      }

      this._notifyObj.parseFailed(errorCode);
   }
}
