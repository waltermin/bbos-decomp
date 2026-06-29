package net.rim.device.apps.internal.ribbon.skin.svg;

import net.rim.device.api.util.Arrays;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.VisualNodeImpl;
import org.xml.sax.Attributes;

class FocusGroup {
   int _eventToTrigger;
   Object[] _items = new Object[0];
   FocusItem[] _directionalItems = new FocusItem[0];
   int _index;
   int _direction;
   boolean _wrap;
   boolean _useCustomFocusEvents;
   int[] _customEventMoveFocusNext;
   int[] _customEventMoveFocusPrevious;
   static final int DIRECTION_OMNI = 0;
   static final int DIRECTION_HORIZONTAL = 1;
   static final int DIRECTION_VERTICAL = 2;

   FocusGroup(Attributes attributes) {
      String sWrap = attributes.getValue("wrap");
      if (sWrap != null && sWrap.equals("true")) {
         this._wrap = true;
      }

      String sDirection = attributes.getValue("direction");
      if (sDirection != null) {
         if (sDirection.startsWith("horiz")) {
            this._direction = 1;
         } else if (sDirection.startsWith("vert")) {
            this._direction = 2;
         }
      }

      String customFocusEvents = attributes.getValue("focus-events");
      if (customFocusEvents != null && customFocusEvents.equals("true")) {
         this._useCustomFocusEvents = true;
      }
   }

   void addLeaf(ModelInteractorImpl modelInteractor, Attributes attributes) {
      String id = attributes.getValue("id");
      if (id != null) {
         Arrays.add(this._items, id);
         String left = attributes.getValue("left");
         String right = attributes.getValue("right");
         String up = attributes.getValue("up");
         String down = attributes.getValue("down");
         FocusItem fi = new FocusItem(id, left, right, up, down);
         Arrays.add(this._directionalItems, fi);
      }
   }

   void addGroup(FocusGroup group) {
      Arrays.add(this._items, group);
   }

   void resolveIds(ModelInteractorImpl modelInteractor) {
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

      this._customEventMoveFocusNext = new int[count];
      this._customEventMoveFocusPrevious = new int[count];

      for (int i = 0; i < count; i++) {
         this._customEventMoveFocusNext[i] = modelInteractor.getHandle(((StringBuffer)(new Object("moveFocusNext"))).append(i).toString());
         this._customEventMoveFocusPrevious[i] = modelInteractor.getHandle(((StringBuffer)(new Object("moveFocusPrevious"))).append(i).toString());
      }
   }

   int getCurrentFocus() {
      Object item = this._items[this._index];
      if (!(item instanceof Object)) {
         FocusGroup focusGroup = (FocusGroup)item;
         return focusGroup.getCurrentFocus();
      } else {
         Integer integer = (Integer)item;
         return integer;
      }
   }

   boolean setFocus(int handle) {
      int count = this._items.length;

      for (int i = 0; i < count; i++) {
         Object item = this._items[i];
         if (!(item instanceof Object)) {
            if (item instanceof FocusGroup) {
               FocusGroup focusGroup = (FocusGroup)item;
               if (focusGroup.setFocus(handle)) {
                  this._index = i;
                  return true;
               }
            }
         } else {
            Integer integer = (Integer)item;
            if (handle == integer) {
               this._index = i;
               return true;
            }
         }
      }

      return false;
   }

   void triggerCustomEvent(ModelInteractorImpl modelInteractor) {
      modelInteractor.trigger(107, this._eventToTrigger, null);
   }

   private boolean isFocusable(Object item, ModelInteractorImpl mi) {
      if (item instanceof Object) {
         int handle = item;
         return VisualNodeImpl.isResolvedDisplayable(handle, mi);
      }

      if (item instanceof FocusGroup) {
         FocusGroup fg = (FocusGroup)item;

         for (int i = 0; i < fg._items.length; i++) {
            if (this.isFocusable(fg._items[i], mi)) {
               return true;
            }
         }
      }

      return false;
   }

   private void moveFocusToIndex(int newIndex, ModelInteractorImpl mi) {
      this._index = newIndex;
      Object item = this._items[newIndex];
      if (item instanceof FocusGroup) {
         boolean done = false;
         FocusGroup fg = (FocusGroup)item;
         int fgIndex = fg._index;
         Object fgItem = fg._items[fgIndex];

         do {
            if (fg.isFocusable(fgItem, mi)) {
               fg.moveFocusToIndex(fgIndex, mi);
               done = true;
            } else {
               fgIndex = fgIndex > 0 ? fgIndex - 1 : fg._items.length - 1;
               fgItem = fg._items[fgIndex];
            }
         } while (!done && fgIndex != fg._index);
      }
   }

   void moveFocus(ModelInteractorImpl modelInteractor, FocusVector vector) {
      while (vector.remaining()) {
         Object item = this._items[this._index];
         if (item instanceof FocusGroup) {
            FocusGroup focusGroup = (FocusGroup)item;
            focusGroup.moveFocus(modelInteractor, vector);
            this._eventToTrigger = focusGroup._eventToTrigger;
         }

         int delta = vector.delta(this._direction);
         if (delta == 0) {
            return;
         }

         this._eventToTrigger = 0;
         int nextIndex = this._index;
         int lastValidIndex = this._index;
         int numMoves = 0;
         boolean next = delta > 0;
         boolean stop = false;

         while (delta != 0 && !stop) {
            int d;
            if (next) {
               d = 1;
               delta--;
            } else {
               d = -1;
               delta++;
            }

            int numItems = this._items.length;

            for (int i = 0; i < numItems; i++) {
               nextIndex += d;
               if (nextIndex < 0) {
                  if (!this._wrap) {
                     nextIndex = lastValidIndex;
                     stop = true;
                     break;
                  }

                  nextIndex = this._items.length - 1;
               } else if (nextIndex > this._items.length - 1) {
                  if (!this._wrap) {
                     nextIndex = lastValidIndex;
                     stop = true;
                     break;
                  }

                  nextIndex = 0;
               }

               if (this.isFocusable(this._items[nextIndex], modelInteractor)) {
                  lastValidIndex = nextIndex;
                  numMoves++;
                  break;
               }
            }
         }

         if (numMoves > 0) {
            this.moveFocusToIndex(nextIndex, modelInteractor);
            if (this._useCustomFocusEvents) {
               numMoves %= this._items.length;
               int customEventHandle = next ? this._customEventMoveFocusNext[numMoves] : this._customEventMoveFocusPrevious[numMoves];
               if (customEventHandle != -1) {
                  this._eventToTrigger = customEventHandle;
               }
            }
         }

         vector.set(this._direction, 0);
      }
   }
}
