package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.internal.browser.ui.BrowserEditField;
import net.rim.device.apps.internal.browser.ui.RigidManager;
import net.rim.device.apps.internal.browser.ui.TextInputField;
import net.rim.device.internal.ui.container.FrameLayout;

public final class HTMLTextAreaField extends FrameLayout implements FocusChangeListener, Validation {
   private RigidManager _manager;
   private EditField _textArea;
   private int _backgroundColor;
   private int _foregroundColor;
   private boolean _emptyOk;
   private boolean _eatTrackwheelRoll;
   private HTMLTextArea _controller;

   public final String getText() {
      return this._textArea.getText();
   }

   public final void setTextWithTruncation(String str) {
      int maxSize = this._textArea.getMaxSize();
      if (str.length() > maxSize) {
         str = str.substring(0, maxSize);
      }

      this._textArea.setText(str);
   }

   public final void selectProgrammatically() {
      this._eatTrackwheelRoll = true;
      this._textArea.setFocus();
      this._textArea.setCursorPosition(0);
      this._textArea.select(true);
      this._textArea.setCursorPosition(this._textArea.getTextLength());
   }

   @Override
   public final boolean validate() {
      return this._emptyOk || this._textArea.getText().length() != 0;
   }

   @Override
   protected final void onUnfocus() {
      boolean isMuddy = this._textArea.isMuddy();
      super.onUnfocus();
      this._controller.eventOccurred(isMuddy ? 4 : 6);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key != '\b' && key != 127 && key != '\n') {
         if ((key < ' ' || key > '~') && key < 160) {
            return super.keyChar(key, status, time);
         }

         if (this._textArea.getMaxSize() > this._textArea.getTextLength()) {
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
         this._manager.setThemeAttributesSpecial(themeAttributeSetWriter.getThemeAttributeSet(), null);
         this._textArea.setThemeAttributesSpecial(themeAttributeSetWriter.getThemeAttributeSet(), null);
      }
   }

   @Override
   public final void focusChanged(Field field, int eventType) {
      if (field == this._textArea && eventType == 1) {
         this._controller.eventOccurred(3);
      }
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      if (this._eatTrackwheelRoll && this._textArea.isSelecting()) {
         this._textArea.select(false);
         this._eatTrackwheelRoll = false;
      }

      return super.trackwheelRoll(amount, status, time);
   }

   HTMLTextAreaField(
      HTMLTextArea controller, int rows, int cols, Font font, long fieldStyle, int backgroundColor, int foregroundColor, String format, boolean emptyOk
   ) {
      super(0);
      this._backgroundColor = backgroundColor;
      this._foregroundColor = foregroundColor;
      this._emptyOk = emptyOk;
      this._controller = controller;
      int size = 512;
      HTMLBrowserContent browserContent = ((HTMLDocumentImpl)this._controller.getOwnerDocument()).getUiPeer();
      if (browserContent != null && browserContent.getRenderingOptions() != null) {
         size = browserContent.getRenderingOptions().getPropertyWithIntValue(4550690918222697397L, 14, 512);
      }

      if (format != null && !format.equals("*M")) {
         this._textArea = new BrowserEditField(size, fieldStyle | 8388608 | 137438953472L, format);
      } else {
         this._textArea = new AutoTextEditField("", "", size, fieldStyle | 8388608 | 137438953472L);
      }

      this._textArea.setFocusListener(this);
      this._textArea.setTag(TextInputField.BROWSER_EDIT_TAG);
      if (font != null) {
         this._textArea.setFont(font);
      }

      this._manager = new RigidManager(cols, rows, 281612415664128L, this._textArea.getFont());
      this._manager.add(this._textArea);
      this.add(this._manager);
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return (status & 1073774592) == 0;
   }
}
