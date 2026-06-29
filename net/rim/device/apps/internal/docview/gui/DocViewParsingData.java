package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.apps.internal.docview.core.ArznAudioData;
import net.rim.device.apps.internal.docview.core.ArznDocumentHeader;
import net.rim.device.apps.internal.docview.core.ArznImageData;
import net.rim.device.apps.internal.docview.core.ArznParseCallback;
import net.rim.device.apps.internal.docview.core.ArznRichTextInfo;
import net.rim.device.apps.internal.docview.core.ArznSheetData;
import net.rim.device.apps.internal.docview.core.EmbeddedHint;
import net.rim.vm.Array;

public final class DocViewParsingData implements ArznParseCallback {
   private DocViewTextContentHandler _docHandler;
   private ArznDocumentHeader _docHeader;
   private DocViewParsingData$DomIDHint[] _domIDHintVector = new DocViewParsingData$DomIDHint[0];
   private int _pageCount = -1;
   private DocViewSheetData[] _aDocSheets = new DocViewSheetData[0];
   private DocViewImageData[] _imageVector = new DocViewImageData[0];
   private DocViewAudioData[] _audioVector = new DocViewAudioData[0];
   private String[] _SummaryDOMIDArray = new Object[0];
   private IntVector _chunkHintArray = (IntVector)(new Object());
   private IntHashtable _docInfoHash;
   private IntIntHashtable _fontSizesHash;
   private int _defaultFontSize;
   private short[] _parsedFontSizes;
   private int _latestDOMIDHintIndex = -1;
   private int _iTextDataSize;
   private byte _stopFlag = 0;
   private int _chunkIndex;
   private boolean _isLastChunk;
   private boolean _pausable;
   private boolean _firstTimePausable;
   private boolean _trackChangesOn;
   protected static final int CHUNKTEXTDATA;
   private static final int DEFAULTSIZE_CPTD = Ui.convertSize(8, 3, 4194307);

   final void parsedSpreadsheetChunk() {
      if (this._pausable) {
         this.setStopFlag((byte)1);
      } else {
         this.notifyWaitingThreads();
      }
   }

