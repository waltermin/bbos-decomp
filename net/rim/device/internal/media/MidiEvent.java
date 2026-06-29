package net.rim.device.internal.media;

class MidiEvent {
   private int _code;
   private long _tick;
   public static final int EVENT_META;
   public static final int NOTE_ON;
   public static final int NOTE_OFF;

   MidiEvent(int code, int tick) {
      this._code = code;
      this._tick = tick;
   }

   public int getCode() {
      return this._code;
   }

   public long getTick() {
      return this._tick;
   }
}
