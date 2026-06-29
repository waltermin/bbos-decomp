package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.internal.blackberryemail.otasync.OTAMessageSync;

final class EmailListener$1 implements Runnable {
   private final EmailInvitation val$email;
   private final String val$sender;
   private final String val$finalbody;
   private final String val$recipient;
   private final ServiceRecord val$serviceRecord;
   private final int val$refId;
   private final EmailListener this$0;

   EmailListener$1(EmailListener _1, EmailInvitation _2, String _3, String _4, String _5, ServiceRecord _6, int _7) {
      this.this$0 = _1;
      this.val$email = _2;
      this.val$sender = _3;
      this.val$finalbody = _4;
      this.val$recipient = _5;
      this.val$serviceRecord = _6;
      this.val$refId = _7;
   }

   @Override
   public final void run() {
      if (this.val$email.getStage() == 1) {
         this.val$email.handleStage1(this.val$sender, this.val$finalbody);
      } else if (this.val$email.getStage() == 2) {
         this.val$email.handleStage2(this.val$sender, this.val$recipient);
      } else if (this.val$email.getStage() == 5) {
         this.val$email.handleDeny();
      }

      OTAMessageSync.getInstance().messageReadStatusChangeOnDevice(this.val$serviceRecord, this.val$refId, true);
      OTAMessageSync.getInstance().messageDeletedOnDevice(this.val$serviceRecord, this.val$refId);
   }
}
