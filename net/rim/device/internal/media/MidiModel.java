package net.rim.device.internal.media;

import javax.microedition.media.control.MetaDataControl;
import net.rim.device.api.system.Alert;
import net.rim.vm.Array;

class MidiModel implements MetaDataControl {
   private byte[] _data;
   private Sequence _sequence;
   private boolean _compiled;
   private String _title;
   private String _copyright;
   private int _polyphonic;
   private long _duration;
   private short[] _buzzerTune;
   static final short[] NOTE_FREQUENCIES = TonePlayer._noteFreq;
   static final int SECONDS_PER_MINUTE = 60;
   static final int MICRO_PER_UNIT = 1000000;
   private static final int POLYPHONIC_UNKNOWN = 0;
   private static final int POLYPHONIC_YES = 1;
   private static final int POLYPHONIC_NO = 2;
   private static final String POLYPHONIC_KEY = "polyphonic";

   public String getCopyright() {
      return this._copyright;
   }

   public Sequence getSequence() {
      return this._sequence;
   }

   public String getTitle() {
      return this._title;
   }

   public boolean isCompiled() {
      return this._compiled;
   }

   public boolean isPolyphonic() {
      if (this._polyphonic == 0) {
         boolean polyphonic = false;
         boolean notesFound = false;
         Sequence seq = this.getSequence();
         int numTracks = seq.getNumTracks();

         for (int trackNum = 0; trackNum < numTracks; trackNum++) {
            TrackChunk track = seq.getTrack(trackNum);
            if (track.containsNoteOn()) {
               if (notesFound) {
                  polyphonic = true;
                  break;
               }

               notesFound = true;
            }
         }

         this._polyphonic = polyphonic ? 1 : 2;
      }

      return this._polyphonic == 1;
   }

   void setMetaData(MetaEvent metaEvent) {
      switch (metaEvent.getType()) {
         case 0:
            break;
         case 1:
         default:
            if (this._title == null) {
               byte[] data = metaEvent.getData();
               if (data != null && data.length > 2 && data[0] == 64 && data[1] == 84) {
                  this._title = (String)(new Object(data, 2, data.length - 2));
                  return;
               }
            }
            break;
         case 2:
            this._copyright = (String)(new Object(metaEvent.getData()));
      }
   }

   public long setMediaTime(long mediaTime) {
      if (Alert.isMIDISupported()) {
         return 0;
      }

      mediaTime /= 1000;
      if (mediaTime >= this._duration) {
         mediaTime = this._duration;
      } else if (mediaTime < 0) {
         mediaTime = 0;
      }

      if (mediaTime != 0 && mediaTime == this._duration) {
      }

      return mediaTime * 1000;
   }

   public long getDuration() {
      return this._duration * 1000;
   }

   public short[] getBuzzerTune() {
      if (this._buzzerTune == null) {
         this.compile(true);
      }

      return this._buzzerTune;
   }

   @Override
   public String getKeyValue(String key) {
      String value = null;
      if ("copyright".equals(key)) {
         value = this.getCopyright();
      } else if ("title".equals(key)) {
         value = this.getTitle();
      } else if ("polyphonic".equals(key)) {
         value = String.valueOf(this.isPolyphonic());
      }

      if (value == null) {
         throw new Object(((StringBuffer)(new Object("key "))).append(key).append(" is not a valid argument.").toString());
      } else {
         return value;
      }
   }

   @Override
   public String[] getKeys() {
      String copyright = this._copyright;
      String title = this._title;
      int numKeys = 1;
      if (copyright != null) {
         numKeys++;
      }

      if (title != null) {
         numKeys++;
      }

      String[] keys = new Object[numKeys];
      keys[0] = "polyphonic";
      if (copyright != null) {
         keys[1] = "copyright";
      }

      if (title != null) {
         keys[numKeys - 1] = "title";
      }

      return keys;
   }

   private void addNote(int frequency, int duration) {
      int length = this._buzzerTune.length;
      Array.resize(this._buzzerTune, length + 2);
      this._buzzerTune[length] = (short)frequency;
      this._buzzerTune[length + 1] = (short)duration;
   }

