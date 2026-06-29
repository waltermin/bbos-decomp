package net.rim.device.api.system;

import net.rim.device.api.util.Persistable;

public final class CellBroadcast$ChannelInfo extends CellBroadcast$Info implements Persistable {
   private String _nickname;

   public CellBroadcast$ChannelInfo(int channelId) {
      super(channelId);
      this.setEnabled(true);
   }

   private CellBroadcast$ChannelInfo() {
   }

   public final String getNickname() {
      return this._nickname;
   }

   public final void setNickname(String nn) {
      this._nickname = nn;
   }

   public final CellBroadcast$ChannelInfo clone() {
      CellBroadcast$ChannelInfo ci = new CellBroadcast$ChannelInfo();
      ci._nickname = this._nickname;
      this.copyInto(ci);
      return ci;
   }
}
