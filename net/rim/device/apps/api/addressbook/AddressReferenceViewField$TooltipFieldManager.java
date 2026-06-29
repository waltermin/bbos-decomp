package net.rim.device.apps.api.addressbook;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

class AddressReferenceViewField$TooltipFieldManager extends HorizontalFieldManager {
   private LabelField _nameField;
   private LabelField _addressField;
   private Manager _labelManager;
   private Field _photoField;
   private XYPoint _preferredPosition = (XYPoint)(new Object());
   private Field _parentField;
   private int _maxWidth;
   private boolean _rightJustified;
   private boolean _focusRectAlignment;
   public static final long FLAG_RIGHT_JUSTIFIED;
   public static final long FLAG_FOCUS_RECT_ALIGNMENT;

   public AddressReferenceViewField$TooltipFieldManager(Field parentField, long flags) {
      super(0);
      this._parentField = parentField;
      this._rightJustified = (flags & 1) != 0;
      this._focusRectAlignment = (flags & 2) != 0;
      this._labelManager = (Manager)(new Object());
      this.add(this._labelManager);
      this.setTag(AddressReferenceViewField.EMAIL_ADDRESS_TOOLTIP_TAG);
   }

   public void setName(String text) {
      if (text != null) {
         if (this._nameField == null) {
            this._nameField = new AddressReferenceViewField$TooltipFieldManager$HeightRestrictedLabelField();
            this._nameField.setTag(AddressReferenceViewField.EMAIL_ADDRESS_TOOLTIP_NAME_TEXT_TAG);
            this._labelManager.insert(this._nameField, 0);
         }

         this._nameField.setText(text);
      } else {
         if (this._nameField != null) {
            this._labelManager.delete(this._nameField);
            this._nameField = null;
         }
      }
   }

   public void setAddress(String text) {
      if (text != null) {
         if (this._addressField == null) {
            this._addressField = new AddressReferenceViewField$TooltipFieldManager$HeightRestrictedLabelField();
            this._addressField.setTag(AddressReferenceViewField.EMAIL_ADDRESS_TOOLTIP_ADDRESS_TEXT_TAG);
            this._labelManager.add(this._addressField);
         }

         this._addressField.setText(text);
      } else {
         if (this._addressField != null) {
            this._labelManager.delete(this._addressField);
            this._addressField = null;
         }
      }
   }

   public void setPhotoField(Field field) {
      if (this._photoField != null) {
         this.delete(this._photoField);
      }

      this._photoField = field;
      if (this._photoField != null) {
         this.insert(this._photoField, 0);
      }
   }

   @Override
   protected void sublayout(int width, int height) {
      if (0 < this._maxWidth && this._maxWidth < width) {
         width = this._maxWidth;
      }

      super.sublayout(width, height);
   }

   public XYPoint getTooltipPosition() {
      this.applyTheme();
      int displayWidth = Display.getWidth();
      int displayHeight = Display.getHeight();
      XYRect parentRect = Ui.getTmpXYRect();
      if (this._focusRectAlignment) {
         this._parentField.getFocusRect(parentRect);
         this._parentField.transformToScreen(parentRect);
      } else {
         this._parentField.getExtent(parentRect);
         this._parentField.getManager().transformToScreen(parentRect);
      }

      this._preferredPosition.x = Math.max(parentRect.x - this.getMarginLeft(), 0);
      this._preferredPosition.y = parentRect.Y2();
      int vSpace = (this.getPaddingLeft() << 1) + (this.getBorderLeft() << 1) + (this.getMarginLeft() << 1);
      int hSpace = (this.getPaddingTop() << 1) + (this.getBorderTop() << 1) + (this.getMarginTop() << 1);
      this._maxWidth = displayWidth - vSpace;
      if (!this._rightJustified) {
         this._maxWidth = this._maxWidth - this._preferredPosition.x;
      }

      this.layout(this._maxWidth, displayHeight);
      int tooltipHeight = this.getContentHeight() + hSpace;
      int tooltipWidth = this.getContentWidth() + vSpace;
      if (this._preferredPosition.y + tooltipHeight > displayHeight) {
         this._preferredPosition.y = parentRect.y - tooltipHeight;
         if (this._preferredPosition.y < 0) {
            if (this._addressField != null && this._nameField != null) {
               this.setName(null);
               return this.getTooltipPosition();
            }

            return null;
         }
      }

      if (this._preferredPosition.x + tooltipWidth > displayWidth) {
         this._preferredPosition.x = Math.max(0, displayWidth - tooltipWidth);
      }

      Ui.returnTmpXYRect(parentRect);
      return this._preferredPosition;
   }
}
