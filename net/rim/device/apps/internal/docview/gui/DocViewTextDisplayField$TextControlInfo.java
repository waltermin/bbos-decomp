package net.rim.device.apps.internal.docview.gui;

import java.util.Vector;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.vm.Array;

final class DocViewTextDisplayField$TextControlInfo {
   private DocViewTextDisplayField$TextControlInfo$TextScreenField _richTextField;
   int _beginBlockIndex;
   int _endBlockIndex;
   private Vector _blocksInfo;
   private int _initialInsertIndex;
   final boolean _ctrlFullDocumentState;
   private IntHashtable _cookies;
   private int[] _ctrlOffsets;
   private byte[] _ctrlAttributes;
   private int[] _foreColors;
   private int[] _bgColors;
   private int[] _appendOffsets;
   private byte[] _appendAttributes;
   private Font[] _appendFonts;
   private IntIntHashtable _fontSizes;
   private IntIntHashtable _fontStyles;
   private IntIntHashtable _attrHashArray;
   private int[] _chunkLinksOffsets;
   private int[] _chunkTrackChangesOffsets;
   private DocViewTrackChange[][][] _chunkTrackChanges;
   private DocViewHyperlinkInfo[][][] _chunkLinks;
   boolean _linksDirty;
   IntIntHashtable _bookmarkHash;
   private final Runnable _linksRefreshRunnable;
   private final DocViewTextDisplayField this$0;
   private static final byte ATTR_MAXREGIONS;

   DocViewTextDisplayField$TextControlInfo(DocViewTextDisplayField _1, int startBlockIndex, int initialInsertIndex, boolean fullDocumentState) {
      this.this$0 = _1;
      this._blocksInfo = (Vector)(new Object());
      this._initialInsertIndex = -1;
      this._ctrlOffsets = new int[0];
      this._ctrlAttributes = new byte[0];
      this._foreColors = new int[0];
      this._bgColors = new int[0];
      this._fontSizes = (IntIntHashtable)(new Object(32));
      this._fontStyles = (IntIntHashtable)(new Object(32));
      this._attrHashArray = (IntIntHashtable)(new Object(64));
      this._linksDirty = true;
      this._linksRefreshRunnable = new DocViewTextDisplayField$TextControlInfo$1(this);
      if (initialInsertIndex < 0) {
         throw new Object("Invalid field insert index.");
      }

      if (startBlockIndex < 0) {
         throw new Object("Invalid start block index.");
      }

      this._beginBlockIndex = this._endBlockIndex = startBlockIndex;
      this._initialInsertIndex = initialInsertIndex;
      this._ctrlFullDocumentState = fullDocumentState;
   }

   private final int getControlIndex() {
      return this._ctrlFullDocumentState ? Arrays.getIndex(this.this$0._FullDocFieldVector, this) : Arrays.getIndex(this.this$0._TOCFieldVector, this);
   }

   private final Manager getDocViewActiveManager() {
      try {
         return this.this$0.getActiveManager(this._ctrlFullDocumentState);
      } finally {
         ;
      }
   }

   private final void executeActionOnTextControl(DocViewTextDisplayField$CallbackAction callback, Runnable[] updates) {
      if (callback != null) {
         callback.executeCallback(updates);
      }
   }

   private final void appendFormattedText(String strText, Runnable[] updates) {
      Object[] cookiesArray = null;
      if (this._cookies != null && this._ctrlAttributes != null) {
         cookiesArray = new Object[this._ctrlAttributes.length];
         IntEnumeration e = this._cookies.keys();

         while (e.hasMoreElements()) {
            int nextKey = e.nextElement();
            cookiesArray[nextKey] = this._cookies.get(nextKey);
         }
      }

      this.executeActionOnTextControl(new DocViewTextDisplayField$TextControlInfo$AppendTextCallbackAction(this, cookiesArray, strText), updates);
   }

   final DocViewTextDisplayField$CacheTextOffsetInfo getTextPosInfo(int position) {
      try {
         int cursorPosition = position;
         int blocksSize = this._blocksInfo.size();

         for (int i = 0; i < blocksSize; i++) {
            DocViewTextDisplayField$TextControlInfo$BlockTextInfo info = (DocViewTextDisplayField$TextControlInfo$BlockTextInfo)this._blocksInfo.elementAt(i);
            if (cursorPosition < info._charCount) {
               return new DocViewTextDisplayField$CacheTextOffsetInfo(info._blockIndex, info._startCharIndex + cursorPosition);
            }

            cursorPosition -= info._charCount;
         }
      } finally {
         return null;
      }

      return null;
   }

