package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.device.internal.ui.component.PopupDialog;

public final class ModalDisplayDlg extends PopupDialog {
   private ActiveRichTextField _textField;
   private int _bgColor;
   public static final byte STYLE_LABELTITLE = 0;
   public static final byte STYLE_RICHTEXTTITLE = 1;
   public static final byte STYLE_SEPARATOR = 16;

   public ModalDisplayDlg(
      Manager manager,
      String label,
      String text,
      Image icon,
      int[] offsets,
      byte[] attributes,
      Font[] fonts,
      int[] foreColors,
      int[] bgColors,
      int textFieldBgColor,
      byte style
   ) {
      super(manager, 65536);
      this._bgColor = textFieldBgColor;
      manager.deleteAll();
      HorizontalFieldManager title = null;
      if (icon != null) {
         if (title == null) {
            title = (HorizontalFieldManager)(new Object());
         }

         ImageField iconField = (ImageField)(new Object(65536));
         iconField.setImage(icon);
         title.add(iconField);
         title.add(new DocViewHorizontalSpacerField(this.getFont().getHeight() >> 2));
         if (title.getIndex() == -1) {
            manager.add(title);
         }
      }

      if (label != null && label.length() > 0) {
         if (title == null) {
            title = (HorizontalFieldManager)(new Object());
         }

         if ((style & 1) != 0) {
            title.add((Field)(new Object(label, 45035996273704960L)));
         } else {
            title.add((Field)(new Object(label, 64)));
         }

         if (title.getIndex() == -1) {
            manager.add(title);
         }

         if ((style & 16) != 0) {
            manager.add((Field)(new Object()));
         }
      }

      if (text != null && text.length() > 0) {
         this._textField = (ActiveRichTextField)(new Object(""));
         this._textField.setText(text, offsets, attributes, fonts, foreColors, bgColors);
         manager.add(this._textField);
      }
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      if (this._bgColor != -1) {
         Theme t = ThemeManager.getActiveTheme();
         if (t != null) {
            Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
            ThemeAttributeSet$Writer writer = themeWriter.createThemeAttributeSetWriter(null);
            writer.setColor(0, this._bgColor);
            int foreColor = 0;
            if (Math.abs(foreColor - this._bgColor) < 255) {
               foreColor = 16777215 - this._bgColor;
            }

            writer.setColor(1, foreColor);
            ThemeAttributeSet attr = writer.getThemeAttributeSet();
            this.setThemeAttributesSpecial(attr, null);
         }
      }
   }

   @Override
   protected final void close(int closeReason) {
      if (this._textField != null) {
         this._textField.select(false);
      }

      super.close(closeReason);
   }

   @Override
   public final boolean onMenu(int instance) {
      if (this._textField == null || !this._textField.isSelecting() && !this._textField.regionHasCookie()) {
         this.close(0);
         return true;
      } else {
         return super.onMenu(instance);
      }
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (c == ' ') {
         this.close(0);
         return true;
      }

      boolean retValue = super.keyChar(c, status, time);
      if (!retValue) {
         switch (c) {
            case '\u001b':
               this.close(-1);
               return true;
         }
      }

      return retValue;
   }
}
