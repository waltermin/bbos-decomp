package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.util.NumberUtilities;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.InputMethodEvent;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.AttributedString$Iterator;

public class HexEditField extends EditField {
   private boolean _firstNibble;
   private int _maxBytes;
   private char _firstNibbleChar;
   private StringBuffer _strBuf;
   private int _nibbleInsertionIndex;
   AttributedString _tempString = new AttributedString();
   AttributedString$Iterator _iterator = this._tempString.getIterator();

   public HexEditField(String label) {
      this(label, Integer.MAX_VALUE);
   }

   public HexEditField(String label, byte[] value) {
      this(label, value, 0, value.length, Integer.MAX_VALUE);
   }

   public HexEditField(String label, byte[] value, int offset, int length) {
      this(label, value, offset, length, Integer.MAX_VALUE);
   }

   public HexEditField(String label, byte[] value, int offset, int length, int maxBytes) {
      super(label, null, Integer.MAX_VALUE, 1140850688);
      this._firstNibble = true;
      this._maxBytes = maxBytes;
      this._strBuf = new StringBuffer(4);
      this.setData(value, offset, length);
   }

   public HexEditField(String label, int maxBytes) {
      super(label, null, Integer.MAX_VALUE, 1140850688);
      this._firstNibble = true;
      this._maxBytes = maxBytes;
      this._strBuf = new StringBuffer(4);
   }

   @Override
   protected boolean insert(char key, int status) {
      key = this.convert(key, status);
      if (NumberUtilities.hexDigitToInt(key, -1) == -1) {
         return false;
      }

      int cursor = this.getCaretPosition();
      int cursorOffset = 0;
      this._tempString.set("");
      int startPos;
      int endPos;
      if (this._firstNibble) {
         if (this._maxBytes <= this.getNumBytes()) {
            return false;
         }

         int anchor = this.getAnchorPosition();
         if (cursor < anchor) {
            startPos = cursor;
            endPos = anchor;
         } else {
            startPos = anchor;
            endPos = cursor;
         }

         this._tempString.insert('0');
         this._tempString.insert(key);
         if (this.getCursorPosition() != super.getTextLength()) {
            this._tempString.insert(' ');
            cursorOffset = -1;
         }

         this._firstNibble = false;
         this._firstNibbleChar = key;
      } else {
         startPos = cursor - 2;
         endPos = cursor;
         this._tempString.insert(this._firstNibbleChar);
         this._tempString.insert(key);
         this._tempString.insert(' ');
         if (this.getCursorPosition() != this.getTextLength() && this.charAt(this.getCursorPosition()) == ' ') {
            endPos++;
         }

         this._firstNibble = true;
      }

      int len = this._tempString.length();
      this._iterator.set(0, len);
      this.replace(startPos, endPos, this._iterator, 0, 0, len, cursorOffset, true, 0);
      return true;
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      int pos = this.getCursorPosition();
      if (!this._firstNibble) {
         if (pos == super.getTextLength()) {
            this._tempString.set(" ");
            this._iterator.set(0, 1);
            pos = this.getCaretPosition();
            this.replace(pos, pos, this._iterator, 0, 0, 1, 0, true, 0);
         } else {
            pos = this.getCaretPosition();
            this.setSelection(pos + 1, true, pos + 1);
         }
      }

      boolean movingHorizontally = false;
      if ((status & 536870912) != 0) {
         movingHorizontally = (status & 65536) != 0;
      } else {
         movingHorizontally = (status & 1) != 0;
      }

      if (movingHorizontally) {
         int curOff = this.getCursorPosition();
         amount *= 3;
         amount -= (amount + curOff) % 3;
         this._firstNibble = true;
         return super.moveFocus(amount, status, time);
      }

      amount = super.moveFocus(amount, status, time);
      int offset = this.getCursorPosition();
      int delta = offset % 3;
      if (offset == this.getTextLength() - 1) {
         this.scrollHorizontally(1);
      } else if (delta != 0) {
         this.scrollHorizontally(delta * -1);
      }

      this._firstNibble = true;
      return amount;
   }

   @Override
   public void selectionDelete() {
      if (this.isSelecting()) {
         super.selectionDelete();
      } else if (this.getCursorPosition() != super.getTextLength()) {
         if (this._firstNibble) {
            int toDelete = this.getCursorPosition() + 3;
            if (toDelete > this.getTextLength()) {
               toDelete--;
            }

            super.setCursorPosition(toDelete);
            super.backspace(toDelete);
         } else {
            super.setCursorPosition(this.getCursorPosition() + 3);
            super.backspace(3);
            this._firstNibble = true;
            this.setCursorPosition(this.getCursorPosition() + 1);
         }

         this.fieldChangeNotify(0);
      }
   }

