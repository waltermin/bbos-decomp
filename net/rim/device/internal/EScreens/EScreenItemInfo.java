package net.rim.device.internal.EScreens;

public final class EScreenItemInfo {
   public int flags;
   public int id;
   public int idCookie;

   public EScreenItemInfo() {
   }

   public EScreenItemInfo(int f, int i, int ic) {
      this.flags = f;
      this.id = i;
      this.idCookie = ic;
   }
}
