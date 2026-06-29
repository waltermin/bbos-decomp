package net.rim.device.cldc.impl.sb;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceBookSyncCollection;
import net.rim.device.api.servicebook.selector.SRSelector;
import net.rim.device.api.servicebook.selector.SRSelectorSyncCollection;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.gme.SBSerialUpdaterThread;
import net.rim.device.internal.deviceagent.OutgoingDeviceAgentCollection;
import net.rim.device.internal.proxy.Proxy;

public final class Registration {
   public static final void SBMain(String[] args) {
      Proxy p = Proxy.getInstance();
      ServiceBook sb = ServiceBook.init();
      p.addGlobalEventListener(sb);
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      reg.put(-863050508581563378L, sb);
      OutgoingDeviceAgentCollection oac = (OutgoingDeviceAgentCollection)OutgoingDeviceAgentCollection.getInstance();
      if (oac != null) {
         oac.addDeviceCapabilities((byte)16, new byte[]{48});
         int devicePIN = DeviceInfo.getDeviceId();
         DataBuffer buff = new DataBuffer(false);
         buff.writeInt(devicePIN);
         buff.trim();
         oac.addDeviceCapabilities((byte)7, buff.toArray());
      }

      p.startThread(new SBSerialUpdaterThread());
      p.startThread(new SBUpdaterThread());
      ServiceBookSyncCollection sbSyncCollection = new ServiceBookSyncCollection(sb);
      SyncManager.getInstance().enableSynchronization(sbSyncCollection);
      reg.put(-7781445676016247535L, sbSyncCollection);
      SRSelector srSel = new SRSelector();
      reg.put(2857166788728229964L, srSel);
      p.addGlobalEventListener(srSel);
      SRSelectorSyncCollection syncCollection = new SRSelectorSyncCollection(srSel, sb);
      reg.put(-1080498432380349415L, syncCollection);
      SyncManager.getInstance().enableSynchronization(syncCollection);
      reg.put(5050782853726896085L, new SBThunksImpl());
   }
}
