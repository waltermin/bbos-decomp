package net.rim.device.api.ui.container;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.ui.component.ImageField;

public class DialogFieldManager extends Manager {
   private Field _icon;
   private VerticalFieldManager _vfmLabel;
   private RichTextField _label;
   private Manager _fm;
   private Manager _middleManager;
   private DialogFieldManager$ButtonManager _buttonManager;
   private Manager _bottomManager;
   private DialogFieldManager$FocusNullField _focusNullField;
   private static final Tag TAG_ICON = Tag.create("dialog-icon");
   private static final int PADDING = 4;
   private static final int FRACTION_RESERVED_FOR_BUTTONS = 3;

   public DialogFieldManager() {
      this(0);
   }

   public DialogFieldManager(long style) {
      this(style, 299067162755072L);
   }

   public DialogFieldManager(long style, long messageStyle) {
      this(style, messageStyle, false);
   }

   public DialogFieldManager(long style, long messageStyle, boolean disableQuantization) {
      super(style);
      this._vfmLabel = new VerticalFieldManager(messageStyle);
      this._vfmLabel.setNonfocusableOverride(true);
      this.add(this._vfmLabel);
      if (disableQuantization) {
         this._fm = new DialogFieldManager$1(this, 299067162755072L);
      } else {
         this._fm = new VerticalFieldManager(299067162755072L);
      }

      this.add(this._fm);
      this._middleManager = new VerticalFieldManager();
      this._fm.add(this._middleManager);
      this._buttonManager = new DialogFieldManager$ButtonManager();
      this._fm.add(this._buttonManager);
      this._bottomManager = new VerticalFieldManager();
      this._fm.add(this._bottomManager);
      this._focusNullField = new DialogFieldManager$FocusNullField(null);
      this.add(this._focusNullField);
   }

   public void addButtonField(ButtonField field) {
      this._buttonManager.add(field);
      this._focusNullField._canFocus = this._middleManager.getFieldCount() + this._buttonManager.getFieldCount() + this._bottomManager.getFieldCount() == 0;
   }

   public void addCustomField(Field f) {
      Manager manager = this.getCustomManager();
      manager.add(f);
      this._focusNullField._canFocus = this._middleManager.getFieldCount() + this._buttonManager.getFieldCount() + this._bottomManager.getFieldCount() == 0;
   }

   public void deleteCustomField(Field field) {
      Manager manager = this.getCustomManager();
      if (manager.getFieldWithFocus() == field) {
      }

      manager.delete(field);
      this._focusNullField._canFocus = this._middleManager.getFieldCount() + this._buttonManager.getFieldCount() + this._bottomManager.getFieldCount() == 0;
   }

   public void deleteCustomFields() {
      Manager manager = this.getCustomManager();
      manager.deleteAll();
      this._focusNullField._canFocus = this._middleManager.getFieldCount() + this._buttonManager.getFieldCount() + this._bottomManager.getFieldCount() == 0;
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      if (index < this._middleManager.getFieldCount()) {
         return this.getCustomField(index);
      } else {
         return index < this._middleManager.getFieldCount() + this._buttonManager.getFieldCount()
            ? this.getButtonField(index - this._middleManager.getFieldCount())
            : this._bottomManager.getField(index - this._buttonManager.getFieldCount() - this._middleManager.getFieldCount());
      }
   }

