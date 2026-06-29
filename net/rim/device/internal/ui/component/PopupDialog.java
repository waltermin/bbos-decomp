package net.rim.device.internal.ui.component;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.container.PopupScreen;

public class PopupDialog extends PopupScreen {
   private boolean _modal = true;
   private PopupDialogClosedListener _dialogClosedListener;
   private UiEngine _owner;
   private int _closeReason = 0;
   private int _statusPriority = 50;
   private boolean _open;
   private boolean _cancelAllowed = true;
   public static final int GLOBAL_STATUS = 33554432;
   public static final int PUSHED_GLOBAL_SCREEN = 134217728;
   public static final int CANCEL = -1;
   public static final int CLOSE = 0;

   public PopupDialog(Manager manager) {
      this(manager, 0);
   }

   public PopupDialog(Manager manager, long style) {
      super(manager, style);
      if ((style & 167772160) != 0) {
         this.setModal(false);
      }
   }

   public void setModal(boolean modal) {
      if (!modal || !this.isStyle(33554432) && !this.isStyle(134217728)) {
         this._modal = modal;
      } else {
         throw new IllegalArgumentException("Global Screens cannot be modal.");
      }
   }

   public boolean isModal() {
      return this._modal;
   }

   public void setOwner(UiEngine owner) {
      this._owner = owner;
   }

   public UiEngine getOwner() {
      return this._owner;
   }

   public void setPopupDialogClosedListener(PopupDialogClosedListener listener) {
      this._dialogClosedListener = listener;
   }

   public PopupDialogClosedListener getPopupDialogClosedListener() {
      return this._dialogClosedListener;
   }

   public void setStatusPriority(int priority) {
      this._statusPriority = priority;
   }

   public int getStatusPriority() {
      return this._statusPriority;
   }

   public int getCloseReason() {
      return this._closeReason;
   }

   @Override
   protected void onDisplay() {
      if (!this._open) {
         this._open = true;
      }

      super.onDisplay();
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (attached && !this._open) {
         Ui.getUiEngine();
         this._open = true;
      }

      super.onUiEngineAttached(attached);
   }

   public void show() {
      if (!this._open) {
         UiEngine owner = this._owner == null ? Ui.getUiEngine() : this._owner;
         this._open = true;
         if (this._modal) {
            owner.pushModalScreen(this);
         } else if (this.isStyle(33554432)) {
            owner.pushGlobalScreen(this, this._statusPriority, 2);
         } else if (this.isStyle(134217728)) {
            owner.pushGlobalScreen(this, this._statusPriority, 0);
         } else {
            owner.pushScreen(this);
         }
      }
   }

   protected void close(int closeReason) {
      if (this._open) {
         if (closeReason != -1 || this.isCancelAllowed()) {
            UiEngine owner = this._owner == null ? Ui.getUiEngine() : this._owner;
            this._closeReason = closeReason;
            owner.popScreen(this);
            if (this._dialogClosedListener != null) {
               this._dialogClosedListener.dialogClosed(this, this._closeReason);
            }

            this._open = false;
         }
      }
   }

   public void setCancelAllowed(boolean cancelAllowed) {
      this._cancelAllowed = cancelAllowed;
   }

   public boolean isCancelAllowed() {
      return this._cancelAllowed;
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      return super.stylusTap(x, y, status, time) ? true : this.trackwheelClick(status, time);
   }
}
