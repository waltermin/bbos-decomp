package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.ui.UiInternal;
import net.rim.tid.im.layout.SLKeyLayout;

public final class ToggleableField extends HorizontalFieldManager {
   private Field _friendlyField;
   private Field _qualifiedField;
   private boolean _friendlyVisible;

   public ToggleableField(Field friendlyField, Field qualifiedField) {
      super(0);
      this._friendlyField = friendlyField;
      this._qualifiedField = qualifiedField;
      if (this._friendlyField != null) {
         this.add(friendlyField);
      }

      this._friendlyVisible = true;
   }

   public final boolean isFriendlyVisible() {
      return this._friendlyVisible;
   }

   public final void toggleVisibleField() {
      Field oldField;
      Field newField;
      if (this._friendlyVisible) {
         oldField = this.getFriendlyField();
         newField = this.getQualifiedField();
      } else {
         oldField = this.getQualifiedField();
         newField = this.getFriendlyField();
      }

      this.delete(oldField);
      this.add(newField);
      newField.setFocus();
      this._friendlyVisible = !this._friendlyVisible;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      char keyToCheck = UiInternal.map(Keypad.getLayout().getOriginalKeyCode(key, SLKeyLayout.convertStatusToModifiers(status)), status);
      if (CharacterUtilities.toLowerCase(keyToCheck, 1701707776) == 'q') {
         this.toggleVisibleField();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   private final Field getFriendlyField() {
      return this._friendlyField;
   }

   private final Field getQualifiedField() {
      return this._qualifiedField;
   }
}
