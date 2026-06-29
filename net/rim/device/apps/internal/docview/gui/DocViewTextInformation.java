package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;

final class DocViewTextInformation {
   private final StringBuffer _strContents;
   private DocViewTrackChange[] _trackChanges;
   private DocViewHyperlinkInfo[] _links;
   private DocViewRichTextInfo[] _format;
   private boolean _keepTheLatestLink;

   DocViewTextInformation(int iInitialStringSize) {
      this._strContents = new StringBuffer(iInitialStringSize);
   }

   DocViewTextInformation(DocViewTextInformation copy) {
      this._strContents = new StringBuffer(copy._strContents.length());
      this._strContents.append(copy._strContents);
      if (copy._trackChanges != null) {
         this._trackChanges = new DocViewTrackChange[copy._trackChanges.length];

         for (int i = 0; i < copy._trackChanges.length; i++) {
            this._trackChanges[i] = copy._trackChanges[i].cloneObject();
         }
      }

      if (copy._links != null) {
         this._links = new DocViewHyperlinkInfo[copy._links.length];

         for (int i = 0; i < copy._links.length; i++) {
            this._links[i] = copy._links[i].cloneObject();
         }
      }

      if (copy._format != null) {
         this._format = new DocViewRichTextInfo[copy._format.length];

         for (int i = 0; i < copy._format.length; i++) {
            this._format[i] = copy._format[i].cloneObject();
         }
      }

      this._keepTheLatestLink = copy._keepTheLatestLink;
   }

   final void append(DocViewTextInformation appendInstance) {
      int currentTextLength = this._strContents.length();
      this._strContents.append(appendInstance._strContents);
      if (appendInstance._trackChanges != null) {
         if (this._trackChanges == null) {
            this._trackChanges = new DocViewTrackChange[0];
         }

         for (int i = 0; i < appendInstance._trackChanges.length; i++) {
            DocViewTrackChange tc = appendInstance._trackChanges[i];
            if (tc.valid()) {
               tc._trackChangeStartOffset += currentTextLength;
               tc._trackChangeEndOffset += currentTextLength;
               Arrays.add(this._trackChanges, appendInstance._trackChanges[i].cloneObject());
            }
         }
      }

      this.markLastTextHyperlinkElementEndOffset();
      int numberOfHyperlinks = this._links != null ? this._links.length : 0;
      if (appendInstance._links != null) {
         for (int i = 0; i < appendInstance._links.length; i++) {
            DocViewHyperlinkInfo hyperlink = appendInstance._links[i];
            if (hyperlink._linkStartOffset >= 0 && hyperlink._linkEndOffset >= 0 && hyperlink._linkType != -1) {
               hyperlink._linkStartOffset += currentTextLength;
               hyperlink._linkEndOffset += currentTextLength;
               if (this._links == null) {
                  this._links = new DocViewHyperlinkInfo[1];
                  this._links[0] = hyperlink;
               } else {
                  Arrays.add(this._links, hyperlink);
               }
            }
         }
      }

      if (appendInstance._format != null) {
         if (this._format == null) {
            this._format = new DocViewRichTextInfo[0];
         }

         for (int i = 0; i < appendInstance._format.length; i++) {
            DocViewRichTextInfo rtInfo = appendInstance._format[i];
            rtInfo._iStartOffset += currentTextLength;
            if (rtInfo._hyperlinkIndex >= 0) {
               rtInfo._hyperlinkIndex += numberOfHyperlinks;
            }

            Arrays.add(this._format, rtInfo);
         }
      }
   }

   final DocViewHyperlinkInfo[] getLinksContent() {
      return this._links;
   }

   final DocViewTrackChange[] getTrackChanges() {
      return this._trackChanges;
   }

   final DocViewRichTextInfo[] getFormatContent() {
      return this._format;
   }

   final StringBuffer getStringContents() {
      return this._strContents;
   }

   final void reset(boolean newDoc) {
      if (newDoc) {
         this._keepTheLatestLink = false;
      }

      this.clearParams(newDoc);
   }

