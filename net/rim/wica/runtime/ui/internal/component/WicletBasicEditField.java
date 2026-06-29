package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.component.ActiveAutoTextEditField;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.tid.awt.event.InputMethodEvent;
import net.rim.wica.runtime.metadata.component.ui.control.EditControl;
import net.rim.wica.runtime.ui.internal.InputMaskTextFilter;
import net.rim.wica.runtime.ui.internal.MultiFocusable;

public class WicletBasicEditField extends ActiveAutoTextEditField implements MultiFocusable {
   private IntIntHashtable _literals;
   private boolean _inInputMethodTextChanged;
   private static final int DEFAULT_MAXCHARS;
   private static final char LITERAL_CHAR;

   public WicletBasicEditField(String format) {
      super(null, null, 124000, 0);
      this.initialize(format);
   }

   public WicletBasicEditField(long style, String format) {
      super(null, null, 124000, style);
      this.initialize(format);
   }

   public WicletBasicEditField(String label, String initialValue, long style, String format, EditControl control) {
      super(label, initialValue, 124000, style);
      this.initialize(format);
   }

   private void initialize(String format) {
      if (format != null && !format.startsWith("*M") && !format.startsWith("*M".toLowerCase())) {
         this.parseLiterals(format);
         InputMaskTextFilter filter = new InputMaskTextFilter(this, format);
         filter.setValidation(false);
         this.setFilter(filter);
      }
   }

   private void parseLiterals(String format) {
      if (format != null) {
         int litFound = 0;
         this._literals = (IntIntHashtable)(new Object());
         int length = format.length();

         for (int i = 0; i < length; i++) {
            if ('\\' == format.charAt(i)) {
               litFound++;
               int litIndex = i + 1;
               this._literals.put(litIndex - litFound, format.charAt(litIndex));
            }
         }
      }
   }

   private boolean preProcessLiterals(int pos, String text) {
      if (this._literals.get(pos) == -1) {
         return true;
      }

      int lit;
      if (pos == text.length()) {
         for (int i = pos; (lit = this._literals.get(i)) != -1; i++) {
            this.insert(String.valueOf((char)lit));
         }
      } else {
         int i = pos;

         while (this._literals.get(i) != -1) {
            i++;
         }

         this.setCursorPosition(Math.min(i, text.length()));
      }

      return false;
   }

   private void postProcessLiterals() {
      int cursorPos = this.getCursorPosition();
      int lit;
      if (cursorPos == this.getTextLength() && this._literals.get(cursorPos) != -1) {
         for (int i = cursorPos; (lit = this._literals.get(i)) != -1; i++) {
            this.insert(String.valueOf((char)lit));
         }
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         return ((WicletTextEditField)this.getManager()).performDefaultAction();
      }

      InputMaskTextFilter filter = (InputMaskTextFilter)this.getFilter();
      if (filter != null && key != '\b' && key != 127 && key != 27) {
         String oldText = this.getText();
         int oldCursorPos = this.getCursorPosition();
         if (!this.preProcessLiterals(oldCursorPos, oldText)) {
            return true;
         } else {
            boolean res = super.keyChar(key, status, time);
            this.postProcessLiterals();
            if (!filter.validate(this.getText())) {
               this.setText(oldText);
               this.setDirty(true);
               this.setCursorPosition(oldCursorPos);
               return true;
            } else {
               return res;
            }
         }
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   public int inputMethodTextChanged(InputMethodEvent event) {
      InputMaskTextFilter filter = (InputMaskTextFilter)this.getFilter();
      if (filter != null && !this._inInputMethodTextChanged) {
         this._inInputMethodTextChanged = true;
         String oldText = this.getText();
         int oldCursorPos = this.getCursorPosition();
         int oldCommitedLen = this.getCommittedTextLength();
         if (!this.preProcessLiterals(oldCursorPos, oldText)) {
            this._inInputMethodTextChanged = false;
            return 1;
         }

         int res = super.inputMethodTextChanged(event);
         String newText = this.getText();
         if (oldCursorPos == oldText.length() - 1 && oldCommitedLen + 2 == newText.length() && this._literals.get(this.getCommittedTextLength()) != -1) {
            newText = newText.substring(0, this.getCommittedTextLength());
            this.setText(newText);
            this.setCursorPosition(newText.length(), Integer.MIN_VALUE);

            int lit;
            for (int i = newText.length(); (lit = this._literals.get(i)) != -1; i++) {
               this.insert(String.valueOf((char)lit));
            }
         }

         this.postProcessLiterals();
         if (!filter.validate(newText)) {
            this.setText(oldText);
            this.setDirty(true);
            this.setCursorPosition(oldCursorPos, Integer.MIN_VALUE);
            this._inInputMethodTextChanged = false;
            return 1;
         } else {
            this._inInputMethodTextChanged = false;
            return res;
         }
      } else {
         return super.inputMethodTextChanged(event);
      }
   }

   @Override
   protected boolean backspace() {
      InputMaskTextFilter filter = (InputMaskTextFilter)this.getFilter();
      if (filter == null) {
         return super.backspace();
      } else {
         String oldText = this.getText();
         int oldCursorPos = this.getCursorPosition();
         boolean res = super.backspace();
         if (!filter.validate(this.getText())) {
            this.setText(oldText);
            this.setDirty(true);
            this.setCursorPosition(oldCursorPos);
            return false;
         } else {
            return res;
         }
      }
   }

   @Override
   protected int backspace(int count, int context) {
      InputMaskTextFilter filter = (InputMaskTextFilter)this.getFilter();
      if (filter != null && !this._inInputMethodTextChanged) {
         String oldText = this.getText();
         int oldCursorPos = this.getCursorPosition();
         int res = super.backspace(count, context);
         if (!filter.validate(this.getText())) {
            this.setText(oldText);
            this.setDirty(true);
            this.setCursorPosition(oldCursorPos);
            return 0;
         } else {
            return res;
         }
      } else {
         return super.backspace(count, context);
      }
   }

   @Override
   public void selectionDelete() {
      InputMaskTextFilter filter = (InputMaskTextFilter)this.getFilter();
      if (filter != null && !this._inInputMethodTextChanged) {
         String oldText = this.getText();
         int oldCursorPos = this.getCursorPosition();
         super.selectionDelete();
         if (!filter.validate(this.getText())) {
            this.setText(oldText);
            this.setDirty(true);
            this.setCursorPosition(oldCursorPos);
         }
      } else {
         super.selectionDelete();
      }
   }

   @Override
   public boolean paste(Clipboard cb) {
      InputMaskTextFilter filter = (InputMaskTextFilter)this.getFilter();
      if (filter != null && !this._inInputMethodTextChanged) {
         String oldText = this.getText();
         int oldCursorPos = this.getCursorPosition();
         boolean res = super.paste(cb);
         this.postProcessLiterals();
         if (!filter.validate(this.getText())) {
            this.setText(oldText);
            this.setDirty(true);
            this.setCursorPosition(oldCursorPos);
         }

         return res;
      } else {
         return super.paste(cb);
      }
   }

   @Override
   public void moveFocus(int x, int y, int status, int time) {
      if (!this.isCursorPositionSet()) {
         this.onFocus(1);
      }

      super.moveFocus(x, y, status, time);
   }

   @Override
   protected void onFocus(int direction) {
      if (direction > 0) {
         this.setCursorPosition(0, 0);
      } else {
         if (direction < 0) {
            this.setCursorPosition(Math.max(0, this.getTextLength()), 0);
         }
      }
   }
}
