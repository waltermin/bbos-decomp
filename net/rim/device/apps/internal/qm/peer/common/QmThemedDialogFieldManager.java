package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.ui.component.ImageField;

public final class QmThemedDialogFieldManager extends Manager {
   private Field _icon;
   private VerticalFieldManager _vfmLabel;
   private RichTextField _label;
   private Manager _fm;
   private Manager _middleManager;
   private QmThemedDialogFieldManager$ButtonManager _buttonManager;
   private Manager _bottomManager;
   private QmThemedDialogFieldManager$FocusNullField _focusNullField;
   private static final Tag TAG_ICON = Tag.create("dialog-icon");
   private static final int PADDING = 4;
   private static final int FRACTION_RESERVED_FOR_BUTTONS = 3;

   public QmThemedDialogFieldManager() {
      this(0);
   }

   public QmThemedDialogFieldManager(long style) {
      this(style, 1153220571769602048L);
   }

   public QmThemedDialogFieldManager(long style, long messageStyle) {
      super(style);
      this._vfmLabel = (VerticalFieldManager)(new Object(messageStyle));
      this._vfmLabel.setNonfocusableOverride(true);
      this.add(this._vfmLabel);
      this._fm = (Manager)(new Object(299067162755072L));
      this.add(this._fm);
      this._middleManager = (Manager)(new Object());
      this._fm.add(this._middleManager);
      this._buttonManager = new QmThemedDialogFieldManager$ButtonManager();
      this._fm.add(this._buttonManager);
      this._bottomManager = (Manager)(new Object());
      this._fm.add(this._bottomManager);
      this._focusNullField = new QmThemedDialogFieldManager$FocusNullField(null);
      this.add(this._focusNullField);
   }

   public final void addButtonField(ButtonField field) {
      this._buttonManager.add(field);
      QmThemedDialogFieldManager$FocusNullField.access$102(
         this._focusNullField, this._middleManager.getFieldCount() + this._buttonManager.getFieldCount() + this._bottomManager.getFieldCount() == 0
      );
   }

   public final void addCustomField(Field f) {
      Manager manager = this.getCustomManager();
      manager.add(f);
      QmThemedDialogFieldManager$FocusNullField.access$102(
         this._focusNullField, this._middleManager.getFieldCount() + this._buttonManager.getFieldCount() + this._bottomManager.getFieldCount() == 0
      );
   }

   @Override
   public final int getAccessibleChildCount() {
      return this._middleManager.getFieldCount() + this._buttonManager.getFieldCount() + this._bottomManager.getFieldCount();
   }

   @Override
   public final int getAccessibleSelectionCount() {
      return 1;
   }

   public final Field getButtonField(int index) {
      return this.getButtonManager().getField(index);
   }

   public final Manager getButtonManager() {
      return this._buttonManager;
   }

   public final Field getCustomField(int index) {
      return this.getCustomManager().getField(index);
   }

   public final Manager getCustomManager() {
      return this._middleManager;
   }

   public final boolean hasButtons() {
      return this._buttonManager.getFieldCount() != 0;
   }

   public final boolean hasCustomFields() {
      return this._middleManager.getFieldCount() > 0;
   }

   @Override
   public final boolean isAccessibleChildSelected(int index) {
      if (index < this._middleManager.getFieldCount()) {
         return this._middleManager.getFieldWithFocusIndex() == index;
      } else {
         return index < this._middleManager.getFieldCount() + this._buttonManager.getFieldCount()
            ? this._buttonManager.getFieldWithFocusIndex() == index - this._middleManager.getFieldCount()
            : this._bottomManager.getFieldWithFocusIndex() == index - this._middleManager.getFieldCount() - this._buttonManager.getFieldCount();
      }
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      int result = super.moveFocus(amount, status, time);
      if (result != 0 && this._label != null) {
         int scroll = this._vfmLabel.getVerticalScroll();
         scroll += this._label.getFont().getHeight() * amount;
         scroll = MathUtilities.clamp(0, scroll, Math.max(0, this._vfmLabel.getVirtualHeight() - this._vfmLabel.getContentHeight()));
         this._vfmLabel.setVerticalScroll(scroll);
      }

      return result;
   }

