package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.apps.internal.docview.core.ArznRichTextInfo;
import net.rim.device.apps.internal.docview.core.EmbeddedHint;

final class DocViewTextContentHandler {
   private final boolean _trackChangesOn;
   private boolean _hasTrackChanges;
   private final DocViewTextInformation _docInfo;
   private DocViewTextInformation _tocInfo;
   private IntIntHashtable _bookmarkMap;
   private BreakObj[] _breakSortedVector;
   private final StringBuffer _paraBuf = (StringBuffer)(new Object(1));
   private int _afterParagraphSpacing;
   private boolean _paraIncomplete;
   private boolean _paragraphDeletedByTrackChange;
   private String _currentPageDomID;
   private static final String INDENT_STRING = "   ";

   DocViewTextContentHandler(int fullDocBufferInitialSize, boolean trackChangesOn) {
      this._docInfo = new DocViewTextInformation(fullDocBufferInitialSize);
      this._trackChangesOn = trackChangesOn;
   }

   DocViewTextContentHandler(DocViewTextContentHandler copy, boolean trackChangesOn) {
      this._docInfo = new DocViewTextInformation(copy._docInfo);
      if (copy._tocInfo == null) {
         this._tocInfo = null;
      } else {
         this._tocInfo = new DocViewTextInformation(copy._tocInfo);
      }

      this._trackChangesOn = trackChangesOn;
      if (copy._bookmarkMap != null) {
         this._bookmarkMap = (IntIntHashtable)(new Object(copy._bookmarkMap.size()));
         IntEnumeration e = copy._bookmarkMap.keys();

         while (e.hasMoreElements()) {
            int key = e.nextElement();
            this._bookmarkMap.put(key, copy._bookmarkMap.get(key));
         }
      }

      this._currentPageDomID = copy._currentPageDomID;
      if (copy._breakSortedVector != null) {
         this._breakSortedVector = new BreakObj[copy._breakSortedVector.length];

         for (int i = 0; i < copy._breakSortedVector.length; i++) {
            this._breakSortedVector[i] = copy._breakSortedVector[i].cloneObject();
         }
      }

      this._hasTrackChanges = copy._hasTrackChanges;
      this._paraBuf.append(copy._paraBuf);
      this._afterParagraphSpacing = copy._afterParagraphSpacing;
      this._paraIncomplete = copy._paraIncomplete;
      this._paragraphDeletedByTrackChange = copy._paragraphDeletedByTrackChange;
   }

   final void append(DocViewTextContentHandler appendInstance) {
      if (this._trackChangesOn != appendInstance.getTrackChangesOn()) {
         throw new Object("Trying to append a handler with a different value for track changes parsing. Not supported. ");
      }

      if (this._hasTrackChanges && appendInstance.hasTrackChanges()) {
         this._hasTrackChanges = true;
      }

      int currentTextLength = this._docInfo.getStringContents().length();
      this._docInfo.append(appendInstance._docInfo);
      if (appendInstance._tocInfo != null) {
         if (this._tocInfo == null) {
            this._tocInfo = appendInstance._tocInfo;
         } else {
            this._tocInfo.append(appendInstance._tocInfo);
         }
      }

      if (appendInstance._bookmarkMap != null) {
         if (this._bookmarkMap == null) {
            this._bookmarkMap = (IntIntHashtable)(new Object(appendInstance._bookmarkMap.size()));
         }

         IntEnumeration e = appendInstance._bookmarkMap.keys();

         while (e.hasMoreElements()) {
            int key = e.nextElement();
            this._bookmarkMap.put(key, appendInstance._bookmarkMap.get(key) + currentTextLength);
         }
      }

      if (appendInstance._breakSortedVector != null) {
         for (int i = 0; i < appendInstance._breakSortedVector.length; i++) {
            BreakObj obj = appendInstance._breakSortedVector[i];
            obj._charOffset += currentTextLength;
            this.addBreakElement(obj);
         }
      }
   }

   final void reset(boolean newDoc) {
      this._docInfo.reset(newDoc);
      if (this._tocInfo != null) {
         this._tocInfo.reset(newDoc);
      }

      this._bookmarkMap = null;
      this._breakSortedVector = null;
      this._paraBuf.setLength(0);
      this._afterParagraphSpacing = 0;
      this._currentPageDomID = null;
      if (newDoc) {
         this._paraIncomplete = false;
         this._hasTrackChanges = false;
      }
   }