   @Override
   protected boolean backspace() {
      if (this.getCursorPosition() == 0) {
         return false;
      }

      int count = 3;
      if (!this._firstNibble) {
         if (super.getTextLength() == this.getCursorPosition()) {
            count = 2;
         } else {
            super.setCursorPosition(this.getCursorPosition() + 1);
         }
      }

      super.backspace(count);
      this._firstNibble = true;
      this.fieldChangeNotify(0);
      return true;
   }

   public int getNumBytes() {
      return (super.getTextLength() + 2) / 3;
   }

   public byte[] getAsBytes() {
      byte[] vals = new byte[this.getNumBytes()];
      int val = 0;
      int byteCount = 0;

      for (int i = 0; byteCount < vals.length; i++) {
         char ch = i < super.getTextLength() ? super.charAt(i) : ' ';
         if (ch != ' ') {
            val <<= 4;
            val |= NumberUtilities.hexDigitToInt(ch);
         } else {
            vals[byteCount] = (byte)(val & 0xFF);
            val = 0;
            byteCount++;
         }
      }

      return vals;
   }

   public short[] getAsShorts() {
      int shortCount = 0;
      int byteCount = 0;
      int sVal = 0;
      if (this.getNumBytes() % 2 != 0) {
         return null;
      }

      short[] vals = new short[this.getNumBytes() / 2];

      for (int i = 0; shortCount < vals.length; i++) {
         char ch = i < super.getTextLength() ? super.charAt(i) : ' ';
         if (ch != ' ') {
            sVal <<= 4;
            sVal |= NumberUtilities.hexDigitToInt(ch);
         } else if ((++byteCount & 1) == 0) {
            vals[shortCount] = (short)(sVal & 65535);
            shortCount++;
            sVal = 0;
         }
      }

      return vals;
   }

   public int[] getAsInts() {
      int intCount = 0;
      int byteCount = 0;
      int iVal = 0;
      if (this.getNumBytes() % 4 != 0) {
         return null;
      }

      int[] vals = new int[this.getNumBytes() / 4];

      for (int i = 0; intCount < vals.length; i++) {
         char ch = i < super.getTextLength() ? super.charAt(i) : ' ';
         if (ch != ' ') {
            iVal <<= 4;
            iVal |= NumberUtilities.hexDigitToInt(ch);
         } else if ((++byteCount & 3) == 0) {
            vals[intCount] = iVal;
            intCount++;
            iVal = 0;
         }
      }

      return vals;
   }

   @Override
   public int inputMethodTextChanged(InputMethodEvent event) {
      AttributedString inserted_text = event.getText();
      if (inserted_text.length() == 0) {
         return super.inputMethodTextChanged(event);
      }

      char ch = inserted_text.getText().charAt(0);
      int hex = NumberUtilities.hexDigitToInt(ch, -1);
      if (hex == -1) {
         return 1;
      }

      ch = NumberUtilities.intToUpperHexDigit(hex);
      StringBuffer strBuf = this._strBuf;
      strBuf.setLength(0);
      int newCc;
      if (this._firstNibble) {
         if (this._maxBytes <= this.getNumBytes()) {
            return 1;
         }

         strBuf.append('0');
         strBuf.append(ch);
         newCc = 2;
         this._firstNibbleChar = ch;
         this._nibbleInsertionIndex = this.getComposedTextStart();
         int len = this.getAttributedText().length() - 1;
         if (this.getCaretPosition() < len && this.getAnchorPosition() < len) {
            strBuf.append(' ');
            newCc++;
         }
      } else {
         if (this.getComposedTextStart() != this._nibbleInsertionIndex
            && !this.isComposedTextExist()
            && this.getLatestCommittedTextStart() != this.getLatestCommittedTextEnd()) {
            this.setComposedText(this.getLatestCommittedTextStart(), this.getLatestCommittedTextEnd());
         }

         strBuf.append(this._firstNibbleChar);
         strBuf.append(ch);
         strBuf.append(' ');
         newCc = 3;
      }

      if (event.getCommittedCharacterCount() == 1) {
         inserted_text.set(strBuf);
         this._firstNibble = !this._firstNibble;
         event.init(
            event.getSource(),
            event.getID(),
            event.getModifiers(),
            inserted_text,
            event.getTextMask(),
            newCc,
            event.getConvertedCharacterCount(),
            event.getCaret(),
            event.getVisiblePosition()
         );
      }

      return super.inputMethodTextChanged(event);
   }

