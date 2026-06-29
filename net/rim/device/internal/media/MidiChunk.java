package net.rim.device.internal.media;

class MidiChunk {
   Sequence _sequence;
   int _id;
   int _length;
   byte[] _data;
   public static final int CHUNK_HEADER;
   public static final int CHUNK_TRACK;

   public MidiChunk(Sequence sequence, int id, int length, byte[] data) {
      this._sequence = sequence;
      this._id = id;
      this._length = length;
      this._data = data;
   }

   public int getLength() {
      return this._length;
   }
}
