package net.rim.device.api.ui.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldLabelProvider;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.text.TextRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.StringProvider;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.SystemIcon;

public class CheckboxField extends Field implements FieldLabelProvider {
   private boolean _checked;
   private String _label;
   private String _optionsMenuText;
   private Bitmap _image;
   private int _iconWidth;
   private int _iconHeight;
   private TextRect _text = new TextRect(this);
   private static Tag TAG = Tag.create("checkbox");
   private static final char CHECKED = '☑';
   private static final char UNCHECKED = '☐';
   public static final long NO_USE_ALL_WIDTH = 2147483648L;
   public static final long SELECT_ON_CLICK = 1073741824L;
   private static final int PADDING = 2;
   private static MenuItem _changeOptionsItem = new CheckboxField$ChangeOptionMenuItem();

   public boolean getChecked() {
      return this._checked;
   }

   public void setOptionsMenuText(String optionsMenuText) {
      this._optionsMenuText = optionsMenuText;
   }

   public void setChecked(boolean checked) {
      this._checked = checked;
      this.fieldChangeNotify(Integer.MIN_VALUE);
      this.invalidate();
   }

   public void setImage(Bitmap image) {
      this._image = image;
   }

   @Override
   public void setLabel(String label) {
      this._label = label;
      this._text.setText(label);
      this.updateLayout();
      this.invalidate();
   }

   @Override
   public void setLabelStringProvider(StringProvider label) {
      throw new IllegalStateException("Unsupported API");
   }

   @Override
   public String getLabel() {
      return this._label;
   }

   @Override
   protected void onFocus(int direction) {
      super.onFocus(direction);
      this.invalidate();
   }

   @Override
   protected void onUnfocus() {
      super.onUnfocus();
      this.invalidate();
   }

   @Override
   public void getFocusRect(XYRect rect) {
      int topAdjust = this.getTopAdjustment();
      rect.set(0, 0, this._iconWidth, topAdjust + this._iconHeight);
   }

   private int getTopAdjustment() {
      if (this._text.getLineCount() == 0) {
         return 0;
      }

      int topAdjust = this._text.getLineHeight(0) - this.getFont().getHeight();
      if (topAdjust < 0) {
         topAdjust = 0;
      }

      return topAdjust;
   }

   @Override
   public int getPreferredHeight() {
      int fontHeight = this.getFont().getHeight();
      int iconHeight = SystemIcon.COLLECTION.getHeight(fontHeight, fontHeight);
      return Math.max(fontHeight, iconHeight);
   }

   @Override
   public int getPreferredWidth() {
      Font font = this.getFont();
      int fontHeight = font.getHeight();
      int width = 0;
      if (this._image != null) {
         width = 2 + this._image.getWidth();
      }

      if (this._label != null) {
         width += 2 + font.getBounds(this._label, 0, this._label.length());
      }

      int iconWidth = SystemIcon.COLLECTION.getWidth(fontHeight, fontHeight);
      return width + iconWidth;
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            if (this.isEditable()) {
               this.toggle();
               return true;
            }
         default:
            return false;
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (this.isEditable() && key == ' ') {
         this.toggle();
         if (Ui.isTTSEnabled()) {
            super.accessibleEventOccurred(1, new Integer(2), new Integer(64), this);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      if (this.isEditable() && Keypad.key(keycode) == 71 && InternalServices.isReducedFormFactor() && (Keypad.status(keycode) & 1) == 0) {
         this.toggle();
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected void layout(int width, int height) {
      Font font = this.getFont();
      int fontHeight = font.getHeight();
      this._iconWidth = SystemIcon.COLLECTION.getWidth(fontHeight, fontHeight);
      this._iconHeight = Math.max(fontHeight, SystemIcon.COLLECTION.getHeight(fontHeight, fontHeight));
      height = this._iconHeight;
      int x_pos = this._iconWidth;
      int imageWidth = 0;
      if (this._image != null) {
         imageWidth = this._image.getWidth() + 2;
         height = Math.max(this._image.getHeight(), height);
      }

      this._text.setPosition(x_pos + imageWidth + 2, 0);
      this._text.layout(Math.max(width - x_pos - imageWidth - 2, 0), height);
      height = Math.max(this._text.getHeight(), height);
      if (!this.isStyle(1152921504606846976L)) {
         width = x_pos + imageWidth + 2;
         if (this._text.getText() != null) {
            XYRect rect = Ui.getTmpXYRect();
            this._text.getTextBounds(rect);
            width += rect.x + rect.width;
            Ui.returnTmpXYRect(rect);
         }
      }

      this.setExtent(width, height);
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (Ui.getMode() < 2 && this.isEditable()) {
         if (this._optionsMenuText == null) {
            contextMenu.addItem(_changeOptionsItem);
            return;
         }

         MenuItem item = new CheckboxField$ChangeOptionMenuItem(this._optionsMenuText);
         contextMenu.addItem(item);
      }
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      if (this.isEditable() && (status & 1) != 0 && (status & 196608) == 0) {
         this.toggle();
         return 0;
      } else {
         return amount;
      }
   }

   private int getIconIndex() {
      if (this._checked) {
         return this.isEditable() ? 1 : 8;
      } else {
         return this.isEditable() ? 0 : 7;
      }
   }

   @Override
   protected void paint(Graphics graphics) {
      int index = this.getIconIndex();
      if (this.isFocus() && SystemIcon.COLLECTION.containsIcon(this._iconHeight, this.getIconIndex() + 12)) {
         index += 12;
      }

      int y_adjust = this.getTopAdjustment();
      SystemIcon.COLLECTION.paint(graphics, 0, y_adjust, this._iconWidth, this._iconHeight, index);
      if (this._image != null) {
         graphics.drawBitmap(this._iconWidth, y_adjust, this._image.getWidth(), this._image.getHeight(), this._image, 0, 0);
      }

      this._text.paintSelf(graphics);
   }

   public CheckboxField(String label, boolean checked, long style) {
      super(verifyStyle(style));
      this.setTag(TAG);
      this.setLabel(label);
      this._checked = checked;
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      this._text.applyTheme();
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (!SystemIcon.COLLECTION.containsIcon(this._iconHeight, this.getIconIndex() + 12)) {
         super.drawFocus(graphics, on);
      }
   }

   public CheckboxField() {
      this(null, false, 0);
   }

   public CheckboxField(String label, boolean checked) {
      this(label, checked, 0);
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      if (!this.isEditable()) {
         return false;
      }

      if (x < 0) {
         return false;
      }

      if (y < 0) {
         return false;
      }

      if (x > this._iconWidth) {
         return false;
      }

      if (y > this._iconHeight) {
         return false;
      }

      this.toggle();
      return true;
   }

   private void toggle() {
      this._checked = !this._checked;
      this.fieldChangeNotify(0);
      this.invalidate();
   }

   @Override
   public String toString() {
      StringBuffer s = new StringBuffer();
      String[] str = CommonResource.getStringArray(10012);
      s.append('[').append(StringUtilities.removeChars(this._checked ? str[0] : str[1], "̲")).append(']');
      return s.toString();
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      if (Ui.isTTSEnabled()) {
         super.accessibleEventOccurred(1, new Integer(2), new Integer(64), this);
      }

      if (this.isStyle(1073741824) && this.isEditable()) {
         this.toggle();
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
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
   public int getAccessibleRole() {
      return 8;
   }

   @Override
   public String getAccessibleName() {
      return this.getLabel();
   }
}
