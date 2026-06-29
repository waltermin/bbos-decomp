package net.rim.device.api.system;

public class GPRSInfo$GPRSCellInfo {
   private int _cellId;
   private int _mcc;
   private int _mnc;
   private int _lac;
   private int _rac;
   private int _arfcn;
   private int _bsic;
   private int _rssi;

   private GPRSInfo$GPRSCellInfo() {
   }

   public int getCellId() {
      return this._cellId;
   }

   public int getLAC() {
      return this._lac;
   }

   public int getMCC() {
      return this._mcc;
   }

   public int getMNC() {
      return this._mnc;
   }

   public int getRAC() {
      return this._rac;
   }

   public int getARFCN() {
      return this._arfcn;
   }

   public int getBSIC() {
      return this._bsic;
   }

   public int getRSSI() {
      return this._rssi;
   }

   GPRSInfo$GPRSCellInfo(GPRSInfo$1 x0) {
      this();
   }
}