   @Override
   public int getAccessibleChildCount() {
      return this._middleManager.getFieldCount() + this._buttonManager.getFieldCount() + this._bottomManager.getFieldCount();
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      if (index >= 0 && index <= this.getAccessibleSelectionCount()) {
         if (this._middleManager.getFieldWithFocusIndex() > 0) {
            index += this._middleManager.getFieldWithFocusIndex();
         }

         if (this._buttonManager.getFieldWithFocusIndex() > 0) {
            index += this._buttonManager.getFieldWithFocusIndex();
         }

         if (this._bottomManager.getFieldWithFocusIndex() > 0) {
            index += this._bottomManager.getFieldWithFocusIndex();
         }

         if (index < this._middleManager.getFieldCount()) {
            return this.getCustomField(index);
         } else {
            return index < this._middleManager.getFieldCount() + this._buttonManager.getFieldCount()
               ? this.getButtonField(index - this._middleManager.getFieldCount())
               : this._bottomManager.getField(index - this._buttonManager.getFieldCount() - this._middleManager.getFieldCount());
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public int getAccessibleSelectionCount() {
      return 1;
   }

   public Field getButtonField(int index) {
      return this.getButtonManager().getField(index);
   }

   public Manager getButtonManager() {
      return this._buttonManager;
   }

   public Field getCustomField(int index) {
      return this.getCustomManager().getField(index);
   }

   public Manager getCustomManager() {
      return this._middleManager;
   }

   public Manager getBottomManager() {
      return this._bottomManager;
   }

   public Manager getMiddleManager() {
      return this._middleManager;
   }

   public boolean hasButtons() {
      return this._buttonManager.getFieldCount() != 0;
   }

   public boolean hasCustomFields() {
      return this._middleManager.getFieldCount() > 0;
   }

   public boolean hasMiddleFields() {
      return this._middleManager.getFieldCount() > 0;
   }

   public void insertCustomField(Field f, int index) {
      Manager manager = this.getCustomManager();
      manager.insert(f, index);
      this._focusNullField._canFocus = this._middleManager.getFieldCount() + this._buttonManager.getFieldCount() + this._bottomManager.getFieldCount() == 0;
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      if (index < this._middleManager.getFieldCount()) {
         return this._middleManager.getFieldWithFocusIndex() == index;
      } else {
         return index < this._middleManager.getFieldCount() + this._buttonManager.getFieldCount()
            ? this._buttonManager.getFieldWithFocusIndex() == index - this._middleManager.getFieldCount()
            : this._bottomManager.getFieldWithFocusIndex() == index - this._middleManager.getFieldCount() - this._buttonManager.getFieldCount();
      }
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      int result = super.moveFocus(amount, status, time);
      if (result != 0 && this._label != null) {
         int scroll = this._vfmLabel.getVerticalScroll();
         scroll += this._label.getFont().getHeight() * amount;
         scroll = MathUtilities.clamp(0, scroll, Math.max(0, this._vfmLabel.getVirtualHeight() - this._vfmLabel.getContentHeight()));
         this._vfmLabel.setVerticalScroll(scroll);
      }

      return result;
   }

   public void setIcon(BitmapField icon) {
      if (this._icon != null) {
         int index = this._icon.getIndex();
         this._icon = null;
         this.deleteRange(index, 1);
      }

      if (icon != null) {
         icon.setTag(TAG_ICON);
         this._icon = icon;
         this.add(icon);
      }
   }

   public void setIcon(ImageField icon) {
      if (this._icon != null) {
         int index = this._icon.getIndex();
         this._icon = null;
         this.deleteRange(index, 1);
      }

      if (icon != null) {
         icon.setTag(TAG_ICON);
         icon.setPreferredSize(Display.getWidth() >> 2, Display.getHeight() >> 2);
         this._icon = icon;
         this.add(icon);
      }
   }

   public void setMessage(RichTextField label) {
      if (this._label != null) {
         this._vfmLabel.delete(this._label);
      }

      this._label = label;
      if (label != null) {
         this._vfmLabel.add(label);
      }
   }

   @Override
   protected void sublayout(int width, int height) {
      int screenHeight = height;
      int labelX = 4;
      int availLabelWidth = width - 8;
      if (this.isStyle(281474976710656L)) {
         height = 1073741823;
      }

      if (this._icon != null) {
         labelX += this._icon.getPreferredWidth() + 4;
         availLabelWidth -= this._icon.getPreferredWidth() + 4;
      }

      int iconWidth = this._icon != null ? this._icon.getPreferredWidth() : 0;
      int iconHeight = this._icon != null ? this._icon.getPreferredHeight() : 0;
      int iconAndLabelHeight = iconHeight;
      int labelWidth = 0;
      int minButtonHeight = 0;
      if (this._buttonManager.getFieldCount() != 0) {
         minButtonHeight = this.getPreferredHeightOfChild(this._buttonManager.getField(0));
      } else if (this._middleManager.getFieldCount() != 0) {
         Field first = this._middleManager.getField(0);
         if (!(first instanceof ListField)) {
            minButtonHeight = this.getPreferredHeightOfChild(first);
         } else {
            minButtonHeight = ((ListField)first).getRowHeight();
         }
      }

      if (minButtonHeight <= 0) {
         minButtonHeight = Math.max(minButtonHeight, (height - 12) / 3);
      }

      Bitmap up = null;
      Bitmap down = null;
      if (this._label != null) {
         this._vfmLabel.setPadding(0, 0, 0, 0);
         this.layoutChild(this._vfmLabel, availLabelWidth, height - minButtonHeight - 12);
         int virtHeight = this._vfmLabel.getVirtualHeight();
         iconAndLabelHeight = Math.max(iconAndLabelHeight, this._vfmLabel.getHeight());
         labelWidth = this._vfmLabel.getWidth();
         this.setPositionChild(this._vfmLabel, labelX, (iconAndLabelHeight - this._vfmLabel.getHeight() >> 1) + 4);
      }

      this.setPositionChild(this._focusNullField, 0, (iconAndLabelHeight - this._vfmLabel.getHeight() >> 1) + 4 + 1);
      int vfmHeight = 0;
      int quanta = 1;
      if (this._middleManager.getFieldCount() == 0 && this._buttonManager.getFieldCount() != 0 && this._bottomManager.getFieldCount() == 0) {
         quanta = this._buttonManager.getPreferredButtonHeight();
      }

      this._fm.setVerticalQuantization(quanta);
      this.layoutChild(this._fm, width - 8, Math.max(height - iconAndLabelHeight - 12, 0));
      int childWidthDelta = width - 8 - this._fm.getWidth();
      if (this._fm.getContentHeight() < this._fm.getVirtualHeight() && 0 < childWidthDelta) {
         if (up == null) {
            up = Theme.getThemeBitmap(0);
         }

         if (down == null) {
            down = Theme.getThemeBitmap(1);
         }

         if (null != up && null != down) {
            int childPadding = Math.min(childWidthDelta >> 1, up.getWidth());
            this._fm.setPadding(0, childPadding, 0, childPadding);
            this.layoutChild(this._fm, width - 8, Math.max(height - iconAndLabelHeight - 12, 0));
         }
      } else {
         this._fm.setPadding(0, 0, 0, 0);
      }

      vfmHeight = this._fm.getHeight();
      this._focusNullField._canFocus = this._middleManager.getFieldCount() + this._buttonManager.getFieldCount() + this._bottomManager.getFieldCount() == 0;
      width = Math.max(labelWidth + iconWidth, this._fm.getWidth()) + 8 + (iconWidth != 0 ? 4 : 0);
      height = iconAndLabelHeight + vfmHeight + 8 + (vfmHeight != 0 ? 4 : 0);
      if (this._icon != null) {
         int iconY = iconAndLabelHeight - iconHeight >> 1;
         this.setPositionChild(this._icon, 4, 4 + iconY);
         this.layoutChild(this._icon, this._icon.getPreferredWidth(), iconHeight);
      }

      this.setPositionChild(this._fm, (width - 8 - this._fm.getWidth() >> 1) + 4, 8 + iconAndLabelHeight);
      this.setVirtualExtent(width, height);
      this.setExtent(width, Math.min(screenHeight, height));
   }
}
