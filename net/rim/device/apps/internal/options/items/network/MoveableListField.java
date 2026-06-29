package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;

final class MoveableListField extends ListField implements ListFieldCallback {
   private boolean _moving;
   private int _movingStartIndex;
   private int _movingCurIndex;
   private XYRect _focus = (XYRect)(new Object());
   private MoveableListFieldCallback _realCallback;
   Image _imageUpDown = IconCollection.get("net_rim_options_MoveUpDown", 1).getImage(0);
   Tag tagMove = Tag.create("list-move");
   private static final int SPACER;

   public MoveableListField() {
      super.setCallback(this);
   }

   @Override
   public final void setCallback(ListFieldCallback callback) {
      this._realCallback = (MoveableListFieldCallback)callback;
   }

   @Override
   public final ListFieldCallback getCallback() {
      return this._realCallback;
   }

   @Override
   public final void delete(int index) {
      super.delete(index);
      if (this._moving) {
         if (index == this._movingCurIndex) {
            this.cancelMove();
            return;
         }

         if (index < this._movingStartIndex) {
            this._movingStartIndex--;
         }

         if (index < this._movingCurIndex) {
            this._movingCurIndex--;
         }
      }
   }

   @Override
   public final void insert(int index) {
      super.insert(index);
      if (this._moving) {
         if (index <= this._movingStartIndex) {
            this._movingStartIndex++;
         }

         if (index <= this._movingCurIndex) {
            this._movingCurIndex++;
         }
      }
   }

   @Override
   public final void setSelectedIndex(int index) {
      if (this._moving) {
         if (index < 0) {
            index = 0;
         }

         if (index >= this.getSize()) {
            index = this.getSize() - 1;
         }

         this._movingCurIndex = index;
      }

      super.setSelectedIndex(index);
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      super.drawFocus(graphics, on);
      if (this._moving && on) {
         this.getFocusRect(this._focus);
         graphics.drawRect(this._focus.x, this._focus.y, this._focus.width, this._focus.height);
      }
   }

   @Override
   protected final void makeContextMenu(ContextMenu cm) {
      if (!this._moving) {
         super.makeContextMenu(cm);
         if (this.getSize() > 1) {
            cm.addItem(new MoveableListField$MyMenu(this, CommonResources.getString(9120), 0));
         }
      }
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      int leftover = super.moveFocus(amount, status, time);
      if (this._moving) {
         this.invalidate(this._movingCurIndex);
         this._movingCurIndex = super.getSelectedIndex();
         this.invalidate(this._movingCurIndex);
         leftover = 0;
      }

      return leftover;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (this._moving) {
         this.endMove();
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      if ((status & 1) != 0 && !this._moving) {
         this.startMove();
      }

      if (this._moving) {
         this.setSelectedIndex(this._movingCurIndex + (amount > 0 ? 1 : -1));
         return true;
      } else {
         return super.trackwheelRoll(amount, status, time);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         if (this._moving) {
            this.endMove();
            return true;
         }
      } else if (key == 27 && this._moving) {
         if (this._movingCurIndex == this._movingStartIndex) {
            this.cancelMove();
            return true;
         }

         this.setSelectedIndex(this._movingStartIndex);
         this.invalidate();
         return true;
      }

      return super.keyChar(key, status, time);
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      if (Keypad.key(keycode) == 257 && this._moving) {
         this.endMove();
         return true;
      } else {
         return super.keyUp(keycode, time);
      }
   }

   private final int translateIndex(int index) {
      if (this._moving) {
         if (index == this._movingCurIndex) {
            return this._movingStartIndex;
         }

         if (index < this._movingCurIndex && index >= this._movingStartIndex) {
            return index + 1;
         }

         if (index > this._movingCurIndex && index <= this._movingStartIndex) {
            index--;
         }
      }

      return index;
   }

   private final void startMove() {
      this._moving = true;
      this._movingStartIndex = super.getSelectedIndex();
      this._movingCurIndex = this._movingStartIndex;
      ThemeAttributeSet tas = ThemeManager.getActiveTheme().getAttributeSet(this.tagMove);
      this.setThemeAttributesSpecial(tas, null);
      this.invalidate();
   }

   private final void cancelMove() {
      this._moving = false;
      this._movingStartIndex = -1;
      this._movingCurIndex = -1;
      this.setThemeAttributesSpecial(null, null);
      this.invalidate();
   }

   private final void endMove() {
      if (this._realCallback != null) {
         this._realCallback.moveFinished(this, this._movingStartIndex, this._movingCurIndex);
      }

      this.cancelMove();
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (this._realCallback != null) {
         if (this._moving && this._movingCurIndex == index) {
            int rowHeight = this.getRowHeight();
            width -= this._imageUpDown.getWidth(rowHeight, rowHeight);
            this._imageUpDown.paint(graphics, width, y, rowHeight, rowHeight);
            width -= 2;
         }

         this._realCallback.drawListRow(listField, graphics, this.translateIndex(index), index, y, width);
      }
   }

   @Override
   public final int getPreferredWidth(ListField field) {
      return this._realCallback != null ? this._realCallback.getPreferredWidth(field) : 0;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return this._realCallback != null ? this._realCallback.indexOfList(listField, prefix, this.translateIndex(start)) : -1;
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this._realCallback != null ? this._realCallback.get(listField, this.translateIndex(index)) : null;
   }
}
