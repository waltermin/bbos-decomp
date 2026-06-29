package net.rim.device.internal.media;

class NoteEvent extends MidiEvent {
   byte _note;

   NoteEvent(int code, byte note, int tick) {
      super(code, tick);
      this._note = note;
   }

   int getNote() {
      return this._note;
   }
}
