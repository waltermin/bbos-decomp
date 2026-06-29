package net.rim.device.apps.api.ribbon.indicators;

import net.rim.device.internal.system.SIMCardEfHandler;
import net.rim.device.internal.system.SIMCardEfTask;
import net.rim.device.internal.system.SIMServiceTable;

class VoicemailIconManager$ReadMwisTask implements SIMCardEfTask {
   private final VoicemailIconManager this$0;

   private VoicemailIconManager$ReadMwisTask(VoicemailIconManager _1) {
      this.this$0 = _1;
   }

   @Override
   public void doWork(SIMCardEfHandler efHandler) {
      boolean isEnabled = SIMServiceTable.isServiceEnabled(47);
      if (isEnabled && efHandler.infoRequest(67) == 0) {
         int numRecords = efHandler.getNumRecords();
         if (numRecords >= 1 && efHandler.getRecordLength() >= 1) {
            this.this$0._mwisEnabled = true;
            byte[] buf = new byte[efHandler.getRecordLength()];
            byte[] newCounts = new byte[this.this$0._indicatorCounts.length];

            for (int profileId = 1; profileId <= numRecords; profileId++) {
               if (efHandler.readRequest(profileId, buf) != 0) {
                  return;
               }

               if (this.this$0._mwisRFUMask == -1) {
                  int validStatusBits = 1;
                  int i = efHandler.getRecordLength();

                  while (--i > 0) {
                     validStatusBits |= validStatusBits << 1;
                  }

                  this.this$0._mwisRFUMask = (byte)(~validStatusBits);
               }

               boolean isInvalid = (buf[0] & this.this$0._mwisRFUMask) != 0;
               if (buf[0] == 0) {
                  int i = buf.length;

                  while (--i > 0) {
                     isInvalid |= buf[i] != 0;
                  }
               }

               if (isInvalid) {
                  int i = buf.length;

                  while (--i >= 0) {
                     buf[i] = 0;
                  }

                  if (efHandler.writeRequest(67, efHandler.getFileStructure(), profileId, buf) != 0) {
                     return;
                  }
               }

               if (buf[0] != 0) {
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
            }

            this.this$0._indicatorCounts = newCounts;
            this.this$0.onIndicatorsUpdated();
         }
      }
   }

   VoicemailIconManager$ReadMwisTask(VoicemailIconManager x0, VoicemailIconManager$1 x1) {
      this(x0);
   }
}
