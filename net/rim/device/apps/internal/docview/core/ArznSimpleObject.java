package net.rim.device.apps.internal.docview.core;

final class ArznSimpleObject extends ArznObject {
   private static final byte FONTEXMASK_SIZE = 1;
   private static final byte FONTEXMASK_STYLE = 2;
   private static final byte FONTEXMASK_NAME = 4;
   public static final byte FONTMASK_ITALIC = 1;
   public static final byte FONTMASK_BOLD = 2;
   public static final byte FONTMASK_UNDERLINE = 4;
   public static final byte FONTMASK_STRIKETHROUGH = 1;

   @Override
   protected final int parseData(ArznParsingInfo parsingData) {
      int offset = 0;
      switch (super._commandCode) {
         case 1:
         case 11:
         case 56:
         case 57:
            boolean unicodeText = super._commandCode == 11 || super._commandCode == 57;
            boolean deletedTrackChangeText = super._commandCode == 56 || super._commandCode == 57;
            this.addText(parsingData, super._ucsParser.readString(unicodeText, super._commandSize), this.isPartOfTOC(), deletedTrackChangeText);
            return offset + super._commandSize;
         case 2:
            this.addBookmarkInfo(parsingData, super._ucsParser.readUnsignedShort());
            return offset + 2;
         case 3:
            byte fontStyle = super._ucsParser.readByte();
            offset++;
            int simpleFontFormat = 0;
            if ((fontStyle & 1) != 0) {
               simpleFontFormat |= 1;
            }

            if ((fontStyle & 2) != 0) {
               simpleFontFormat |= 2;
            }

            if ((fontStyle & 4) != 0) {
               simpleFontFormat |= 4;
            }

            parsingData.setCurrentFormattingInfo(0, simpleFontFormat);
            return offset;
         case 4:
         case 5:
            if (super._commandCode == 4) {
               int bookmarkID = super._ucsParser.readShort();
               offset += 2;
               this.addInternalHyperlink(parsingData, bookmarkID, this.isPartOfTOC());
               return offset;
            }

            this.markLastHyperlinkElementEndOffset(parsingData, this.isPartOfTOC());
            return offset;
         case 8:
            parsingData.addDOMIDHint(super._ucsParser.readString(false, super._commandSize).toString());
            return offset + super._commandSize;
         case 9:
            parsingData.addChunkHint(super._ucsParser.readUnsignedShort());
            return offset + 2;
         case 10:
         case 13:
            int color = super._ucsParser.readInt();
            offset += 4;
            if (super._commandCode == 10) {
               parsingData.setCurrentFormattingInfo(1, color);
            } else {
               parsingData.setCurrentFormattingInfo(2, color);
            }

            this.colorParsed(super._commandCode == 10, color);
            return offset;
         case 12:
         case 14:
            int index = super._ucsParser.readUnsignedByte();
            offset++;

            try {
               int indexColor = ArznColorTable._colorTable[index];
               if (super._commandCode == 12) {
                  parsingData.setCurrentFormattingInfo(1, indexColor);
               } else {
                  parsingData.setCurrentFormattingInfo(2, indexColor);
               }

               this.colorParsed(super._commandCode == 12, indexColor);
               return offset;
            } finally {
               ;
            }
         case 23:
            byte[] image = new byte[super._commandSize];
            super._ucsParser.readByteArray(image);
            offset += super._commandSize;
            this.addImageContents(image);
            byte[] var34 = null;
            return offset;
         case 51:
            int chunkHint = super._ucsParser.readUnsignedShort();
            offset += 2;
            parsingData.setLastHyperlinkElementChunkID(chunkHint, this.isPartOfTOC());
            return offset;
         case 53:
            byte flagByte = super._ucsParser.readByte();
            offset++;
            if ((flagByte & 1) != 0) {
               parsingData.setCurrentFormattingInfo(3, super._ucsParser.readUnsignedByte());
               offset++;
            }

            if ((flagByte & 2) != 0) {
               byte extendedStyle = super._ucsParser.readByte();
               offset++;
               int crtFontStyle = parsingData.getCurrentFormattingInfo(0);
               int newFontStyle = crtFontStyle;
               if ((extendedStyle & 1) != 0) {
                  if (newFontStyle != -1) {
                     newFontStyle |= 8;
                  } else {
                     newFontStyle = 8;
                  }
               } else if (newFontStyle != -1) {
                  newFontStyle &= -9;
               }

               if (newFontStyle != crtFontStyle) {
                  parsingData.setCurrentFormattingInfo(0, newFontStyle);
                  return offset;
               }
            }
            break;
         case 64:
            ArznDocumentHeader docHeader = new ArznDocumentHeader();
            super._ucsParser.readByteArray(docHeader._abyUCSVersion);
            offset += docHeader._abyUCSVersion.length;
            docHeader._byDocType = super._ucsParser.readByte();
            offset++;
            parsingData.addDocumentHeader(docHeader);
            ArznDocumentHeader var27 = null;
            return offset;
         case 65:
            parsingData.setPageCount(super._ucsParser.readUnsignedShort());
            return offset + 2;
         case 72:
            int shorts = super._commandSize / 2;
            if (shorts > 0) {
               short[] values = new short[shorts];

               for (int i = 0; i < shorts; i++) {
                  values[i] = super._ucsParser.readShort();
                  offset += 2;
               }

               parsingData.addFontSizes(values);
               Object var31 = null;
               return offset;
            }
            break;
         case 74:
         case 75:
            StringBuffer strTarget = super._ucsParser.readString(super._commandCode == 75, super._commandSize);
            offset += super._commandSize;
            this.addExternalHyperlink(parsingData, strTarget, this.isPartOfTOC());
            return offset;
         case 86:
            byte[] audio = new byte[super._commandSize];
            super._ucsParser.readByteArray(audio);
            offset += super._commandSize;
            this.addAudioContents(audio);
            Object var35 = null;
      }

      return offset;
   }
}