   private final void clearParams(boolean newDoc) {
      DocViewHyperlinkInfo addLink = null;
      if (!newDoc && this._keepTheLatestLink && this._links != null && this._links.length > 0) {
         addLink = this._links[this._links.length - 1];
      }

      this._links = null;
      this._format = null;
      this._trackChanges = null;
      this._strContents.setLength(0);
      if (addLink != null && addLink._linkType == 0) {
         DocViewHyperlinkInfo newLink = addLink.cloneObject();
         newLink._linkStartOffset = 0;
         newLink._linkEndOffset = -1;
         this._links = new DocViewHyperlinkInfo[1];
         this._links[0] = newLink;
         DocViewHyperlinkInfo var4 = null;
      }
   }

   final void addTrackChange(String authorName, String dateTimeInfo) {
      this.removeLastTrackChangeIfInvalid();
      if (this._trackChanges == null) {
         this._trackChanges = new DocViewTrackChange[1];
         this._trackChanges[0] = new DocViewTrackChange(authorName, dateTimeInfo);
      } else {
         Arrays.add(this._trackChanges, new DocViewTrackChange(authorName, dateTimeInfo));
      }
   }

   final void removeLastTrackChangeIfInvalid() {
      if (this._trackChanges != null && this._trackChanges.length > 0) {
         DocViewTrackChange lastTrackChange = this._trackChanges[this._trackChanges.length - 1];
         if (!lastTrackChange.valid()) {
            Arrays.removeAt(this._trackChanges, this._trackChanges.length - 1);
         }
      }
   }

   private final boolean firstHyperlinkInvalid() {
      if (this._keepTheLatestLink && this._links != null && this._links.length == 1 && this._links[0]._linkEndOffset == -1) {
         this.removeLastHyperlink();
         this._keepTheLatestLink = false;
         return true;
      } else {
         return false;
      }
   }

   final void addTextLinksContent(StringBuffer strTarget) {
      if (strTarget.length() > 0) {
         DocViewEmbHyperlinkInfo extLink = new DocViewEmbHyperlinkInfo();
         extLink._linkType = 2;
         extLink._linkTargetString = strTarget.toString();
         extLink._linkStartOffset = this._strContents.length();
         if (this._links == null) {
            this._links = new DocViewHyperlinkInfo[1];
            this._links[0] = extLink;
            return;
         }

         Arrays.add(this._links, extLink);
      }
   }

   final void endCommandCodeParsing() {
      this.removeLastTrackChangeIfInvalid();
   }

   final void addTextLinksContent(int bookmarkID) {
      if (bookmarkID != -1) {
         DocViewTextHyperlinkInfo newDocHyperlink = new DocViewTextHyperlinkInfo();
         newDocHyperlink._iLinkTargetBookmark = bookmarkID;
         newDocHyperlink._linkStartOffset = this._strContents.length();
         if (!this.firstHyperlinkInvalid() && this._links != null && this._links.length > 0) {
            DocViewHyperlinkInfo hyperInfo = this._links[this._links.length - 1];
            if (hyperInfo.identicalAndSuccesive(newDocHyperlink, 0)) {
               if (newDocHyperlink._linkEndOffset != -1) {
                  hyperInfo._linkEndOffset = newDocHyperlink._linkEndOffset;
                  return;
               }

               hyperInfo._linkEndOffset = -1;
               return;
            }
         }

         if (this._links == null) {
            this._links = new DocViewHyperlinkInfo[1];
            this._links[0] = newDocHyperlink;
            return;
         }

         Arrays.add(this._links, newDocHyperlink);
      }
   }

