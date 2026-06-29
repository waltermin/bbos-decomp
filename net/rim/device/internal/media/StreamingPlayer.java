package net.rim.device.internal.media;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.MetaDataControl;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.media.metadata.ID3v1Reader;
import net.rim.device.internal.media.metadata.ID3v2Reader;
import net.rim.device.internal.media.metadata.MP4Info;
import net.rim.device.internal.util.RingBuffer;
import net.rim.vm.Array;

class StreamingPlayer extends PlayerImpl implements MediaStreamingCallback, VolumeControl {
   private int _volume = 70;
   private boolean _mute;
   private int _interruptable = 0;
   private int _codec = -1;
   private boolean _alreadyPlayed = false;
   private int _dataChunkSize;
   private int _bytesRead = 0;
   private long _duration = -1;
   private MetaDataControl _metaDataControl;
   private RingBuffer _tuneBuffer;
   private SourceStream _tuneSourceStream;
   private DataSource _dataSource;
   private MediaStreamingManager _streamingManager = MediaStreamingManager.getInstance();
   private MediaStreamingManager$StreamingSession _streamingSession;
   private static final String ROUTE_AS_ALERT = "route_as_alert";
   private static String PCM_TYPE = "audio/basic";
   private static String WAVE_TYPE = "audio/x-wav";
   private static String MP3_TYPE = "audio/mpeg";
   private static String AMR_TYPE = "audio/amr";
   private static String MP4_TYPE = "audio/mp4";
   private static String AAC_TYPE = "audio/aac";
   private static String THREEGP_TYPE = "audio/3gpp";
   private static String THREEGPP2_TYPE = "audio/3gpp2";
   private static String GSM610_TYPE = "audio/x-gsm";

   public StreamingPlayer() {
      this._dataChunkSize = this.getStreamingChunkSize(-1);
      this._tuneBuffer = new RingBuffer(this._dataChunkSize);
      super._audioSourceId = 10;
   }

