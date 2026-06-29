package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class FocusManager extends VerticalFieldManager {
   private FocusManager$VerticalScrollManager _manager;
   private FocusHolder _focusHolder = new FocusHolder();
   public static final int DEVICE_SCREEN_HEIGHT = Graphics.getScreenHeight();
   public static final int SCROLL_HEIGHT = 20;

   FocusManager() {
      this._manager = new FocusManager$VerticalScrollManager(this);
      this.add(this._focusHolder);
      this.add(this._manager);
   }

   final void setContent(Manager manager) {
      this.enableFocusHolder();
      this.setFocusHolder();
      this._manager.deleteAll();
      this._manager.add(manager);
      this.disableFocusHolder();
   }

   public final void notifyUpdateStarted() {
      this._manager._lastFocus = this._manager.getLeafFieldWithFocus();
      this.setFieldWithFocus(this._focusHolder);
      this.enableFocusHolder();
      this.setFocusHolder();
   }

   public final void notifyUpdateCompleted() {
      this.disableFocusHolder();
      if (this._manager.isFieldFocusable(this._manager._lastFocus)) {
         this._manager._lastFocus.setFocus();
      } else {
         this._manager.onFocus(1);
      }
   }

   final Manager getScrollingManager() {
      return this._manager;
   }

   final void restoreFocus(Field focusField) {
      this.disableFocusHolder();
      focusField.setFocus();
   }

   @Override
   protected final void onFocus(int direction) {
      this.setFieldWithFocus(this._manager);
      this._manager.onFocus(direction);
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      return this._manager.moveFocus(amount, status, time);
   }

   private final FocusHolder getFocusHolder() {
      return this._focusHolder;
   }

   private final void disableFocusHolder() {
      this._focusHolder.setFocusable(false);
      this.setFieldWithFocus(this._manager);
   }

   private final void enableFocusHolder() {
      this._focusHolder.setFocusable(true);
   }

   private final void setFocusHolder() {
      if (this._focusHolder.isFocusable()) {
         this._focusHolder.setFocus();
      } else {
         throw new IllegalStateException("Attempted to focus the null field when it is non-focusable!");
      }
   }

   private final boolean isFocusHolderActive() {
      return this._focusHolder.isFocusable();
   }

   final boolean isFieldVisible(Field field) {
      return this._manager.isFocusVisibleOnScreen(field, true);
   }

   public final void scrollPage(int where) {
      PageScroller.getInstance(this._manager).scrollPage(where);
   }

   public final void scrollScreen(int where) {
      this._manager.scrollScreen(where);
   }
}
