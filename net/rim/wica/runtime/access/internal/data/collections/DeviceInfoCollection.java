package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.IDENInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.wica.common.builtindata.componentdefn.DeviceInfoCompDef;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.internal.component.DataCollectionImpl;

public class DeviceInfoCollection extends DataCollectionImpl {
   private long _deviceInfoHandle;
   private EventService _eventService;
   private static final String DEVICE_INFO_COLLECTION_NAME = "Device Info Collection";
   static Class class$net$rim$wica$runtime$event$EventService;

   public DeviceInfoCollection(WicletEx wiclet) {
      super(wiclet, DeviceInfoCompDef.getInstance(), true);
      this.loadDeviceInfo();
      this._eventService = (EventService)wiclet.getRuntime()
         .getService(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService
         );
   }

   private void loadDeviceInfo() {
      this._deviceInfoHandle = super.create();
      this.loadDeviceIMEI();
      this.loadDeviceIMSI();
      this.loadDevicePIN();
      this.loadDevicePhoneNumber();
   }

   private void loadDevicePhoneNumber() {
      String phoneNumber = null;

      label20:
      try {
         phoneNumber = Phone.getDevicePhoneNumber(false);
      } finally {
         break label20;
      }

      super.setObjectFieldValue(this._deviceInfoHandle, 3, phoneNumber);
   }

   private void loadDevicePIN() {
      super.setObjectFieldValue(this._deviceInfoHandle, 2, Integer.toHexString(DeviceInfo.getDeviceId()));
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void loadDeviceIMSI() {
      String deviceIMSI = "";
      boolean var4 = false /* VF: Semaphore variable */;

      label23:
      try {
         var4 = true;
         if (SIMCard.isSupported()) {
            deviceIMSI = SIMCard.imsiToString(SIMCard.getIMSI());
            var4 = false;
         } else {
            var4 = false;
         }
      } finally {
         if (var4) {
            System.out.println("net.rim.mds.runtime - Device Info Collection: Problem retrieving IMSI");
            break label23;
         }
      }

      super.setObjectFieldValue(this._deviceInfoHandle, 1, deviceIMSI);
   }

   private void loadDeviceIMEI() {
      String deviceIMEI;
      switch (RadioInfo.getNetworkType()) {
         case 2:
         case 6:
            deviceIMEI = "";
            break;
         case 3:
         case 7:
         default:
            deviceIMEI = this.imeiToString(GPRSInfo.getIMEI());
            break;
         case 4:
            deviceIMEI = ((StringBuffer)(new Object(""))).append(CDMAInfo.getESN()).toString();
            break;
         case 5:
            deviceIMEI = this.imeiToString(IDENInfo.getIMEI());
      }

      super.setObjectFieldValue(this._deviceInfoHandle, 0, deviceIMEI);
   }

   private String imeiToString(byte[] imei) {
      if (imei == null) {
         return null;
      }

      StringBuffer sb = (StringBuffer)(new Object());

      for (int i = 0; i < imei.length; i++) {
         sb.append((char)(imei[i] + 48));
      }

      return sb.toString();
   }

   @Override
   public boolean equals(long data1, long data2) {
      return data1 == data2;
   }

   @Override
   public long create() {
      return this._deviceInfoHandle;
   }

   @Override
   public void remove(long dataHandle) {
   }

   @Override
   public void save() {
   }

   @Override
   public void setBooleanFieldValue(long dataHandle, int field, boolean value) {
   }

   @Override
   public void setFieldValueFromObject(long dataHandle, int field, Object value) {
   }

   @Override
   public void setDoubleFieldValue(long dataHandle, int field, double value) {
   }

   @Override
   public void setIntFieldValue(long dataHandle, int field, int value) {
   }

   @Override
   public void setLongFieldValue(long dataHandle, int field, long value) {
   }

   @Override
   public void setObjectFieldValue(long dataHandle, int field, Object value) {
   }

   @Override
   public void setReferenceField(long dataHandle, int field, long reference) {
   }

   @Override
   public void restoreHandle(long handle) {
   }

   @Override
   public Object getObjectFieldValue(long dataHandle, int field) {
      if (super._wiclet.getContext().getExternalAccessType() != 0) {
         return super.getObjectFieldValue(dataHandle, field);
      }

      this._eventService.dispatchEvent(this, 605, 106);
      return null;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
