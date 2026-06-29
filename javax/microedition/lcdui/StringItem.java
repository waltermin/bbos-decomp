package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

public class StringItem extends Item {
   StringItem$PrivateRichTextField _field;
   String _text;
   String _label;
   Font _font;
   int _appearanceMode;

   public StringItem(String label, String text) {
      synchronized (Application.getEventLock()) {
         this._label = label;
         this._text = text;
         this._field = new StringItem$PrivateRichTextField(this.makeValue());
         this._field.setCookie(this);
         this._font = Font.getDefaultFont();
         this._field.setFont(this._font.getPeer());
         this.setPeer(this._field);
      }
   }

   public StringItem(String label, String text, int appearanceMode) {
      this(label, text);
      synchronized (Application.getEventLock()) {
         if (appearanceMode != 0 && appearanceMode != 1 && appearanceMode != 2) {
            throw new IllegalArgumentException();
         }

         this._appearanceMode = appearanceMode;
      }
   }

   @Override
   Field addToForm(FieldChangeListener changeListener) {
      return this._field;
   }

   private String makeValue() {
      StringBuffer value = new StringBuffer();
      if (this._label != null) {
         value.append(this._label);
      }

      if (this._text != null) {
         if (value.length() > 1) {
            value.append(' ');
         }

         int beginIndex = 0;
         int textLength = this._text.length();
         int endIndex = textLength;
         if (textLength > 0 && this._text.charAt(0) == '\n') {
            beginIndex = 1;
         }

         if (textLength > 1 && this._text.charAt(textLength - 1) == '\n') {
            endIndex = textLength - 1;
         }

         value.append(this._text.substring(beginIndex, endIndex));
      }

      return new String(value);
   }

   public String getText() {
      synchronized (Application.getEventLock()) {
         return this._text;
      }
   }

   public void setText(String text) {
      synchronized (Application.getEventLock()) {
         this._text = text;
         if (this._text != null) {
            int textLength = this._text.length();
            if (textLength > 0 && this._text.charAt(0) == '\n') {
               this.setLayout(this.getLayout() | 256);
            }

            if (textLength > 1 && this._text.charAt(textLength - 1) == '\n') {
               this.setLayout(this.getLayout() | 512);
            }
         }

         this._field.setText(this.makeValue());
      }
   }

   @Override
   public void setLabel(String label) {
      synchronized (Application.getEventLock()) {
         this._label = label;
         this._field.setText(this.makeValue());
      }
   }

   @Override
   public String getLabel() {
      synchronized (Application.getEventLock()) {
         return this._label;
      }
   }

   public int getAppearanceMode() {
      synchronized (Application.getEventLock()) {
         return this._appearanceMode;
      }
   }

   public void setFont(Font font) {
      synchronized (Application.getEventLock()) {
         if (font == null) {
            this._font = Font.getDefaultFont();
         } else {
            this._font = font;
         }

         this._field.setFont(this._font.getPeer());
      }
   }

   public Font getFont() {
      synchronized (Application.getEventLock()) {
         return this._font;
      }
   }
}
