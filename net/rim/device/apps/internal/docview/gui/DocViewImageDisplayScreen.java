package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.UiInternal;

final class DocViewImageDisplayScreen extends DocViewDisplayScreen {
   DocViewImageDisplayScreen(IntHashtable paramsHash) {
      super(paramsHash);
      int flipImage = 0;

      label45:
      try {
         flipImage = paramsHash.get(10);
      } finally {
         break label45;
      }

      byte presentationValue = 0;

      label41:
      try {
         presentationValue = paramsHash.get(13);
      } finally {
         break label41;
      }

      super._displayField = new DocViewImageDisplayField(this, this, null, super._docData, super._startArbDomID, flipImage, presentationValue);
   }

   @Override
   public final void processMoreResponse(ServerResponse response, byte[] ucsData, int totalRetrievedBlocks) {
      super.processMoreResponse(response, ucsData, totalRetrievedBlocks);
      if ((response._docID._partIndex == 1002 || response._docID._partIndex == 1003)
         && (response._errorCode != 0 || response._crtBlockIndex == response._docID._totalBlocks - 1)) {
         String arbDomID = response._docID._arbDOMID;
         DocViewImageDisplayField imgFld = this.getImageField();
         if (super._clientRequest != null && super._clientRequest._commandCode != null && super._clientRequest._commandCode.compareTo("RENDER") == 0) {
            arbDomID = imgFld._currentItemDomID;
         }

         imgFld.resetEnlargeSentState(response._docID._partIndex, arbDomID);
      }
   }

   @Override
   public final void notifyMoreRequestFailed(LatestRequestInfo reqInfo) {
      if (reqInfo != null
         && reqInfo._messageID == super._messageID
         && reqInfo._attachmentMoreID == super._morePartID
         && reqInfo._applicationID == super._applicationID
         && ObjectUtilities.objEqual(reqInfo._archiveIndicatorString, super._archiveIndicator)
         && (reqInfo._attachmentPartID == 1002 || reqInfo._attachmentPartID == 1003)) {
         this.getImageField().resetEnlargeSentState(reqInfo._attachmentPartID, null);
      }

      super.notifyMoreRequestFailed(reqInfo);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      boolean performHide = false;
      char ch = UiInternal.map(keycode);
      if (InternalServices.isReducedFormFactor()) {
         performHide = ch == 'q' || ch == 'Q';
      } else {
         performHide = ch == 'u' || ch == 'U';
      }

      if (performHide) {
         if (super._titleField.getIndex() != -1) {
            this.setTitle((Field)((Object)null));
         } else {
            this.setTitle(super._titleField);
            this.displayRetrievedPercentage();
         }

         this.updateLayout();
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected final void gotMoreData(ServerResponse response, byte[] ucsData, int totalRetrievedBlocks) {
      if (response._docID._partIndex != 1002 && response._docID._partIndex != 1003) {
         super.gotMoreData(response, ucsData, totalRetrievedBlocks);
      } else if (response._areaToEnlarge != null) {
         DocViewImageDisplayField imageField = this.getImageField();
         String arbDomID = response._docID._arbDOMID;
         if (super._clientRequest != null && super._clientRequest._commandCode != null && super._clientRequest._commandCode.compareTo("RENDER") == 0) {
            arbDomID = imageField._currentItemDomID;
         }

         DocViewImageDisplayField$ImageState state = imageField.getStateWithDomID(arbDomID, false);
         if (state != null) {
            if (response._docID._partIndex != 1002) {
               if (response._docID._partIndex == 1003) {
                  state.enlargeAllAreaDataArrived(response._crtBlockIndex, response._areaToEnlarge, response._docID._totalBlocks, ucsData);
               }
            } else {
               if (state._rotationValue == 90 || state._rotationValue == 270) {
                  response._reqScreenWidth = DocViewGUIInternalConstants.SCREEN_HEIGHT;
                  response._reqScreenHeight = DocViewGUIInternalConstants.SCREEN_WIDTH;
               }

               state.enlargeAreaDataArrived(response._crtBlockIndex, response._areaToEnlarge, response._docID._totalBlocks, ucsData);
            }

            if (response._crtBlockIndex == response._docID._totalBlocks - 1) {
               switch (response._docID._partIndex) {
                  case 1001:
                     break;
                  case 1002:
                  default:
                     imageField.gotEnlargedArea(state, response._areaToEnlarge);
                     return;
                  case 1003:
                     imageField.gotEnlargedAllArea(state, response._areaToEnlarge);
                     return;
               }
            }
         }
      }
   }

   @Override
   public final boolean executeMore(MoreDescriptor request, boolean autoMore, boolean allowDuplicateRequest) {
      if (request != null) {
         if (super._clientRequest != null && super._clientRequest._commandCode != null && super._clientRequest._commandCode.compareTo("RENDER") == 0) {
            if (super._clientRequest._arbDOMID == null) {
               return false;
            }

            request._arbDomID = super._clientRequest._arbDOMID;
         }

         request._isImageRequest = true;
         if (request._chunkSize == -1) {
            request._chunkSize = 64000;
         }
      }

      return super.executeMore(request, autoMore, allowDuplicateRequest);
   }

   @Override
   public final Object parseCustomData(byte dataID, Object param) {
      if (dataID != 2) {
         return super.parseCustomData(dataID, param);
      }

      if (param instanceof DocViewImageDisplayField$EnlargeAllPersistentQuery) {
         DocViewImageDisplayField$EnlargeAllPersistentQuery query = (DocViewImageDisplayField$EnlargeAllPersistentQuery)param;
         String domIDPrefix = null;
         String queryDomID = query._arbDomID;
         if (super._clientRequest != null && super._clientRequest._commandCode != null && super._clientRequest._commandCode.compareTo("RENDER") == 0) {
            if (super._clientRequest._arbDOMID == null) {
               return null;
            }

            domIDPrefix = "EnlargeAllDomID";
            queryDomID = super._clientRequest._arbDOMID;
         } else {
            domIDPrefix = ((StringBuffer)(new Object()))
               .append(super._isEmbScreen ? ((StringBuffer)(new Object())).append(super._domID).append("/").toString() : "")
               .append("EnlargeAllDomID")
               .toString();
         }

         String stringRect = CommandHandler.getStringRect(query._cropRect);
         int blockCount = this.getBlockCount(
            ((StringBuffer)(new Object())).append(domIDPrefix).append("/").append(queryDomID).append("/").append(stringRect).toString()
         );
         if (blockCount <= 0 && query._firstImage) {
            blockCount = this.getBlockCount(((StringBuffer)(new Object())).append(domIDPrefix).append("/").append(stringRect).toString());
         }

         for (int j = 0; j < blockCount; j++) {
            byte[] data = this.getSavedUCSData(
               ((StringBuffer)(new Object())).append(domIDPrefix).append("/").append(queryDomID).append("/").append(stringRect).toString(), j
            );
            if (data == null && query._firstImage) {
               data = this.getSavedUCSData(((StringBuffer)(new Object())).append(domIDPrefix).append("/").append(stringRect).toString(), j);
            }

            if (data != null) {
               query._state.enlargeAllAreaDataArrived(j, query._cropRect, blockCount, data);
            }
         }
      }

      return null;
   }

   @Override
   protected final boolean hasAutoLoad() {
      return false;
   }

   private final DocViewImageDisplayField getImageField() {
      return (DocViewImageDisplayField)super._displayField;
   }
}
