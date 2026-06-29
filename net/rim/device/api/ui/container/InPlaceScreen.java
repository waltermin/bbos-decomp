package net.rim.device.api.ui.container;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.theme.Tag;

public class InPlaceScreen extends Screen {
   private Field _original;
   private Field _fake;
   private Manager _delegate;
   int _fontHeight;
   int _result;
   public static final long ALT_DISMISS;
   private static final Tag TAG = Tag.create("inplace");

   public InPlaceScreen(Field original, Field fake, long style) {
      super(new VerticalFieldManager(style), style);
      this.setTag(TAG);
      this._delegate = this.getDelegate();
      this._original = original;
      this._fake = fake;
      this._delegate.add(this._fake);
   }

   public boolean doModal() {
      Ui.getUiEngine().pushModalScreen(this);
      return this._result != -1;
   }

   public Field getOriginalField() {
      return this._original;
   }

   public Field getField() {
      return this._fake;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         this._result = 0;
         this.close();
         return true;
      } else if (key == 27) {
         this._result = -1;
         this.close();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected boolean keyStatus(int keycode, int time) {
      if (this.isStyle(1) && Keypad.key(keycode) == 257 && (Keypad.status(keycode) & 1) == 0) {
         this._result = 0;
         this.close();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      if (x > 0 && y > 0 && x < this.getWidth() && y < this.getHeight()) {
         this._result = 0;
      } else {
         this._result = -1;
      }

      this.close();
      return true;
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this._result = 0;
            this.close();
            return true;
         default:
            return false;
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      return this.invokeAction(1);
   }
}
