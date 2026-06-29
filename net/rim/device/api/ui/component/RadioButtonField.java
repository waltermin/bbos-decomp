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
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.SystemIcon;

public class RadioButtonField extends Field implements FieldLabelProvider {
   private String _label;
   RadioButtonGroup _group;
   int _index = -1;
   private String _optionsMenuText;
   private Bitmap _image;
   private int _iconWidth;
   private int _iconHeight;
   private TextRect _text = new TextRect(this);
   private XYRect _focus = new XYRect();
   private static Tag TAG = Tag.create("radiobutton");
   private static final int PADDING;
   public static final long NO_USE_ALL_WIDTH;
   private static MenuItem _changeOptionsItem = new RadioButtonField$ChangeOptionMenuItem();

   public void setSelected(boolean selected) {
      this.setSelected(selected, Integer.MIN_VALUE);
   }

   public RadioButtonGroup getGroup() {
      return this._group;
   }

   public boolean isSelected() {
      try {
         return this._group.getSelectedIndex() == this._index;
      } catch (NullPointerException npe) {
         throw new IllegalStateException();
      }
   }

   void selectionChange(int context) {
      this.invalidate();
      this.fieldChangeNotify(context);
   }

   public void setImage(Bitmap image) {
      this._image = image;
   }

   public void setOptionsMenuText(String optionsMenuText) {
      this._optionsMenuText = optionsMenuText;
   }

   @Override
   public void setLabelStringProvider(StringProvider label) {
      throw new IllegalStateException("Unsupported API");
   }

   @Override
   public void setLabel(String label) {
      this._label = label;
      this._text.setText(label);
      this.updateLayout();
      this.invalidate();
   }

   @Override
   public String getLabel() {
      return this._label;
   }

   @Override
   public void getFocusRect(XYRect rect) {
      rect.set(0, 0, this._iconWidth, this._iconHeight);
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

   public RadioButtonField(String label) {
      this(label, null, false, 0);
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this.setSelected(true, 0);
            return true;
         default:
            return false;
      }
   }

   public RadioButtonField(String label, RadioButtonGroup group, boolean selected) {
      this(label, group, selected, 0);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean result = false;
      if (this.isEditable()) {
         switch (key) {
            case '\n':
            case ' ':
               this.setSelected(true, 0);
               result = true;
               if (Ui.isTTSEnabled()) {
                  super.accessibleEventOccurred(1, new Integer(2), new Integer(4), this);
               }
         }
      }

      return result;
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      if (this.isEditable() && Keypad.key(keycode) == 71 && InternalServices.isReducedFormFactor()) {
         this.setSelected(true, 0);
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected void layout(int width, int height) {
      if (this._group == null) {
         throw new IllegalStateException("Displayed RadioButton is not a member of a group.");
      }

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

      if (this.isStyle(2147483648L)) {
         width = x_pos + imageWidth + 2;
         if (this._text.getText() != null) {
            width += font.getBounds(this._label, 0, this._label.length());
         }
      }

      this._text.setPosition(x_pos + imageWidth + 2, 0);
      this._text.layout(Math.max(width - x_pos - imageWidth - 2, 0), height);
      height = Math.max(this._text.getHeight(), height);
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

         MenuItem item = new RadioButtonField$ChangeOptionMenuItem(this._optionsMenuText);
         contextMenu.addItem(item);
      }
   }

   private int getIconIndex() {
      if (this.isSelected()) {
         return this.isEditable() ? 3 : 11;
      } else {
         return this.isEditable() ? 2 : 10;
      }
   }

   @Override
   protected void paint(Graphics graphics) {
      int index = this.getIconIndex();
      if (this.isFocus() && SystemIcon.COLLECTION.containsIcon(this._iconHeight, this.getIconIndex() + 12)) {
         index += 12;
      }

      int x_pos = this._iconWidth;
      int imgHeight = 0;
      SystemIcon.COLLECTION.paint(graphics, 0, 0, this._iconWidth, this._iconHeight, index);
      if (this._image != null) {
         imgHeight = this._image.getHeight();
         graphics.drawBitmap(x_pos, 0, this._image.getWidth(), imgHeight, this._image, 0, 0);
      }

      this._text.paintSelf(graphics);
   }

   public RadioButtonField(String label, RadioButtonGroup group, boolean selected, long style) {
      super(verifyStyle(style));
      this.setTag(TAG);
      this.setLabel(label);
      if (group != null) {
         group.add(this);
         if (selected) {
            group.setSelectedIndex(this._index);
         }
      }
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
   }

   @Override
   protected void onUnfocus() {
      super.onUnfocus();
      this.invalidate();
   }

   @Override
   protected void onFocus(int direction) {
      super.onFocus(direction);
      this.invalidate();
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (!SystemIcon.COLLECTION.containsIcon(this._iconHeight, this.getIconIndex() + 12)) {
         this.getFocusRect(this._focus);
         graphics.invert(this._focus);
         int fontheight = this.getFont().getHeight();
         graphics.invert(this._focus.x, this._focus.y + fontheight, this._focus.width - 1, this._focus.height - fontheight);
      }
   }

   public RadioButtonField() {
      this(null, null, false, 0);
   }

   private void setSelected(boolean selected, int context) {
      if (this._group == null) {
         throw new IllegalStateException();
      }

      if (selected) {
         this._group.setSelectedIndex(this._index, context);
      } else if (this.isSelected()) {
         this._group.setSelectedIndex(-1, context);
      }

      this.invalidate();
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      if (Ui.isTTSEnabled()) {
         super.accessibleEventOccurred(1, new Integer(2), new Integer(4), this);
      }

      return super.trackwheelClick(status, time);
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
      return 11;
   }

   @Override
   public String getAccessibleName() {
      return this.getLabel();
   }
}
