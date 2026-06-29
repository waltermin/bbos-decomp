package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.internal.browser.ui.TextInputField;

final class HTMLTextInputField extends TextInputField implements Validation {
   private int _backgroundColor;
   private int _foregroundColor;
   private boolean _emptyOk;
   private boolean _eatTrackwheelRoll;
   private HTMLInput _controller;

   public final void selectProgrammatically() {
      this._eatTrackwheelRoll = true;
      super._edit.setFocus();
      super._edit.setCursorPosition(0);
      super._edit.select(true);
      super._edit.setCursorPosition(super._edit.getTextLength());
   }

   @Override
   public final boolean validate() {
      return this._emptyOk || super._edit.getText().length() != 0;
   }

   HTMLTextInputField(
      HTMLInput controller,
      Font defaultFont,
      String initialValue,
      int size,
      int maxLength,
      byte flags,
      long style,
      int backgroundColor,
      int foregroundColor,
      String format,
      boolean emptyOk
   ) {
      super("", initialValue, size, maxLength, defaultFont, flags, style | 2147483648L, format);
      this._backgroundColor = backgroundColor;
      this._foregroundColor = foregroundColor;
      this._emptyOk = emptyOk;
      this._controller = controller;
   }

   @Override
   public final String getText() {
      return super._edit.getText();
   }

   @Override
   protected final void onFocus(int direction) {
      super.onFocus(direction);
      if (this._controller.getPeer() == this) {
         this._controller.eventOccurred(3);
      }
   }

   @Override
   protected final void onUnfocus() {
      boolean isMuddy = super._edit.isMuddy();
      super.onUnfocus();
      Field peer = this._controller.getPeer();
      if (peer == this) {
         this._controller.eventOccurred(isMuddy ? 4 : 6);
      } else {
         if (isMuddy && peer instanceof HTMLFileInputField) {
            ((HTMLFileInputField)peer).setMuddy(true);
         }
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n' && this._controller != null) {
         this._controller.submitForm();
         return true;
      }

      if (key != '\b' && key != 127 && key != '\n') {
         if ((key < ' ' || key > '~') && key < 160) {
            return super.keyChar(key, status, time);
         }

         if (super._edit.getMaxSize() > super._edit.getTextLength()) {
            super.keyChar(key, status, time);
         }

         return true;
      } else {
         super.keyChar(key, status, time);
         return true;
      }
   }

   @Override
   public final void applyTheme() {
      super.applyTheme();
      if (Graphics.isColor() && (this._backgroundColor != -1 || this._foregroundColor != -1)) {
         Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
         ThemeAttributeSet$Writer themeAttributeSetWriter = themeWriter.createThemeAttributeSetWriter(null);
         if (this._backgroundColor != -1) {
            themeAttributeSetWriter.setColor(0, this._backgroundColor);
         }

         if (this._foregroundColor != -1) {
            themeAttributeSetWriter.setColor(1, this._foregroundColor);
         }

         this.setThemeAttributesSpecial(themeAttributeSetWriter.getThemeAttributeSet(), null);
         super._edit.setThemeAttributesSpecial(themeAttributeSetWriter.getThemeAttributeSet(), null);
      }
   }

   HTMLTextInputField(
      HTMLInput controller, Font defaultFont, String initialValue, int size, int maxLength, byte flags, long style, int backgroundColor, int foregroundColor
   ) {
      this(controller, defaultFont, initialValue, size, maxLength, flags, style, backgroundColor, foregroundColor, null, true);
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      if (this._eatTrackwheelRoll && super._edit.isSelecting()) {
         super._edit.select(false);
         this._eatTrackwheelRoll = false;
      }

      return super.trackwheelRoll(amount, status, time);
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return (status & 1073774592) == 0;
   }
}
