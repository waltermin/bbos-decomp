package net.rim.device.cldc.impl.hrt;

import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioInfo;

final class RegisterNowRunnable implements Runnable {
   @Override
   public final void run() {
      HRUtils.getThunks().sendRegistrationRequest();
      boolean sent = HRUtils.getNpcForActiveNetwork() != -1 && RadioInfo.getSignalLevel() != -256;
      RIMGlobalMessagePoster.postGlobalEvent(-8887235436078834958L, sent ? 1 : 0, 0);
   }
}
