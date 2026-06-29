package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.XYRect;

public final class DocViewImageDisplayField$ImageState {
   protected DocViewImageField _mainImageField;
   private DocViewImageField _enlargedImageField;
   private DocViewImageField _enlargeAllImageField;
   private boolean _enlargeRequestSent;
   private boolean _enlargeAllRequestSent;
   protected int _rotationValue;
   private DocViewImageDisplayField$EnlargeInfo _enlargeAreaInfo;
   private DocViewImageDisplayField$EnlargeInfo _enlargeAllAreaInfo;

   DocViewImageDisplayField$ImageState() {
   }

   final void discardCurrentEnlargeArea() {
      if (this._enlargeAreaInfo != null) {
         this._enlargeAreaInfo.releaseRefs();
         this._enlargeAreaInfo = null;
      }

      this._rotationValue = 0;
      this._enlargeRequestSent = false;
   }

   final DocViewImageField getActiveImageField() {
      if (this._mainImageField != null && this._mainImageField.getIndex() != -1) {
         return this._mainImageField;
      } else if (this._enlargedImageField != null && this._enlargedImageField.getIndex() != -1) {
         return this._enlargedImageField;
      } else {
         return this._enlargeAllImageField != null && this._enlargeAllImageField.getIndex() != -1 ? this._enlargeAllImageField : null;
      }
   }

   final boolean enlargeAreaDataArrived(int currentBlockIndex, XYRect enlargeArea, int totalBlockCount, byte[] ucsData) {
      if (this._enlargeAreaInfo == null) {
         this._enlargeAreaInfo = new DocViewImageDisplayField$EnlargeInfo(enlargeArea, totalBlockCount);
      }

      return this._enlargeAreaInfo.addData(currentBlockIndex, enlargeArea, ucsData);
   }

   final boolean enlargeAllAreaDataArrived(int currentBlockIndex, XYRect enlargeArea, int totalBlockCount, byte[] ucsData) {
      if (this._enlargeAllAreaInfo == null) {
         this._enlargeAllAreaInfo = new DocViewImageDisplayField$EnlargeInfo(enlargeArea, totalBlockCount);
      }

      return this._enlargeAllAreaInfo.addData(currentBlockIndex, enlargeArea, ucsData);
   }

   final DocViewParsingData getEnlargedImageData(XYRect enlargeArea) {
      return getImageData(this._enlargeAreaInfo, enlargeArea);
   }

   final DocViewParsingData getEnlargedAllImageData() {
      return getImageData(this._enlargeAllAreaInfo, this._enlargeAllAreaInfo._cropRect);
   }

   final boolean isEnlargeAllAreaRetrieved() {
      return this._enlargeAllAreaInfo != null && this._enlargeAllAreaInfo.isComplete(this._enlargeAllAreaInfo._cropRect);
   }

   private static final DocViewParsingData getImageData(DocViewImageDisplayField$EnlargeInfo enlargeObj, XYRect enlargeArea) {
      DocViewParsingData parsingData = null;
      if (enlargeObj != null) {
         DocViewParser imageParser = new DocViewParser(false);
         enlargeObj.parseImageData(enlargeArea, imageParser);
         if (imageParser.getLastParsingStatus() == 0) {
            parsingData = imageParser.getParsingData();
         }

         Object var4 = null;
      }

      return parsingData;
   }
}
