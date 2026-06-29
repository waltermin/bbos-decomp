package net.rim.device.internal.ui.component;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.vm.Message;

public class SimplePasswordDialog extends SimpleInputDialog implements HolsterListener, GlobalEventListener {
   private boolean _hasITAdminSetPassword;
   private boolean _numeric;
   private char _initial;
   private int _initialModifier;
   private SimplePasswordDialog$ClearPasswordOnIdleThread _clearPasswordOnIdleThread;
   private boolean _pasteable = true;
   private boolean _passwordEntryState;
   private boolean _deviceIsUnlocked;
   private boolean _revealPassword;
   private Locale _storedLocale;

   public void setInitial(char initial) {
      this._initial = initial;
      if (CharacterUtilities.isUpperCase(initial)) {
         this._initialModifier = 1;
      } else {
         if (Keypad.getLayout().getUnaltedChar(initial) != 0) {
            this._initialModifier = 8;
         }
      }
   }

   public void setNumeric(boolean numeric) {
      this._numeric = numeric;
      this.resetType();
   }

   public void setPasteable(boolean pasteable) {
      this._pasteable = pasteable;
      BasicEditField editField = this.getEditField();
      if (editField != null) {
         editField.setPasteable(this._pasteable);
      }
   }

   public void setRevealPassword(boolean revealPassword) {
      this._revealPassword = revealPassword;
      this.resetType();
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 1309561383038111736L) {
         this._hasITAdminSetPassword = true;
         this.cancel();
      }
   }

   @Override
   public void outOfHolster() {
   }

   @Override
   public void inHolster() {
      this.cancel();
      if (this.getEditField().dependentScreen() != null && Ui.getUiEngine().getActiveScreen() == this.getEditField().dependentScreen()) {
         UiEngine owner = this.getOwner() == null ? Ui.getUiEngine() : this.getOwner();
         owner.popScreen(this.getEditField().dependentScreen());
      }
   }

   private void passwordEntryStateChanged() {
      if (!this._deviceIsUnlocked) {
         BasicEditField editField = this.getEditField();
         boolean passwordEntryState;
         if (editField != null && editField.getText().length() > 0) {
            passwordEntryState = true;
         } else {
            passwordEntryState = false;
         }

         if (passwordEntryState != this._passwordEntryState) {
            this._passwordEntryState = passwordEntryState;
            RIMGlobalMessagePoster.postGlobalEvent(306123729322610706L, passwordEntryState ? 1 : 0, 0);
         }
      }
   }

   private void startClearPasswordThreadIfNecessary() {
      BasicEditField editField = this.getEditField();
      if (editField != null && editField.getText().length() > 0 && this._clearPasswordOnIdleThread == null) {
         this._clearPasswordOnIdleThread = new SimplePasswordDialog$ClearPasswordOnIdleThread(this, Application.getApplication());
         this._clearPasswordOnIdleThread.start();
      }
   }

   @Override
   protected boolean cancel() {
      if (super.cancel()) {
         this.passwordEntryStateChanged();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean result = super.keyChar(key, status, time);
      if (!InputContext.getInstance().isSureType()) {
         this.passwordEntryStateChanged();
         this.startClearPasswordThreadIfNecessary();
      }

      return result;
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      boolean result = super.keyDown(keycode, time);
      if (InputContext.getInstance().isSureType()) {
         this.passwordEntryStateChanged();
         this.startClearPasswordThreadIfNecessary();
      }

      return result;
   }

   @Override
   protected void onFocusNotify(boolean focus) {
      super.onFocusNotify(focus);
      if (focus) {
         if (this._hasITAdminSetPassword) {
            this._hasITAdminSetPassword = false;
            Application.getApplication().invokeLater(new SimplePasswordDialog$SimplePasswordDialogDismisser(this));
            return;
         }

         this.startClearPasswordThreadIfNecessary();
         if (this._initial != 0 && InputContext.getInstance(false).isSureType()) {
            if (this._initialModifier == 8) {
               UiApplication.getUiApplication()
                  .publicProcessMessage(
                     new Message(
                        2,
                        520,
                        0,
                        Keypad.keycode('ā', Keypad.status(SLKeyLayout.convertModifiersToStatus(this._initialModifier))),
                        (int)System.currentTimeMillis()
                     )
                  );
            }

            UiApplication.getUiApplication()
               .publicProcessMessage(
                  new Message(
                     2,
                     513,
                     this._initial,
                     Keypad.keycode(
                        (char)Keypad.getLayout().getOriginalKeyCode(this._initial, this._initialModifier),
                        Keypad.status(SLKeyLayout.convertModifiersToStatus(this._initialModifier))
                     ),
                     (int)System.currentTimeMillis()
                  )
               );
            this._initial = 0;
            this._initialModifier = 0;
         }
      }
   }

   @Override
   public void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         if (DeviceInfo.isInHolster()) {
            Application.getApplication().invokeLater(new SimplePasswordDialog$SimplePasswordDialogDismisser(this));
         } else {
            Application.getApplication().addHolsterListener(this);
            Application.getApplication().addGlobalEventListener(this);
            if (!(this.getEditField() instanceof PasswordEditField)) {
               Locale currentLocale = Locale.getDefaultInputForSystem();
               Locale l;
               if (InputContext.getInstance().getActiveInputMethodID() == 4096) {
                  l = Locale.get("en", "US", "Multitap");
               } else {
                  l = Locale.get("en", "US");
               }

               if (!currentLocale.equals(l)) {
                  InputContext.getInstance().selectInputMethod(l);
                  this._storedLocale = currentLocale;
               }
            }

            this.passwordEntryStateChanged();
            this.startClearPasswordThreadIfNecessary();
         }
      } else {
         Application.getApplication().removeHolsterListener(this);
         Application.getApplication().removeGlobalEventListener(this);
         if (this._storedLocale != null) {
            InputContext.getInstance().selectInputMethod(this._storedLocale);
            this._storedLocale = null;
         }

         if (this._clearPasswordOnIdleThread != null) {
            this._clearPasswordOnIdleThread.stopThread();
         }
      }
   }

   public SimplePasswordDialog(String prompt, int minLength, int maxLength, boolean numeric, long style) {
      super(numeric ? 6 : 5, prompt, minLength, maxLength, style);
      this._numeric = numeric;
      this._deviceIsUnlocked = !ApplicationManager.getApplicationManager().isSystemLocked();
      this.setStatusPriority(-2147483644);
   }

   private void resetType() {
      if (this._revealPassword) {
         this.setType(this._numeric ? 3 : 10);
      } else {
         this.setType(this._numeric ? 6 : 5);
      }

      this.setPasteable(this._pasteable);
   }
}
