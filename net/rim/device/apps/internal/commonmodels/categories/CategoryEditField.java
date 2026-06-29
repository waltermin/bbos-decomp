package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.ui.component.EditField;

final class CategoryEditField extends EditField {
   private char _lastAutoCapChar;
   private int _lastAutoCapLocation = -1;
   private int _lastAutoCapUndoLocation = -1;

   CategoryEditField(String label) {
      super(label, null);
      this.setFilter(new CategoryEditField$CategoryTextFilter());
   }

   @Override
   protected final boolean backspace() {
      int characterLocation = this.getCursorPosition() - 1;
      if (characterLocation == this._lastAutoCapLocation) {
         this._lastAutoCapUndoLocation = characterLocation;
         this._lastAutoCapLocation = -1;
      } else if (characterLocation == this._lastAutoCapUndoLocation) {
         this._lastAutoCapUndoLocation = -1;
      }

      return super.backspace();
   }

   @Override
   protected final boolean insert(char key, int status) {
      if (!this.validate(key)) {
         return false;
      }

      if (key == ' ' && this.isCursorAtBeginning()) {
         return false;
      }

      if (Character.isLowerCase(key)) {
         key = this.handleLowerCaseCharacter(key);
      }

      return super.insert(key, status);
   }

   private final char handleLowerCaseCharacter(char character) {
      int cursorPosition = this.getCursorPosition();
      if (cursorPosition == this._lastAutoCapUndoLocation && this._lastAutoCapChar == character) {
         return character;
      } else if (this.isAutoCapLocation()) {
         this._lastAutoCapChar = character;
         this._lastAutoCapLocation = cursorPosition;
         this._lastAutoCapUndoLocation = -1;
         return Character.toUpperCase(character);
      } else {
         return character;
      }
   }

   private final boolean isAutoCapLocation() {
      return this.isCursorAtBeginning() ? true : this.getTextAbstractString().charAt(this.getCursorPosition() - 1) == ' ';
   }

   private final boolean isCursorAtBeginning() {
      return this.getCursorPosition() == this.getLabelLength();
   }
}