   @Override
   public void dispatchEvent(Event rEvent) {
      if (rEvent.getID() != 514 && rEvent.getID() != 519) {
         super.dispatchEvent(rEvent);
      }
   }

   @Override
   public boolean paste(Clipboard cb) {
      if (!this.isPasteable()) {
         return false;
      }

      if (this.getComposedTextStart() != this.getComposedTextEnd()) {
         return false;
      }

      Object pasted = cb.get();
      AttributedString attrString;
      if (!(pasted instanceof AttributedString)) {
         attrString = new AttributedString(cb.toString());
      } else {
         attrString = (AttributedString)pasted;
      }

      int len = attrString.length();
      if (len % 3 != 0) {
         return false;
      }

      for (int i = 0; i < len; i++) {
         char ch = attrString.charAt(i);
         if (i % 3 == 2) {
            if (ch != ' ') {
               return false;
            }
         } else {
            char converted = this.convert(ch, 0);
            int hex = NumberUtilities.hexDigitToInt(converted, -1);
            if (hex == -1 || converted != ch) {
               return false;
            }
         }
      }

      if (!this._firstNibble) {
         this._firstNibble = true;
         attrString.insert(0, ' ');
      }

      AttributedString$Iterator iter = attrString.getIterator();
      this.setDefaultInsertionAttributes();
      this.replace(this.getAnchorPosition(), this.getCaretPosition(), iter, 0, 0, iter.length(), 0, true, 0);
      return true;
   }

   public void setData(byte[] val, int offset, int length) {
      length = Math.min(Math.min(length, val.length - offset), this._maxBytes);
      StringBuffer strBuf = new StringBuffer(length * 3);

      for (int i = offset; i < length; i++) {
         strBuf.append(NumberUtilities.intToUpperHexDigit(val[i] >> 4));
         strBuf.append(NumberUtilities.intToUpperHexDigit(val[i]));
         strBuf.append(' ');
      }

      AttributedString$Iterator iter = new AttributedString(strBuf).getIterator();
      this.setDefaultInsertionAttributes();
      this.replace(this.getLabelLength(), this.getLabelLength() + this.getTextLength(), iter, 0, 0, iter.length(), 0, true, 0);
      this._firstNibble = true;
   }

   public void setData(int[] val, int offset, int length) {
      StringBuffer strBuf = new StringBuffer(val.length * 3 * 4);
      int max = Math.min(Math.min(length, val.length - offset), this._maxBytes / 4);

      for (int i = 0; i < max; i++) {
         int nibble = 7;
         strBuf.append(' ');

         for (; nibble >= 0; nibble--) {
            strBuf.append(NumberUtilities.intToUpperHexDigit(val[i] >> nibble * 4));
            if (nibble % 2 == 0 && nibble != 0) {
               strBuf.append(' ');
            }
         }
      }

      AttributedString$Iterator iter = new AttributedString(strBuf).getIterator();
      this.setDefaultInsertionAttributes();
      this.replace(this.getLabelLength(), this.getLabelLength() + this.getTextLength(), iter, 0, 0, iter.length(), 0, true, 0);
      this._firstNibble = true;
   }

   public void setData(short[] val, int offset, int length) {
      StringBuffer strBuf = new StringBuffer(val.length * 3 * 2);
      int max = Math.min(Math.min(length, val.length - offset), this._maxBytes / 2);

      for (int i = offset; i < max; i++) {
         int nibble = 3;
         strBuf.append(' ');

         for (; nibble >= 0; nibble--) {
            strBuf.append(NumberUtilities.intToUpperHexDigit(val[i] >> nibble * 4));
            if (nibble % 2 == 0 && nibble != 0) {
               strBuf.append(' ');
            }
         }
      }

      AttributedString$Iterator iter = new AttributedString(strBuf).getIterator();
      this.setDefaultInsertionAttributes();
      this.replace(this.getLabelLength(), this.getLabelLength() + this.getTextLength(), iter, 0, 0, iter.length(), 0, true, 0);
      this._firstNibble = true;
   }

   @Override
   public void setMaxSize(int maxSize) {
      byte[] oldBytes = this.getAsBytes();
      int pos = this.getCursorPosition();
      if (pos > maxSize) {
         pos = maxSize;
      }

      this._maxBytes = maxSize / 3;
      synchronized (this) {
         this.setText("");
         super.setMaxSize(maxSize);
         this.setData(oldBytes, 0, this._maxBytes);
      }

      this.setCursorPosition(pos, this.isCursorPositionSet() ? 0 : Integer.MIN_VALUE);
   }
}