   final void endParsing(byte parseStatus) {
      boolean parseSuccess = parseStatus == 0;
      if (parseSuccess && this._docHandler != null) {
         this._docHandler.endParsing();
      }

      for (int i = this._imageVector.length - 1; i >= 0; i--) {
         DocViewImageData imgData = this._imageVector[i];
         imgData.endParsing();
         String name = imgData.toString();
         if (name == null || name.length() == 0) {
            imgData.setName(
               ((StringBuffer)(new Object()))
                  .append(ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView").getString(44))
                  .append(' ')
                  .append(String.valueOf(i + 1))
                  .toString()
            );
         }
      }

      for (int i = this._audioVector.length - 1; i >= 0; i--) {
         this._audioVector[i].endParsing();
      }

      for (int i = this._aDocSheets.length - 1; i >= 0; i--) {
         this._aDocSheets[i].endParsing(parseSuccess);
      }

      this.notifyWaitingThreads();
   }

   final synchronized void waitForData() {
      try {
         this.wait();
      } finally {
         return;
      }
   }

   final synchronized void notifyWaitingThreads() {
      this.notifyAll();
   }

   public final int getTextFontSize(ArznRichTextInfo formatInfo, boolean useDefaultFontSize) {
      int fontSize = useDefaultFontSize ? this._defaultFontSize : 0;
      if (formatInfo != null && this._parsedFontSizes != null && formatInfo._textSizeIndex >= 0 && formatInfo._textSizeIndex < this._parsedFontSizes.length) {
         fontSize = this.convertFromParsedFontSize(this._parsedFontSizes[formatInfo._textSizeIndex]);
      }

      return fontSize;
   }

   final DocViewParser cloneAsText(DocViewTextContentHandler textHandler) {
      DocViewParser retValue = new DocViewParser(textHandler.getTrackChangesOn());
      DocViewParsingData parsingData = retValue.getParsingData();
      parsingData._docHeader = new ArznDocumentHeader();
      parsingData._docHeader._byDocType = 0;
      parsingData._docHeader._abyUCSVersion[0] = 1;
      parsingData._docHeader._abyUCSVersion[1] = 0;
      parsingData._docHandler = textHandler;
      parsingData._domIDHintVector = this._domIDHintVector;
      parsingData._aDocSheets = this._aDocSheets;
      parsingData._imageVector = this._imageVector;
      parsingData._audioVector = this._audioVector;
      parsingData._defaultFontSize = this._defaultFontSize;
      return retValue;
   }

   public final synchronized void setStopFlag(byte parserCmd) {
      if (parserCmd != this._stopFlag) {
         switch (parserCmd) {
            case -1:
               break;
            case 0:
            case 2:
            default:
               this._stopFlag = parserCmd;
               return;
            case 1:
               if (!this._firstTimePausable) {
                  this._stopFlag = parserCmd;
                  this._firstTimePausable = true;
               }
         }
      }
   }

   public final BreakObj[] getBreakVector() {
      return this._docHandler != null ? this._docHandler.getBreakVector() : null;
   }

   public final IntHashtable getDocInfoHash() {
      return this._docInfoHash;
   }

   public final IntIntHashtable getBookmarkMap() {
      return this._docHandler != null ? this._docHandler.getBookmarkMap() : null;
   }

   public final IntVector getChunkHintVector() {
      return this._chunkHintArray;
   }

   public final String[] getSummaryDOMIDVector() {
      return this._SummaryDOMIDArray;
   }

   public final DocViewAudioData[] getAudio() {
      return this._audioVector;
   }

   public final DocViewImageData[] getImages() {
      return this._imageVector;
   }

   public final DocViewSheetData[] getSpreadsheets() {
      return this._aDocSheets;
   }

   public final boolean hasTrackChanges() {
      return this._docHandler != null ? this._docHandler.hasTrackChanges() : false;
   }

   public final DocViewTrackChange[] getTrackChangesContent(boolean fullDocument) {
      return this._docHandler != null ? this._docHandler.getTrackChangesContent(fullDocument) : null;
   }

   public final DocViewRichTextInfo[] getFormatContent(boolean fullDocument) {
      return this._docHandler != null ? this._docHandler.getFormatContent(fullDocument) : null;
   }

   public final DocViewHyperlinkInfo[] getLinksContent(boolean fullDocument) {
      return this._docHandler != null ? this._docHandler.getLinksContent(fullDocument) : null;
   }

   public final StringBuffer getTextContent(boolean fullDocument) {
      return this._docHandler != null ? this._docHandler.getTextContent(fullDocument) : null;
   }

   public final int getEmbeddedObjectFlip(String domID) {
      try {
         return ((EmbeddedHint)this.getObjectWithDOMID(domID))._flipImage;
      } finally {
         ;
      }
   }

   public final String getEmbeddedObjectOwner(String domID) {
      try {
         return ((EmbeddedHint)this.getObjectWithDOMID(domID))._owner;
      } finally {
         ;
      }
   }

   public final Object getEmbeddedObjectPreview(String domID) {
      try {
         return ((EmbeddedHint)this.getObjectWithDOMID(domID))._previewData;
      } finally {
         ;
      }
   }

   public final boolean isEmbeddedObjectComplete(String domID) {
      try {
         return ((EmbeddedHint)this.getObjectWithDOMID(domID))._complete;
      } finally {
         ;
      }
   }

   public final String getEmbeddedObjectName(String domID) {
      try {
         return ((EmbeddedHint)this.getObjectWithDOMID(domID))._name;
      } finally {
         ;
      }
   }

   public final String getDomIDForAssocObject(Object assocObj) {
      if (assocObj != null) {
         for (int i = this._domIDHintVector.length - 1; i >= 0; i--) {
            DocViewParsingData$DomIDHint hint = this._domIDHintVector[i];
            if (ObjectUtilities.objEqual(hint._assocObj, assocObj)) {
               return hint._domIDString;
            }
         }
      }

      return null;
   }

   public final Object getObjectWithDOMID(String domID) {
      if (domID != null) {
         int size = this._domIDHintVector.length;

         for (int i = 0; i < size; i++) {
            DocViewParsingData$DomIDHint hint = this._domIDHintVector[i];
            if (hint._domIDString.compareTo(domID) == 0) {
               return hint._assocObj;
            }
         }
      }

      return null;
   }

   public final boolean isEmptyDoc() {
      switch (this._docHeader._byDocType) {
         case -1:
         case 3:
         case 4:
            break;
         case 0:
         default:
            return AttachmentViewerFactory.isTextDocumentEmpty(this._docHandler);
         case 1:
            if (this._aDocSheets.length > 0) {
               return false;
            }
            break;
         case 2:
            if (this._imageVector.length > 0) {
               return false;
            }
            break;
         case 5:
            if (this._audioVector.length > 0) {
               return false;
            }
      }

      return true;
   }

   final void resetForContinue() {
      if (this._docHandler != null) {
         this._docHandler.reset(false);
      }

      this._stopFlag = 0;
      DocViewParsingData$DomIDHint latestDomID = null;
      if (this._latestDOMIDHintIndex != -1) {
         latestDomID = this._domIDHintVector[this._latestDOMIDHintIndex];
      }

      int iIndex = this._domIDHintVector.length;

      while (--iIndex >= 0) {
         try {
            DocViewParsingData$DomIDHint hint = this._domIDHintVector[iIndex];
            if (hint._assocObj instanceof DocViewTextHint) {
               Arrays.removeAt(this._domIDHintVector, iIndex);
            }
         } finally {
            continue;
         }
      }

      if (latestDomID != null) {
         this._latestDOMIDHintIndex = -1;
         iIndex = this._domIDHintVector.length;

         while (--iIndex >= 0) {
            if (ObjectUtilities.objEqual(this._domIDHintVector[iIndex], latestDomID)) {
               this._latestDOMIDHintIndex = iIndex;
               break;
            }
         }
      }
   }

   final void reset(boolean newDoc, int chunkIndex, boolean pausable, boolean isLastChunk) {
      if (this._docHandler != null) {
         this._docHandler.reset(newDoc);
      }

      this._chunkIndex = chunkIndex;
      this._isLastChunk = isLastChunk;
      this._pausable = pausable;
      this._stopFlag = 0;
      if (newDoc) {
         Array.resize(this._aDocSheets, 0);
         Array.resize(this._imageVector, 0);
         Array.resize(this._SummaryDOMIDArray, 0);
         this._chunkHintArray.removeAllElements();
         Array.resize(this._domIDHintVector, 0);
         this._docInfoHash = null;
         this._docHandler = null;
      }

      this._docHeader = null;
      this._pageCount = -1;
      Array.resize(this._audioVector, 0);
      if (!newDoc) {
         int iIndex = this._domIDHintVector.length;

         while (--iIndex >= 0) {
            label99:
            try {
               DocViewParsingData$DomIDHint hint = this._domIDHintVector[iIndex];
               if (hint._assocObj instanceof DocViewTextHint) {
                  Arrays.removeAt(this._domIDHintVector, iIndex);
               }
            } finally {
               break label99;
            }

            try {
               DocViewParsingData$DomIDHint hint = this._domIDHintVector[iIndex];
               if (hint._assocObj instanceof DocViewAudioData) {
                  Arrays.removeAt(this._domIDHintVector, iIndex);
               }
            } finally {
               continue;
            }
         }

         for (int i = this._imageVector.length - 1; i >= 0; i--) {
            this._imageVector[i].setID(chunkIndex);
         }
      }

      this._iTextDataSize = 0;
      this._firstTimePausable = false;
      this._fontSizesHash = null;
      this._parsedFontSizes = null;
      this._defaultFontSize = DEFAULTSIZE_CPTD;
      this._latestDOMIDHintIndex = -1;
   }

   public final boolean getTrackChangesOnStatus() {
      return this._trackChangesOn;
   }

   final void setTrackChangesOnStatus(boolean trackChangesOn) {
      this._trackChangesOn = trackChangesOn;
   }

   public final byte getDocumentType() {
      return this._docHeader != null ? this._docHeader._byDocType : -1;
   }

   public final int getPageCount() {
      return this._pageCount;
   }

   public final int getDefaultFontSize() {
      return this._defaultFontSize;
   }

   @Override
   public final void afterParagraphParsing(boolean incompleteParagraph, boolean isPartOfTOC, boolean isLastCommand) {
      if (this._docHandler != null) {
         this._docHandler.afterParagraphParsing(incompleteParagraph, isPartOfTOC, this._isLastChunk && isLastCommand);
      }
   }

   @Override
   public final void addParagraphInfo(byte paraType, int listLevel, int beforeParaSpacing, int afterParaSpacing, boolean isPartOfTOC) {
      if (this._docHandler != null) {
         this._docHandler.addParagraphInfo(paraType, listLevel, beforeParaSpacing, afterParaSpacing, isPartOfTOC);
      }
   }

   @Override
   public final void beforeParagraphParsing(boolean deletedByTrackChange) {
      if (this._docHandler != null) {
         if (this._latestDOMIDHintIndex != -1) {
            this.setLatestDOMIDHint(new DocViewTextHint(this._docHandler.getTextContentLength(true), this._docHandler.getTextContentLength(false)));
         }

         this._docHandler.beforeParagraphParsing(deletedByTrackChange);
      }
   }

   @Override
   public final ArznAudioData addAudio() {
      ArznAudioData audioData = null;
      if (this._latestDOMIDHintIndex != -1) {
         label39:
         try {
            audioData = (ArznAudioData)this._domIDHintVector[this._latestDOMIDHintIndex]._assocObj;
         } finally {
            break label39;
         }
      }

      if (audioData == null) {
         audioData = new DocViewAudioData();
         ((DocViewObjectData)audioData).setID(this._chunkIndex);
         if (this._docHeader._byDocType == 5 && this._audioVector.length > 0) {
            if (this._pausable) {
               this.setStopFlag((byte)1);
            } else {
               this.notifyWaitingThreads();
            }
         }

         Arrays.add(this._audioVector, audioData);
         this.setLatestDOMIDHint(audioData);
      }

      this._latestDOMIDHintIndex = -1;
      return audioData;
   }

   @Override
   public final ArznImageData addImage() {
      ArznImageData imageData = null;
      if (this._latestDOMIDHintIndex != -1) {
         label42:
         try {
            imageData = (ArznImageData)this._domIDHintVector[this._latestDOMIDHintIndex]._assocObj;
         } finally {
            break label42;
         }
      }

      if (imageData == null) {
         imageData = new DocViewImageData();
         ((DocViewObjectData)imageData).setID(this._chunkIndex);
         if (this._docHeader._byDocType == 2 && this._imageVector.length > 0) {
            if (this._pausable) {
               if (this._imageVector.length % 2 == 0) {
                  this.setStopFlag((byte)1);
               }
            } else {
               this.notifyWaitingThreads();
            }
         }

         Arrays.add(this._imageVector, imageData);
         this.setLatestDOMIDHint(imageData);
      }

      this._latestDOMIDHintIndex = -1;
      return imageData;
   }

   @Override
   public final ArznSheetData addSpreadsheet(int nRows, int nCols, String strSheetName, boolean isComplete) {
      ArznSheetData sheetData = null;
      if (this._latestDOMIDHintIndex != -1) {
         label63:
         try {
            sheetData = (ArznSheetData)this._domIDHintVector[this._latestDOMIDHintIndex]._assocObj;
         } finally {
            break label63;
         }
      }

      if (sheetData == null) {
         sheetData = new DocViewSheetData(nRows, nCols, strSheetName, this._docHeader._byDocType == 1, this, !this._pausable);
         if (this._docHeader._byDocType == 1 && this._aDocSheets.length > 0) {
            if (this._pausable) {
               this.setStopFlag((byte)1);
            } else {
               this.notifyWaitingThreads();
            }
         }

         Arrays.add(this._aDocSheets, sheetData);
         this.setLatestDOMIDHint(sheetData);
      }

      this._latestDOMIDHintIndex = -1;
      if (sheetData != null) {
         ((DocViewSheetData)sheetData).setID(this._chunkIndex);
         if (isComplete) {
            ((DocViewSheetData)sheetData).setSpreadsheetComplete(this._chunkIndex);
         }
      }

      return sheetData;
   }

   @Override
   public final void parsedEmbeddedObjectHint(ArznSheetData sheetData, EmbeddedHint hint) {
      if (hint != null && sheetData instanceof DocViewSheetData) {
         this.parsedEmbeddedObjectHintImpl(
            hint,
            new DocViewTextContentHandler[]{
               ((DocViewSheetData)sheetData).getCurrentCellFormatterExtern(true, false),
               ((DocViewSheetData)sheetData).getCurrentCellFormatterExtern(false, true)
            }
         );
         ((DocViewSheetData)sheetData)._hasEmbeddedObjects = true;
      }
   }

   @Override
   public final void parsedEmbeddedObjectHint(EmbeddedHint hint) {
      if (hint != null) {
         this.parsedEmbeddedObjectHintImpl(hint, new DocViewTextContentHandler[]{this._docHandler});
      }
   }

   @Override
   public final EmbeddedHint addEmbeddedObjectHint(byte type) {
      EmbeddedHint embHint = null;
      if (this._latestDOMIDHintIndex != -1) {
         label26:
         try {
            embHint = (EmbeddedHint)this._domIDHintVector[this._latestDOMIDHintIndex]._assocObj;
         } finally {
            break label26;
         }
      }

      if (embHint == null) {
         embHint = new EmbeddedHint(type);
         this.setLatestDOMIDHint(embHint);
      }

      this._latestDOMIDHintIndex = -1;
      return embHint;
   }

   @Override
   public final void addFontSizes(short[] parseFontSizes) {
      if (parseFontSizes != null && parseFontSizes.length > 0) {
         int[] fontSizes = new int[parseFontSizes.length + 1];
         IntIntHashtable addedHash = (IntIntHashtable)(new Object(fontSizes.length));

         for (int i = 0; i < fontSizes.length; i++) {
            int twips = i < parseFontSizes.length ? Math.abs(parseFontSizes[i]) : Math.abs(-240);
            if (!addedHash.containsKey(twips)) {
               fontSizes[addedHash.size()] = Ui.convertSize(7200 * twips / 1440, 4194306, 4194307);
               addedHash.put(twips, 1);
            }
         }

         Array.resize(fontSizes, addedHash.size());
         addedHash = null;
         Arrays.sort(fontSizes, 0, fontSizes.length);
         this._fontSizesHash = AttachmentViewerFactory.processFontSizes(fontSizes);
         int[] var6 = null;
         this._defaultFontSize = this.convertFromParsedFontSize(Math.abs(-240));
         this._parsedFontSizes = parseFontSizes;
      }
   }

   @Override
   public final void setLastHyperlinkElementChunkID(int chunkHint, boolean isPartOfTOC) {
      if (this._docHandler != null) {
         this._docHandler.setLastHyperlinkElementChunkID(chunkHint, isPartOfTOC);
      }
   }

   @Override
   public final void markLastHyperlinkElementEndOffset(boolean isPartOfTOC) {
      if (this._docHandler != null) {
         this._docHandler.markLastHyperlinkElementEndOffset(isPartOfTOC);
      }
   }

   @Override
   public final void afterPageParsing(int pageIndex) {
      if (this._docHandler != null) {
         if (this._latestDOMIDHintIndex != -1) {
            this.setLatestDOMIDHint(new DocViewTextHint(this._docHandler.getTextContentLength(true), this._docHandler.getTextContentLength(false)));
         }

         this._docHandler.afterPageParsing(pageIndex);
      }
   }

   @Override
   public final void beforePageParsing() {
      if (this._docHandler != null && this._latestDOMIDHintIndex != -1) {
         this._docHandler.beforePageParsing(this._domIDHintVector[this._latestDOMIDHintIndex]._domIDString);
      }
   }

   @Override
   public final void addDocInfoValue(int docInfoID, String value) {
      if (this._docInfoHash == null) {
         this._docInfoHash = (IntHashtable)(new Object());
      }

      switch (docInfoID) {
         case 40:
            this._docInfoHash
               .put(
                  docInfoID,
                  ((StringBuffer)(new Object()))
                     .append(value)
                     .append(' ')
                     .append(ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView").getString(49))
                     .toString()
               );
            return;
         case 45:
         case 46:
            this._docInfoHash.put(docInfoID, AttachmentViewerFactory.processDocInfoTimeString(value));
            return;
         case 78:
            this._docInfoHash
               .put(
                  docInfoID,
                  ((StringBuffer)(new Object()))
                     .append(value)
                     .append(' ')
                     .append(ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView").getString(99))
                     .toString()
               );
            return;
         default:
            this._docInfoHash.put(docInfoID, value);
      }
   }

   @Override
   public final void addSummaryDOMID(String strDOMID) {
      Arrays.add(this._SummaryDOMIDArray, strDOMID);
   }

   @Override
   public final void endCommandCodeParsing(byte atomicIdentifier) {
      if (atomicIdentifier != 0) {
      }

      if (this._docHandler != null && atomicIdentifier != 1) {
         this._docHandler.removeLastTrackChangeIfInvalid();
      }
   }

   @Override
   public final void addDOMIDHint(String domIDHint) {
      int size = this._domIDHintVector.length;

      for (int i = size - 1; i >= 0; i--) {
         String domID = this._domIDHintVector[i]._domIDString;
         if (domIDHint.compareTo(domID) == 0) {
            this._latestDOMIDHintIndex = i;
            return;
         }
      }

      DocViewParsingData$DomIDHint hint = new DocViewParsingData$DomIDHint(null);
      hint._domIDString = domIDHint;
      Arrays.add(this._domIDHintVector, hint);
      this._latestDOMIDHintIndex = size;
   }

   @Override
   public final void addTrackChange(String authorName, String dateTimeInfo, boolean isPartOfTOC) {
      if (this._docHandler != null) {
         this._docHandler.addTrackChange(authorName, dateTimeInfo, isPartOfTOC);
      }
   }

   @Override
   public final void addChunkHint(int chunkHint) {
      this._chunkHintArray.addElement(chunkHint);
   }

   @Override
   public final byte getStopFlag() {
      if (this._stopFlag == 1) {
         this.notifyWaitingThreads();
      }

      return this._stopFlag;
   }

   @Override
   public final void setPageCount(int pageCount) {
      if (pageCount >= 0) {
         this._pageCount = pageCount;
      }
   }

   @Override
   public final void addDocumentHeader(ArznDocumentHeader docHeader) {
      this._docHeader = docHeader;
      if (this._docHandler == null && docHeader._byDocType == 0) {
         this._docHandler = new DocViewTextContentHandler(2000, this._trackChangesOn);
      }
   }

   @Override
   public final void addBookmarkInfo(int bookmarkID) {
      if (this._docHandler != null) {
         this._docHandler.addBookmarkInfo(bookmarkID);
      }
   }

   @Override
   public final void addExternalHyperlink(StringBuffer strTarget, boolean isPartOfTOC) {
      if (this._docHandler != null) {
         this._docHandler.addHyperlink(strTarget, isPartOfTOC);
      }
   }

   @Override
   public final void addInternalHyperlink(int bookmarkID, boolean isPartOfTOC) {
      if (this._docHandler != null) {
         this._docHandler.addHyperlink(bookmarkID, isPartOfTOC);
      }
   }

   @Override
   public final void addTextContent(ArznRichTextInfo formatInfo, StringBuffer strText, boolean deletedByTrackChange, boolean isPartOfTOC) {
      if (this._docHandler != null) {
         int iLength = strText.length();
         if (iLength > 0) {
            int deviceFontSize = this.getTextFontSize(formatInfo, true);
            this._docHandler.addText(true, formatInfo, strText, deviceFontSize, deletedByTrackChange);
            if (isPartOfTOC) {
               this._docHandler.addText(false, formatInfo, strText, deviceFontSize, deletedByTrackChange);
            }

            this._iTextDataSize += iLength;
            if (this._iTextDataSize >= 750) {
               this._iTextDataSize = 0;
               if (this._pausable) {
                  this.setStopFlag((byte)1);
                  return;
               }

               this.notifyWaitingThreads();
            }
         }
      }
   }

   private final void setLatestDOMIDHint(Object hint) {
      if (this._latestDOMIDHintIndex != -1) {
         label21:
         try {
            DocViewParsingData$DomIDHint lastHint = this._domIDHintVector[this._latestDOMIDHintIndex];
            lastHint._assocObj = hint;
         } finally {
            break label21;
         }

         this._latestDOMIDHintIndex = -1;
      }
   }

   private final int convertFromParsedFontSize(int parsedFontSize) {
      int size = Ui.convertSize(Math.abs(parsedFontSize) * 7200 / 1440, 4194306, 4194307);
      return this._fontSizesHash.containsKey(size) ? this._fontSizesHash.get(size) : DEFAULTSIZE_CPTD;
   }

   private final void parsedEmbeddedObjectHintImpl(EmbeddedHint hint, DocViewTextContentHandler[] textHandler) {
      if (hint != null) {
         if (!hint._parsedOnce) {
            for (int i = this._domIDHintVector.length - 1; i >= 0; i--) {
               DocViewParsingData$DomIDHint crtHint = this._domIDHintVector[i];
               if (ObjectUtilities.objEqual(hint, crtHint._assocObj)) {
                  if (textHandler != null && textHandler.length > 0) {
                     for (int j = 0; j < textHandler.length; j++) {
                        if (textHandler[j] != null) {
                           textHandler[j].addEmbeddedObjectHint(hint, crtHint._domIDString, this._defaultFontSize);
                        }
                     }
                  }

                  hint._parsedOnce = true;
                  return;
               }
            }
         } else if (hint._complete && hint._previewData instanceof DocViewSheetData && !((DocViewSheetData)hint._previewData).isSpreadsheetComplete()) {
            ((DocViewSheetData)hint._previewData).setSpreadsheetComplete(this._chunkIndex);
         }
      }
   }

   DocViewParsingData(boolean trackChangesOn) {
      this._trackChangesOn = trackChangesOn;
   }
}
