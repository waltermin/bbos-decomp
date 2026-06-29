package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;

public final class ApplicationControlInformation {
   private MainScreen _mainScreen;
   private int _moduleHandle;
   private int[] _moduleHandles;
   private String _name;

   public ApplicationControlInformation(int moduleHandle) {
      this._moduleHandle = moduleHandle;
   }

   public ApplicationControlInformation(int[] moduleHandles, String name) {
      this._moduleHandles = moduleHandles;
      this._moduleHandle = this._moduleHandles[0];
      this._name = name;
   }

   public final void open() {
      if (this._moduleHandles == null) {
         this._mainScreen = new ApplicationControlScreen(this._moduleHandle);
      } else {
         this._mainScreen = new ApplicationControlScreen(this._moduleHandles, this._name);
      }

      UiApplication.getUiApplication().pushModalScreen(this._mainScreen);
   }
}
