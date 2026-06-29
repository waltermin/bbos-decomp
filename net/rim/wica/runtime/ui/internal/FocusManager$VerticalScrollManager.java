package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.MathUtilities;
import net.rim.wica.runtime.ui.View;

final class FocusManager$VerticalScrollManager extends VerticalFieldManager implements IScrollManager {
   private Field _lastFocus;
   private FocusManager _manager;

   FocusManager$VerticalScrollManager(FocusManager manager) {
      super(1155454779397242880L);
      this._manager = manager;
   }

   @Override
   public final boolean isFocusable() {
      return true;
   }

   private final boolean isFieldFocusable(Field field) {
      if (field != null && field.isFocusable()) {
         while (field != null && !(field instanceof View)) {
            field = field.getManager();
         }

         return field instanceof View ? field.isFocusable() : false;
      } else {
         return false;
      }
   }

   @Override
   protected final void onFocus(int direction) {
      if (direction != 0) {
         if (!this.isFieldFocusable(this.getLeafFieldWithFocus())) {
            this.setVerticalScroll(0);
            Field firstFocusableField = this.findFirstFocus(direction);
            boolean visible = this.isVisibleOnScreen(firstFocusableField, 0, false);
            this._lastFocus = firstFocusableField;
            if (!visible) {
               this._manager.enableFocusHolder();
               this._manager.setFocusHolder();
               return;
            }

            super.onFocus(direction);
            this.restoreFocusChain(this);
         }
      }
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      int sign = MathUtilities.clamp(-1, amount, 1);
      int remaining = amount;

      while (remaining != 0) {
         amount = remaining;
         Field currentFocus = this.getLeafFieldWithFocus();
         if (currentFocus != null && currentFocus != this._manager.getFocusHolder()) {
            this._lastFocus = currentFocus;

            while (!(currentFocus instanceof Focusable)) {
               currentFocus = currentFocus.getManager();
               if (currentFocus == this) {
                  return super.moveFocus(remaining, status, time);
               }
            }

            remaining = ((Focusable)currentFocus).moveFocus(remaining, status, time);
            if (remaining != amount) {
               this._lastFocus = currentFocus = this.getLeafFieldWithFocus();
               XYRect focusArea = (XYRect)(new Object());
               this._lastFocus.getFocusRect(focusArea);
               this.transformToAppScreen(this._lastFocus, focusArea);
               super.makeFocusVisible(true, focusArea, true, false);
            }
         }

         Field nextFocus = null;
         if (remaining != 0) {
            if (currentFocus == null) {
               if (this._lastFocus != null) {
                  XYRect focus = (XYRect)(new Object());
                  this._lastFocus.getFocusRect(focus);
                  focus.translate(this._lastFocus.getContentRect().x, this._lastFocus.getContentRect().y);
                  this.transformToAppScreen(this._lastFocus, focus);
                  int position = focus.y - this.getVerticalScroll();
                  if (position * sign > 0) {
                     nextFocus = this._lastFocus;
                  } else {
                     this.restoreFocusChain(this._lastFocus);
                     nextFocus = this.getNextFocusableField(sign);
                     this._manager.enableFocusHolder();
                     this._manager.setFocusHolder();
                  }
               }
            } else {
               nextFocus = this.getNextFocusableField(sign);
            }

            if (this._lastFocus == nextFocus) {
               this.scrollScreen(sign);
               remaining -= sign;
               if (this.isVisibleOnScreen(this._lastFocus, 0, false) && this.isFocusVisibleOnScreen(this._lastFocus, true)) {
                  this._manager.disableFocusHolder();
                  if (!this._lastFocus.isFocus()) {
                     this._lastFocus.setFocus();
                  }
               } else if (!this._manager.isFocusHolderActive()) {
                  this._manager.enableFocusHolder();
                  this._manager.setFocusHolder();
               }
            } else if (!this.isVisibleOnScreen(nextFocus, 0, false) && !this.isVisibleOnScreen(nextFocus, 20 * sign, false)) {
               this.scrollScreen(sign);
               remaining -= sign;
               XYRect last = (XYRect)(new Object(this._lastFocus.getContentRect()));
               XYRect next = (XYRect)(new Object(nextFocus.getContentRect()));
               this.transformToAppScreen(this._lastFocus, last);
               this.transformToAppScreen(nextFocus, next);
               if ((sign <= 0 || last.y + last.height <= next.y + next.height) && (sign >= 0 || last.y >= next.y)) {
                  if (this.isVisibleOnScreen(this._lastFocus, 0, false) && this.isFocusVisibleOnScreen(this._lastFocus, true)) {
                     this._manager.disableFocusHolder();
                     if (!this._lastFocus.isFocus()) {
                        this._lastFocus.setFocus();
                     }
                  } else if (!this._manager.isFocusHolderActive()) {
                     this._manager.enableFocusHolder();
                     this._manager.setFocusHolder();
                  }
               } else {
                  super.moveFocus(sign, status, time);
                  if (remaining != 0) {
                     XYRect focusArea = (XYRect)(new Object());
                     nextFocus.getFocusRect(focusArea);
                     this.transformToAppScreen(nextFocus, focusArea);
                     this.makeFocusVisible(false, focusArea, true, false);
                  }
               }
            } else {
               if (this._manager.isFocusHolderActive()) {
                  this._manager.disableFocusHolder();
                  this.restoreFocusChain(this._lastFocus);
               }

               remaining -= sign;
               XYRect last = (XYRect)(new Object());
               this._lastFocus.getFocusRect(last);
               last.translate(this._lastFocus.getContentLeft(), this._lastFocus.getContentTop());
               XYRect next = (XYRect)(new Object());
               nextFocus.getFocusRect(next);
               next.translate(nextFocus.getContentLeft(), nextFocus.getContentTop());
               this.transformToAppScreen(this._lastFocus, last);
               this.transformToAppScreen(nextFocus, next);
               if ((sign <= 0 || last.y + last.height <= next.y + next.height) && (sign >= 0 || last.y >= next.y)) {
                  if (super.moveFocus(sign, status, time) == sign) {
                     break;
                  }

                  while (!this.isFocusVisibleOnScreen(nextFocus, false)) {
                     this.scrollScreen(sign);
                     nextFocus = this.getNextFocusableField(sign);
                  }
               } else {
                  super.moveFocus(sign, status, time);
               }
            }
         }
      }

      return remaining;
   }

