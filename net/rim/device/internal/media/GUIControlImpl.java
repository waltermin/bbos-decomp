package net.rim.device.internal.media;

import javax.microedition.lcdui.Item;
import javax.microedition.media.control.GUIControl;

public class GUIControlImpl implements GUIControl {
   Object _item;
   private boolean _displayModeInitialized;

   public Object getGUIObject() {
      return this._item;
   }

   @Override
   public Object initDisplayMode(int mode, Object arg) {
      if (this._displayModeInitialized) {
         throw new IllegalStateException();
      }

      if (mode != 0) {
         throw new IllegalArgumentException();
      }

      if (arg instanceof String && !((String)arg).equals("javax.microedition.lcdui.Item")) {
         throw new IllegalArgumentException();
      }

      this._displayModeInitialized = true;
      return this._item;
   }

   public GUIControlImpl(Item item) {
      this._item = item;
   }

   public GUIControlImpl(Object item) {
      this._item = item;
   }
}
