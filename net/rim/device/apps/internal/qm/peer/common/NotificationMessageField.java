package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class NotificationMessageField extends VerticalFieldManager {
   LabelField _lf;
   public static long MATCH = 1;
   public static long SWITCH = 2;

   public NotificationMessageField(long style, NotificationMessage owner) {
      this.add((Field)(new Object()));
      this._lf = new SwitchField(style, owner);
      this.add(this._lf);
      this.setCookie(owner);
   }

   @Override
   public final boolean isFocus() {
      return this._lf.isFocus();
   }
}
