package net.rim.device.api.ui.component;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.text.TextRect;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.util.StringUtilities;

public class LabelField extends Field implements DrawStyle {
   private long _rbId;
   private int _rbKey;
   private String _rbName;
   private int _cachedLocaleCode;
   private int _position;
   private String _text;
   private TextRect _labelText;
   private int _length;
   private Bitmap _image;
   private boolean _inSetTextInternal;
   private boolean _isSingleLine;
   private boolean _inLayout;
   public static final int DEFAULT_POSITION;
   public static final long REMOVE_COMBINING_LOW_LINE;
   private static final int ATTRIBUTES_MASK;
   private static final int STRING;
   private static final int STRINGBUFFER;
   private static final int CHARARRAY;
   private static final int BYTEARRAY;

   public LabelField() {
      this(null);
   }

   public LabelField(Object text) {
      this(text, 0, -1, 0);
   }

   public LabelField(Object text, long style) {
      this(text, 0, -1, style);
   }

   public LabelField(Object text, int offset, int length, long style) {
      super(style);
      if ((style & 18014398509481984L) != 0) {
         this._position = 1;
      }

      this._labelText = new TextRect(this, this.getFieldStyle() & 199);
      this.setText(text, offset, length);
   }

   public LabelField(ResourceBundleFamily rb, int key) {
      this(null);
      this.setText(rb, key);
   }

   private void checkLocale() {
      if (this._rbId != 0) {
         int currentCode = Locale.getDefault().getCode();
         if (this._cachedLocaleCode != currentCode) {
            this._cachedLocaleCode = currentCode;
            ResourceBundleFamily family = ResourceBundle.getBundle(this._rbId, this._rbName);
            String translated = family.getString(this._rbKey);
            this.setTextInternal(translated, 0, translated.length());
         }
      }
   }

   @Override
   public boolean isSelectionCopyable() {
      return true;
   }

   public int getPosition() {
      return this._position;
   }

   @Override
   public int getPreferredHeight() {
      return this.getFont().getHeight();
   }

   @Override
   public int getPreferredWidth() {
      this.checkLocale();
      Font font = this.getFont();
      int width = 0;
      if (this._image != null) {
         width += this._image.getWidth();
         width += font.getHeight() >> 2;
      }

      if (this._length != 0) {
         int measureLength = Math.min(this._length, Display.getWidth() >> 1);
         width += font.getBounds(this._text, 0, measureLength) + this._position + 1;
      }

      return width;
   }

   public String getText() {
      this.checkLocale();
      return this._text;
   }

   @Override
   protected void layout(int width, int height) {
      this._inLayout = true;
      this.checkLocale();
      Font font = this.getFont();
      int imageWidth = 0;
      if (this._image != null) {
         imageWidth += this._image.getWidth();
         imageWidth += font.getHeight() >> 2;
      }

      this._labelText.setPosition(this._position + imageWidth, 0);
      this._labelText.layout(Math.max(width - this._position - imageWidth, 0), height);
      int preferredWidth = this.getPreferredWidth();
      if (!this.isStyle(64) && preferredWidth > width) {
         this._isSingleLine = false;
         if (!this.isStyle(1152921504606846976L)) {
            width = Math.min(this._position + imageWidth + this._labelText.getWidth(), width);
         }
      } else {
         this._isSingleLine = true;
         if (!this.isStyle(1152921504606846976L)) {
            width = Math.min(preferredWidth, width);
         }
      }

      height = Math.max(this._labelText.getHeight(), this._image != null ? this._image.getHeight() : 0);
      int numLines = 0;
      ThemeAttributeSet tas = this.getThemeAttributeSet();
      if (tas != null) {
         numLines = tas.getMaximumLineWrapping();
      }

      if (numLines > 0) {
         int lines = Math.min(numLines, this._labelText.getLineCount());
         height = Math.max(lines * font.getHeight(), height);
         this._isSingleLine = lines == 1;
      }

      this.setExtent(width, height);
      this._inLayout = false;
   }

   @Override
   protected void paint(Graphics graphics) {
      this.checkLocale();
      int x = this._position;
      int imageHeight = 0;
      int textHeight = this.getFont().getHeight();
      if (this._image != null) {
         imageHeight = this._image.getHeight();
         graphics.drawBitmap(x, 0, this._image.getWidth(), imageHeight, this._image, 0, 0);
         x += this._image.getWidth();
         x += textHeight >> 2;
      }

      if (this._isSingleLine) {
         int y = 0;
         int labelTextHeight = this._labelText.getHeight();
         if (imageHeight > labelTextHeight) {
            y = imageHeight - labelTextHeight >> 1;
         }

         y = Math.max(labelTextHeight - textHeight, y);
         int width = this.getContentWidth() - 1 - x;
         graphics.drawText(this._text, x, y, this.getFieldStyle() & 199, width);
      } else {
         this._labelText.paintSelf(graphics);
      }
   }

