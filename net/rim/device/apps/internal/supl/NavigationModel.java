package net.rim.device.apps.internal.supl;

import net.rim.device.api.gps.GPSRequestedAssistData;

final class NavigationModel {
   private short gpsWeek;
   private short gpsToe;
   private byte nSat;
   private byte toeLimit;
   private SatelliteInfoElement[] satInfo;
   private byte optionals;
   static final byte NAV_MODEL_OPT_SAT_INFO;
   static final byte NUM_OPT_ELEMENTS;
   static final byte GPS_WEEK_BIT_SIZE;
   static final byte GPS_TOE_BIT_SIZE;
   static final byte NSAT_BIT_SIZE;
   static final byte TOE_LIMIT_BIT_SIZE;
   static final byte MIN_NSAT_VALUE;

   NavigationModel() {
   }

   NavigationModel(GPSRequestedAssistData reqAssistData) {
      this.gpsWeek = (short)reqAssistData.getGpsWeek();
      this.gpsToe = (short)(0xFF & reqAssistData.getGpsToe());
      this.nSat = (byte)(0xFF & reqAssistData.getNumSatellites());
      this.toeLimit = (byte)(0xFF & reqAssistData.getTToeLimit());
      if (this.nSat > 0) {
         if (this.nSat > 15) {
            System.out.println("Truncating the number of satInfo elements to 15");
            this.nSat = 15;
         }

         this.satInfo = new SatelliteInfoElement[this.nSat];
         this.optionals = 1;

         for (int i = 0; i < this.nSat; i++) {
            this.satInfo[i] = new SatelliteInfoElement();
            this.satInfo[i].satId = (byte)(0xFF & reqAssistData.getSatelliteID(i));
            this.satInfo[i].iODE = (short)(0xFF & reqAssistData.getSatelliteIode(i));
         }
      }
   }

   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(this.optionals, 1);
      stuff.putBits(this.gpsWeek, 10);
      stuff.putBits(this.gpsToe, 8);
      stuff.putBits(this.nSat, 5);
      stuff.putBits(this.toeLimit, 4);
      if ((this.optionals & 1) == 1) {
         stuff.putBits(this.nSat - 1, 5);

         for (byte i = 0; i < this.nSat; i++) {
            this.satInfo[i].encode(stuff);
         }
      }
   }

   final void print() {
      System.out.println("Navigation Model: ");
      System.out.println(((StringBuffer)(new Object("Optionals: "))).append(this.optionals).toString());
      System.out.println(((StringBuffer)(new Object("GPS Week: "))).append(this.gpsWeek).toString());
      System.out.println(((StringBuffer)(new Object("GPS Toe: "))).append(this.gpsToe).toString());
      System.out.println(((StringBuffer)(new Object("Nsat: "))).append(this.nSat).toString());
      System.out.println(((StringBuffer)(new Object("Toe Limit: "))).append(this.toeLimit).toString());
      if ((this.optionals & 1) == 1) {
         System.out.println(((StringBuffer)(new Object("Num Satellites: "))).append(this.nSat).toString());

         for (int i = 0; i < this.nSat; i++) {
            System.out.println(((StringBuffer)(new Object("Sat Element "))).append(i).toString());
            this.satInfo[i].print();
         }
      }
   }
}