   final int getAbsOffset(DocViewTextDisplayField$CacheTextOffsetInfo info) {
      try {
         int globalPosition = 0;
         int blocksSize = this._blocksInfo.size();

         for (int i = 0; i < blocksSize; i++) {
            DocViewTextDisplayField$TextControlInfo$BlockTextInfo blkInfo = (DocViewTextDisplayField$TextControlInfo$BlockTextInfo)this._blocksInfo
               .elementAt(i);
            if (blkInfo._blockIndex == info._blockIndex
               && info._charOffset >= blkInfo._startCharIndex
               && info._charOffset < blkInfo._startCharIndex + blkInfo._charCount) {
               return globalPosition + info._charOffset - blkInfo._startCharIndex;
            }

            globalPosition += blkInfo._charCount;
         }
      } finally {
         return -1;
      }

      return -1;
   }

   final DocViewHyperlinkInfo getHyperlink(int offset) {
      if (this._richTextField != null && this._chunkLinks != null && offset < this._richTextField.getTextLength()) {
         int chunksWithLinks = this._chunkLinks.length;

         for (int index = 0; index < chunksWithLinks; index++) {
            for (DocViewHyperlinkInfo crtHyperlink : this._chunkLinks[index]) {
               if (offset < crtHyperlink._linkStartOffset - this._chunkLinksOffsets[index]) {
                  break;
               }

               if (crtHyperlink._linkStartOffset - this._chunkLinksOffsets[index] <= offset
                  && offset <= crtHyperlink._linkEndOffset - this._chunkLinksOffsets[index]) {
                  return crtHyperlink;
               }
            }
         }
      }

      return null;
   }

   private final int adjustFontStyle(int style) {
      if (!this.this$0._useOriginalFont && this.this$0._crtFontStyle != 0) {
         int underlineStyle = style & 44;
         return this.this$0._crtFontStyle | underlineStyle;
      } else {
         return style;
      }
   }

   final synchronized void notifyChangeFonts() {
      if (this._richTextField != null) {
         this._richTextField.notifyChangeFonts();
      }
   }

   private final int getAndAddDefaultRegion(
      IntIntHashtable hashCodeTable, Vector fontVector, IntVector foreColorVector, IntVector bgColorVector, int nextRegionIndex
   ) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   private final void addRegionAttrIndex(
      int hashCode,
      int fontStyle,
      int fontSize,
      int fgColor,
      int bgColor,
      Vector fontVector,
      IntVector foreColorVector,
      IntVector bgColorVector,
      IntIntHashtable tempHashCode,
      int regionFontIndex
   ) {
      fontVector.addElement(this.this$0._fontFactory.getFont(this.adjustFontStyle(fontStyle), fontSize));
      foreColorVector.addElement(fgColor);
      bgColorVector.addElement(bgColor);
      tempHashCode.put(hashCode, regionFontIndex);
   }

   private final int getRegionAttrIndex(int hashCode, IntIntHashtable tempHashCode) {
      int regionFontIndex = this._attrHashArray.get(hashCode);
      if (regionFontIndex == -1) {
         regionFontIndex = tempHashCode.get(hashCode);
      }

      return regionFontIndex;
   }

   private final int adjustArrays(DocViewRichTextInfo[] formatInfo, int formattingOffset, String strText) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   private final boolean setUnderlineFont(boolean solidUnderline, int attributeIndex) {
      if (solidUnderline) {
         int regionHashCode = this.this$0
            .getHashCode(
               this._fontStyles.get(this._ctrlAttributes[attributeIndex]) & -9,
               this._fontSizes.get(this._ctrlAttributes[attributeIndex]),
               this._foreColors[this._ctrlAttributes[attributeIndex]],
               this._bgColors[this._ctrlAttributes[attributeIndex]]
            );
         if (this._attrHashArray.containsKey(regionHashCode)) {
            this._ctrlAttributes[attributeIndex] = (byte)this._attrHashArray.get(regionHashCode);
            return true;
         }
      } else {
         int regionHashCode = this.this$0
            .getHashCode(
               this._fontStyles.get(this._ctrlAttributes[attributeIndex]) | 8,
               this._fontSizes.get(this._ctrlAttributes[attributeIndex]),
               this._foreColors[this._ctrlAttributes[attributeIndex]],
               this._bgColors[this._ctrlAttributes[attributeIndex]]
            );
         if (this._attrHashArray.containsKey(regionHashCode)) {
            this._ctrlAttributes[attributeIndex] = (byte)this._attrHashArray.get(regionHashCode);
            return true;
         }
      }

      return false;
   }