   @Override
   public void selectionCopy(Clipboard cb) {
      cb.put(this.getText());
   }

   public void setImage(Bitmap image) {
      this._image = image;
   }

   public void setPosition(int position) {
      if (position < 0) {
         throw new IllegalArgumentException();
      }

      this._position = position;
      Manager manager = this.getManager();
      if (manager != null && this.isStyle(1152921504606846976L)) {
         this.updateLayout();
         this.focusAdd(false);
      }

      this.invalidate();
   }

   public void setText(Object text) {
      this.setText(text, 0, -1);
   }

   public void setText(Object text, int offset, int length) {
      this._rbId = 0;
      this._rbName = null;
      this.setTextInternal(text, offset, length);
   }

   public void setText(String text, int offset, int length) {
      this.setText((Object)text, offset, length);
   }

   public void setText(ResourceBundleFamily rb, int key) {
      if (rb == null) {
         this.setText(null);
      } else {
         this._cachedLocaleCode = Locale.getDefault().getCode();
         this._rbId = rb.getId();
         this._rbName = rb.getName();
         this._rbKey = key;
         String translated = rb.getString(key);
         this.setTextInternal(translated, 0, translated.length());
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void setTextInternal(Object text, int offset, int length) {
      if (!this._inSetTextInternal) {
         boolean var15 = false /* VF: Semaphore variable */;

         try {
            var15 = true;
            this._inSetTextInternal = true;
            String IAException = null;
            StringBuffer stringBufferValue = null;
            char[] charValue = null;
            byte[] byteValue = null;
            int len;
            int type;
            if (text == null) {
               IAException = "";
               len = 0;
               type = 1;
            } else if (!(text instanceof String)) {
               if (!(text instanceof StringBuffer)) {
                  if (!(text instanceof char[])) {
                     if (!(text instanceof byte[])) {
                        throw new IllegalArgumentException();
                     }

                     byteValue = (byte[])text;
                     len = byteValue.length;
                     type = 4;
                  } else {
                     charValue = (char[])text;
                     len = charValue.length;
                     type = 3;
                  }
               } else {
                  stringBufferValue = (StringBuffer)text;
                  len = stringBufferValue.length();
                  type = 2;
               }
            } else {
               IAException = (String)text;
               len = IAException.length();
               type = 1;
            }

            if (offset < 0 || offset > len) {
               throw new IllegalArgumentException();
            }

            if (length == -1) {
               length = len - offset;
            }

            if (length < 0 || offset + length > len) {
               throw new IllegalArgumentException();
            }

            if (type == 1) {
               this._text = IAException.substring(offset, offset + length);
            } else {
               switch (type) {
                  case 1:
                     break;
                  case 2:
                  default:
                     this._text = stringBufferValue.toString().substring(offset, offset + length);
                     break;
                  case 4:
                     charValue = new char[length];

                     for (int i = 0; i < length; i++) {
                        charValue[i] = (char)(byteValue[i + offset] & 0xFF);
                     }

                     offset = 0;
                  case 3:
                     this._text = new String(charValue, offset, length);
               }
            }

            String temp = this.getText();
            if (temp != null && !temp.equals(this._text)) {
               this._text = temp;
            }

            if (this._text == null) {
               this._text = "";
            } else if (this.isStyle(268435456)) {
               this._text = StringUtilities.removeChars(this._text, "̲");
            }

            this._length = this._text.length();
            this._labelText.setText(this._text);
            if (!this._inLayout) {
               Manager manager = this.getManager();
               if (manager != null && (!this.isStyle(1152921504606846976L) || !this._isSingleLine)) {
                  this.updateLayout();
                  this.focusAdd(false);
               }

               this.invalidate();
            }

            this.fieldChangeNotify(Integer.MIN_VALUE);
            var15 = false;
         } catch (IllegalArgumentException IAException) {
            throw IAException;
         } finally {
            if (var15) {
               this._inSetTextInternal = false;
            }
         }

         this._inSetTextInternal = false;
      }
   }

   @Override
   public String toString() {
      return this.getText();
   }

   @Override
   public int getAccessibleRole() {
      return 16;
   }

   @Override
   public String getAccessibleName() {
      return this.toString();
   }
}
