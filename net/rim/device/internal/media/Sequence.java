package net.rim.device.internal.media;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.microedition.media.MediaException;

class Sequence {
   private MidiModel _model;
   private byte[] _data;
   private HeaderChunk _header;
   private TrackChunk[] _tracks;
   public static final int PPQ = 0;
   public static final int SMPTE_24 = 1;
   public static final int SMPTE_25 = 2;
   public static final int SMPTE_30DROP = 3;
   public static final int SMPTE_30 = 4;

   public Sequence(MidiModel model, byte[] data) throws MediaException {
      this._model = model;
      this._data = data;
      InputStream stream = new ByteArrayInputStream(this._data);
      MidiInputStream midiStream = new MidiInputStream(this, stream);
      this._header = (HeaderChunk)midiStream.readChunk();
      if (this._header == null) {
         throw new MediaException();
      }

      this._tracks = new TrackChunk[this._header.getNumTracks()];
      int currTrack = 0;

      while (true) {
         MidiChunk chunk = midiStream.readChunk();
         if (chunk == null) {
            return;
         }

         if (chunk instanceof TrackChunk) {
            TrackChunk track = (TrackChunk)chunk;
            this._tracks[currTrack] = track;
            currTrack++;
         }
      }
   }

   public int getDivision() {
      return this.getDivisionType() == 0 ? this.getResolution() : 0;
   }

   public int getDivisionType() {
      return this._header.getDivisionType();
   }

   public MidiModel getModel() {
      return this._model;
   }

   public int getResolution() {
      return this._header.getResolution();
   }

   public TrackChunk getTrack(int track) {
      return this._tracks[track];
   }

   public int getNumTracks() {
      return this._tracks.length;
   }

   public int getTicksPerSecond() {
      int microsecPerQuarter = 500000;
      int ticksPerSecond = 0;
      switch (this.getDivisionType()) {
         case 0:
         default:
            int division = this.getResolution();
            return division * 1000000 / microsecPerQuarter;
         case 1:
            return this.getResolution() * 24;
         case 2:
            return this.getResolution() * 25;
         case 3:
            return this.getResolution() * 2997 / 100;
         case 4:
            ticksPerSecond = this.getResolution() * 30;
         case -1:
            return ticksPerSecond;
      }
   }
}
