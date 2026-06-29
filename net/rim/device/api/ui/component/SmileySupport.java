package net.rim.device.api.ui.component;

import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.EmoticonStringPattern;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.internal.ui.FormatParams;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.AttributedString$Iterator;

class SmileySupport {
   private TextField _field;
   private EmoticonStringPattern _smileyFacility;
   private StringPattern$Match _match = new StringPattern$Match();
   private static int _size;

   SmileySupport(TextField field) {
      this._field = field;
   }

   void setPattern(EmoticonStringPattern facility) {
      this._smileyFacility = facility;
      _size = this._smileyFacility.emoticonSize();
   }

   EmoticonStringPattern getSmileyFacility() {
      return this._smileyFacility;
   }

   void showSymbolScreen() {
      if (this._smileyFacility != null) {
         SmileySymbolScreen.show(this._smileyFacility, this._field);
      } else {
         SymbolScreen.show(this._field);
      }
   }

   boolean isSmileyAvailable() {
      if (this._smileyFacility == null) {
         return false;
      }

      AttributedString$Iterator iter = this._field._text.getIterator();

      while (!(iter.runPicture() instanceof SmileySupport$SmileyPicture)) {
         if (!iter.next()) {
            return false;
         }
      }

      return true;
   }

   String getDecodedString(int start, int end) {
      return start == end ? "" : new String(this.getDecodedText(start, end));
   }

   StringBufferGap getDecodedTextAbstractString(int start, int end) {
      StringBuffer sb = new StringBuffer();
      sb.append(this.getDecodedText(start, end));
      StringBufferGap sbg = new StringBufferGap();
      sbg.insert(sb);
      return sbg;
   }

   void scanForSmileys(FormatParams aParams) {
      int labelLength = this._field.getLabelLength();
      int start = 0;

      for (int i = aParams._changedTextStart - 1; i > labelLength; i--) {
         if (CharacterUtilities.isSpaceChar(this._field._text.charAt(i))) {
            start = i;
            break;
         }
      }

      int diff = this.smileyScan(start, aParams._changedTextStart + aParams._newLength);
      if (diff != 0) {
         int lenDiff = aParams._changedTextStart - start;
         aParams._changedTextStart = start;
         aParams._oldLength += lenDiff;
         aParams._newLength += lenDiff + diff;
         aParams._cursorOffset += lenDiff + diff;
      }
   }

   public int getDecodedStringLength(int start, int end) {
      int len = end - start;
      if (this._smileyFacility != null) {
         AttributedString$Iterator iter = this._field._text.getIterator(start, end);

         do {
            if (iter.runPicture() instanceof SmileySupport$SmileyPicture) {
               len += this._smileyFacility.emoticonReplacementText(((SmileySupport$SmileyPicture)iter.runPicture()).getId()).length() - 1;
            }
         } while (iter.next());
      }

      return len;
   }

   private char[] getDecodedText(int start, int end) {
      StringBufferGap text = this._field.getAttributedText().getText();
      AttributedString$Iterator iter = this._field._text.getIterator(start, end);
      int len = end - start;

      do {
         if (iter.runPicture() instanceof SmileySupport$SmileyPicture) {
            len += this._smileyFacility.emoticonReplacementText(((SmileySupport$SmileyPicture)iter.runPicture()).getId()).length() - 1;
         }
      } while (iter.next());

      char[] result = new char[len];
      int srcOffset = start;
      int dstOffset = 0;
      iter.set(start, end);

      do {
         if (iter.runPicture() instanceof SmileySupport$SmileyPicture) {
            String smText = this._smileyFacility.emoticonReplacementText(((SmileySupport$SmileyPicture)iter.runPicture()).getId());
            smText.getChars(0, smText.length(), result, dstOffset);
            dstOffset += smText.length();
         } else {
            text.getChars(srcOffset, srcOffset + iter.runLength(), result, dstOffset);
            dstOffset += iter.runLength();
         }

         srcOffset += iter.runLength();
      } while (iter.next());

      return result;
   }

   private int insertSmiley(StringPattern$Match match) {
      AttributedString insertedSmiley = new AttributedString();
      if (!this.isHttpConflict(match)) {
         insertedSmiley.insert(new SmileySupport$SmileyPicture(this, (int)match.id));
         this._field._text.replace(match.beginIndex, match.endIndex, insertedSmiley.getIterator(), 0, 0);
         return this._match.endIndex - this._match.beginIndex - 1;
      } else {
         return 0;
      }
   }

   private boolean isHttpConflict(StringPattern$Match match) {
      String[] patterns = new String[]{"http:/", "https:/"};
      int x = 0;

      do {
         boolean noMatch = false;
         int prefixLength = patterns[x].length() - 2;
         if (match.beginIndex - prefixLength >= 0) {
            int i = match.beginIndex - prefixLength;

            for (int j = 0; i < match.endIndex; j++) {
               if (Character.toLowerCase(this._field._text.charAt(i)) != patterns[x].charAt(j)) {
                  noMatch = true;
                  break;
               }

               i++;
            }

            if (!noMatch) {
               return true;
            }
         }
      } while (++x < patterns.length);

      return false;
   }

   private int smileyScan(int start, int end) {
      if (this._smileyFacility == null) {
         return 0;
      }

      int origEnd = end;
      StringBufferGap string = this._field.getDisplayText();

      while (start < end && this._smileyFacility.findMatch(string, start, end, this._match)) {
         int result = this.insertSmiley(this._match);
         end -= result;
         start++;
      }

      return end - origEnd;
   }
}