   final boolean hasTrackChanges() {
      return this._hasTrackChanges;
   }

   final boolean getTrackChangesOn() {
      return this._trackChangesOn;
   }

   final IntIntHashtable getBookmarkMap() {
      return this._bookmarkMap;
   }

   private final void addBreakElement(BreakObj obj) {
      if (obj != null) {
         if (this._breakSortedVector == null) {
            this._breakSortedVector = new BreakObj[0];
         }

         if (this._breakSortedVector.length > 0) {
            BreakObj lastElem = this._breakSortedVector[this._breakSortedVector.length - 1];
            if (lastElem._charOffset == obj._charOffset) {
               this._paraBuf.setLength(1);
               if (lastElem instanceof PageBreak && obj instanceof PageBreak) {
                  this._paraBuf.setCharAt(0, '\n');
               } else {
                  this._paraBuf.setCharAt(0, ' ');
               }

               this.addTextContent(true, null, this._paraBuf, false, (byte)-1, false);
               obj._charOffset++;
            }
         } else if (obj._charOffset == 0) {
            this._paraBuf.setLength(1);
            this._paraBuf.setCharAt(0, ' ');
            this.addTextContent(true, null, this._paraBuf, false, (byte)-1, false);
            obj._charOffset++;
         }

         Arrays.add(this._breakSortedVector, obj);
      }
   }

   final int getTextContentLength(boolean fullDocument) {
      if (fullDocument) {
         return this._docInfo.getStringContents().length();
      } else {
         return this._tocInfo != null ? this._tocInfo.getStringContents().length() : 0;
      }
   }

   final StringBuffer getTextContent(boolean fullDocument) {
      if (fullDocument) {
         return this._docInfo.getStringContents();
      } else {
         return this._tocInfo != null ? this._tocInfo.getStringContents() : null;
      }
   }

   final DocViewHyperlinkInfo[] getLinksContent(boolean bFullDoc) {
      if (bFullDoc) {
         return this._docInfo.getLinksContent();
      } else {
         return this._tocInfo != null ? this._tocInfo.getLinksContent() : null;
      }
   }

   final DocViewTrackChange[] getTrackChangesContent(boolean fullDocument) {
      if (fullDocument) {
         return this._docInfo.getTrackChanges();
      } else {
         return this._tocInfo != null ? this._tocInfo.getTrackChanges() : null;
      }
   }

   final DocViewRichTextInfo[] getFormatContent(boolean fullDocument) {
      if (fullDocument) {
         return this._docInfo.getFormatContent();
      } else {
         return this._tocInfo != null ? this._tocInfo.getFormatContent() : null;
      }
   }

   final void addBookmarkInfo(int bookmarkID) {
      if (this._bookmarkMap == null) {
         this._bookmarkMap = (IntIntHashtable)(new Object(1));
      }

      this._bookmarkMap.put(bookmarkID, this._docInfo.getStringContents().length());
   }

   final BreakObj[] getBreakVector() {
      return this._breakSortedVector;
   }

   final void beforePageParsing(String domID) {
      this._currentPageDomID = domID;
   }

   final void afterPageParsing(int pageIndex) {
      this.addBreakElement(new PageBreak(pageIndex, this._docInfo.getStringContents().length(), this._currentPageDomID));
      this._currentPageDomID = null;
   }

   final void addTrackChange(String authorName, String dateTimeInfo, boolean isPartOfTOC) {
      this._hasTrackChanges = true;
      if (this._trackChangesOn) {
         String processedDateTime = AttachmentViewerFactory.processDocInfoTimeString(dateTimeInfo);
         this._docInfo.addTrackChange(authorName, processedDateTime);
         if (isPartOfTOC) {
            this.createTOCInfo();
            this._tocInfo.addTrackChange(authorName, processedDateTime);
         }
      }
   }

   final void removeLastTrackChangeIfInvalid() {
      if (this._trackChangesOn) {
         this._docInfo.removeLastTrackChangeIfInvalid();
         if (this._tocInfo != null) {
            this._tocInfo.removeLastTrackChangeIfInvalid();
         }
      }
   }

