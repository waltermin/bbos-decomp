package net.rim.device.cldc.impl.api;

import net.rim.device.api.ui.Manager;
import net.rim.device.internal.ui.component.PopupDialog;

public class SoftTokenDialog extends PopupDialog {
   public SoftTokenDialog(Manager manager) {
      super(manager, 33554432);
   }

   public String getUserName() {
      throw null;
   }

   public String getChallengeResponse() {
      throw null;
   }
}