   @Override
   protected final void paintBackground(Graphics graphics) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   public final void setIcon(BitmapField icon) {
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

   public final void setIcon(ImageField icon) {
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

   public final void setMessage(RichTextField label) {
      if (this._label != null) {
         this._vfmLabel.delete(this._label);
      }

      this._label = label;
      if (label != null) {
         this._vfmLabel.add(label);
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      int padding = 4;
      int screenHeight = height;
      int labelX = padding;
      int availLabelWidth = width - padding * 2;
      if (this.isStyle(281474976710656L)) {
         height = 1073741823;
      }

      if (this._icon != null) {
         labelX += this._icon.getPreferredWidth() + padding;
         availLabelWidth -= this._icon.getPreferredWidth() + padding;
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
         if (!(first instanceof Object)) {
            minButtonHeight = this.getPreferredHeightOfChild(first);
         } else {
            minButtonHeight = ((ListField)first).getRowHeight();
         }
      }

      if (minButtonHeight <= 0) {
         minButtonHeight = Math.max(minButtonHeight, (height - 3 * padding) / 3);
      }

      Bitmap up = null;
      Bitmap down = null;
      if (this._label != null) {
         this._vfmLabel.setPadding(0, 0, 0, 0);
         this.layoutChild(this._vfmLabel, availLabelWidth, height - minButtonHeight - padding * 3);
         int virtHeight = this._vfmLabel.getVirtualHeight();
         iconAndLabelHeight = Math.max(iconAndLabelHeight, this._vfmLabel.getHeight());
         labelWidth = this._vfmLabel.getWidth();
         this.setPositionChild(this._vfmLabel, labelX, (iconAndLabelHeight - this._vfmLabel.getHeight() >> 1) + padding);
      }

      this.setPositionChild(this._focusNullField, 0, (iconAndLabelHeight - this._vfmLabel.getHeight() >> 1) + padding + 1);
      int vfmHeight = 0;
      int quanta = 1;
      if (this._middleManager.getFieldCount() == 0 && this._buttonManager.getFieldCount() != 0 && this._bottomManager.getFieldCount() == 0) {
         quanta = this._buttonManager.getPreferredButtonHeight();
      }

      this._fm.setVerticalQuantization(quanta);
      this.layoutChild(this._fm, width - padding * 2, Math.max(height - iconAndLabelHeight - padding * 3, 0));
      int childWidthDelta = width - padding * 2 - this._fm.getWidth();
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
            this.layoutChild(this._fm, width - padding * 2, Math.max(height - iconAndLabelHeight - padding * 3, 0));
         }
      } else {
         this._fm.setPadding(0, 0, 0, 0);
      }

      vfmHeight = this._fm.getHeight();
      QmThemedDialogFieldManager$FocusNullField.access$102(
         this._focusNullField, this._middleManager.getFieldCount() + this._buttonManager.getFieldCount() + this._bottomManager.getFieldCount() == 0
      );
      width = Math.max(labelWidth + iconWidth, this._fm.getWidth()) + padding * 2 + (iconWidth != 0 ? padding : 0);
      height = iconAndLabelHeight + vfmHeight + 2 * padding + (vfmHeight != 0 ? padding : 0);
      if (this._icon != null) {
         int iconY = iconAndLabelHeight - iconHeight >> 1;
         this.setPositionChild(this._icon, padding, padding + iconY);
         this.layoutChild(this._icon, this._icon.getPreferredWidth(), iconHeight);
      }

      this.setPositionChild(this._fm, (width - padding * 2 - this._fm.getWidth() >> 1) + padding, padding * 2 + iconAndLabelHeight);
      this.setVirtualExtent(width, height);
      this.setExtent(width, Math.min(screenHeight, height));
   }
}
