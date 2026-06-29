package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;
import net.rim.device.apps.internal.bis.api.ui.InputController;

public final class InitializationScreen extends BasicScreen {
   public InitializationScreen() {
      this.setTitle(ApplicationResources.getString(9));
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.addContentField(new LabelField(ApplicationResources.getString(77)));
   }

   @Override
   protected final void onDisplay() {
      InputController.getInstance().submitInput(0, null);
   }

   @Override
   public final boolean canComeBack() {
      return false;
   }
}