   private long getLastEventTick() {
      long lastEventTime = 0;
      int numTracks = this._sequence.getNumTracks();

      for (int trackIndex = 0; trackIndex < numTracks; trackIndex++) {
         TrackChunk track = this._sequence.getTrack(trackIndex);
         long lastEventTimeForTrack = track.getLastEventTime();
         if (lastEventTimeForTrack > lastEventTime) {
            lastEventTime = lastEventTimeForTrack;
         }
      }

      return lastEventTime;
   }

   public MidiModel(byte[] data) {
      this._data = data;
      this._sequence = new Sequence(this, this._data);
      this._buzzerTune = this._buzzerTune;
      this._duration = this.getLastEventTick();
   }

   private void compile(boolean allocateTracks) {
      this._compiled = true;
      this._buzzerTune = new short[0];
      int microsecPerQuarter = 500000;
      long longestTrackDuration = 0;
      boolean notesFound = false;
      MidiEvent event = null;
      Sequence seq = this._sequence;
      int numTracks = seq.getNumTracks();

      for (int trackIndex = 0; !notesFound && trackIndex < numTracks; trackIndex++) {
         TrackChunk track = this._sequence.getTrack(trackIndex);
         if (track.size() == 0) {
            track.readTrack();
         }

         int division = seq.getDivision();
         int ticksPerSecond = seq.getTicksPerSecond();
         int end = track.size();
         if (end == 0) {
            return;
         }

         int oldFreq = 0;
         event = track.get(0);

         for (int lv = 1; lv < end; lv++) {
            MidiEvent nextEvent = track.get(lv);
            int code = event.getCode();
            if (code == 255) {
               MetaEvent metaEvent = (MetaEvent)event;
               switch (metaEvent.getType()) {
                  case 81:
                     if (division != 0) {
                        byte[] bytes = metaEvent.getData();
                        microsecPerQuarter = ((bytes[0] & 255) << 16) + ((bytes[1] & 255) << 8) + (bytes[2] & 255);
                        ticksPerSecond = division * 1000000 / microsecPerQuarter;
                     }
               }
            } else if (code != 144) {
               if (code == 128 && ((NoteEvent)event).getNote() >= 56) {
                  long currentTimestamp = event.getTick();
                  long newTimestamp = nextEvent.getTick();
                  int duration = (int)((newTimestamp - currentTimestamp) * 1000 / ticksPerSecond);
                  if (duration > 0) {
                     oldFreq = 0;
                  }
               }
            } else if (((NoteEvent)event).getNote() >= 56) {
               int currentFreq = NOTE_FREQUENCIES[((NoteEvent)event).getNote()];

               for (int nextCode = nextEvent.getCode(); nextCode != 144 && nextCode != 255; nextCode = nextEvent.getCode()) {
                  if (nextCode == 128) {
                     int nextFreq = NOTE_FREQUENCIES[((NoteEvent)nextEvent).getNote()];
                     if (nextFreq == currentFreq) {
                        break;
                     }
                  }

                  if (++lv >= end) {
                     break;
                  }

                  nextEvent = track.get(lv);
               }

               long currentTimestamp = event.getTick();
               long newTimestamp = nextEvent.getTick();
               int duration = (int)((newTimestamp - currentTimestamp) * 1000 / ticksPerSecond);
               if (duration > 0) {
                  if (oldFreq == currentFreq) {
                     this.addNote(0, 10);
                  }

                  this.addNote(currentFreq, duration);
                  notesFound = true;
                  oldFreq = currentFreq;
               }
            }

            event = nextEvent;
         }

         long lastEventTickInTrack = event.getTick();
         long trackDurationBasedOnLastTick = lastEventTickInTrack * 1000 / ticksPerSecond;
         if (trackDurationBasedOnLastTick > longestTrackDuration) {
            longestTrackDuration = trackDurationBasedOnLastTick;
         }
      }
   }
}
