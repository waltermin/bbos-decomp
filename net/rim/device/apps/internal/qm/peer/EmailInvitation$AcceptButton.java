package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.system.Application;
import net.rim.device.apps.internal.qm.peer.common.QmButton;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class EmailInvitation$AcceptButton extends QmButton {
   private final EmailInvitation this$0;

   EmailInvitation$AcceptButton(EmailInvitation _1) {
      super(QmResources.getString(3), 12884901888L);
      this.this$0 = _1;
   }

   @Override
   public final void invoke() {
      Application.getApplication().invokeLater(new EmailInvitation$AcceptButton$1(this));
   }

   static final EmailInvitation access$000(EmailInvitation$AcceptButton x0) {
      return x0.this$0;
   }
}