   private final boolean processLinks(boolean createCookies) {
      boolean retValue = false;
      if (this._linksDirty) {
         this._linksDirty = false;
         if (this._chunkLinks != null) {
            int chunksWithLinks = this._chunkLinks.length;
            int currentStartOffset = 0;
            int[] offsets = null;
            if (this._richTextField != null) {
               offsets = this._richTextField.getControlOffsets();
            }

            for (int index = 0; index < chunksWithLinks; index++) {
               if (!createCookies || index >= chunksWithLinks - 1) {
                  DocViewHyperlinkInfo[] hyperlinks = this._chunkLinks[index];
                  int hyperSize = hyperlinks.length;
                  int offsetSize = this._ctrlOffsets.length;

                  for (int i = 0; i < hyperSize; i++) {
                     DocViewHyperlinkInfo crtHyperlink = hyperlinks[i];
                     if (crtHyperlink._linkStartOffset - this._chunkLinksOffsets[index] > this._ctrlOffsets[offsetSize - 1]) {
                        break;
                     }

                     if (crtHyperlink._linkEndOffset - this._chunkLinksOffsets[index] >= this._ctrlOffsets[0]) {
                        boolean valid = this.this$0._bookmarkInfoMap != null
                              && crtHyperlink._linkType == 0
                              && this.this$0._bookmarkInfoMap.containsKey(((DocViewTextHyperlinkInfo)crtHyperlink)._iLinkTargetBookmark)
                           || this.this$0.isRetrievedHyperlink(crtHyperlink);
                        int hyperlinkLength = Math.min(crtHyperlink._linkEndOffset - crtHyperlink._linkStartOffset + 1, this._ctrlOffsets[offsetSize - 1]);
                        int crtHyperStartIdx = Math.max(crtHyperlink._linkStartOffset - this._chunkLinksOffsets[index], 0);

                        for (int j = currentStartOffset; j < offsetSize - 1; j++) {
                           if (this._ctrlOffsets[j] == crtHyperStartIdx) {
                              int k;
                              for (k = j; hyperlinkLength > 0 && k < offsetSize - 1 && this._ctrlOffsets[k] + hyperlinkLength >= this._ctrlOffsets[k + 1]; k++) {
                                 if (this.setUnderlineFont(valid, k)) {
                                    if (this._richTextField != null
                                       && (this._appendAttributes == null || k < this._ctrlAttributes.length - this._appendAttributes.length)) {
                                       int newIndex = Arrays.binarySearch(offsets, this._ctrlOffsets[k], k, offsets.length - 1);
                                       if (newIndex >= 0) {
                                          label178:
                                          try {
                                             this._richTextField.setAttribute(newIndex, this._ctrlAttributes[k]);
                                          } finally {
                                             break label178;
                                          }
                                       }
                                    }

                                    retValue = true;
                                 }

                                 hyperlinkLength -= this._ctrlOffsets[k + 1] - this._ctrlOffsets[k];
                                 if (createCookies) {
                                    if (this._cookies == null) {
                                       this._cookies = (IntHashtable)(new Object(32));
                                    }

                                    if (!this._cookies.containsKey(k)) {
                                       this._cookies.put(k, crtHyperlink);
                                    } else {
                                       Object crtInfo = this._cookies.get(k);
                                       if (crtInfo instanceof DocViewTrackChange) {
                                          DocViewComplexRegionInfo newInfo = new DocViewComplexRegionInfo();
                                          newInfo._hyperInfo = crtHyperlink;
                                          newInfo._trackChange = (DocViewTrackChange)crtInfo;
                                          this._cookies.put(k, newInfo);
                                       }
                                    }
                                 }
                              }

                              currentStartOffset = k;
                              break;
                           }

                           if (this._ctrlOffsets[j] > crtHyperlink._linkStartOffset - this._chunkLinksOffsets[index]) {
                              currentStartOffset = j;
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return retValue;
   }

   private final void processTrackChanges() {
      if (this._chunkTrackChanges != null && this._chunkTrackChanges.length > 0) {
         int currentStartOffset = 0;
         DocViewTrackChange[] trackChanges = this._chunkTrackChanges[this._chunkTrackChanges.length - 1];
         int trackChangesSize = trackChanges.length;
         int offsetSize = this._ctrlOffsets.length;
         int trackChangesOffset = this._chunkTrackChangesOffsets[this._chunkTrackChangesOffsets.length - 1];

         for (int i = 0; i < trackChangesSize; i++) {
            DocViewTrackChange crtTrackChange = trackChanges[i];
            if (crtTrackChange._trackChangeStartOffset - trackChangesOffset > this._ctrlOffsets[offsetSize - 1]) {
               return;
            }

            if (crtTrackChange._trackChangeEndOffset - trackChangesOffset >= this._ctrlOffsets[0]) {
               int crtTrackChangeStartIdx = Math.max(crtTrackChange._trackChangeStartOffset - trackChangesOffset, 0);

               for (int j = currentStartOffset; j < offsetSize - 1; j++) {
                  if (this._ctrlOffsets[j] == crtTrackChangeStartIdx) {
                     if (this._cookies == null) {
                        this._cookies = (IntHashtable)(new Object(32));
                     }

                     if (!this._cookies.containsKey(j)) {
                        this._cookies.put(j, crtTrackChange);
                     } else {
                        Object crtInfo = this._cookies.get(j);
                        if (crtInfo instanceof DocViewHyperlinkInfo) {
                           DocViewComplexRegionInfo newInfo = new DocViewComplexRegionInfo();
                           newInfo._hyperInfo = (DocViewHyperlinkInfo)crtInfo;
                           newInfo._trackChange = crtTrackChange;
                           this._cookies.put(j, newInfo);
                        }
                     }

                     currentStartOffset = j;
                     break;
                  }

                  if (this._ctrlOffsets[j] > crtTrackChange._trackChangeStartOffset - trackChangesOffset) {
                     currentStartOffset = j;
                     break;
                  }
               }
            }
         }
      }
   }

   final void doProcessLinks() {
      if (this._richTextField != null && this._linksDirty && this._chunkLinks != null) {
         this.this$0._application.invokeLater(this._linksRefreshRunnable);
      }
   }

   final int calculateCorrectArrays(
      DocViewRichTextInfo[] aFormattingInfo,
      DocViewHyperlinkInfo[] links,
      DocViewTrackChange[] trackChanges,
      IntIntHashtable bookmarkMap,
      String strText,
      int formattingOffset,
      Runnable[] updates,
      int currentBlockIndex
   ) {
      int nStringLength = strText.length();
      if (nStringLength == 0) {
         return 0;
      }

      if (this._richTextField != null && this._richTextField.getTextLength() > 0) {
         this._foreColors = this._richTextField.getControlForegroundColors();
         this._bgColors = this._richTextField.getControlBackgroundColors();
      }

      int nRemainingLength = this.adjustArrays(aFormattingInfo, formattingOffset, strText);
      int nNewStringLength = nStringLength - nRemainingLength;
      if (nNewStringLength > 0) {
         int nCrtTextLength = this._richTextField != null ? this._richTextField.getTextLength() : 0;
         int crtOffsetsLength = this._ctrlOffsets.length;
         if (crtOffsetsLength > 0) {
            Array.resize(this._ctrlOffsets, crtOffsetsLength + this._appendOffsets.length - 1);
            System.arraycopy(this._appendOffsets, 1, this._ctrlOffsets, crtOffsetsLength, this._appendOffsets.length - 1);
            if (nCrtTextLength > 0) {
               for (int i = crtOffsetsLength; i < this._ctrlOffsets.length; i++) {
                  this._ctrlOffsets[i] = this._ctrlOffsets[i] + nCrtTextLength;
               }
            }
         } else {
            Array.resize(this._ctrlOffsets, this._appendOffsets.length);
            System.arraycopy(this._appendOffsets, 0, this._ctrlOffsets, 0, this._appendOffsets.length);
         }

         int crtAttrLength = this._ctrlAttributes.length;
         Array.resize(this._ctrlAttributes, crtAttrLength + this._appendAttributes.length);
         System.arraycopy(this._appendAttributes, 0, this._ctrlAttributes, crtAttrLength, this._appendAttributes.length);
         IntVector newBookmarks = null;
         if (this._ctrlFullDocumentState && bookmarkMap != null && bookmarkMap.size() > 0) {
            IntEnumeration bookEnum = bookmarkMap.keys();
            boolean addedFirstBookmark = false;

            while (bookEnum.hasMoreElements()) {
               int bookmarkID = bookEnum.nextElement();
               int bookmarkTarget = bookmarkMap.get(bookmarkID);
               if (bookmarkTarget >= formattingOffset && bookmarkTarget <= nNewStringLength + formattingOffset) {
                  if (this._bookmarkHash == null) {
                     this._bookmarkHash = (IntIntHashtable)(new Object());
                  }

                  if (!this._bookmarkHash.containsKey(bookmarkID)) {
                     if (!addedFirstBookmark) {
                        if (this._ctrlFullDocumentState) {
                           this.this$0.markControlsDirty(true);
                           this.this$0.markControlsDirty(false);
                        }

                        addedFirstBookmark = true;
                     }

                     this._bookmarkHash.put(bookmarkID, bookmarkTarget - formattingOffset + nCrtTextLength);
                     if (newBookmarks == null) {
                        newBookmarks = (IntVector)(new Object());
                     }

                     newBookmarks.addElement(bookmarkID);
                  }
               }
            }
         }

         boolean forceLinksRecalculation = this._chunkLinks != null && this._chunkLinks.length > 0;
         if (newBookmarks == null) {
            if (this._ctrlFullDocumentState) {
               forceLinksRecalculation = false;
            }
         } else {
            for (int i = newBookmarks.size() - 1; i >= 0; i--) {
               this.this$0._bookmarkInfoMap.put(newBookmarks.elementAt(i), this);
            }

            newBookmarks = null;
         }

         int linkCount = links != null ? links.length : 0;
         if (linkCount > 0) {
            boolean adjustedInfo = false;

            for (int i = 0; i < linkCount; i++) {
               DocViewHyperlinkInfo hyperInfo = links[i];
               if (hyperInfo._linkStartOffset > formattingOffset + nNewStringLength) {
                  break;
               }

               if (hyperInfo._linkEndOffset >= formattingOffset) {
                  if (!adjustedInfo) {
                     if (this._chunkLinksOffsets == null) {
                        this._chunkLinksOffsets = new int[0];
                     }

                     Arrays.add(this._chunkLinksOffsets, formattingOffset - nCrtTextLength);
                     if (this._chunkLinks == null) {
                        this._chunkLinks = new DocViewHyperlinkInfo[0][][];
                     }

                     Arrays.add(this._chunkLinks, new DocViewHyperlinkInfo[0]);
                     adjustedInfo = true;
                  }

                  Arrays.add(this._chunkLinks[this._chunkLinks.length - 1], hyperInfo);
               }
            }
         }

         int trackChangesCount = trackChanges != null ? trackChanges.length : 0;
         if (trackChangesCount > 0) {
            boolean adjustedInfo = false;

            for (int i = 0; i < trackChangesCount; i++) {
               DocViewTrackChange trackChangeInfo = trackChanges[i];
               if (trackChangeInfo._trackChangeStartOffset > formattingOffset + nNewStringLength) {
                  break;
               }

               if (trackChangeInfo._trackChangeEndOffset >= formattingOffset) {
                  if (!adjustedInfo) {
                     if (this._chunkTrackChangesOffsets == null) {
                        this._chunkTrackChangesOffsets = new int[0];
                     }

                     Arrays.add(this._chunkTrackChangesOffsets, formattingOffset - nCrtTextLength);
                     if (this._chunkTrackChanges == null) {
                        this._chunkTrackChanges = new DocViewTrackChange[0][][];
                     }

                     Arrays.add(this._chunkTrackChanges, new DocViewTrackChange[0]);
                     adjustedInfo = true;
                  }

                  Arrays.add(this._chunkTrackChanges[this._chunkTrackChanges.length - 1], trackChangeInfo);
               }
            }
         }

         this._linksDirty = true;
         if (this.processLinks(true)) {
            System.arraycopy(this._ctrlAttributes, crtAttrLength, this._appendAttributes, 0, this._appendAttributes.length);
         }

         this.processTrackChanges();
         if (forceLinksRecalculation) {
            this._linksDirty = true;
         }

         String textToAdd = nRemainingLength == 0 ? strText : strText.substring(0, nNewStringLength);
         this.appendFormattedText(textToAdd, updates);
         boolean addInfo = true;
         if (this._blocksInfo.size() > 0) {
            DocViewTextDisplayField$TextControlInfo$BlockTextInfo lastElement = (DocViewTextDisplayField$TextControlInfo$BlockTextInfo)this._blocksInfo
               .lastElement();
            if (lastElement._blockIndex == currentBlockIndex) {
               lastElement._charCount = lastElement._charCount + textToAdd.length();
               addInfo = false;
            }
         }

         if (addInfo) {
            this._blocksInfo
               .addElement(new DocViewTextDisplayField$TextControlInfo$BlockTextInfo(this, currentBlockIndex, formattingOffset, textToAdd.length()));
         }
      }

      return nRemainingLength;
   }
}
