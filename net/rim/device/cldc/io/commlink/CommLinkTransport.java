package net.rim.device.cldc.io.commlink;

class CommLinkTransport {
   void sendReply(int _1) {
      throw null;
   }

   int sendDataPacket(int _1, byte[] _2, int _3, int _4, boolean _5, boolean _6) {
      throw null;
   }

   int readFrame(long _1) {
      throw null;
   }

   int readFrame() {
      return this.readFrame(0);
   }

   boolean open() {
      throw null;
   }

   void checkSpeed() {
      throw null;
   }

   void close() {
      throw null;
   }

   void standbyMode() {
      throw null;
   }

   int sendChallenge(int _1) {
      throw null;
   }

   boolean checkResponse(byte[] _1, int _2, int _3) {
      throw null;
   }

   boolean needsResponse() {
      throw null;
   }

   void die() {
      throw null;
   }
}
