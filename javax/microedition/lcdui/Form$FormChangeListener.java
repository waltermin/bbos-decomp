package javax.microedition.lcdui;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

class Form$FormChangeListener implements FieldChangeListener {
   public ItemStateListener _itemStateListener;

   @Override
   public void fieldChanged(Field field, int context) {
      if ((context & -2147483648) == 0 && this._itemStateListener != null) {
         Object cookie = field.getCookie();
         if (cookie instanceof Vector) {
            Vector vector = (Vector)cookie;
            cookie = vector.firstElement();
         }

         if (cookie instanceof Item) {
            Item item = (Item)cookie;
            this._itemStateListener.itemStateChanged(item);
         }
      }
   }
}
