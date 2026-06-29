package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.internal.browser.ui.SelectField;

final class HTMLSelectField extends SelectField {
   private int _backgroundColor;
   private HTMLSelect _controller;
   private int _foregroundColor;

   HTMLSelectField(BrowserContent browserContent, HTMLSelect controller, int size, boolean multiple, long style, int backgroundColor, int foregroundColor) {
      super(size, multiple, style | 137438953472L);
      this._backgroundColor = backgroundColor;
      this._foregroundColor = foregroundColor;
      this._controller = controller;
   }

   public final void reset() {
      this.resetOptions();
      this.invalidate();
   }

   @Override
   public final void toggleOption(int optionIndex, boolean programmatic) {
      super.toggleOption(optionIndex, programmatic);
   }

   @Override
   protected final void onChange() {
      super.onChange();
      this._controller.eventOccurred(4);
   }

   public final void setOption(int index, boolean value) {
      if (this.isOptionSet(index) != value) {
         this.toggleOption(index, true);
      }
   }

   public final void addNewOption(int index, String text, boolean selected, boolean defaultSelected) {
      super.addOption(index, text, (byte)((selected ? 1 : 0) | (defaultSelected ? 2 : 0)));
   }

   @Override
   protected final void onFocus(int direction) {
      super.onFocus(direction);
      this._controller.eventOccurred(3);
   }

   @Override
   protected final void onUnfocus() {
      super.onUnfocus();
      this._controller.eventOccurred(6);
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
      }
   }
}
