package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.event.EventEngine;
import net.rim.plazmic.internal.mediaengine.event.EventLogic;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.internal.mediaengine.service.MediaService;
import net.rim.plazmic.internal.mediaengine.service.ModelInteractor;
import net.rim.plazmic.internal.mediaengine.ui.ForeignObject;

public class FocusHandler implements MediaService, FocusInteractor {
   private ModelInteractor _model;
   private AnimationModel _data;
   private MediaServices _services;
   private EventEngine _engine;
   private Event _event = new Event();
   private boolean _inFocus;

   public void setItemInFocus(int newItem) {
      this._data._itemInFocus = newItem;
   }

   @Override
   public void setMedia(Object media) {
      this._data = (AnimationModel)media;
      this._inFocus = false;
   }

   @Override
   public Object getMedia() {
      return this._data;
   }

   @Override
   public void dispose() {
   }

   @Override
   public boolean hasFocus() {
      return this._data._itemInFocus != -1 && this.isFocusable(this._data._itemInFocus);
   }

   @Override
   public boolean activateItemInFocus() {
      boolean consumed = false;
      int currentIdx = this._data._itemInFocus;
      if (currentIdx != -1 && this.validateFocus(currentIdx)) {
         int parentIdx = this._data._nodes[currentIdx + 3];

         do {
            this._event._event = 105;
            this._event._eventParam = currentIdx;
            consumed |= this.triggerChain(this._event);
            this._event.clear();
            currentIdx = parentIdx;
            if (parentIdx >= 0) {
               parentIdx = this._data._nodes[parentIdx + 3];
            }
         } while (currentIdx >= 0);
      }

      return consumed;
   }

   @Override
   public boolean keyChar(int key, int status) {
      boolean consumed = false;
      this._event._event = 106;
      this._event._eventParam = key;
      this._event._eventParamLong = this._data._itemInFocus == -1 ? 0 : this._data._itemInFocus;
      consumed = this.triggerChain(this._event);
      if (!consumed) {
         this._event._eventParamLong = 0;
         consumed = this.triggerChain(this._event);
      }

      this._event.clear();
      return consumed;
   }

   @Override
   public void setFocusToItem(int handle) {
      this._inFocus = handle != -1;
      this._event._time = this._engine.getTime();
      int currentIdx = this._data._itemInFocus;
      if (currentIdx != handle) {
         if (currentIdx != -1) {
            int parentIdx = this._data._nodes[currentIdx + 3];
            this._event._eventParamLong = 0;
            ForeignObject fo = this._data.getNodeForeignObject(currentIdx);
            if (fo != null) {
               fo.killFocus();
            }

            do {
               this._event._event = 104;
               this._event._eventParam = currentIdx;
               this._model.trigger(this._event);
               currentIdx = parentIdx;
               if (parentIdx >= 0) {
                  parentIdx = this._data._nodes[parentIdx + 3];
               }
            } while (currentIdx >= 0);
         }

         this._data._itemInFocus = handle;
         if (handle != -1) {
            int parentIdx = this._data._nodes[handle + 3];
            currentIdx = handle;
            this._event._eventParamLong = 0;
            ForeignObject fo = this._data.getNodeForeignObject(handle);
            if (fo != null) {
               fo.setFocus();
            }

            do {
               this._event._event = 103;
               this._event._eventParam = currentIdx;
               this._model.trigger(this._event);
               currentIdx = parentIdx;
               if (parentIdx >= 0) {
                  parentIdx = this._data._nodes[parentIdx + 3];
               }
            } while (currentIdx >= 0);
         }

         this._event.clear();
      }
   }

   @Override
   public void setDefaultItem(int handle) {
      this._data._defaultItemInFocus = handle;
   }

   @Override
   public int getItemInFocus() {
      return this._data._itemInFocus;
   }

   @Override
   public int getItemCount() {
      return this._data == null ? 0 : this._data._focusableList.length;
   }

