package net.rim.device.apps.internal.vad;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.ui.component.AnimatedBitmapField;

final class VADApplication$ListeningScreen extends PopupScreen {
   private VADApplication$ChoiceListField _listeningListField;
   private final VADApplication this$0;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   VADApplication$ListeningScreen(VADApplication _1, int state) {
      super((Manager)(new Object()));
      this.this$0 = _1;
      this.setMargin(5, 5, 5, 5);
      VerticalFieldManager vfm = (VerticalFieldManager)this.getDelegate();
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object());
      boolean var9 = false /* VF: Semaphore variable */;

      label55:
      try {
         var9 = true;
         Theme list = ThemeManager.getActiveTheme();
         EncodedImage i = list.getImage("net_rim_vad_listening_image");
         AnimatedBitmapField abf = new Object(i, 1000, 0);
         hfm.add((Field)abf);
         var9 = false;
      } finally {
         if (var9) {
            hfm.add((Field)(new Object(Bitmap.getPredefinedBitmap(1))));
            break label55;
         }
      }

      hfm.add((Field)(new Object(10)));
      hfm.add((Field)(new Object(_1._manager.getVSTString(36), 36028848558571520L)));
      vfm.add(hfm);
      String[] list;
      switch (state) {
         case 1:
            list = new Object[3];
            list[0] = _1._manager.getVSTString(0);
            list[1] = _1._manager.getVSTString(15);
            if (_1._manager.getParameters()._playPrompts) {
               list[2] = _1._manager.getVSTString(7);
            } else {
               list[2] = _1._manager.getVSTString(8);
            }
            break;
         case 5:
         case 23:
            list = new Object[]{_1._manager.getVSTString(9), _1._manager.getVSTString(10), _1._manager.getVSTString(11), _1._manager.getVSTString(12)};
            break;
         case 6:
            list = _1._listeningChoices;
            break;
         case 9:
            list = new Object[]{
               _1._manager.getVSTString(6),
               _1._manager.getVSTString(13),
               _1._manager.getVSTString(3),
               _1._manager.getVSTString(5),
               _1._manager.getVSTString(14)
            };
            break;
         default:
            return;
      }

      vfm.add((Field)(new Object(5)));
      vfm.add((Field)(new Object()));
      vfm.add((Field)(new Object(5)));
      if (list == _1._listeningChoices) {
         this._listeningListField = new VADApplication$ChoiceListField(_1, list);
         vfm.add(this._listeningListField);
      } else {
         for (int i = 0; i < list.length; i++) {
            vfm.add((Field)(new Object(list[i])));
         }

         this.setAcceptsInput(false);
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      width -= this.getMarginLeft() + this.getMarginRight();
      height -= this.getMarginTop() + this.getMarginBottom();
      Field f = this.this$0._mainScreen.getStatus();
      if (f != null) {
         height -= f.getHeight();
      }

      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
      XYRect fmExtent = this.getDelegate().getExtent();
      int newX = width - fmExtent.width >> 1;
      int newY = height - fmExtent.height;
      this.setPosition(newX + this.getMarginLeft(), newY + this.getMarginTop());
      this.setExtent(fmExtent.width, fmExtent.height);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            return this.navigationClick(0, 0);
         case '\u001b':
         case '\u0086':
            this.this$0._manager.sendEvent(28);
            this.this$0.requestBackground();
            return true;
         default:
            if (this._listeningListField == null) {
               return super.keyChar(key, status, time);
            } else {
               if (key < '1' || key > '9') {
                  key = Keypad.getAltedChar(key);
                  if (key < '1' || key > '9') {
                     return false;
                  }
               }

               int index = key - '0' - 1;
               if (index >= this._listeningListField.getSize()) {
                  return false;
               } else {
                  this._listeningListField.overrideSelectedIndex(index);
                  return this.navigationClick(0, 0);
               }
            }
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      if (this._listeningListField != null) {
         int index = this._listeningListField.getSelectedIndex();
         this.this$0._manager.sendEvent(1 + index);
         return true;
      } else {
         return false;
      }
   }
}