   final void addText(boolean fullDocument, ArznRichTextInfo formatInfo, StringBuffer strText, int deviceFontSize, boolean deletedByTrackChange) {
      if (this._trackChangesOn || !deletedByTrackChange) {
         DocViewRichTextInfo fmtInfo = null;
         if (formatInfo != null) {
            int regionFont = AttachmentViewerFactory.convertFromParsedFontStyle(formatInfo._regionFont);
            int textForeColor = AttachmentViewerFactory.convertFromParsedForeColor(formatInfo._textForeColor);
            int textBgColor = AttachmentViewerFactory.convertFromParsedBgColor(formatInfo._textBgColor);
            if (!fullDocument) {
               this.createTOCInfo();
            }

            fmtInfo = new DocViewRichTextInfo(
               fullDocument ? this._docInfo.getStringContents().length() : this._tocInfo.getStringContents().length(),
               strText.length(),
               regionFont,
               textForeColor,
               textBgColor,
               deviceFontSize
            );
         }

         this.addTextContent(fullDocument, fmtInfo, strText, this._trackChangesOn, (byte)(deletedByTrackChange ? 1 : 2), false);
      }
   }

   private final void addTextContent(
      boolean fullDocument, DocViewRichTextInfo formatInfo, StringBuffer strText, boolean checkIfParsedTrackChange, byte trackChangeType, boolean cloneLastTC
   ) {
      if (fullDocument) {
         this._docInfo.addStringContent(formatInfo, strText, checkIfParsedTrackChange, trackChangeType, cloneLastTC);
      } else {
         this.createTOCInfo();
         this._tocInfo.addStringContent(formatInfo, strText, checkIfParsedTrackChange, trackChangeType, cloneLastTC);
      }
   }

   final void addEmbeddedObjectHint(EmbeddedHint embHint, String domID, int deviceFontSize) {
      if (embHint != null && domID != null && (embHint._type != 1 || embHint._name != null && embHint._name.length() > 0)) {
         String strName = "";
         DocViewEmbHyperlinkInfo newHyperlink = new DocViewEmbHyperlinkInfo();
         ResourceBundle bundle = ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView");
         int linkStart = this._docInfo.getStringContents().length();
         switch (embHint._type) {
            case -1:
               break;
            case 0:
            default:
               strName = ((StringBuffer)(new Object())).append('[').append(bundle.getString(45)).toString();
               if (embHint._index != -1) {
                  strName = ((StringBuffer)(new Object())).append(strName).append(':').append(String.valueOf(embHint._index + 1)).toString();
               }

               strName = ((StringBuffer)(new Object())).append(strName).append(']').toString();
               newHyperlink._linkType = 1;
               break;
            case 1:
               strName = ((StringBuffer)(new Object()))
                  .append('[')
                  .append(bundle.getString(37))
                  .append(':')
                  .append(embHint._name.toString())
                  .append(']')
                  .toString();
               newHyperlink._linkType = 3;
               break;
            case 2:
               String commentName = embHint._name;
               if ((commentName == null || commentName.length() == 0) && embHint._index != -1) {
                  commentName = String.valueOf(embHint._index + 1);
               }

               strName = ((StringBuffer)(new Object())).append('[').append(bundle.getString(61)).append(':').append(commentName).append(']').toString();
               newHyperlink._linkType = 4;
               embHint._name = commentName;
               break;
            case 3:
               strName = ((StringBuffer)(new Object())).append('[').append(bundle.getString(87)).toString();
               if (embHint._index != -1) {
                  strName = ((StringBuffer)(new Object())).append(strName).append(':').append(String.valueOf(embHint._index + 1)).toString();
               }

               strName = ((StringBuffer)(new Object())).append(strName).append(']').toString();
               newHyperlink._linkType = 5;
               break;
            case 4:
               strName = ((StringBuffer)(new Object())).append('[').append(bundle.getString(96)).toString();
               if (embHint._index != -1) {
                  strName = ((StringBuffer)(new Object())).append(strName).append(':').append(String.valueOf(embHint._index + 1)).toString();
               }

               strName = ((StringBuffer)(new Object())).append(strName).append(']').toString();
               newHyperlink._linkType = 6;
         }

         if (embHint._previewData != null || !embHint._complete) {
            this.addBreakElement(new EmbeddedBreak(strName, newHyperlink._linkType, domID, this._docInfo.getStringContents().length()));
            return;
         }

         newHyperlink._linkStartOffset = linkStart;
         newHyperlink._linkEndOffset = newHyperlink._linkStartOffset + strName.length() - 1;
         newHyperlink._linkTargetString = domID;
         this._docInfo.addProgrammaticLinksContent(newHyperlink, (StringBuffer)(new Object(strName)), deviceFontSize);
      }
   }

