package net.rim.device.apps.internal.phone;

import net.rim.device.apps.internal.phone.data.PhoneFolders;

final class VoiceApp$8 implements Runnable {
   private final int val$oldShowCallLogsOption;
   private final int val$newShowCallLogsOption;
   private final VoiceApp this$0;

   VoiceApp$8(VoiceApp _1, int _2, int _3) {
      this.this$0 = _1;
      this.val$oldShowCallLogsOption = _2;
      this.val$newShowCallLogsOption = _3;
   }

   @Override
   public final void run() {
      PhoneFolders.registerFoldersWithNewShowCallLogsOption(this.val$oldShowCallLogsOption, this.val$newShowCallLogsOption);
   }
}
