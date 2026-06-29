package net.rim.device.apps.internal.profiles;

class Profiles$SourceExpireRunnable implements Runnable {
   @Override
   public void run() {
      Profiles ps = Profiles.getInstance();
      ps.checkForExpiredSources();
   }
}
