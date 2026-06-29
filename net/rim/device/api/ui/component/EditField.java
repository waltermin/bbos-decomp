package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Keypad;

public class EditField extends BasicEditField {
   private int _lastKeyPressed;
   private int _rollerCharacterIndex = -1;

   public EditField() {
      this(null, null, 1000000, validateStyle(0));
   }

   public EditField(long style) {
      this(null, null, 1000000, validateStyle(style));
   }

   public EditField(String label, String initialValue) {
      this(label, initialValue, 1000000, validateStyle(0));
   }

   public EditField(String label, String initialValue, int maxNumChars, long style) {
      super(label, initialValue, maxNumChars, validateStyle(style));
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      int status = Keypad.status(keycode);
      int newKey = Character.toUpperCase(Keypad.map(key, status));
      this.setLastKeyPressed(newKey);
      this._rollerCharacterIndex = -1;
      return super.keyDown(keycode, time);
   }

   @Override
   protected boolean keyRepeat(int keycode, int time) {
      return !this.isEditable() ? false : super.keyRepeat(keycode, time);
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      return super.moveFocus(amount, status, time);
   }

   protected boolean isEnteringRollerCharacter() {
      return this._rollerCharacterIndex != -1;
   }

   private static long validateStyle(long style) {
      if ((style & 3298534883328L) == 0) {
         style |= 1099511627776L;
      }

      return style;
   }

   protected int getLastKeyPressed() {
      return this._lastKeyPressed;
   }

   protected void setLastKeyPressed(int key) {
      this._lastKeyPressed = key;
   }
}
