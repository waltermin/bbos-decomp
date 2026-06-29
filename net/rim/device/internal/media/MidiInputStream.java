package net.rim.device.internal.media;

import java.io.DataInputStream;
import java.io.InputStream;

class MidiInputStream extends DataInputStream {
   private Sequence _sequence;

   public MidiInputStream(Sequence sequence, InputStream stream) {
      super(stream);
      this._sequence = sequence;
   }

   public int readNumber() {
      int acc = 0;

      int b;
      do {
         b = this.read();
         acc = acc << 7 | b & 127;
      } while ((b & 128) != 0);

      return acc;
   }

   public MidiChunk readChunk() {
      int id;
      try {
         id = this.readInt();
      } finally {
         ;
      }

      int length = this.readInt();
      MidiChunk chunk = null;
      switch (id) {
         case 1297377380: {
            byte[] data = new byte[length];
            this.read(data);
            return new HeaderChunk(this._sequence, id, length, data);
         }
         case 1297379947: {
            byte[] data = new byte[length];
            this.read(data);
            return new TrackChunk(this._sequence, id, length, data);
         }
         default:
            this.skip(length);
            return chunk;
      }
   }
}
