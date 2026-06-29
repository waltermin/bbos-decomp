package net.rim.device.apps.internal.supl;

import net.rim.device.api.gps.GPS;
import net.rim.device.api.gps.GPSRequestedAssistData;

final class RequestedAssistData {
   private boolean almanacReq;
   private boolean utcModelReq;
   private boolean ionosphericModelReq;
   private boolean dgpsCorrectionsReq;
   private boolean referenceLocationReq;
   private boolean referenceTimeReq;
   private boolean acquisitionAssistanceReq;
   private boolean realTimeIntegrityReq;
   private boolean navigationModelReq;
   private NavigationModel navigationModel;
   private byte optionals = 0;
   static final byte REQ_ASSIST_DATA_OPT_NAV_MODEL = 1;
   static final byte NUM_OPT_ELEMENTS = 1;

   RequestedAssistData() {
      GPSRequestedAssistData reqAssistData = (GPSRequestedAssistData)(new Object());
      if (reqAssistData == null) {
         System.out.println("Couldn't create new GPSRequestedAssistData object");
      }

      System.out.println("Getting GPS Requested Assistance Data");
      GPS.getRequestedGPSAssistData(reqAssistData);
      this.almanacReq = reqAssistData.isAlmanac();
      this.utcModelReq = reqAssistData.isUtcModel();
      this.ionosphericModelReq = reqAssistData.isIonosphericModel();
      this.dgpsCorrectionsReq = reqAssistData.isDGpsCorrections();
      this.referenceLocationReq = reqAssistData.isReferenceLocation();
      this.referenceTimeReq = reqAssistData.isReferenceTime();
      this.acquisitionAssistanceReq = reqAssistData.isAcquisitionAssistance();
      this.realTimeIntegrityReq = reqAssistData.isRealTimeIntegrity();
      this.navigationModelReq = reqAssistData.isNavigationModel();
      if (this.navigationModelReq) {
         this.optionals = 1;
         this.navigationModel = new NavigationModel(reqAssistData);
      }
   }

   final boolean isReqAssistElementsPresent() {
      return this.almanacReq
         || this.utcModelReq
         || this.ionosphericModelReq
         || this.dgpsCorrectionsReq
         || this.referenceLocationReq
         || this.referenceTimeReq
         || this.acquisitionAssistanceReq
         || this.realTimeIntegrityReq
         || this.navigationModelReq;
   }

   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(this.optionals, 1);
      stuff.putBit(this.almanacReq);
      stuff.putBit(this.utcModelReq);
      stuff.putBit(this.ionosphericModelReq);
      stuff.putBit(this.dgpsCorrectionsReq);
      stuff.putBit(this.referenceLocationReq);
      stuff.putBit(this.referenceTimeReq);
      stuff.putBit(this.acquisitionAssistanceReq);
      stuff.putBit(this.realTimeIntegrityReq);
      stuff.putBit(this.navigationModelReq);
      if ((this.optionals & 1) == 1) {
         this.navigationModel.encode(stuff);
      }
   }

   final void print() {
      System.out.println("Requested Assistance Data: ");
      System.out.println(((StringBuffer)(new Object("Optionals: "))).append(this.optionals).toString());
      System.out.println(((StringBuffer)(new Object("Almanac: "))).append(this.almanacReq).toString());
      System.out.println(((StringBuffer)(new Object("Utc Model: "))).append(this.utcModelReq).toString());
      System.out.println(((StringBuffer)(new Object("Ionospheric Model: "))).append(this.ionosphericModelReq).toString());
      System.out.println(((StringBuffer)(new Object("DGPS Corrections: "))).append(this.dgpsCorrectionsReq).toString());
      System.out.println(((StringBuffer)(new Object("Reference Location: "))).append(this.referenceLocationReq).toString());
      System.out.println(((StringBuffer)(new Object("Reference Time: "))).append(this.referenceTimeReq).toString());
      System.out.println(((StringBuffer)(new Object("Acquisition Assistance: "))).append(this.acquisitionAssistanceReq).toString());
      System.out.println(((StringBuffer)(new Object("Real Time Integrity: "))).append(this.realTimeIntegrityReq).toString());
      System.out.println(((StringBuffer)(new Object("Navigation Model: "))).append(this.navigationModelReq).toString());
      if ((this.optionals & 1) == 1) {
         this.navigationModel.print();
      }
   }
}
