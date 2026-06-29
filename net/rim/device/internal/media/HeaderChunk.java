package net.rim.device.internal.media;

class HeaderChunk extends MidiChunk {
   private short _format;
   private short _numTracks;
   private short _resolution;
   private int _divisionType;

   public HeaderChunk(Sequence sequence, int id, int length, byte[] data) {
      super(sequence, id, length, data);
      this._format = (short)(data[0] << 8 | data[1] & 0xFF);
      this._numTracks = (short)(data[2] << 8 | data[3] & 0xFF);
      this._resolution = (short)(data[4] << 8 | data[5] & 0xFF);
      if (this._resolution > 0) {
         this._divisionType = 0;
      } else {
         int smpte = this._resolution >> 8;
         switch (smpte) {
            case -30:
               this._divisionType = 4;
               break;
            case -29:
               this._divisionType = 3;
               break;
            case -25:
               this._divisionType = 2;
               break;
            case -24:
               this._divisionType = 1;
         }

         short resolution = (short)(this._resolution & 0xFF);
         this._resolution = resolution;
      }
   }

   short getFormat() {
      return this._format;
   }

   short getNumTracks() {
      return this._numTracks;
   }

   short getResolution() {
      return this._resolution;
   }

   int getDivisionType() {
      return this._divisionType;
   }
}
