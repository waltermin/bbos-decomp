package javax.bluetooth;

public class DeviceClass {
   private int _record;

   public DeviceClass(int record) {
      if ((record & 0xFF000000) != 0) {
         throw new Object();
      }

      this._record = record;
   }

   public int getServiceClasses() {
      return this._record & 16760832;
   }

   public int getMajorDeviceClass() {
      return this._record & 7936;
   }

   public int getMinorDeviceClass() {
      return this._record & 252;
   }
}