   final void addHyperlink(int bookmarkID, boolean isPartOfTOC) {
      this._docInfo.addTextLinksContent(bookmarkID);
      if (isPartOfTOC) {
         this.createTOCInfo();
         this._tocInfo.addTextLinksContent(bookmarkID);
      }
   }

   final void addHyperlink(StringBuffer strTarget, boolean isPartOfTOC) {
      this._docInfo.addTextLinksContent(strTarget);
      if (isPartOfTOC) {
         this._docInfo.addTextLinksContent(strTarget);
      }
   }

   final void markLastHyperlinkElementEndOffset(boolean isPartOfTOC) {
      this._docInfo.markLastTextHyperlinkElementEndOffset();
      if (isPartOfTOC && this._tocInfo != null) {
         this._tocInfo.markLastTextHyperlinkElementEndOffset();
      }
   }

   final void setLastHyperlinkElementChunkID(int chunkHint, boolean isPartOfTOC) {
      this._docInfo.setLastTextHyperlinkElementChunkID(chunkHint);
      if (isPartOfTOC && this._tocInfo != null) {
         this._tocInfo.setLastTextHyperlinkElementChunkID(chunkHint);
      }
   }

   final void endParsing() {
      this._docInfo.endParsing();
      if (this._tocInfo != null) {
         this._tocInfo.endParsing();
      }
   }

   final void beforeParagraphParsing(boolean deletedByTrackChange) {
      this._paragraphDeletedByTrackChange = deletedByTrackChange;
      this._afterParagraphSpacing = 0;
   }

   final void addParagraphInfo(byte paraType, int listLevel, int beforeParaSpacing, int afterParaSpacing, boolean isPartOfTOC) {
      this._afterParagraphSpacing = afterParaSpacing;
      if (!this._paraIncomplete && (this._trackChangesOn || !this._paragraphDeletedByTrackChange)) {
         boolean paraBufUsed = false;
         if (beforeParaSpacing > 0) {
            this._paraBuf.setLength(1);
            paraBufUsed = true;
            this._paraBuf.setCharAt(0, '\n');
         }

         switch (paraType) {
            case 0:
               break;
            case 1:
            default:
               if (listLevel < 0) {
                  break;
               }

               if (!paraBufUsed) {
                  this._paraBuf.setLength(0);
                  paraBufUsed = true;
               }

               for (int i = 0; i <= listLevel; i++) {
                  this._paraBuf.append("   ");
               }
               break;
            case 2:
               if (!paraBufUsed) {
                  this._paraBuf.setLength(0);
                  paraBufUsed = true;
               }

               this._paraBuf.append("   ");
         }

         if (paraBufUsed && this._paraBuf.length() > 0) {
            this.addTextContent(true, null, this._paraBuf, this._trackChangesOn, (byte)(this._paragraphDeletedByTrackChange ? 1 : 2), false);
            if (isPartOfTOC) {
               this.addTextContent(false, null, this._paraBuf, this._trackChangesOn, (byte)(this._paragraphDeletedByTrackChange ? 1 : 2), false);
            }
         }
      }
   }

   final void afterParagraphParsing(boolean incompleteParagraph, boolean isPartOfTOC, boolean isLastParagraph) {
      this._paraIncomplete = incompleteParagraph;
      if ((this._trackChangesOn || !this._paragraphDeletedByTrackChange) && !incompleteParagraph) {
         boolean paraBufUsed = false;
         if (this._afterParagraphSpacing > 0) {
            this._paraBuf.setLength(1);
            paraBufUsed = true;
            this._paraBuf.setCharAt(0, '\n');
         }

         if (!isLastParagraph) {
            if (!paraBufUsed) {
               this._paraBuf.setLength(1);
               paraBufUsed = true;
               this._paraBuf.setCharAt(0, '\n');
            } else {
               this._paraBuf.append('\n');
            }
         }

         if (paraBufUsed && this._paraBuf.length() > 0) {
            this.addTextContent(true, null, this._paraBuf, this._trackChangesOn, (byte)(this._paragraphDeletedByTrackChange ? 1 : 2), false);
            if (isPartOfTOC) {
               this.addTextContent(false, null, this._paraBuf, this._trackChangesOn, (byte)(this._paragraphDeletedByTrackChange ? 1 : 2), false);
            }
         }
      }
   }

   private final void createTOCInfo() {
      if (this._tocInfo == null) {
         this._tocInfo = new DocViewTextInformation(64);
      }
   }
}
