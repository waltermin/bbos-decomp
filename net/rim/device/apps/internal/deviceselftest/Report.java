package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;

public final class Report implements Persistable {
   String str;
   long timeStamp;
   int keyboard = -1;
   int trackball = -1;
   int holsterDetector = -1;
   int lightSensor = -1;
   int vibrator = -1;
   int keypadBacklight = -1;
   int lcdBacklight = -1;
   int lcdPixels = -1;
   int led = -1;
   int handsetSpeaker = -1;
   int handsfreeSpeaker = -1;
   int handsetMicrophone = -1;
   int headsetDetectSwitch = -1;
   int headsetSpeaker = -1;
   int headsetMicrophone = -1;
   int rfAntenna = -1;
   int gpsAntenna = -1;
   int bluetoothSpeaker = -1;
   int bluetoothMicrophone = -1;
   private Object pin;
   private Object deviceType;
   private Object appVersion;
   private Object platform;

   public final String getAppVersion() {
      return PersistentContent.decodeString(this.appVersion);
   }

   public final void setAppVersion(String str) {
      if (str == null) {
         this.appVersion = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.appVersion = PersistentContent.encode(str, false, true);
      }
   }

   public final String getDeviceType() {
      return PersistentContent.decodeString(this.deviceType);
   }

   public final void setDeviceType(String str) {
      if (str == null) {
         this.deviceType = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.deviceType = PersistentContent.encode(str, false, true);
      }
   }

   public final String getPin() {
      return PersistentContent.decodeString(this.pin);
   }

   public final void setPin(String str) {
      if (str == null) {
         this.pin = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.pin = PersistentContent.encode(str, false, true);
      }
   }

   public final String getPlatform() {
      return PersistentContent.decodeString(this.platform);
   }

   public final void setPlatform(String str) {
      if (str == null) {
         this.platform = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.platform = PersistentContent.encode(str, false, true);
      }
   }

   Report(long startTime) {
      this.timeStamp = startTime;
      this.getDeviceInfo();
   }

   final void getDeviceInfo() {
      this.setPin(Integer.toHexString(DeviceInfo.getDeviceId()));
      this.setDeviceType(DeviceInfo.getDeviceName());
      this.setAppVersion("v" + DeviceInfo.getSoftwareVersion());
      this.setPlatform(DeviceInfo.getPlatformVersion());
   }
}
