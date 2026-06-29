package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.HorizontalFieldManager;

public class CursorProviderHorizontalFieldManager extends HorizontalFieldManager implements CursorProviderField {
   private Field _currentCursorField;

   public CursorProviderHorizontalFieldManager() {
   }

   public CursorProviderHorizontalFieldManager(long style) {
      super(style);
   }

   @Override
   public int getCurrentCursorPosition(Object context) {
      return CursorProviderManagerUtilities.getCurrentCursorPosition(this, context);
   }

   @Override
   public void setCursorPosition(int pos, Object context) {
      this._currentCursorField = CursorProviderManagerUtilities.setCursorPosition(this, pos, context);
   }

   @Override
   public int getCursorCount(Object context) {
      return CursorProviderManagerUtilities.getCursorCount(this, context);
   }

   @Override
   public void setFocus() {
      if (this._currentCursorField != null) {
         this._currentCursorField.setFocus();
      } else {
         super.setFocus();
      }
   }

   @Override
   public void delete(Field field) {
      super.delete(field);
      this.checkCurrentCursorField();
   }

   @Override
   public void deleteRange(int start, int count) {
      super.deleteRange(start, count);
      this.checkCurrentCursorField();
   }

   private void checkCurrentCursorField() {
      if (this._currentCursorField != null && this._currentCursorField.getManager() != this) {
         this._currentCursorField = null;
      }
   }
}