   @Override
   public int moveFocus(int direction, int offset, boolean wrap) {
      this._inFocus = true;
      if (!this.hasFocus() && this.getItemCount() > 0) {
         if (offset == 0) {
            offset = 1;
         }

         if (direction == 1 && this._data._defaultItemInFocus >= 0 && this.isFocusable(this._data._defaultItemInFocus)) {
            this.setFocusToItem(this._data._defaultItemInFocus);
            offset--;
         }
      }

      if (this._data != null && offset != 0 && this.getItemCount() != 0) {
         int newHotspot = -1;
         int prevHotspot = this._data._itemInFocus;
         boolean stopSearch = false;

         while (offset > 0 && !stopSearch) {
            offset--;
            int lastValidHotspot = prevHotspot;

            do {
               newHotspot = this.getNextFocusableItem(direction, prevHotspot, wrap);
               if (newHotspot == prevHotspot) {
                  newHotspot = lastValidHotspot;
                  offset++;
                  stopSearch = true;
               } else {
                  prevHotspot = newHotspot;
               }
            } while (!stopSearch && !this.validateFocus(newHotspot));
         }

         this.setFocusToItem(newHotspot);
      }

      return offset;
   }

   @Override
   public int getFirstFocusableItem(int direction) {
      if (this._data._focusableList.length > 0) {
         return direction == 1 ? this._data._focusableList[0] : this._data._focusableList[this._data._focusableList.length - 1];
      } else {
         return -1;
      }
   }

   @Override
   public Object getWrappedObject(int index) {
      ForeignObject fo = this._data.getNodeForeignObject(index);
      return fo != null ? fo.getInstance() : null;
   }

   @Override
   public int getNextFocusableItem(int direction, int focusHandle, boolean wrap) {
      if (this._data == null) {
         return -1;
      }

      int itemCount = this._data._focusableList.length;
      int curItem;
      if (focusHandle == -1) {
         curItem = -1;
      } else {
         curItem = 0;

         while (curItem < itemCount && this._data._focusableList[curItem] != focusHandle) {
            curItem++;
         }
      }

      if (itemCount != 0 && curItem < itemCount) {
         boolean next = direction == 1;
         int curr;
         int i;
         if (curItem < 0) {
            if (!next) {
               i = itemCount;
               curr = 0;
            } else {
               i = -1;
               curr = itemCount - 1;
            }
         } else {
            i = curItem;
            curr = curItem;
         }

         do {
            i += next ? 1 : -1;
            if (wrap) {
               i = (i + itemCount) % itemCount;
            } else if (i < 0 || i >= itemCount) {
               break;
            }

            if (this.isFocusable(this._data._focusableList[i])) {
               return this._data._focusableList[i];
            }
         } while (i != curr);

         return curItem >= 0 && this.isFocusable(this._data._focusableList[curItem]) ? this._data._focusableList[curItem] : -1;
      } else {
         return -1;
      }
   }

   @Override
   public boolean getWrap() {
      return this._data == null ? false : this._data._wrapFocus;
   }

   @Override
   public void setWrap(boolean wrapFocus) {
      if (this._data != null) {
         this._data._wrapFocus = wrapFocus;
      }
   }

   @Override
   public void setServices(MediaServices s) {
      if (s == null) {
         throw new IllegalArgumentException("Provided MediaServices cannot be null");
      }

      this._services = s;
      this._model = (ModelInteractor)this._services.getService("ModelInteractor");
      this._engine = this._services.getEngine();
   }

   private boolean isFocusable(int handle) {
      if (this._data == null) {
         return false;
      }

      boolean isFocusable = false;
      int hsbits = this._data._nodes[handle + 8];
      if ((hsbits & 176) == 176) {
         ForeignObject fo = this._data.getNodeForeignObject(handle);
         isFocusable = fo != null ? fo.isFocusable() : true;
      }

      return isFocusable;
   }

   private boolean triggerChain(Event event) {
      if (this._data == null) {
         return false;
      } else {
         EventLogic logic = this._data._logic;
         if (logic != null && logic.getDependentEvents(event) != null) {
            event._time = this._engine.getTime();
            this._model.trigger(event);
            return true;
         } else {
            return false;
         }
      }
   }

   private int getVisibility(int index) {
      int visibility = Integer.MAX_VALUE;
      int bits = this._data._nodes[index + 8];
      if ((bits & 64) != 0) {
         visibility = (bits & 128) != 0 ? 1 : 0;
      }

      return visibility;
   }

   private boolean validateFocus(int index) {
      boolean isDisplay = this._data.bitsAreSet(index, 16);

      for (int parentIndex = this._data._nodes[index + 3]; parentIndex != -1; parentIndex = this._data._nodes[parentIndex + 3]) {
         isDisplay &= this._data.bitsAreSet(parentIndex, 16);
      }

      if (!isDisplay) {
         return false;
      }

      boolean isVisible = true;
      return isDisplay && isVisible;
   }

   public FocusHandler() {
      this._inFocus = this._inFocus;
   }
}
