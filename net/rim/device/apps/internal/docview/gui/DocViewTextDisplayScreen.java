package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;

final class DocViewTextDisplayScreen extends DocViewDisplayScreen {
   DocViewTextDisplayScreen(IntHashtable paramsHash) {
      super(paramsHash);
      boolean isPresentation = false;

      label63:
      try {
         isPresentation = (Byte)paramsHash.get(13) == 1;
      } finally {
         break label63;
      }

      boolean isSpecificBgDisplay = false;

      label59:
      try {
         isSpecificBgDisplay = (Boolean)paramsHash.get(14);
      } finally {
         break label59;
      }

      super._displayField = new DocViewTextDisplayField(
         this,
         this,
         null,
         (DocViewParser)super._docData,
         !super._isEmbScreen ? super._domID : null,
         super._nextDOMID,
         super._themeScreenBgColor,
         super._themeScreenForeColor,
         isPresentation,
         isSpecificBgDisplay
      );
      super._optionsVerb = new OptionsVerb(DocViewDisplayScreen._resources.getString(18), this);
   }

   @Override
   public final void releaseRefs() {
      DocViewTextDisplayField$CacheTextOffsetInfo offsetInfo = ((DocViewTextDisplayField)super._displayField).getCurrentTextOffset();
      if (offsetInfo != null && offsetInfo._blockIndex >= 0 && offsetInfo._charOffset >= 0) {
         this.persistTextOffsetInfo(offsetInfo._blockIndex, offsetInfo._charOffset);
      }

      super.releaseRefs();
   }

   @Override
   protected final void newDataParsed(int currentBlockIndex) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   @Override
   public final void notifyMoreRequestCompleted(int messageID, int morePartID, ServerResponse response, byte[] ucsData, int totalRetrievedBlocks) {
      if (response._docID._arbDOMID != null && response._docID._srcType == -1 && (response._docID._partIndex == 1002 || response._docID._partIndex == 1003)) {
         String embDomID = response._docID._domID;
         response._docID._srcType = 1;
         response._docID._domID = AttachmentViewerFactory.constructCustomDomIDStringEx(
            super._isEmbScreen ? super._domID : null, response._docID._arbDOMID, "RenderDomID"
         );
         super.notifyMoreRequestCompleted(messageID, morePartID, response, ucsData, totalRetrievedBlocks);
         response._docID._srcType = -1;
         response._docID._domID = embDomID;
      } else {
         super.notifyMoreRequestCompleted(messageID, morePartID, response, ucsData, totalRetrievedBlocks);
      }
   }

   @Override
   protected final void gotMoreData(ServerResponse response, byte[] ucsData, int totalRetrievedBlocks) {
      if (response._errorCode == 0 && (response._docID._partIndex == 1005 || response._docID._partIndex == 1006)) {
         if (response._docID._totalBlocks == totalRetrievedBlocks) {
            ((DocViewTextDisplayField)super._displayField)
               .processPageDisplay(response._docID._arbDOMID, response._arbDOMIDStartArray, totalRetrievedBlocks == 1 ? ucsData : null);
            return;
         }
      } else {
         super.gotMoreData(response, ucsData, totalRetrievedBlocks);
      }
   }

   @Override
   protected final void serverFindResponse(short errorCode, String pattern, int findIncomplete, boolean findCaseSensitive) {
      super.serverFindResponse(errorCode, pattern, findIncomplete, findCaseSensitive);
      if (errorCode == 0 || errorCode != 248) {
         ((DocViewTextDisplayField)super._displayField)._crtStringNotFoundInChunks = null;
      }

      if (errorCode == 0) {
         ((DocViewTextDisplayField)super._displayField).serverFindResponseSucceed(pattern, findIncomplete, findCaseSensitive);
      } else {
         ((DocViewTextDisplayField)super._displayField)._strFindString = "";
         super._application.invokeLater(new DocViewTextDisplayScreen$2(this, errorCode, pattern), 1500, false);
      }
   }

