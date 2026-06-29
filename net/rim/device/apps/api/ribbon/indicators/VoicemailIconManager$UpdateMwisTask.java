package net.rim.device.apps.api.ribbon.indicators;

import net.rim.device.internal.system.SIMCardEfHandler;
import net.rim.device.internal.system.SIMCardEfTask;
import net.rim.device.internal.system.SIMServiceTable;

class VoicemailIconManager$UpdateMwisTask implements SIMCardEfTask {
   private byte _profileId;
   private byte _type;
   private byte _count;
   private final VoicemailIconManager this$0;

   VoicemailIconManager$UpdateMwisTask(VoicemailIconManager _1, byte id, byte type, byte count) {
      this.this$0 = _1;
      this._profileId = id;
      this._type = type;
      this._count = count;
   }

   @Override
   public void doWork(SIMCardEfHandler efHandler) {
      if (SIMServiceTable.isServiceEnabled(47) && efHandler.infoRequest(67) == 0) {
         byte[] buf = new byte[efHandler.getRecordLength()];
         if (efHandler.getNumRecords() >= this._profileId) {
            if (efHandler.readRequest(this._profileId, buf) == 0) {
               buf[0] = this._count == 0 ? (byte)(buf[0] & ~(1 << this._type)) : (byte)(buf[0] | 1 << this._type);
               buf[this._type + 1] = this._count;
               if (efHandler.writeRequest(67, efHandler.getFileStructure(), this._profileId, buf) != 0) {
               }

               byte[] newCounts = new byte[this.this$0._indicatorCounts.length];
               int numRecords = efHandler.getNumRecords();
               if (numRecords > 0) {
                  for (int profileId = 1; profileId <= numRecords; profileId++) {
                     if (efHandler.readRequest(profileId, buf) != 0) {
                        return;
                     }

                     int i = Math.min(buf.length - 1, newCounts.length);

                     while (--i >= 0) {
                        int additional = buf[i + 1] & 255;
                        if (additional == 255) {
                           if (newCounts[i] == 0) {
                              newCounts[i] = -1;
                           }
                        } else if (additional >= 0) {
                           if (newCounts[i] == -1) {
                              newCounts[i] = (byte)additional;
                           } else {
                              newCounts[i] = (byte)(newCounts[i] + additional);
                           }
                        }
                     }
                  }

                  this.this$0._indicatorCounts = newCounts;
                  this.this$0.onIndicatorsUpdated();
               }
            }
         }
      }
   }
}
