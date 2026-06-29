package net.rim.device.internal.gps;

import net.rim.device.api.gps.GPS;
import net.rim.device.api.gps.GPSListener;

final class GPSFirewallImpl$PrivacyListener implements GPSListener {
   private final GPSFirewallImpl this$0;

   GPSFirewallImpl$PrivacyListener(GPSFirewallImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public final void gpsModeChangeComplete(boolean success, int mode) {
   }

   @Override
   public final void gpsLocationUpdated(int error, int type, int mode) {
   }

   @Override
   public final void gpsResponseGetLPS(int result) {
      this.this$0.setCurrentPrivacy(GPS.requestGetLPS());
   }

   @Override
   public final void gpsResponseSetLPS(int result) {
   }

   @Override
   public final void gpsResponseEnablePIN(int result) {
   }

   @Override
   public final void gpsResponseChangePIN(int result) {
   }

   @Override
   public final void gpsPDEChangeComplete(boolean success, int ip, int port) {
   }

   @Override
   public final void gpsEphemerisDataRequired(int format) {
   }

   @Override
   public final void gpsCredentialChangeComplete(boolean success, int clientId) {
   }

   @Override
   public final void gpsLocationAidingRequest() {
   }
}