   @Override
   public final Object parseCustomData(byte dataID, Object param) {
      if (dataID != 0) {
         if (dataID == 3) {
            return this.getSavedUCSData(AttachmentViewerFactory.constructCustomDomIDString(super._isEmbScreen ? super._domID : null, "RPageDomID"), 0);
         }

         if (dataID != 4) {
            return super.parseCustomData(dataID, param);
         }

         byte[] data = this.getSavedUCSData(AttachmentViewerFactory.constructCustomDomIDString(super._isEmbScreen ? super._domID : null, "TxtOffset"), 0);
         if (data != null) {
            int blockIdx = AttachmentViewerFactory.readInt(data, 0);
            int txtOffset = AttachmentViewerFactory.readInt(data, 4);
            if (blockIdx >= 0 && txtOffset >= 0) {
               return new DocViewTextDisplayField$CacheTextOffsetInfo(blockIdx, txtOffset);
            }
         }
      } else if (!(param instanceof String)) {
         if (param instanceof byte[]) {
            DocViewParser parser = new DocViewParser(false);
            parser.parseDocument((byte[])param, true, 0, false, true);
            Object retValue = null;
            if (parser.getLastParsingStatus() == 0) {
               retValue = parser.getParsingData();
            }

            DocViewParser var9 = null;
            return retValue;
         }
      } else {
         String renderDomID = AttachmentViewerFactory.constructCustomDomIDStringEx(super._isEmbScreen ? super._domID : null, (String)param, "RenderDomID");
         int blockCount = this.getBlockCount(renderDomID);
         if (blockCount <= 0) {
            String[] atoms = new String[0];
            if (super._isEmbScreen) {
               Arrays.add(atoms, super._domID + "/");
            }

            Arrays.add(atoms, "/RenderDomID");
            Arrays.add(atoms, (String)param + ',');
            String[] matchDomIDs = this.getMatchingAvailableEmbeddedDomIDs(atoms);
            if (matchDomIDs != null) {
               for (int i = 0; i < matchDomIDs.length; i++) {
                  blockCount = this.getBlockCount(matchDomIDs[i]);
                  if (blockCount > 0) {
                     renderDomID = matchDomIDs[i];
                     break;
                  }
               }
            }

            String[] var14 = null;
            matchDomIDs = null;
         }

         if (blockCount > 0) {
            int i = 0;
            DocViewParser parser = new DocViewParser(false);

            for (i = 0; i < blockCount; i++) {
               byte[] data = this.getSavedUCSData(renderDomID, i);
               if (data == null) {
                  break;
               }

               parser.parseDocument(data, i == 0, i, false, i == blockCount - 1);
               if (parser.getLastParsingStatus() != 0) {
                  break;
               }
            }

            Object retValue = null;
            if (i == blockCount) {
               retValue = parser.getParsingData();
            }

            DocViewParser var19 = null;
            return retValue;
         }
      }

      return null;
   }

   private final boolean persistTextOffsetInfo(int blockIndex, int offset) {
      if (super._parentMessage != null && (!super._isEmbScreen || super._domID != null)) {
         byte[] data = new byte[8];
         if (data != null && AttachmentViewerFactory.writeInt(data, 0, blockIndex) && AttachmentViewerFactory.writeInt(data, 4, offset)) {
            DocViewAtomicID atomicID = new DocViewAtomicID();
            atomicID._totalBlocks = 1;
            atomicID._partIndex = 999;
            atomicID._srcType = 1;
            atomicID._domID = AttachmentViewerFactory.constructCustomDomIDString(super._isEmbScreen ? super._domID : null, "TxtOffset");
            short errCode = DocViewAttachmentPersist.getInstance()
               .addChunkData(super._messageID, super._morePartID, super._archiveIndicator, 0, (short)0, atomicID, null, data, true);
            DocViewAtomicID var7 = null;
            byte[] var6 = null;
            if (errCode == 0) {
               return true;
            }

            return false;
         }
      }

      return false;
   }
}
