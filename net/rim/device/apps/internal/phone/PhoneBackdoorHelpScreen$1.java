package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.phone.VoiceServices;

final class PhoneBackdoorHelpScreen$1 implements Runnable {
   private final PhoneBackdoorHelpScreen this$0;

   PhoneBackdoorHelpScreen$1(PhoneBackdoorHelpScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      VoiceServices.postEvent(
         5100,
         1,
         "1 nelksjdf aslk fjsldjf slk lfkjsldkfjs lkfj sldf jdf sldkfj lsdfj slkfj sldfj sldkfj slkjdf lsd fls dfls dlfks lfkjs dlfkjs lfs dflws\n2 sports\n3 weather\n"
      );
   }
}
