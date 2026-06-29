package net.rim.device.api.servicebook;

import net.rim.device.api.util.CRC16;
import net.rim.device.api.util.CRC32;

public class ServiceIdentifier {
   private ServiceRecord _sr;
   private long _sid;
   public static final long INVALID_SID;

   public ServiceIdentifier(ServiceRecord sr) {
      this._sr = sr;
      this._sid = createSid(sr);
   }

   public static long createSid(ServiceRecord sr) {
      long id = -1;
      if (sr == null) {
         return id;
      }

      if (sr.getUid() != null && sr.getDataSourceId() != null) {
         if (sr.getUserId() == -1) {
            int hashUID = sr.getUidHash();
            int hashName = sr.getNameHash();
            long highPart = hashUID & 4294967295L;
            long lowPart = hashName & 4294967295L;
            return highPart << 32 | lowPart;
         } else {
            int hashUID = CRC16.update(65535, sr.getUid().getBytes());
            int hashDSID = CRC16.update(65535, sr.getDataSourceId().getBytes());
            int hashUserID = CRC32.update(-1, sr.getUserId());
            long highPart = hashUID & 65535;
            long middlePart = hashDSID & 65535;
            long lowPart = hashUserID & 4294967295L;
            return highPart << 48 | middlePart << 32 | lowPart;
         }
      } else {
         return id;
      }
   }

   public static boolean isSameService(long sid1, long sid2) {
      if (sid1 == sid2) {
         return true;
      }

      long sid1Shifted = sid1 << 16;
      long sid2Shifted = sid2 << 16;
      return sid1Shifted == sid2Shifted;
   }

   public ServiceRecord getServiceRecord() {
      return this._sr;
   }

   public long getSid() {
      return this._sid;
   }
}
