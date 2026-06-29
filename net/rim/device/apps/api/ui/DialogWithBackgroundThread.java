package net.rim.device.apps.api.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.ui.component.ImageField;

public class DialogWithBackgroundThread {
   HorizontalFieldManager _hfm = (HorizontalFieldManager)(new Object());
   PopupScreen _popupScreen = (PopupScreen)(new Object(this._hfm));
   LabelField _labelField = (LabelField)(new Object("", 51539607552L));
   ImageField _imageField = (ImageField)(new Object());
   DialogWithBackgroundThreadRunnable _dialogWithBackgroundThreadRunnable;
   boolean _displayPopupScreen;

   public DialogWithBackgroundThread() {
      this._imageField.setImage(ThemeManager.getThemeAwareImage("dialog_hourglass"));
      this._imageField.setPreferredSize(Display.getWidth() >> 2, Display.getHeight() >> 2);
      this._imageField.setPadding(0, 3, 0, 0);
      this._hfm.add(this._imageField);
      this._hfm.add(this._labelField);
   }

   public void initialize(String label, DialogWithBackgroundThreadRunnable dialogWithBackgroundThreadRunnable) {
      this.initialize(label, dialogWithBackgroundThreadRunnable, true);
   }

   public void initialize(String label, DialogWithBackgroundThreadRunnable dialogWithBackgroundThreadRunnable, boolean showPopup) {
      this._labelField.setText(label);
      this._dialogWithBackgroundThreadRunnable = dialogWithBackgroundThreadRunnable;
      this._displayPopupScreen = showPopup;
   }

   public void run() {
      this._dialogWithBackgroundThreadRunnable.setDialogWithBackgroundThread(this);
      UiApplication.getUiApplication().invokeLater(new DialogWithBackgroundThread$1(this));
      if (this._displayPopupScreen) {
         UiApplication.getUiApplication().pushScreen(this._popupScreen);
      }
   }

   public void dismiss() {
      UiApplication.getUiApplication().invokeLater(new DialogWithBackgroundThread$2(this));
   }
}
