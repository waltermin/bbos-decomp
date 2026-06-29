package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.IntHashtable;

final class DocViewImageDisplayField$EnlargeInfo {
   private XYRect _cropRect;
   private IntHashtable _enlargeAreaData;
   private int _enlargeAreaBlockCount;

   DocViewImageDisplayField$EnlargeInfo(XYRect cropRect, int totalBlockCount) {
      if (cropRect != null && totalBlockCount > 0) {
         this._cropRect = cropRect;
         this._enlargeAreaBlockCount = totalBlockCount;
      } else {
         throw new IllegalArgumentException("Invalid args");
      }
   }

   final boolean addData(int currentBlockIndex, XYRect enlargeArea, byte[] ucsData) {
      if (enlargeArea == null || ucsData == null || currentBlockIndex < 0) {
         return false;
      }

      if (!this._cropRect.equals(enlargeArea)) {
         return false;
      }

      if (this._enlargeAreaData == null) {
         this._enlargeAreaData = new IntHashtable(1);
      }

      if (!this._enlargeAreaData.containsKey(currentBlockIndex)) {
         this._enlargeAreaData.put(currentBlockIndex, ucsData);
      }

      return true;
   }

   final void releaseRefs() {
      this._cropRect = null;
      this._enlargeAreaBlockCount = -1;
      if (this._enlargeAreaData != null) {
         this._enlargeAreaData.clear();
         this._enlargeAreaData = null;
      }
   }

   final void parseImageData(XYRect enlargeArea, DocViewParser imageParser) {
      if (this._cropRect.equals(enlargeArea)) {
         if (this._enlargeAreaData != null && this._enlargeAreaData.size() == this._enlargeAreaBlockCount) {
            for (int i = 0; i < this._enlargeAreaBlockCount; i++) {
               imageParser.parseDocument((byte[])this._enlargeAreaData.get(i), i == 0, i, false, i == this._enlargeAreaBlockCount - 1);
               if (imageParser.getLastParsingStatus() != 0) {
                  return;
               }
            }
         }
      }
   }

   final boolean isComplete(XYRect enlargeArea) {
      return !this._cropRect.equals(enlargeArea) ? false : this._enlargeAreaData != null && this._enlargeAreaData.size() == this._enlargeAreaBlockCount;
   }
}
