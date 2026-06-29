package net.rim.device.apps.api.utility.editor;

import java.util.Enumeration;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.FieldProvider;

public class EditorUsingRIMModelFactory$EditorFieldEnumerator implements Enumeration {
   private int _index;
   private Manager _manager;
   private final EditorUsingRIMModelFactory this$0;

   EditorUsingRIMModelFactory$EditorFieldEnumerator(EditorUsingRIMModelFactory _1) {
      this.this$0 = _1;
      this._index = 0;
      this._manager = _1.getMainManager();
   }

   @Override
   public boolean hasMoreElements() {
      return this.getNextElement(false) != null;
   }

   private Object getNextElement(boolean proceedToNextElement) {
      while (true) {
         if (this._index >= this._manager.getFieldCount()) {
            if (this._manager == this.this$0.getMainManager()) {
               return null;
            }

            this._index = this._manager.getIndex() + 1;
            this._manager = this._manager.getManager();
         } else {
            Field field = this._manager.getField(this._index);
            if (proceedToNextElement) {
               this._index++;
            }

            if (field.getCookie() instanceof FieldProvider) {
               return field;
            }

            if (field instanceof Object) {
               this._manager = (Manager)field;
               this._index = 0;
            } else if (!proceedToNextElement) {
               this._index++;
            }
         }
      }
   }

   @Override
   public Object nextElement() {
      Object element = this.getNextElement(true);
      if (element == null) {
         throw new Object();
      } else {
         return element;
      }
   }
}
