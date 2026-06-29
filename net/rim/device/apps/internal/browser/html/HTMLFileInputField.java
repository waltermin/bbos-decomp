package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.apps.api.framework.file.FileSelector;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.internal.io.file.FileUtilities;

final class HTMLFileInputField extends FlowFieldManager implements FieldChangeListener, Validation {
   private HTMLTextInputField _textField;
   private ButtonField _buttonField;
   private HTMLInput _controller;
   private boolean _muddy;

   final void setFilename(String filename) {
      this._textField.setTextWithTruncation(filename != null ? filename : "");
   }

   final String getFilename() {
      return this._textField.getText();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._buttonField) {
         FileSelector selector = new FileSelector(null);
         String fileName = selector.selectFile(FileUtilities.getPath(this._textField.getText()));
         if (fileName != null) {
            this._textField.setTextWithTruncation(fileName);
            this.setMuddy(true);
         }
      }
   }

   @Override
   public final boolean validate() {
      return this._textField.validate();
   }

   @Override
   protected final void onUnfocus() {
      boolean isMuddy = this.isMuddy();
      super.onUnfocus();
      this._controller.eventOccurred(isMuddy ? 4 : 6);
      this.setMuddy(false);
   }

   @Override
   public final boolean isMuddy() {
      return this._muddy || this._textField.isMuddy();
   }

   @Override
   public final void setMuddy(boolean muddy) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   HTMLFileInputField(
      HTMLInput controller, Font defaultFont, int size, byte flags, long style, int backgroundColor, int foregroundColor, String format, boolean emptyOk
   ) {
      super(281474976710656L);
      this._controller = controller;
      this._buttonField = new ButtonField(BrowserResources.getString(747), style | 65536);
      this._buttonField.setChangeListener(this);
      if ((style & 9007199254740992L) != 0) {
         style |= 1024;
      }

      this._textField = new HTMLTextInputField(controller, defaultFont, "", size, -1, flags, style, backgroundColor, foregroundColor, null, emptyOk);
      this.add(this._textField);
      this.add(this._buttonField);
   }

   HTMLFileInputField(HTMLInput controller, Font defaultFont, int size, byte flags, long style, int backgroundColor, int foregroundColor) {
      this(controller, defaultFont, size, flags, style, backgroundColor, foregroundColor, null, true);
   }

   @Override
   protected final void onFocus(int direction) {
      this.setMuddy(false);
      super.onFocus(direction);
      this._controller.eventOccurred(3);
   }
}
