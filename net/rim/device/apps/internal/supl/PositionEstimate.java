package net.rim.device.apps.internal.supl;

final class PositionEstimate {
   private LatitudeSign latitudeSign;
   private int latitude;
   private int longitude;
   private Uncertainty uncertainty;
   private byte confidence;
   private AltitudeInfo altitudeInfo;
   private byte optionals;
   static final byte POS_EST_OPT_UNCERTAINTY;
   static final byte POS_EST_OPT_CONFIDENCE;
   static final byte POS_EST_OPT_ALT_INFO;
   static final byte NUM_OPT_ELEMENTS;
   static final int LONGITUDE_BASE_VALUE;
   static final byte LATITUDE_BIT_SIZE;
   static final byte LONGITUDE_BIT_SIZE;
   static final byte CONFIDENCE_BIT_SIZE;

   final void decode(Nibbler nib) {
      nib.getBit();
      this.optionals = (byte)nib.getBitsLarge(3);
      this.latitudeSign = new LatitudeSign();
      this.latitudeSign.decode(nib);
      this.latitude = nib.getBitsLarge(23);
      this.longitude = nib.getBitsLarge(24) + -8388608;
      if ((this.optionals & 4) == 4) {
         this.uncertainty = new Uncertainty();
         this.uncertainty.decode(nib);
      }

      if ((this.optionals & 2) == 2) {
         this.confidence = (byte)nib.getBitsLarge(7);
      }

      if ((this.optionals & 1) == 1) {
         this.altitudeInfo = new AltitudeInfo();
         this.altitudeInfo.decode(nib);
      }
   }

   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(this.optionals, 3);
      this.latitudeSign.encode(stuff);
      stuff.putBits(this.latitude, 23);
      stuff.putBits(this.longitude - -8388608, 24);
      if ((this.optionals & 4) == 4) {
         this.uncertainty.encode(stuff);
      }

      if ((this.optionals & 2) == 2) {
         stuff.putBits(this.confidence, 7);
      }

      if ((this.optionals & 1) == 1) {
         this.altitudeInfo.encode(stuff);
      }
   }

   final void print() {
      System.out.println("Position Est: ");
      System.out.println(((StringBuffer)(new Object("Optionals: "))).append(this.optionals).toString());
      if ((this.optionals & 4) == 4) {
         this.uncertainty.print();
      }

      if ((this.optionals & 2) == 2) {
         System.out.println(((StringBuffer)(new Object("Confidence: "))).append(this.confidence).toString());
      }

      if ((this.optionals & 1) == 1) {
         this.altitudeInfo.print();
      }
   }
}