   private final boolean isVisibleOnScreen(Field field, int amount, boolean containsEntirely) {
      if (field == null) {
         return false;
      }

      XYRect fieldContent = (XYRect)(new Object(field.getContentRect()));
      XYRect topManagerVisibleArea = null;
      this.transformToAppScreen(field, fieldContent);
      int headerHeight = 0;
      Object var8;
      if (this.getScreen() instanceof ApplicationDialog) {
         Field header = ((ApplicationDialog)this.getScreen()).getHeader();
         headerHeight = header != null ? header.getHeight() : 0;
         var8 = new Object(this.getLeft(), this.getVerticalScroll() + amount, Integer.MAX_VALUE, this.getScreen().getHeight() - headerHeight);
      } else {
         headerHeight = this.getScreen().getDelegate().getField(0).getHeight();
         var8 = new Object(this.getLeft(), this.getVerticalScroll() + amount, Integer.MAX_VALUE, FocusManager.DEVICE_SCREEN_HEIGHT - headerHeight);
      }

      if (!containsEntirely) {
         return ((XYRect)var8).intersects(fieldContent);
      }

      fieldContent.width = fieldContent.width == 0 ? 1 : fieldContent.width;
      return ((XYRect)var8).contains(fieldContent);
   }

   private final boolean isFocusVisibleOnScreen(Field field, boolean entirely) {
      if (field == null) {
         return false;
      }

      XYRect focus = (XYRect)(new Object());
      field.getFocusRect(focus);
      focus.translate(field.getContentRect().x, field.getContentRect().y);
      XYRect topManagerVisibleArea = null;
      this.transformToAppScreen(field, focus);
      int headerHeight = 0;
      Object var7;
      if (this.getScreen() instanceof ApplicationDialog) {
         Field header = ((ApplicationDialog)this.getScreen()).getHeader();
         headerHeight = header != null ? header.getHeight() : 0;
         var7 = new Object(this.getLeft(), this.getVerticalScroll(), Integer.MAX_VALUE, this.getScreen().getHeight() - headerHeight);
      } else {
         headerHeight = this.getScreen().getDelegate().getField(0).getHeight();
         var7 = new Object(this.getLeft(), this.getVerticalScroll(), Integer.MAX_VALUE, FocusManager.DEVICE_SCREEN_HEIGHT - headerHeight);
      }

      if (!entirely) {
         return ((XYRect)var7).intersects(focus);
      }

      focus.width = focus.width == 0 ? 1 : focus.width;
      return ((XYRect)var7).contains(focus);
   }

