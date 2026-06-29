package net.rim.device.internal.media;

import java.util.Vector;

class TrackChunk extends MidiChunk {
   private Vector _events = new Vector();
   private int _offset;
   private boolean _containsNoteOn;
   private long _lastEventTime;
   private static final byte[] EVENT_SKIP_TABLE = new byte[]{2, 2, 2, 2, 1, 1, 2};

   public TrackChunk(Sequence sequence, int id, int length, byte[] data) {
      super(sequence, id, length, data);
      this.readTrack();
   }

   private void append(MidiEvent event) {
      if (super._sequence.getModel().isCompiled()) {
         this._events.addElement(event);
      }

      if (event.getCode() == 144) {
         this._containsNoteOn = true;
      }
   }

   public boolean containsNoteOn() {
      return this._containsNoteOn;
   }

   public MidiEvent get(int index) {
      return (MidiEvent)this._events.elementAt(index);
   }

   public long getLastEventTime() {
      return this._lastEventTime;
   }

   void readTrack() {
      this._offset = 0;
      MidiEvent event = null;
      int time = 0;
      int runningStatus = 0;
      int microsecPerQuarter = 500000;
      int division = super._sequence.getDivision();
      int ticksPerSecond = super._sequence.getTicksPerSecond();

      while (this._offset != this.getLength()) {
         int deltatime = this.readNumber();
         time += deltatime;
         int eventCode = this.read();
         if ((eventCode & 128) != 0) {
            runningStatus = eventCode;
         } else {
            this.skip(-1);
            eventCode = runningStatus;
         }

         if (eventCode >= 128) {
            switch (eventCode & 240) {
               case 128:
                  byte var18 = (byte)this.read();
                  this.read();
                  event = new NoteEvent(128, var18, time);
                  this.append(event);
                  break;
               case 144:
                  byte note = (byte)this.read();
                  this.read();
                  event = new NoteEvent(144, note, time);
                  this.append(event);
                  break;
               case 240:
                  runningStatus = 0;
                  if (eventCode != 255) {
                     int length = this.readNumber();
                     this.skip(length);
                  } else {
                     byte type = (byte)this.read();
                     int length = this.readNumber();
                     byte[] data = this.read(length);
                     MetaEvent metaEvent = new MetaEvent(type, length, data, time);
                     event = metaEvent;
                     this.append(metaEvent);
                     switch (metaEvent.getType()) {
                        case 1:
                        case 2:
                           super._sequence.getModel().setMetaData(metaEvent);
                           break;
                        case 81:
                           if (division != 0) {
                              byte[] bytes = metaEvent.getData();
                              microsecPerQuarter = ((bytes[0] & 255) << 16) + ((bytes[1] & 255) << 8) + (bytes[2] & 255);
                              ticksPerSecond = division * 1000000 / microsecPerQuarter;
                           }
                     }
                  }
                  break;
               default:
                  event = new MidiEvent(eventCode & 240, time);
                  this.append(event);
                  this.skip(EVENT_SKIP_TABLE[((eventCode & 240) >> 4) - 8]);
            }
         }
      }

      if (event != null) {
         long lastEventTickInTrack = event.getTick();
         this._lastEventTime = lastEventTickInTrack * 1000 / ticksPerSecond;
      }
   }

   void skip(int length) {
      this._offset += length;
   }

   int read() {
      return super._data[this._offset++] & 0xFF;
   }

   byte[] read(int length) {
      byte[] data = new byte[length];
      System.arraycopy(super._data, this._offset, data, 0, length);
      this._offset += length;
      return data;
   }

   int readNumber() {
      int acc = 0;

      int b;
      do {
         b = this.read();
         acc = acc << 7 | b & 127;
      } while ((b & 128) != 0);

      return acc;
   }

   public int size() {
      return this._events.size();
   }
}