   @Override
   protected void setContentType(String contentType) {
      super.setContentType(contentType);
      if (MP3_TYPE.equals(contentType)) {
         this._codec = 3;
      } else if (MP4_TYPE.equals(contentType) || AAC_TYPE.equals(contentType) || THREEGP_TYPE.equals(contentType) || THREEGPP2_TYPE.equals(contentType)) {
         this._codec = 10;
      } else if (WAVE_TYPE.equals(contentType)) {
         this._codec = 0;
      } else if (AMR_TYPE.equals(contentType)) {
         this._codec = 7;
      } else if (GSM610_TYPE.equals(contentType)) {
         this._codec = 11;
      } else {
         if (PCM_TYPE.equals(contentType)) {
            this._codec = 9;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void playTune() {
      if (this._streamingManager != null && this._codec != -1) {
         this._bytesRead = 0;
         if (this._alreadyPlayed) {
            boolean var3 = false /* VF: Semaphore variable */;

            try {
               var3 = true;
               if (this._tuneSourceStream.getSeekType() != 2 && this._tuneSourceStream.getSeekType() != 1) {
                  this.notifyListeners("error", "Seek to start failed. Media cannot be played again.");
                  var3 = false;
               } else {
                  this._tuneSourceStream.seek(0);
                  this._tuneBuffer.clear();
                  var3 = false;
               }
            } finally {
               if (var3) {
                  return;
               }
            }
         }

         this.read(this._tuneSourceStream);
         this.starting();
         int size = (int)Math.min(this._tuneSourceStream.getContentLength(), Integer.MAX_VALUE);
         this._streamingSession = this._streamingManager
            .playStream(this, this._tuneBuffer, this._codec, size, this._interruptable, this.isMuted() ? 0 : this._volume);
         this._alreadyPlayed = true;
         super._state = 400;
         this.notifyListeners("started", new Long(this.getMediaTime()));
      }
   }

   @Override
   protected void read(InputStream stream) throws MediaException {
      throw new MediaException();
   }

   protected boolean read(SourceStream stream) {
      int bytesRead = 0;

      label47:
      try {
         bytesRead = this._tuneBuffer.write(new DataSourceInputStream(stream));
      } finally {
         break label47;
      }

      if (bytesRead > 0) {
         this._bytesRead += bytesRead;
      }

      long contentLength = stream.getContentLength();
      return bytesRead < 0 || this._bytesRead >= contentLength && contentLength > -1;
   }

   public void setInterruptable(int interruptable) {
      this._interruptable = interruptable;
   }

   public int isInterruptable() {
      return this._interruptable;
   }

   public void setVolume(int volume) {
      this._volume = MathUtilities.clamp(0, volume, 100);
      if (this._streamingSession != null && !this.isMuted()) {
         this._streamingSession.setVolume(this._volume);
      }
   }

   public int getVolume() {
      return this._streamingSession != null && !this.isMuted() ? this._streamingSession.getVolume() : this._volume;
   }

   @Override
   public boolean moreData() {
      return !this.read(this._tuneSourceStream);
   }

   @Override
   public void recordingDone(int reason, int data) {
   }

   @Override
   public boolean sessionSourceEnded() {
      return false;
   }

   @Override
   public void streamingDone(int reason) {
      this._streamingSession = null;
      this.stopped();
      this.onDone(reason);
   }

   @Override
   public void streamingSentAllData() {
      this._streamingSession.stopWhenComplete();
   }

   @Override
   protected void doStart() {
      this.playTune();
   }

   @Override
   protected void doStop() {
      super._currentLoopIteration = 0;
      if (this._streamingManager != null && this._streamingSession != null) {
         this._streamingSession.stop();
         this._streamingSession = null;
      }
   }

   private int readArray(byte[] b) {
      return this.readArray(b, 0, b.length);
   }

   private int readArray(byte[] b, int offset, int length) {
      int bytesRead = 0;
      int newBytesRead = 0;

      do {
         newBytesRead = this._tuneSourceStream.read(b, offset + bytesRead, length - bytesRead);
         if (newBytesRead <= 0) {
            break;
         }

         bytesRead += newBytesRead;
      } while (bytesRead < length);

      return bytesRead;
   }

   private boolean checkForID3() {
      return this._codec == 3 || this._codec == 10 && "audio/aac".equals(super._type);
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void doRealize() throws MediaException {
      super.doRealize();
      if (this._tuneSourceStream != null) {
         long contentLengthExcludingID3v2Tag = this._tuneSourceStream.getContentLength();
         byte[] buffer = null;
         int offset = 0;
         if (this._metaDataControl == null) {
            if (!this.checkForID3()) {
               if (this._codec == 10 && this._tuneSourceStream.getSeekType() == 2 && "audio/mp4".equals(super._type)) {
                  try {
                     long startPosition = this._tuneSourceStream.tell();
                     MP4Info mp4Info = new MP4Info(new DataSourceInputStream(this._tuneSourceStream));
                     this._metaDataControl = mp4Info.getMetaData();
                     if (this._duration == -1) {
                        this._duration = mp4Info.getDuration();
                     }

                     this._tuneSourceStream.seek(startPosition);
                  } catch (Throwable var51) {
                     throw new MediaException(ioe.getMessage());
                  }
               }
            } else {
               buffer = new byte[10];

               try {
                  int bytesRead = this.readArray(buffer);
                  if (bytesRead == 10) {
                     int tagSize = -1;
                     boolean var43 = false /* VF: Semaphore variable */;

                     label537:
                     try {
                        var43 = true;
                        tagSize = ID3v2Reader.getTagSize(buffer);
                        if (tagSize > 2097152) {
                           tagSize = -1;
                           throw new IllegalArgumentException();
                        }

                        var43 = false;
                     } finally {
                        if (var43) {
                           this._tuneBuffer.write(buffer, 0, bytesRead);
                           offset += bytesRead;
                           break label537;
                        }
                     }

                     if (tagSize > 0) {
                        Array.resize(buffer, tagSize);
                        this.readArray(buffer, 10, tagSize - 10);
                        contentLengthExcludingID3v2Tag -= tagSize;

                        label531:
                        try {
                           this._metaDataControl = ID3v2Reader.readTag(buffer);
                        } finally {
                           break label531;
                        }
                     }
                  } else if (bytesRead > 0) {
                     this._tuneBuffer.write(buffer, 0, bytesRead);
                     offset += bytesRead;
                  }
               } catch (Throwable var55) {
                  throw new MediaException(ioe.getMessage());
               }
            }
         }

         if (this._duration == -1 && (this._codec == 3 || this._codec == 0 || this._codec == 7 || this._codec == 10 && "audio/aac".equals(super._type))) {
            int durationBufferSize = 4096;
            switch (this._codec) {
               case 0:
                  durationBufferSize = 2048;
                  break;
               case 10:
                  durationBufferSize = 16384;
            }

            durationBufferSize = Math.min(durationBufferSize, this._tuneBuffer.getBufferSize());
            if (buffer == null) {
               buffer = new byte[durationBufferSize];
            } else {
               Array.resize(buffer, durationBufferSize);
            }

            try {
               int bytesRead = this.readArray(buffer, offset, buffer.length - offset);
               if (bytesRead < buffer.length - offset) {
                  Array.resize(buffer, offset + bytesRead);
               }
            } catch (Throwable var52) {
               throw new MediaException(ioe.getMessage());
            }

            this.setDuration(buffer, contentLengthExcludingID3v2Tag);
            this._tuneBuffer.write(buffer, offset, buffer.length - offset);
         }

         if (this._metaDataControl == null && this.checkForID3() && this._tuneSourceStream.getSeekType() == 2) {
            long contentLength = this._tuneSourceStream.getContentLength();
            long tagOffset = contentLength - 128;
            if (tagOffset > 0) {
               try {
                  long startPosition = this._tuneSourceStream.tell();
                  if (this._tuneSourceStream.seek(tagOffset) == tagOffset) {
                     byte[] tagID = new byte[3];
                     if (this.readArray(tagID) == tagID.length && tagID[0] == 84 && tagID[1] == 65 && tagID[2] == 71) {
                        byte[] tag = new byte[125];
                        if (this.readArray(tag) == tag.length) {
                           this._metaDataControl = ID3v1Reader.readTag(tag);
                        }
                     }
                  }

                  this._tuneSourceStream.seek(startPosition);
                  return;
               } catch (Throwable var50) {
                  throw new MediaException(ioe.getMessage());
               }
            }
         }
      }
   }

   @Override
   protected void doDeallocate() {
   }

   private void onDone(int reason) {
      if (super._state > 100) {
         if (reason == 2) {
            Long mediaTime = new Long(this.getMediaTime());
            this.notifyListeners("endOfMedia", mediaTime);
            boolean playAgain = super._loopCount == -1;
            if (!playAgain && super._currentLoopIteration < super._loopCount - 1) {
               super._currentLoopIteration++;
               playAgain = true;
            }

            if (playAgain) {
               super._app.invokeLater(new StreamingPlayer$1(this));
               return;
            }

            super._currentLoopIteration = 0;
            super._state = 300;
            return;
         }

         if (reason != 3) {
            super._currentLoopIteration = 0;
            super._state = 300;
         }
      }
   }

   @Override
   public void setKeyValue(String key, Object value) {
      if ("interrupt_on_user_input".equals(key)) {
         if (value instanceof Integer) {
            this.setInterruptable((Integer)value);
            return;
         }
      } else if ("datasource".equals(key)) {
         this._dataSource = (DataSource)value;
         SourceStream[] streams = this._dataSource.getStreams();
         this._tuneSourceStream = streams[0];
         if (this._tuneSourceStream.getSeekType() != 2) {
            this._tuneSourceStream = new CacheEnabledSourceStream(this._tuneSourceStream);
            return;
         }
      } else {
         if ("audiosource".equals(key)) {
            super._audioSourceId = (Integer)value;
            return;
         }

         super.setKeyValue(key, value);
      }
   }

   @Override
   protected void doDataSourceClose() {
      if (this._dataSource != null) {
         this._dataSource.disconnect();
      }
   }

   @Override
   public Control getControl(String controlType) {
      this.chkClosed(true);
      controlType = this.getFullyQualifiedControlType(controlType);
      if ("javax.microedition.media.control.VolumeControl".equals(controlType)) {
         return this;
      } else if (this._metaDataControl == null
         || !"javax.microedition.media.control.MetaDataControl".equals(controlType)
            && !"net.rim.device.api.media.control.BinaryMetaDataControl".equals(controlType)) {
         return "net.rim.device.api.media.control.AudioPathControl".equals(controlType) ? this : super.getControl(controlType);
      } else {
         return this._metaDataControl;
      }
   }

   @Override
   public Control[] getControls() {
      Control[] controls = super.getControls();
      if (controls == null) {
         controls = new Control[this._metaDataControl != null ? 2 : 1];
         controls[0] = this;
         if (controls.length > 1) {
            controls[1] = this._metaDataControl;
            return controls;
         }
      } else {
         int tail = controls.length;
         Array.resize(controls, tail + (this._metaDataControl != null ? 2 : 1));
         controls[tail] = this;
         if (tail + 1 < controls.length) {
            controls[tail + 1] = this._metaDataControl;
         }
      }

      return controls;
   }

   @Override
   public long getDuration() {
      this.chkClosed(false);
      return this._duration;
   }

   private void setDuration(byte[] data, long fullLength) {
      if (data != null) {
         label30:
         try {
            switch (this._codec) {
               case 0:
                  this._duration = getWavDuration(new ByteArrayInputStream(data));
                  return;
               case 3:
               case 10:
                  this._duration = MpegDuration.getDuration(data, fullLength);
                  return;
               case 7:
                  this._duration = AmrDuration.getDuration(data, fullLength);
                  return;
            }
         } finally {
            break label30;
         }
      }

      this._duration = -1;
   }

   private static long getWavDuration(InputStream is) {
      try {
         if (readIntLittleEndian(is) == 1179011410) {
            skipFully(is, 4);
            if (readIntLittleEndian(is) == 1163280727) {
               int bytesPerSecond = -1;
               int dataSize = -1;
               boolean readFmtSubchunk = false;
               boolean readDataSubchunk = false;

               do {
                  int subchunkID = readIntLittleEndian(is);
                  int subchunkSize = readIntLittleEndian(is);
                  if (subchunkSize < 0) {
                     return -1;
                  }

                  if ((subchunkSize & 1) != 0) {
                     subchunkSize++;
                  }

                  switch (subchunkID) {
                     case 544501094:
                        readFmtSubchunk = true;
                        if (subchunkSize < 16) {
                           return -1;
                        }

                        skipFully(is, 2);
                        int numChannels = readUnsignedShortLittleEndian(is);
                        int sampleRate = readIntLittleEndian(is);
                        bytesPerSecond = readIntLittleEndian(is);
                        skipFully(is, 2);
                        int bitsPerSample = readUnsignedShortLittleEndian(is);
                        if (sampleRate > 0 && numChannels > 0 && bitsPerSample > 0) {
                           bytesPerSecond = sampleRate * numChannels * bitsPerSample / 8;
                        }

                        skipFully(is, subchunkSize - 16);
                        break;
                     case 1635017060:
                        readDataSubchunk = true;
                        dataSize = subchunkSize;
                        if (!readFmtSubchunk) {
                           skipFully(is, subchunkSize);
                        }
                        break;
                     default:
                        skipFully(is, subchunkSize);
                  }
               } while (!readFmtSubchunk || !readDataSubchunk);

               if (dataSize > 0 && bytesPerSecond > 0) {
                  return (long)dataSize * 1000000 / bytesPerSecond;
               }
            }
         }
      } finally {
         return -1;
      }

      return -1;
   }

   private static int readIntLittleEndian(InputStream is) throws EOFException {
      int b1 = is.read();
      int b2 = is.read();
      int b3 = is.read();
      int b4 = is.read();
      if ((b1 | b2 | b3 | b4) < 0) {
         throw new EOFException();
      } else {
         return b4 << 24 | b3 << 16 | b2 << 8 | b1;
      }
   }

   private static int readUnsignedShortLittleEndian(InputStream is) throws EOFException {
      int b1 = is.read();
      int b2 = is.read();
      if ((b1 | b2) < 0) {
         throw new EOFException();
      } else {
         return b2 << 8 | b1;
      }
   }

   private static void skipFully(InputStream is, int numBytes) throws IOException {
      while (numBytes > 0) {
         int skipped = (int)is.skip(numBytes);
         if (skipped <= 0) {
            throw new IOException();
         }

         numBytes -= skipped;
      }
   }

   @Override
   public boolean isMuted() {
      return this._mute;
   }

   @Override
   public void setMute(boolean mute) {
      boolean oldMute = this.isMuted();
      this._mute = mute;
      if (oldMute != mute) {
         if (this._streamingSession != null) {
            this._streamingSession.setVolume(mute ? 0 : this._volume);
         }

         this.notifyListeners("volumeChanged", this);
      }
   }

   @Override
   public int getLevel() {
      return this.getVolume();
   }

   @Override
   public int setLevel(int level) {
      level = MathUtilities.clamp(0, level, 100);
      int oldLevel = this.getLevel();
      this.setVolume(level);
      if (oldLevel != level) {
         this.notifyListeners("volumeChanged", this);
      }

      return level;
   }
}
