package net.rim.device.cldc.io.stp;

class StpUtil$ServiceInfo {
   public String _uid;
   public String[] _hosts;
   public int[] _ports;
   public boolean _state;
   public byte[] _capabilities;

   public boolean capableOf(byte id) {
      if (id == 0) {
         return true;
      }

      id--;
      return this._capabilities == null || id < 0 || id >= this._capabilities.length ? false : this._capabilities[id] != 0;
   }
}
