package net.rim.device.cldc.impl.hrt;

import net.rim.device.api.hrt.HRThunks;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.cldc.impl.hrt.editor.HRTAppContents;

public final class HRThunksImpl implements HRThunks {
   @Override
   public final void displayEditor(HostRoutingTable hrt, int viewMode) {
      HRTAppContents contents = new HRTAppContents(hrt, viewMode);
      UiApplication.getUiApplication().pushScreen(contents);
   }

   private final HRTRequestThread getRequestThread() {
      return (HRTRequestThread)HRUtils.getFactoryObject(4019666953250015899L);
   }

   @Override
   public final void sendRegistrationRequest() {
      this.getRequestThread().registerNow(false);
   }

   @Override
   public final void sendRegistrationInfoRequest() {
      this.getRequestThread().registerNow(true);
   }

   @Override
   public final void enableRequestThread(boolean enable) {
      this.getRequestThread().enableThread(enable);
   }

   @Override
   public final void setRegistrationServerPresent(boolean present) {
      this.getRequestThread().setRegServerPresent(present);
   }

   @Override
   public final void requestThreadAbort() {
      this.getRequestThread().requestThreadAbort();
   }

   @Override
   public final boolean isRequestThreadIdle() {
      return this.getRequestThread().isIdle();
   }

   @Override
   public final void useRegistrationVersion(int version) {
      this.getRequestThread().useRegistrationVersion(version);
   }

   @Override
   public final boolean toggleSendEFSPN() {
      return this.getRequestThread().toggleSendEFSPN();
   }
}