   @Override
   public final void transformToAppScreen(Field f, XYRect rect) {
      for (Manager manager = f.getManager(); manager != null; manager = manager.getManager()) {
         if (manager == this) {
            return;
         }

         rect.translate(manager.getContentRect().x, manager.getContentRect().y);
         int dx = manager.getHorizontalScroll();
         int dy = manager.getVerticalScroll();
         rect.translate(-dx, -dy);
      }
   }

   private final void scrollScreen(int direction) {
      int headerHeight = 0;
      int viewportHeight = 0;
      Screen s = this.getScreen();
      if (!(s instanceof ApplicationDialog)) {
         headerHeight = s.getDelegate().getField(0).getHeight();
         viewportHeight = FocusManager.DEVICE_SCREEN_HEIGHT - headerHeight;
      } else {
         Field header = ((ApplicationDialog)s).getHeader();
         headerHeight = header != null ? header.getHeight() : 0;
         viewportHeight = s.getContentHeight() - headerHeight;
      }

      int virtualHeight = this.getVirtualHeight();
      int newOffset = MathUtilities.clamp(0, this.getVerticalScroll() + direction * FocusManager.DEVICE_SCREEN_HEIGHT / 8, virtualHeight - viewportHeight);
      if (newOffset >= 0 && newOffset + viewportHeight <= virtualHeight) {
         this.setVerticalScroll(newOffset);
      }
   }

   private final Field getNextFocusableField(int direction) {
      Field oldFocus = this.getLeafFieldWithFocus();
      if (oldFocus == null) {
         return oldFocus;
      }

      Manager m = oldFocus.getManager();
      Field f = oldFocus;
      switch (direction) {
         case -1:
            while (m != this) {
               int indexNext = f.getIndex() - 1;
               if (indexNext != -1) {
                  for (; indexNext > -1; indexNext--) {
                     Field nextField = ((Manager)m).getField(indexNext);
                     if (nextField.isFocusable()) {
                        if (!(nextField instanceof Object)) {
                           return nextField;
                        }

                        m = nextField;
                        indexNext = ((Manager)m).getFieldCount();
                     }
                  }
               }

               f = f.getManager();
               m = f.getManager();
            }
            break;
         case 1:
            while (m != this) {
               int fieldCount = ((Manager)m).getFieldCount();
               int indexNext = f.getIndex() + 1;
               if (indexNext == fieldCount) {
                  indexNext = -1;
               }

               if (indexNext != -1) {
                  for (; indexNext < fieldCount; indexNext++) {
                     Field nextField = ((Manager)m).getField(indexNext);
                     if (nextField.isFocusable()) {
                        if (!(nextField instanceof Object)) {
                           return nextField;
                        }

                        m = nextField;
                        fieldCount = ((Manager)m).getFieldCount();
                        indexNext = -1;
                     }
                  }
               }

               f = f.getManager();
               m = f.getManager();
            }
      }

      return oldFocus;
   }

   private final Field findFirstFocus(int direction) {
      int indexFocus = this.firstFocus(direction);
      if (indexFocus == -1) {
         return null;
      }

      Field f = this.getField(indexFocus);

      while (f instanceof Object) {
         Manager m = (Manager)f;

         for (int i = 0; i < m.getFieldCount(); i++) {
            f = m.getField(i);
            if (f.isFocusable()) {
               break;
            }
         }
      }

      return f;
   }

   private final void restoreFocusChain(Field f) {
      if (f != null) {
         Manager m = f.getManager();

         for (Manager screen = this.getScreen(); m != screen; f = m) {
            m = f.getManager();
            m.setFieldWithFocus(f);
         }
      }
   }

   @Override
   public final void disableFocusHolder() {
      this._manager.disableFocusHolder();
   }

   @Override
   public final void setLastFocus(Field focus) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void enableFocusHolder() {
      this._manager.enableFocusHolder();
   }

   @Override
   public final void setFocusHolder() {
      this._manager.setFocusHolder();
   }

   @Override
   public final Manager castToManager() {
      return this;
   }
}
