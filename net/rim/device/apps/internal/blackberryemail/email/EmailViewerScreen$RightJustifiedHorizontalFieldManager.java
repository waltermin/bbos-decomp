package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

class EmailViewerScreen$RightJustifiedHorizontalFieldManager extends HorizontalFieldManager {
   private VerticalFieldManager _contentField = new EmailViewerScreen$RightJustifiedHorizontalFieldManager$1(this);
   private Field _rightJustifiedField;
   private Manager _footerManager = (Manager)(new Object());
   private Field _verticalAlignmentField;

   public EmailViewerScreen$RightJustifiedHorizontalFieldManager() {
      super(1152921504606846976L);
      this._contentField.add(this._footerManager);
      super.add(this._contentField);
   }

   @Override
   public void add(Field field) {
      this._contentField.insert(field, this._footerManager.getIndex());
   }

   @Override
   public void delete(Field field) {
      this._contentField.delete(field);
   }

   @Override
   public void replace(Field oldField, Field newField) {
      this._contentField.replace(oldField, newField);
   }

   public void setRightJustifiedField(Field field) {
      if (field == null) {
         if (this._rightJustifiedField != null) {
            this.delete(this._rightJustifiedField);
         }

         this._rightJustifiedField = null;
      } else {
         if (this._rightJustifiedField != null) {
            super.replace(this._rightJustifiedField, field);
         } else {
            super.add(field);
         }

         this._rightJustifiedField = field;
      }
   }

   public Field getRightJustifiedField() {
      return this._rightJustifiedField;
   }

   public Manager getFooterManager() {
      return this._footerManager;
   }

   public void setVerticalAlignmentField(Field field) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected void sublayout(int maxWidth, int maxHeight) {
      super.sublayout(maxWidth, maxHeight);
      if (this._verticalAlignmentField != null && this._rightJustifiedField != null) {
         XYRect verticalExtent = this._verticalAlignmentField.getExtent();
         XYRect rightJustifiedExtent = this._rightJustifiedField.getExtent();
         this.setPositionChild(this._rightJustifiedField, rightJustifiedExtent.x, verticalExtent.y);
         XYRect myExtent = this.getExtent();
         if (verticalExtent.y + rightJustifiedExtent.height > myExtent.height) {
            this.setExtent(myExtent.width, verticalExtent.y + rightJustifiedExtent.height);
         }
      }
   }
}
