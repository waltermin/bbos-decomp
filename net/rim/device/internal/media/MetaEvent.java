package net.rim.device.internal.media;

class MetaEvent extends MidiEvent {
   private int _type;
   private byte[] _data;
   public static final int TYPE_SEQUENCE_NUBER = 0;
   public static final int TYPE_TEXT = 1;
   public static final int TYPE_COPYRIGHT = 2;
   public static final int TYPE_NAME = 3;
   public static final int TYPE_INSTRUMENT_NAME = 4;
   public static final int TYPE_LYRIC = 5;
   public static final int TYPE_MARKER = 6;
   public static final int TYPE_CUE_POINT = 7;
   public static final int TYPE_CHANNEL_PREFIX = 32;
   public static final int TYPE_END_OF_TRACK = 47;
   public static final int TYPE_TEMPO = 81;
   public static final int TYPE_SMPTE_OFFSET = 84;
   public static final int TYPE_TIME_SIGNATURE = 88;
   public static final int TYPE_KEY_SIGNATURE = 89;
   public static final int TYPE_SEQUENCER_SPECIFIC = 127;

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
