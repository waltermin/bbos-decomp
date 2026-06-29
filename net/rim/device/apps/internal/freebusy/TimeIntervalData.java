package net.rim.device.apps.internal.freebusy;

class TimeIntervalData {
   private int _FBType;
   private int[] _FBTypeArray = new int[4];

   TimeIntervalData(int FBType) {
      this._FBType = FBType;
      this._FBTypeArray[0] = FBType;
      this._FBTypeArray[1] = FBType;
      this._FBTypeArray[2] = FBType;
      this._FBTypeArray[3] = FBType;
   }

   TimeIntervalData(int[] FBTypeData) {
      if (FBTypeData != null && FBTypeData.length == 4) {
         this._FBTypeArray[0] = FBTypeData[0];
         this._FBTypeArray[1] = FBTypeData[1];
         this._FBTypeArray[2] = FBTypeData[2];
         this._FBTypeArray[3] = FBTypeData[3];
         this._FBType = FBTypeData[0];
      }
   }

   public int getFBType() {
      return this._FBType;
   }

   public int[] getFBData() {
      return this._FBTypeArray;
   }
}
