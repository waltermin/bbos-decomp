package net.rim.device.cldc.impl.hrt;

import net.rim.device.api.util.Persistable;

final class HRTReqThreadState implements Persistable {
   public int flags;
   public int simHash;
   public int uidHash;
   public String reg3GPPAddress;
   public String regCDMAAddress;
   public String regIDENAddress;
   public String regApn;
   public String regUsername;
   public String regPassword;
   public String lastMdn;
   public byte[] lastCDMA_IMSI;
   public long homeNPC_3GPP = -1;
   public long homeNPC_CDMA = -1;
   public long homeNPC_IDEN = -1;
   public long pendingLifetimeDynPIN;
   public static final int FLAG_THREAD_ENABLED = 1;
   public static final int FLAG_REG_SERVER_PRESENT = 2;
   public static final int FLAG_FIRST_TIME_EVER = 4;

   public final boolean isFlagSet(int f) {
      return (this.flags & f) != 0;
   }

   public final void setFlag(int f) {
      this.flags |= f;
   }

   public final void clearFlag(int f) {
      this.flags &= ~f;
   }
}
