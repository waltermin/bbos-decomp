package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.device.api.util.Arrays;
import net.rim.plazmic.internal.mediaengine.service.ModelInteractor;
import org.xml.sax.Attributes;

final class FocusGroup {
   Object[] _items = new Object[0];
   boolean[] _disables;
   int _index;
   int _direction;
   FocusVector _vector = new FocusVector();
   static final int DIRECTION_OMNI = 0;
   static final int DIRECTION_HORIZONTAL = 1;
   static final int DIRECTION_VERTICAL = 2;

   FocusGroup(Attributes attributes) {
      String sDirection = attributes.getValue("direction");
      if (sDirection != null) {
         if (sDirection.startsWith("horiz")) {
            this._direction = 1;
            return;
         }

         if (sDirection.startsWith("vert")) {
            this._direction = 2;
         }
      }
   }

   final void addLeaf(ModelInteractor modelInteractor, Attributes attributes) {
      String id = attributes.getValue("id");
      if (id != null) {
         Arrays.add(this._items, id);
      }
   }

   final void addGroup(FocusGroup group) {
      Arrays.add(this._items, group);
   }

   final void resolveIds(ModelInteractor modelInteractor) {
      int count = this._items.length;

      for (int i = 0; i < count; i++) {
         Object item = this._items[i];
         if (!(item instanceof Object)) {
            if (item instanceof FocusGroup) {
               FocusGroup focusGroup = (FocusGroup)item;
               focusGroup.resolveIds(modelInteractor);
            }
         } else {
            String sId = (String)item;
            int id = modelInteractor.getHandle(sId);
            this._items[i] = new Object(id);
         }
      }
   }

   final int getCurrentFocus() {
      Object item = this._items[this._index];
      if (!(item instanceof Object)) {
         FocusGroup focusGroup = (FocusGroup)item;
         return focusGroup.getCurrentFocus();
      } else {
         Integer integer = (Integer)item;
         return integer;
      }
   }

   final boolean setFocus(int handle) {
      int count = this._items.length;

      for (int i = 0; i < count; i++) {
         Object item = this._items[i];
         if (item instanceof Object) {
            Integer integer = (Integer)item;
            if (handle == integer) {
               this._index = i;
               return true;
            }
         }

         if (item instanceof FocusGroup) {
            FocusGroup focusGroup = (FocusGroup)item;
            if (focusGroup.setFocus(handle)) {
               this._index = i;
               return true;
            }
         }
      }

      return false;
   }

   final void resetFocus() {
      this._index = 0;
      if (this._items.length > 0) {
         Object item = this._items[0];
         if (item instanceof FocusGroup) {
            FocusGroup focusGroup = (FocusGroup)item;
            focusGroup.resetFocus();
         }
      }
   }

   final void moveFocus(FocusVector vector) {
      while (vector.remaining()) {
         int i = this._index;
         Object item = this._items[i];
         if (item instanceof FocusGroup) {
            FocusGroup focusGroup = (FocusGroup)item;
            focusGroup.moveFocus(vector);
         }

         int delta = vector.delta(this._direction);
         if (delta == 0) {
            return;
         }

         if (delta > 0) {
            i++;
            if (this._disables != null) {
               while (i < this._disables.length && this._disables[i]) {
                  i++;
               }
            }

            if (i < this._items.length) {
               this._index = i;
            }
         } else if (delta < 0) {
            i--;
            if (this._disables != null) {
               while (i >= 0 && this._disables[i]) {
                  i--;
               }
            }

            if (i >= 0) {
               this._index = i;
            }
         }

         vector.decrement(this._direction, delta);
      }
   }
}
