package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.component.Dialog;

final class PeerApplication$ContactAvailableDialog extends Dialog {
   PeerContact _contact;

   PeerApplication$ContactAvailableDialog(PeerContact contact, String message) {
      super(3, message, 0, null, 0);
      this._contact = contact;
   }

   final void queue() {
      PeerApplication.access$200().pushGlobalScreen(this, 499, 1);
   }
}
