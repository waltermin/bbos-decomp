package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.PopupScreen;

final class PropertiesDialog extends PopupScreen {
   static int[][][] tagToLabel = new int[][][]{
      (int[][])({3, 4, 1, 51, -804651005, 131, 132, 130, 712179968, 1864070467, 16826221, 1701539702}),
      (int[][])({131, 132, 130, 712179968, 1864070467, 16826221, 1701539702, 1870004480, 1849779563, 56711012, 1870004480, 290219371})
   };

   public PropertiesDialog(Object field) {
      super((Manager)(new Object(299067162755072L)), 299067162755072L);
      this.setItem(field);
   }

   public final void setItem(Object item) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (super.keyChar(c, status, time)) {
         return true;
      }

      UiApplication.getUiApplication().popScreen(this);
      return true;
   }
}
