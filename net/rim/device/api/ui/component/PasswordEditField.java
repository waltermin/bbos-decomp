package net.rim.device.api.ui.component;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.ui.FormatParams;
import net.rim.device.internal.ui.StringBufferGap;

public class PasswordEditField extends BasicEditField {
   public PasswordEditField() {
      this(null, null);
   }

   public PasswordEditField(String label, String initialValue) {
      this(label, initialValue, 1000000, 536870912);
   }

   public PasswordEditField(String label, String initialValue, int maxNumChars, long style) {
      super(label, "", maxNumChars, verifyStyle(style | 2147483648L | 1073741824));
      this.setAttrib(0, 0, 268435456, 268435456, 0, 0);
      this.setText(initialValue);
      this.setAllowUnicodeInput(false);
   }

   @Override
   protected StringBufferGap getDisplayText() {
      char[] buffer = new char[super._text.length()];
      Arrays.fill(buffer, '*');
      return new StringBufferGap(new String(buffer));
   }

   @Override
   public final boolean isSelectionCopyable() {
      return false;
   }

   @Override
   protected boolean keyRepeat(int keycode, int time) {
      int key = Keypad.key(keycode);
      return !Trackball.isSupported() || key != 273;
   }

   @Override
   protected void notifyTextChanged(FormatParams aParams, boolean aIsInsertionOrDeletion) {
      if (aIsInsertionOrDeletion) {
         int labelLen = this.getLabelLength();
         if (aParams._changedTextStart + aParams._newLength > labelLen) {
            int from = Math.max(aParams._changedTextStart, labelLen);
            this.setAttrib(from, aParams._changedTextStart + aParams._newLength, 268435456, 268435456, 0, 0);
            if (this.getComposedTextStart() != this.getComposedTextEnd()) {
               this.setAttrib(this.getComposedTextStart(), this.getComposedTextEnd(), 0, 268435456, 0, 0);
            }

            if (this.getMaxSize() == this.getDecodedTextLength() && this.getLatestCommittedTextEnd() == this.getDecodedTextLength()) {
               this.setAttrib(this.getLatestCommittedTextStart(), this.getLatestCommittedTextEnd(), 268435456, 268435456, 0, 0);
            }
         }
      }
   }

   @Override
   public final void selectionCopy(Clipboard cb) {
   }

   private static long verifyStyle(long style) {
      if ((style & 54043195528445952L) == 0) {
         style |= 18014398509481984L;
      }

      if ((style & 13510798882111488L) == 0) {
         style |= 4503599627370496L;
      }

      return style;
   }

   @Override
   protected void onUnfocus() {
      super.onUnfocus();
   }

   @Override
   protected boolean backspace() {
      return super.backspace();
   }

   @Override
   public int backspace(int count) {
      return super.backspace(count);
   }

   @Override
   protected int backspace(int count, int context) {
      return super.backspace(count, context);
   }

   @Override
   public char charAt(int offset) {
      return super.charAt(offset);
   }

   @Override
   public void clear(int context) {
      super.clear(context);
   }

   @Override
   public void wipe() {
      super.wipe();
   }

   @Override
   public void selectionDelete() {
      super.selectionDelete();
   }

   @Override
   protected void displayFieldFullMessage() {
      super.displayFieldFullMessage();
   }

   @Override
   public String getLabel() {
      return super.getLabel();
   }

   @Override
   public int getLabelLength() {
      return super.getLabelLength();
   }

   @Override
   public int getMaxSize() {
      return super.getMaxSize();
   }

   @Override
   public int getPreferredHeight() {
      return super.getPreferredHeight();
   }

   @Override
   public int getPreferredWidth() {
      return super.getPreferredWidth();
   }

   @Override
   public String getText() {
      return super.getText();
   }

   @Override
   public String getText(int offset, int length) {
      return super.getText(offset, length);
   }

   @Override
   public void getText(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      super.getText(srcBegin, srcEnd, dst, dstBegin);
   }

   @Override
   public AbstractString getTextAbstractString() {
      return super.getTextAbstractString();
   }

   @Override
   public int getTextLength() {
      return super.getTextLength();
   }

   @Override
   public int insert(String text) {
      return super.insert(text);
   }

   @Override
   protected int insert(String text, int context) {
      return super.insert(text, context);
   }

   @Override
   protected boolean isSymbolScreenAllowed() {
      return super.isSymbolScreenAllowed();
   }

   @Override
   public void setLabel(String newLabel) {
      super.setLabel(newLabel);
   }

   @Override
   protected void update(int delta) {
      super.update(delta);
   }

   @Override
   public void setMaxSize(int maxSize) {
      super.setMaxSize(maxSize);
   }

   @Override
   public void setCursorPosition(int offset) {
      super.setCursorPosition(offset);
   }

   @Override
   protected void setCursorPosition(int offset, int context) {
      super.setCursorPosition(offset, context);
   }

   @Override
   public int getCursorPosition() {
      return super.getCursorPosition();
   }

   @Override
   public boolean paste(Clipboard cb) {
      return super.paste(cb);
   }

   @Override
   public void setText(String text) {
      super.setText(text);
   }

   @Override
   protected void setText(String text, int context) {
      super.setText(text, context);
   }
}
