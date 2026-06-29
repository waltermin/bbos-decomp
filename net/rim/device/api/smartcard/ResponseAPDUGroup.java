package net.rim.device.api.smartcard;

public class ResponseAPDUGroup {
   private ResponseAPDU[] _responseAPDUs;
   private int _numAPDUs;

   public ResponseAPDUGroup() {
      this(0);
   }

   public ResponseAPDUGroup(ResponseAPDU[] responseAPDUs) {
      this(responseAPDUs, responseAPDUs.length);
   }

   public ResponseAPDUGroup(ResponseAPDU[] responseAPDUs, int numAPDUs) {
      this.setAPDUs(responseAPDUs, numAPDUs);
   }

   public ResponseAPDUGroup(int numAPDUs) {
      ResponseAPDU[] responseAPDUs = new ResponseAPDU[numAPDUs];

      for (int i = numAPDUs - 1; i >= 0; i--) {
         responseAPDUs[i] = new ResponseAPDU();
      }

      this.setAPDUs(responseAPDUs, numAPDUs);
   }

   public void setAPDUs(ResponseAPDU[] responseAPDUs) {
      this.setAPDUs(responseAPDUs, responseAPDUs.length);
   }

   public void setAPDUs(ResponseAPDU[] responseAPDUs, int numAPDUs) {
      if (numAPDUs >= 0 && (numAPDUs <= 0 || responseAPDUs != null)) {
         this._responseAPDUs = responseAPDUs;
         this._numAPDUs = numAPDUs;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getNumAPDUs() {
      return this._numAPDUs;
   }

   public ResponseAPDU getAPDU(int index) {
      if (index >= 0 && index <= this._numAPDUs) {
         return this._responseAPDUs[index];
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }
}