   final void addProgrammaticLinksContent(DocViewHyperlinkInfo hyperlinkInfo, StringBuffer hyperlinkText, int deviceFontSize) {
      if (hyperlinkInfo != null) {
         this.firstHyperlinkInvalid();
         if (this._links != null && this._links.length > 0) {
            DocViewHyperlinkInfo lastHyperInfo = this._links[this._links.length - 1];
            boolean interruptedTextLink = this.markLastTextHyperlinkElementEndOffset();
            Arrays.add(this._links, hyperlinkInfo);
            if (interruptedTextLink) {
               DocViewHyperlinkInfo newInfo = null;
               if (lastHyperInfo._linkType == 0) {
                  DocViewTextHyperlinkInfo newTextInfo = new DocViewTextHyperlinkInfo();
                  DocViewTextHyperlinkInfo lastTextInfo = (DocViewTextHyperlinkInfo)lastHyperInfo;
                  newTextInfo._iLinkTargetBookmark = lastTextInfo._iLinkTargetBookmark;
                  newTextInfo._iLinkTargetChunkHint = lastTextInfo._iLinkTargetChunkHint;
                  newInfo = newTextInfo;
               } else if (lastHyperInfo._linkType == 2) {
                  newInfo = new DocViewEmbHyperlinkInfo();
                  ((DocViewEmbHyperlinkInfo)newInfo)._linkTargetString = ((DocViewEmbHyperlinkInfo)lastHyperInfo)._linkTargetString;
               }

               if (newInfo != null) {
                  newInfo._linkStartOffset = this._strContents.length() + hyperlinkText.length();
                  Arrays.add(this._links, newInfo);
               }
            }
         } else if (this._links == null) {
            this._links = new DocViewHyperlinkInfo[1];
            this._links[0] = hyperlinkInfo;
         } else {
            Arrays.add(this._links, hyperlinkInfo);
         }

         this.addStringContent(
            new DocViewRichTextInfo(
               hyperlinkInfo._linkStartOffset,
               hyperlinkInfo._linkEndOffset - hyperlinkInfo._linkStartOffset + 1,
               4,
               Graphics.isColor() ? 255 : -1,
               -1,
               deviceFontSize
            ),
            hyperlinkText,
            false,
            (byte)-1,
            false
         );
      }
   }

   final boolean markLastTextHyperlinkElementEndOffset() {
      if (this._links != null && this._links.length > 0) {
         DocViewHyperlinkInfo lastHyperlink = this._links[this._links.length - 1];
         if (lastHyperlink._linkEndOffset == -1 && (lastHyperlink._linkType == 0 || lastHyperlink._linkType == 2)) {
            int iEndOffsetValue = this._strContents.length() - 1;
            if (iEndOffsetValue >= lastHyperlink._linkStartOffset) {
               lastHyperlink._linkEndOffset = iEndOffsetValue;
               return true;
            }

            this.removeLastHyperlink();
         }
      }

      return false;
   }

   final void setLastTextHyperlinkElementChunkID(int chunkHint) {
      if (this._links != null && this._links.length > 0) {
         DocViewHyperlinkInfo lastHyperlink = this._links[this._links.length - 1];
         if (lastHyperlink._linkType == 0) {
            ((DocViewTextHyperlinkInfo)lastHyperlink)._iLinkTargetChunkHint = chunkHint;
         }
      }
   }

   final void endParsing() {
      if ((!this._keepTheLatestLink || this._links != null && this._links.length > 1) && this._links != null && this._links.length > 0) {
         DocViewHyperlinkInfo lastHyperlink = this._links[this._links.length - 1];
         if (lastHyperlink._linkType == 0 && lastHyperlink._linkEndOffset == -1) {
            int length = this._strContents.length();
            if (lastHyperlink._linkStartOffset != length) {
               lastHyperlink._linkEndOffset = length - 1;
            }

            DocViewHyperlinkInfo newLink = lastHyperlink.cloneObject();
            newLink._linkStartOffset = 0;
            newLink._linkEndOffset = -1;
            Arrays.add(this._links, newLink);
            this._keepTheLatestLink = true;
            return;
         }
      }

      this._keepTheLatestLink = false;
   }

   final void addStringContent(
      DocViewRichTextInfo formatInfo, StringBuffer text, boolean checkIfParsedTrackChange, byte trackChangeType, boolean cloneLastTrackChange
   ) {
      DocViewRichTextInfo fmtInfo = formatInfo;
      int iLength = text.length();
      boolean isTrackChange = false;
      if (checkIfParsedTrackChange) {
         if (this._trackChanges != null && this._trackChanges.length > 0) {
            DocViewTrackChange trackChange = this._trackChanges[this._trackChanges.length - 1];
            if (!trackChange.valid()) {
               isTrackChange = true;
               trackChange._trackChangeStartOffset = this._strContents.length();
               trackChange._trackChangeEndOffset = trackChange._trackChangeStartOffset + iLength - 1;
               trackChange._trackChangeType = trackChangeType;
               if (cloneLastTrackChange) {
                  Arrays.add(this._trackChanges, new DocViewTrackChange(trackChange._trackChangeAuthor, trackChange._trackChangeDateTime));
               }
            }
         }

         if (isTrackChange || trackChangeType == 1) {
            isTrackChange = true;
            int regionFont = formatInfo == null ? 0 : formatInfo._regionFont;
            int textForeColor = Graphics.isColor() ? 16711680 : -1;
            int textBgColor = formatInfo == null ? -1 : formatInfo._textBgColor;
            int startOffset = formatInfo == null ? this._strContents.length() : formatInfo._iStartOffset;
            int length = formatInfo == null ? iLength : formatInfo._iLength;
            int fontSize = formatInfo == null ? 0 : formatInfo._textSize;
            if (trackChangeType == 1) {
               regionFont |= 32;
            } else if (trackChangeType == 2) {
               regionFont &= -33;
               regionFont &= -9;
               regionFont |= 4;
            }

            fmtInfo = new DocViewRichTextInfo(startOffset, length, regionFont, textForeColor, textBgColor, fontSize);
         }
      }

      synchronized (this._strContents) {
         synchronized (text) {
            for (int i = 0; i < iLength; i++) {
               this._strContents.append(text.charAt(i));
            }
         }
      }

      if (fmtInfo != null) {
         this.addFormatContent(fmtInfo, isTrackChange);
      }
   }

