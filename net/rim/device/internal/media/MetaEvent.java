package net.rim.device.internal.media;

class MetaEvent extends MidiEvent {
   private int _type;
   private byte[] _data;
   public static final int TYPE_SEQUENCE_NUBER;
   public static final int TYPE_TEXT;
   public static final int TYPE_COPYRIGHT;
   public static final int TYPE_NAME;
   public static final int TYPE_INSTRUMENT_NAME;
   public static final int TYPE_LYRIC;
   public static final int TYPE_MARKER;
   public static final int TYPE_CUE_POINT;
   public static final int TYPE_CHANNEL_PREFIX;
   public static final int TYPE_END_OF_TRACK;
   public static final int TYPE_TEMPO;
   public static final int TYPE_SMPTE_OFFSET;
   public static final int TYPE_TIME_SIGNATURE;
   public static final int TYPE_KEY_SIGNATURE;
   public static final int TYPE_SEQUENCER_SPECIFIC;

   MetaEvent(byte type, int length, byte[] data, int tick) {
      super(255, tick);
      this._type = type;
      this._data = data;
   }

   public byte[] getData() {
      return this._data;
   }

   public int getType() {
      return this._type;
   }
}
