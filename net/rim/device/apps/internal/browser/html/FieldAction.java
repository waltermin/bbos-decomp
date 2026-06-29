package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;

final class FieldAction implements Runnable {
   private HTMLInput _input;
   private int _action;
   static final int ACTION_CLICK = 0;
   static final int ACTION_FOCUS = 1;
   static final int ACTION_SELECT = 2;
   static final int ACTION_ON_FOCUS = 3;
   static final int ACTION_ON_CHANGE = 4;
   static final int ACTION_ON_CLICK = 5;
   static final int ACTION_ON_BLUR = 6;

   FieldAction(HTMLInput input, int action) {
      this._input = input;
      this._action = action;
   }

   private final boolean ensureLayout(Field field) {
      HTMLBrowserContent parent = ((HTMLDocumentImpl)this._input.getOwnerDocument()).getUiPeer();
      parent.flushPendingLayout();
      return field.getScreen() != null;
   }

   @Override
   public final void run() {
      Field field = this._input.getPeer();
      switch (this._action) {
         case -1:
            break;
         case 0:
         default:
            if (field != null) {
               synchronized (Application.getEventLock()) {
                  if (this.ensureLayout(field)) {
                     this._input.clickInternal();
                  }

                  return;
               }
            }
            break;
         case 1:
            if (field != null) {
               synchronized (Application.getEventLock()) {
                  if (this.ensureLayout(field)) {
                     field.setFocus();
                  }

                  return;
               }
            }
            break;
         case 2:
            if (field != null) {
               synchronized (Application.getEventLock()) {
                  if (this.ensureLayout(field)) {
                     if (!(field instanceof HTMLTextInputField)) {
                        if (!(field instanceof HTMLInput)) {
                           field.select(true);
                        } else {
                           ((HTMLInput)field).selectProgrammatically();
                        }
                     } else {
                        ((HTMLTextInputField)field).selectProgrammatically();
                     }
                  }

                  return;
               }
            }
            break;
         case 3:
            this._input.onFocus();
            return;
         case 4:
            this._input.onChange();
            return;
         case 5:
            this._input.onClick();
            return;
         case 6:
            this._input.onBlur();
      }
   }
}