   private final void addFormatContent(DocViewRichTextInfo formatInfo, boolean isTrackChange) {
      formatInfo._hyperlinkIndex = this.regionInLink(formatInfo._iStartOffset, formatInfo._iLength);
      if (!isTrackChange) {
         if (Graphics.isColor() && (formatInfo._regionFont & 4) != 0 && formatInfo._textForeColor == -1 && formatInfo._hyperlinkIndex != -1) {
            formatInfo._textForeColor = 255;
         }

         if (this._format != null && this._format.length > 0) {
            DocViewRichTextInfo lastFormatElement = this._format[this._format.length - 1];
            if (!this.regionIsTrackChange(lastFormatElement._iStartOffset) && lastFormatElement.identical(formatInfo)) {
               int iNextRegionStart = lastFormatElement._iStartOffset + lastFormatElement._iLength;
               int searchOffset = 0;
               boolean succesive = true;
               if (iNextRegionStart < formatInfo._iStartOffset) {
                  if ((formatInfo._regionFont & 4) == 0) {
                     while (iNextRegionStart < formatInfo._iStartOffset) {
                        if (this._strContents.charAt(iNextRegionStart) != '\n') {
                           succesive = false;
                           break;
                        }

                        searchOffset++;
                        iNextRegionStart++;
                     }
                  } else {
                     succesive = false;
                  }
               }

               if (succesive) {
                  lastFormatElement._iLength = lastFormatElement._iLength + formatInfo._iLength + searchOffset;
                  return;
               }
            }
         }
      }

      if (this._format == null) {
         this._format = new DocViewRichTextInfo[1];
         this._format[0] = formatInfo;
      } else {
         Arrays.add(this._format, formatInfo);
      }
   }

   private final int regionInLink(int startIndex, int regionLength) {
      if (this._links != null) {
         int linksSize = this._links.length;

         for (int i = 0; i < linksSize; i++) {
            DocViewHyperlinkInfo hyperlinkInfo = this._links[i];
            if (hyperlinkInfo._linkStartOffset >= startIndex + regionLength) {
               return -1;
            }

            if (startIndex >= hyperlinkInfo._linkStartOffset && (hyperlinkInfo._linkEndOffset == -1 || startIndex <= hyperlinkInfo._linkEndOffset)) {
               return i;
            }
         }
      }

      return -1;
   }

   private final boolean regionIsTrackChange(int startIndex) {
      if (this._trackChanges != null) {
         int tcSize = this._trackChanges.length;

         for (int i = 0; i < tcSize; i++) {
            DocViewTrackChange tcInfo = this._trackChanges[i];
            if (tcInfo._trackChangeStartOffset == startIndex) {
               return true;
            }

            if (tcInfo._trackChangeStartOffset > startIndex) {
               return false;
            }
         }
      }

      return false;
   }

   private final void removeLastHyperlink() {
      if (this._links != null) {
         int hyperCount = this._links.length;
         if (hyperCount > 0) {
            Arrays.removeAt(this._links, hyperCount - 1);
            if (this._format != null) {
               int formatSize = this._format.length;
               if (formatSize > 0) {
                  for (int i = 0; i < formatSize; i++) {
                     DocViewRichTextInfo formatElement = this._format[i];
                     if (formatElement._hyperlinkIndex == hyperCount - 1) {
                        formatElement._hyperlinkIndex = -1;
                     }
                  }
               }
            }
         }
      }
   }
}
