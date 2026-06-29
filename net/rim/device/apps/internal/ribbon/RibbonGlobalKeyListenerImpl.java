package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Trackball;
import net.rim.device.apps.api.ribbon.ConvenienceKeyOptionsProvider;
import net.rim.device.apps.api.ribbon.system.StandbyManager;
import net.rim.device.apps.internal.profiles.ProfileQuickToggle;
import net.rim.device.internal.system.UnhandledGlobalKeyListener;

public final class RibbonGlobalKeyListenerImpl implements UnhandledGlobalKeyListener {
   private boolean _leftSideKeyDown;
   private boolean _ignoreConvKeyRepeat;

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return false;
   }

   private static final boolean leftSideConvenienceKeySupported() {
      return !DirectConnect.isSupported() && Keypad.hasLeftSideConvenienceKey();
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (!Keypad.hasSendEndKeys()) {
         return false;
      }

      if (key == 273 && Trackball.isSupported()) {
         return StandbyManager.getInstance().handleKeyDown(time);
      }

      if (ApplicationManager.getApplicationManager().isSystemLocked()) {
         return false;
      }

      if (key == 19) {
         ConvenienceKeyOptionsProvider provider = ConvenienceKeyOptionsProvider.getInstance();
         return provider.invokeConvenienceKey1App();
      }

      if (key == 273) {
         return ProfileQuickToggle.handleKeyDown(time);
      }

      if (key == 21 && leftSideConvenienceKeySupported()) {
         this._leftSideKeyDown = true;
         this._ignoreConvKeyRepeat = false;
      }

      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      if (Keypad.key(keycode) == 21 && this._leftSideKeyDown && leftSideConvenienceKeySupported()) {
         this._leftSideKeyDown = false;
         this._ignoreConvKeyRepeat = false;
         return ConvenienceKeyOptionsProvider.getInstance().invokeConvenienceKey2App();
      } else {
         return false;
      }
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 273) {
         return Trackball.isSupported() ? StandbyManager.getInstance().handleKeyRepeat(time) : ProfileQuickToggle.handleKeyRepeat(time);
      } else if (key == 21 && this._leftSideKeyDown && leftSideConvenienceKeySupported() && !this._ignoreConvKeyRepeat) {
         this._leftSideKeyDown = false;
         this._ignoreConvKeyRepeat = true;
         return ConvenienceKeyOptionsProvider.getInstance().invokeConvenienceKey2ClickAndHoldApp();
      } else {
         return false;
      }
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }
}
