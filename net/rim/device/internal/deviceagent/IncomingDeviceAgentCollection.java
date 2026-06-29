package net.rim.device.internal.deviceagent;

import java.util.Enumeration;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.DataBuffer;

public class IncomingDeviceAgentCollection extends DeviceAgentCollection implements DeviceAgent {
   private static final long DEVICE_CONFIG_DB;

   private IncomingDeviceAgentCollection() {
      super(-6163296018230867973L);
   }

   public static DeviceAgentCollection getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      DeviceAgentCollection deviceAgentCollection = (IncomingDeviceAgentCollection)ar.getOrWaitFor(-6163296018230867973L);
      if (deviceAgentCollection == null) {
         deviceAgentCollection = new IncomingDeviceAgentCollection();
         ar.put(-6163296018230867973L, deviceAgentCollection);
      }

      return deviceAgentCollection;
   }

   @Override
   public SyncConverter getSyncConverter() {
      return getInstance();
   }

   @Override
   public String getDatabaseName() {
      return "Handheld Configuration";
   }

   @Override
   public String getSyncName() {
      return "Handheld Configuration";
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
   }

   @Override
   public boolean addSyncObject(SyncObject object) {
      boolean result = false;
      if (object instanceof DeviceAgentModel) {
         result = super.addSyncObject(object);
         DeviceAgentModel model = (DeviceAgentModel)object;
         RIMGlobalMessagePoster.postGlobalEvent(-5907912590164645220L, model.getTableId(), model.getUID());
      }

      return result;
   }

   @Override
   public boolean updateSyncObject(SyncObject oldSyncObject, SyncObject newSyncObject) {
      boolean result = false;
      if (newSyncObject instanceof DeviceAgentModel) {
         result = super.updateSyncObject(oldSyncObject, newSyncObject);
         DeviceAgentModel model = (DeviceAgentModel)newSyncObject;
         RIMGlobalMessagePoster.postGlobalEvent(-5907912590164645220L, model.getTableId(), model.getUID());
      }

      return result;
   }

   @Override
   public boolean removeSyncObject(SyncObject object) {
      boolean result = false;
      if (object instanceof DeviceAgentModel) {
         result = super.removeSyncObject(object);
         DeviceAgentModel model = (DeviceAgentModel)object;
         RIMGlobalMessagePoster.postGlobalEvent(-5907912590164645220L, model.getTableId(), model.getUID());
      }

      return result;
   }

   @Override
   public SyncObject convert(DataBuffer data, int version, int uid) {
      return data.available() > 0 ? new DeviceAgentModel(uid, data) : null;
   }

   @Override
   public synchronized boolean addDeviceCapabilities(byte tag, byte[] info) {
      return false;
   }

   @Override
   public synchronized boolean removeDeviceCapabilities(byte tag) {
      return false;
   }

   @Override
   public byte[] getDeviceCapabilities(byte tag) {
      return null;
   }

   @Override
   public boolean setDeviceCapabilitiesFlag(byte tag, byte[] mask) {
      return false;
   }

   @Override
   public boolean clearDeviceCapabilitiesFlag(byte tag, byte[] mask) {
      return false;
   }

   @Override
   public synchronized DataBuffer getDeviceAgentInfo(byte group) {
      DataBuffer buffer = (DataBuffer)(new Object(false));
      Enumeration enumeration = super._database.elements();

      while (enumeration.hasMoreElements()) {
         DeviceAgentModel element = (DeviceAgentModel)enumeration.nextElement();
         if (element.getTableId() == group) {
            buffer.writeByteArray(element.getData().getArray());
         }
      }

      return buffer;
   }
}
