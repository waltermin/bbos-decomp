package net.rim.device.internal.media;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Canvas;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.MetaDataControl;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.media.control.AuthenticationControl;
import net.rim.device.api.media.control.VideoPositionControl;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.lcdui.LcduiPlayerController;
import net.rim.device.internal.media.metadata.ASFHeader;
import net.rim.device.internal.media.metadata.ID3v1Reader;
import net.rim.device.internal.media.metadata.ID3v2Reader;
import net.rim.device.internal.media.metadata.MP4Info;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.ActiveMedia;
import net.rim.device.internal.util.RingBuffer;
import net.rim.vm.Array;

class StreamingMediaPlayer
   extends PlayerImpl
   implements MediaStreamingCallback,
   VideoControl,
   VideoPositionControl,
   VolumeControl,
   AuthenticationControl,
   MediaEventListener {
   private int _volume = 70;
   private boolean _mute;
   private int _interruptible = 0;
   private boolean _bigSession;
   private int _codec = -1;
   private int _dataChunkSize;
   private int _streams = 3;
   private long _duration = -1;
   private MetaDataControl _metaDataControl;
   private boolean _initDisplayCalled;
   private boolean _seekable;
   private boolean _queuePlayback;
   private boolean _alreadyPlayed;
   private int _retry = 3;
   private long _queuedSeekTime = -1;
   private boolean _loaded;
   private boolean _expectedStop;
   private int _fileHandle = -1;
   private String _rtspUrl;
   private String _rtspUserAgent;
   private int _rtspApnId;
   private RingBuffer _mediaBuffer;
   private SourceStream _mediaSourceStream;
   private DataSourceInputStream _dsInputStream;
   private DataSource _dataSource;
   private int _dataOffset;
   private int _videoX = 0;
   private int _videoY = 0;
   private int _videoWidth = Display.getWidth();
   private int _videoHeight = Display.getHeight();
   private int _contentWidth = -1;
   private int _contentHeight = -1;
   private boolean _fullscreen;
   LcduiPlayerController _controller;
   private int _displayMode = -1;
   private boolean _queuedByVideoControl = false;
   private int _offsetX = 0;
   private int _offsetY = 0;
   private boolean _onScreen = true;
   private boolean _resumeWhenOnScreen = false;
   private MediaStreamingManager _streamingManager;
   private MediaStreamingManager$StreamingSession _streamingSession;
   private MediaPlayer _mediaPlayer;
   private int _timeEventRunnableID = -1;
   private Object _lock = new Object();
   private boolean _usingBluetoothAMRHack = false;
   private int _sinkBeforeHackedForBluetoothAMR = -1;
   private static final String BIG_SESSION_KEY = "big_session";
   private static final String AUDIO = "audio";
   private static final String VIDEO = "video";
   private static String PCM_TYPE = "audio/basic";
   private static String WAVE_TYPE = "audio/x-wav";
   private static String MP3_TYPE = "audio/mpeg";
   private static String AMR_TYPE = "audio/amr";
   private static String MP4_TYPE = "audio/mp4";
   private static String AAC_TYPE = "audio/aac";
   private static String THREEGP_TYPE = "audio/3gpp";
   private static String THREEGPP2_TYPE = "audio/3gpp2";
   private static String GSM610_TYPE = "audioset/x-gsm";
   private static String WMA_TYPE = "audio/x-ms-wma";
   private static String QCELP_TYPE = "audio/qcelp";
   private static String RTSP_TYPE = "application/rtsp";
   private static final boolean DEBUG = false;

   public StreamingMediaPlayer() {
      this._streamingManager = MediaStreamingManager.getInstance();
      this._dataChunkSize = this.getStreamingChunkSize(-1);
      this._mediaBuffer = new RingBuffer(this._dataChunkSize);
      super._mediaTime = -1;
      super._audioSourceId = 10;
   }

   @Override
   public boolean canSwitchToPath(int path) {
      return !super.canSwitchToPath(path) ? false : this._mediaPlayer == null || this._mediaPlayer.isSinkSupported(path);
   }

   @Override
   protected void starting() {
      super.starting();
      if (this._mediaPlayer != null && !this._mediaPlayer.isSinkSupported(this.getAudioPath())) {
         try {
            this._sinkBeforeHackedForBluetoothAMR = this.isPathExplicitlySet() ? this.getAudioPath() : -1;
            this.setAudioPath(super._audioSourceId == 5 ? 0 : 1);
            this._usingBluetoothAMRHack = true;
         } finally {
            return;
         }
      }
   }

   @Override
   public void setAudioPath(int newPath) {
      super.setAudioPath(newPath);
      this._usingBluetoothAMRHack = false;
   }

   @Override
   protected void stopped() {
      if (this._usingBluetoothAMRHack) {
         if (this._sinkBeforeHackedForBluetoothAMR != -1) {
            label22:
            try {
               this.setAudioPath(this._sinkBeforeHackedForBluetoothAMR);
            } finally {
               break label22;
            }
         } else {
            this.resetAudioPath();
         }
      }

      super.stopped();
   }

   @Override
   protected void setContentType(String contentType) {
      if (!contentType.startsWith("audio")) {
         if (contentType.startsWith("video")) {
            super.setContentType(contentType);
            this._codec = 1000;
            this._streams = 3;
            return;
         }

         if (contentType.equals(RTSP_TYPE)) {
            String fileContentType = MIMETypeAssociations.getMIMEType(this._rtspUrl);
            super.setContentType(fileContentType == null ? contentType : fileContentType);
            this._codec = 1000;
            this._streams = 3;
         }
      } else {
         super.setContentType(contentType);
         this._streams = 1;
         if (MP3_TYPE.equals(contentType)) {
            this._codec = 3;
            return;
         }

         if (MP4_TYPE.equals(contentType) || AAC_TYPE.equals(contentType) || THREEGP_TYPE.equals(contentType) || THREEGPP2_TYPE.equals(contentType)) {
            this._codec = 10;
            return;
         }

         if (WAVE_TYPE.equals(contentType)) {
            this._codec = 0;
            return;
         }

         if (AMR_TYPE.equals(contentType)) {
            this._codec = 7;
            return;
         }

         if (WMA_TYPE.equals(contentType)) {
            this._codec = 12;
            return;
         }

         if (GSM610_TYPE.equals(contentType)) {
            this._codec = 11;
            return;
         }

         if (PCM_TYPE.equals(contentType)) {
            this._codec = 9;
            return;
         }

         if (QCELP_TYPE.equals(contentType)) {
            this._codec = 13;
            return;
         }
      }
   }

   @Override
   protected void read(InputStream stream) {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected boolean read() {
      int bytesRead = 0;
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         if (this._dsInputStream == null) {
            this._dsInputStream = new DataSourceInputStream(this._mediaSourceStream);
         }

         bytesRead = this._mediaBuffer.write(this._dsInputStream);
         var4 = false;
      } finally {
         if (var4) {
            this.doDeallocate();
            this.notifyListeners("error", Integer.toString(5));
            return bytesRead < 0;
         }
      }

      return bytesRead < 0;
   }

   public void setInterruptible(int interruptible) {
      this._interruptible = interruptible;
   }

   public int isInterruptible() {
      return this._interruptible;
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
   public void streamingDone(int reason) {
      this._streamingSession = null;
      System.out.println("Streaming done reason=" + reason + " prev-state=" + super._state);
      this.onDone(reason);
   }

   @Override
   public boolean moreData() {
      return this._mediaPlayer == null || !this._mediaPlayer.isAlive() ? false : !this.read();
   }

   @Override
   public void streamingSentAllData() {
      if (this._mediaPlayer != null) {
         this._mediaPlayer.signifyNoMoreData();
      }
   }

   @Override
   public boolean sessionSourceEnded() {
      this._streamingSession = null;
      if (this._loaded) {
         this._queuedSeekTime = super._mediaTime;
      }

      this.doDeallocate();
      return true;
   }

   private int readArray(byte[] b) {
      return this.readArray(b, 0, b.length);
   }

   private int readArray(byte[] b, int offset, int length) {
      int bytesRead = 0;
      int newBytesRead = 0;

      do {
         newBytesRead = this._mediaSourceStream.read(b, offset + bytesRead, length - bytesRead);
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
      if (this._mediaSourceStream != null && this._mediaSourceStream.getContentDescriptor().getContentType().startsWith("audio")) {
         byte[] buffer = null;
         int offset = 0;
         if (this._metaDataControl == null) {
            if (!this.checkForID3()) {
               if (this._codec == 10 && this._mediaSourceStream.getSeekType() == 2 && "audio/mp4".equals(super._type)) {
                  try {
                     long startPosition = this._mediaSourceStream.tell();
                     MP4Info mp4Info = new MP4Info(new DataSourceInputStream(this._mediaSourceStream));
                     this._metaDataControl = mp4Info.getMetaData();
                     if (this._duration == -1) {
                        this._duration = mp4Info.getDuration();
                     }

                     this._mediaSourceStream.seek(startPosition);
                  } catch (Throwable var94) {
                     throw new MediaException(ioe.getMessage());
                  }
               } else if (this._codec == 12) {
                  buffer = new byte[24];

                  try {
                     int bytesRead = this.readArray(buffer);
                     if (bytesRead == 24) {
                        int headerSize = -1;

                        label967:
                        try {
                           headerSize = Math.min(ASFHeader.getHeaderSize(buffer), this._mediaBuffer.getBufferSize());
                        } finally {
                           break label967;
                        }

                        if (headerSize > 0) {
                           Array.resize(buffer, headerSize);
                           bytesRead += this.readArray(buffer, 24, headerSize - 24);

                           label963:
                           try {
                              ASFHeader asfHeader = new ASFHeader(buffer);
                              this._metaDataControl = asfHeader.getMetaData();
                              if (this._duration == -1) {
                                 this._duration = asfHeader.getDuration();
                              }
                           } finally {
                              break label963;
                           }
                        }
                     }

                     this._mediaBuffer.write(buffer, 0, bytesRead);
                     offset += bytesRead;
                  } catch (Throwable var99) {
                     throw new MediaException(ioe.getMessage());
                  }
               }
            } else {
               buffer = new byte[10];

               try {
                  int bytesRead = this.readArray(buffer);
                  if (bytesRead == 10) {
                     int tagSize = -1;
                     boolean var56 = false /* VF: Semaphore variable */;

                     label977:
                     try {
                        var56 = true;
                        tagSize = ID3v2Reader.getTagSize(buffer);
                        if (tagSize > 2097152) {
                           tagSize = -1;
                           throw new IllegalArgumentException();
                        }

                        var56 = false;
                     } finally {
                        if (var56) {
                           this._mediaBuffer.write(buffer, 0, bytesRead);
                           offset += bytesRead;
                           break label977;
                        }
                     }

                     if (tagSize > 0) {
                        Array.resize(buffer, tagSize);
                        this.readArray(buffer, 10, tagSize - 10);
                        this._dataOffset = tagSize;

                        label957:
                        try {
                           this._metaDataControl = ID3v2Reader.readTag(buffer);
                        } finally {
                           break label957;
                        }
                     }
                  } else if (bytesRead > 0) {
                     this._mediaBuffer.write(buffer, 0, bytesRead);
                     offset += bytesRead;
                  }
               } catch (Throwable var101) {
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

            durationBufferSize = Math.min(durationBufferSize, this._mediaBuffer.getBufferSize());
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
            } catch (Throwable var95) {
               throw new MediaException(ioe.getMessage());
            }

            long contentLength = this._mediaSourceStream.getContentLength();
            if (contentLength >= this._dataOffset) {
               contentLength -= this._dataOffset;
            } else {
               contentLength = -1;
            }

            this.setDuration(buffer, contentLength);
            this._mediaBuffer.write(buffer, offset, buffer.length - offset);
         }

         if (this._metaDataControl == null && this.checkForID3() && this._mediaSourceStream.getSeekType() == 2) {
            long contentLength = this._mediaSourceStream.getContentLength();
            long tagOffset = contentLength - 128;
            if (tagOffset > 0) {
               try {
                  long startPosition = this._mediaSourceStream.tell();
                  if (this._mediaSourceStream.seek(tagOffset) == tagOffset) {
                     byte[] tagID = new byte[3];
                     if (this.readArray(tagID) == tagID.length && tagID[0] == 84 && tagID[1] == 65 && tagID[2] == 71) {
                        byte[] tag = new byte[125];
                        if (this.readArray(tag) == tag.length) {
                           this._metaDataControl = ID3v1Reader.readTag(tag);
                        }
                     }
                  }

                  this._mediaSourceStream.seek(startPosition);
                  return;
               } catch (Throwable var93) {
                  throw new MediaException(ioe.getMessage());
               }
            }
         }
      }
   }

   @Override
   public void recordingDone(int reason, int data) {
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void initialize() throws MediaException {
      if (super._state <= 300) {
         if (Proxy.getInstance() == Application.getApplication() && Proxy.getInstance().isEventThread()) {
            throw new IllegalStateException("Player cannot be run on Proxy's application event thread.");
         }

         long time = System.currentTimeMillis();

         while (this._streamingSession != null && System.currentTimeMillis() - time < 500) {
            synchronized (this._lock) {
               try {
                  this._lock.wait(50);
               } finally {
                  continue;
               }
            }
         }

         if (this._streamingManager != null) {
            super._mediaTime = 0;
            this._loaded = false;
            this._expectedStop = false;
            int seekType = this._mediaSourceStream.getSeekType();
            if (this._fileHandle < 0 && this._rtspUrl == null) {
               if (this._alreadyPlayed) {
                  if (seekType == 0 && this._mediaSourceStream.tell() > this._dataOffset) {
                     throw new MediaException("Unable to play non-seekable stream again");
                  }

                  try {
                     if (seekType != 0 && (seekType != 2 || this._mediaSourceStream.seek(this._dataOffset) > this._dataOffset)) {
                        if (this._mediaSourceStream.seek(0) != 0) {
                           throw new MediaException("Seek to beginning failed");
                        }

                        if (this._dataOffset > 0) {
                           byte[] unneededData = new byte[this._dataOffset];
                           this.readArray(unneededData);
                        }
                     }

                     this._mediaBuffer.clear();
                  } catch (Throwable var16) {
                     throw new MediaException(e.getMessage());
                  }
               } else {
                  this._alreadyPlayed = true;
               }

               this.read();
            }

            int size = (int)Math.min(this._mediaSourceStream.getContentLength() - this._dataOffset, Integer.MAX_VALUE);
            if (size < -1) {
               size = -1;
            }

            MediaNatives.addListener(Proxy.getInstance(), this);
            this._streamingSession = this._streamingManager.openStream(this, this._mediaBuffer, this._codec, size, this._interruptible, this._bigSession);
            MediaNatives.removeListener(Proxy.getInstance(), this);
            if (this._streamingSession != null) {
               this._streamingSession.setVolume(this._mute ? 0 : this._volume);
               synchronized (this._lock) {
                  this._mediaPlayer = MediaPlayer.initialize(
                     this,
                     this._streams,
                     this._streamingSession,
                     this._videoX + this._offsetX,
                     this._videoY + this._offsetY,
                     this._videoWidth,
                     this._videoHeight,
                     size,
                     this.getSeekableFlag(seekType),
                     this._interruptible,
                     this._fileHandle,
                     this._rtspUrl,
                     this._rtspUserAgent,
                     this._rtspApnId,
                     this.getContentType()
                  );
                  if (this._mediaPlayer == null) {
                     super._state = 200;
                     this.notifyListeners("error", Integer.toString(1));
                     throw new MediaException("Media processor is busy.");
                  }

                  return;
               }
            }
         }
      }
   }

   private boolean getSeekableFlag(int seekType) {
      if (seekType == 2) {
         return true;
      }

      if (this._mediaSourceStream instanceof HTTPDataSource) {
         HTTPBufferingManager bufferingManager = ((HTTPDataSource)this._mediaSourceStream).getBufferingManager();
         if (bufferingManager != null && bufferingManager.bufferWillContainAllContent()) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected void preStart() {
      if (this._mediaPlayer != null && !this._mediaPlayer.isAlive()) {
         this.doDeallocate();
      }

      if (this._mediaPlayer == null && super._state > 300) {
         super._state = 300;
      }
   }

   @Override
   protected void doStart() {
      if (!this._queuedByVideoControl) {
         if (!this._onScreen) {
            this._resumeWhenOnScreen = true;
         } else {
            this.setVolume(this._volume);
            if (this._mediaPlayer == null || !this._mediaPlayer.isAlive()) {
               this._queuePlayback = true;
               this.initialize();
            } else if (this._mediaPlayer != null && !this._loaded) {
               this._queuePlayback = true;
            } else {
               if (this._queuedSeekTime >= 0) {
                  this._mediaPlayer.seek((int)(this._queuedSeekTime / 1000));
                  this._queuedSeekTime = -1;
               }

               this.starting();
               this._mediaPlayer.play();
               this.notifyListeners("started", new Long(this.getMediaTime()));
            }
         }
      }
   }

   @Override
   protected void doStop() {
      super._currentLoopIteration = 0;
      if (this._resumeWhenOnScreen) {
         this._resumeWhenOnScreen = false;
      }

      if (!this._queuePlayback && this._loaded) {
         if (this._mediaPlayer != null) {
            this._mediaPlayer.pause();
            this.stopped();
         }
      } else {
         this._queuePlayback = false;
      }
   }

   @Override
   protected void doDeallocate() {
      MediaPlayer mediaPlayer = this._mediaPlayer;
      if (mediaPlayer != null) {
         synchronized (mediaPlayer) {
            this._mediaPlayer = null;
            mediaPlayer.removeListener(this);

            label51:
            try {
               this._loaded = false;
               mediaPlayer.unload();
               this.stopped();
               this._expectedStop = true;
               if ((this._fileHandle >= 0 || this._rtspUrl != null) && this._streamingSession != null) {
                  this._streamingSession.release();
                  this._streamingSession.clearCallback();
                  this._streamingSession = null;
               }
            } finally {
               break label51;
            }

            if (super._state == 400) {
               this.notifyListeners("stopped", new Long(super._mediaTime));
            }
         }
      }

      super._state = 300;
   }

   private void onDone(int reason) {
      this.sessionSourceEnded();
   }

   @Override
   public void setKeyValue(String key, Object value) {
      if ("interrupt_on_user_input".equals(key)) {
         if (value instanceof Integer) {
            this.setInterruptible((Integer)value);
            return;
         }
      } else if ("datasource".equals(key)) {
         this._dataSource = (DataSource)value;
         SourceStream[] streams = this._dataSource.getStreams();
         this._mediaSourceStream = streams[0];
         if (this._dataSource instanceof SourceInformationProvider) {
            this._fileHandle = ((SourceInformationProvider)this._dataSource).getFileHandle();
            return;
         }

         if (this._dataSource instanceof RTSPDataSource) {
            RTSPDataSource source = (RTSPDataSource)this._dataSource;
            this._rtspUrl = source.getLocator();
            this._rtspUserAgent = source.getUserAgent();
            this._rtspApnId = source.getAccessPointNumber();
            return;
         }
      } else {
         if ("audiosource".equals(key)) {
            super._audioSourceId = (Integer)value;
            return;
         }

         if ("big_session".equals(key)) {
            this._bigSession = Boolean.TRUE.equals(value);
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
      }

      if ("javax.microedition.media.control.VideoControl".equals(controlType)) {
         if ((this._streams & 2) != 0) {
            return this;
         }
      } else if ("net.rim.device.api.media.control.VideoPositionControl".equals(controlType)) {
         if ((this._streams & 2) != 0 && PlayerRegistry.hasInternalMMAPIAccess()) {
            return this;
         }
      } else {
         if (this._metaDataControl != null
            && (
               "javax.microedition.media.control.MetaDataControl".equals(controlType)
                  || "net.rim.device.api.media.control.BinaryMetaDataControl".equals(controlType)
            )) {
            return this._metaDataControl;
         }

         if ("net.rim.device.api.media.control.AudioPathControl".equals(controlType)) {
            return this;
         }

         if ("net.rim.device.api.media.control.AuthenticationControl".equals(controlType)) {
            return this;
         }
      }

      return super.getControl(controlType);
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

   @Override
   public synchronized long setMediaTime(long now) throws MediaException {
      this.chkClosed(true);
      if (now < 0) {
         now = 0;
      }

      if (this._duration == -1) {
         if (now > 0) {
            return super._mediaTime;
         }
      } else if (now > this._duration) {
         now = this._duration;
      }

      if (this._mediaPlayer != null && this._mediaPlayer.isAlive() && this._loaded) {
         this._queuedSeekTime = -1;
         now /= 1000;
         boolean allowSeekToHead = this._mediaSourceStream.getSeekType() != 0 && now == 0;
         if (!this._seekable && !allowSeekToHead) {
            throw new MediaException("Seeking not allowed on media.");
         }

         int milliseconds = (int)now;
         boolean timeSeekRequestedWhilePlaying = false;
         if (super._state == 400 && this._mediaPlayer != null && this._seekable) {
            this._mediaPlayer.pause();
            timeSeekRequestedWhilePlaying = true;
         }

         if (this._mediaPlayer != null) {
            if (this._seekable) {
               this._mediaPlayer.seek(milliseconds);
               super._mediaTime = now * 1000;
            } else {
               this.deallocate();
            }
         }

         if (timeSeekRequestedWhilePlaying) {
            if (this._mediaPlayer != null) {
               this.starting();
               this._mediaPlayer.play();
            } else {
               this.start();
            }
         }

         return now * 1000;
      } else {
         this._queuedSeekTime = now;
         super._mediaTime = now;
         return now;
      }
   }

   private void doLoop() {
      if (super._app != null) {
         super._app.invokeLater(new StreamingMediaPlayer$1(this));
      }
   }

   @Override
   protected synchronized void doMakeMediaUnavailable(ActiveMedia media) {
      if (this._mediaPlayer != null
         && (
            Alert.isSingleSharedAudioChannel()
               || media != null && media.isAudioInUse()
               || media != null && media.isForce()
               || this.getContentType().startsWith("video")
         )) {
         this.notifyListeners("deviceUnavailable", "");
         MediaLogger.logMakeUnavailable(this._streamingSession, this._codec);
         this.doDeallocate();
         this._queuedSeekTime = super._mediaTime == 0 ? this._queuedSeekTime : super._mediaTime;
         super._state = 200;
      }
   }

   @Override
   public void mediaError(int handle, int errorCode) {
      if (errorCode == 3 && this._retry > 0) {
         MediaLogger.logMediaError(this._streamingSession, this._codec);
         this.doDeallocate();
         this._retry--;
         super._app.invokeLater(new StreamingMediaPlayer$2(this), 500, false);
      } else {
         this.notifyListeners("error", Integer.toString(errorCode));
         this._retry = 3;
         MediaLogger.logMediaError(this._streamingSession, this._codec);
         this.doDeallocate();
      }
   }

   @Override
   public void mediaStopped(int handle) {
      this.stopped();
      if (!this._expectedStop) {
         if (!this._loaded) {
            return;
         }

         this.notifyListeners("error", Integer.toString(5));
      }

      this.doDeallocate();
   }

   @Override
   public void mediaPauseComplete(int handle) {
      if (this._mediaPlayer != null) {
         Bitmap bitmap = this._mediaPlayer.getPauseBitmap();
         if (bitmap != null) {
            this.notifyListeners("com.rim.pauseBitmap", bitmap);
         }
      }
   }

   @Override
   public void mediaParametersChangedComplete(int handle) {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void mediaSeek(int handle, int offset) {
      if (this._mediaPlayer != null && this._mediaPlayer.isAlive()) {
         offset += this._dataOffset;
         boolean var6 = false /* VF: Semaphore variable */;

         try {
            var6 = true;
            boolean seekSuccessful;
            switch (this._mediaSourceStream.getSeekType()) {
               case 0:
                  seekSuccessful = false;
                  break;
               case 1:
                  if (offset == 0) {
                     this._streamingSession.clear();
                     this._mediaBuffer.clear();
                     this._mediaSourceStream.seek(offset);
                     seekSuccessful = true;
                  } else if (offset == this._dataOffset) {
                     this._streamingSession.clear();
                     this._mediaBuffer.clear();
                     this._mediaSourceStream.seek(0);
                     byte[] unneededData = new byte[this._dataOffset];
                     this.readArray(unneededData);
                     seekSuccessful = true;
                  } else {
                     seekSuccessful = false;
                  }
                  break;
               case 2:
               default:
                  this._streamingSession.clear();
                  this._mediaBuffer.clear();
                  this._mediaSourceStream.seek(offset);
                  seekSuccessful = true;
            }

            if (!seekSuccessful) {
               this.doDeallocate();
               this.notifyListeners("error", "Data stream is not seekable");
               return;
            }

            this.read();
            if (this._mediaPlayer != null) {
               this._mediaPlayer.seekComplete();
               return;
            }

            var6 = false;
         } finally {
            if (var6) {
               System.out.println("Error seeking.");
               this.doDeallocate();
               return;
            }
         }
      }
   }

   @Override
   public void mediaLoaded(int handle) {
      synchronized (this._lock) {
         this._retry = 3;
         long length = -1;
         int streams = -1;
         this._seekable = false;
         MediaPlayer mediaPlayer = this._mediaPlayer;
         if (mediaPlayer != null) {
            length = mediaPlayer.getLength();
            streams = mediaPlayer.getPlayableStreams();
            this._seekable = mediaPlayer.isSeekable();
            this._contentWidth = mediaPlayer.getContentWidth();
            this._contentHeight = mediaPlayer.getContentHeight();
            if (super._audioSourceId == 10 || super._audioSourceId == 9) {
               super._audioSourceId = mediaPlayer.hasVideo() ? 9 : 10;
            }

            if (!mediaPlayer.hasVideo() && super._audioSourceId == 3) {
               super._audioSourceId = 4;
            }
         }

         Long durationUpdate = null;
         if (length > 0) {
            length *= 1000;
            if (length != this._duration) {
               this._duration = length;
               durationUpdate = new Long(length);
            }
         }

         if (this._duration != -1) {
            super._mediaTime = 0;
         }

         this.notifyListeners("com.rim.mediaLoaded");
         if (durationUpdate != null) {
            this.notifyListeners("durationUpdated", durationUpdate);
         }

         this.notifyListeners("com.rim.seekableUpdate", new Boolean(this._seekable));
         this.notifyListeners("com.rim.playableStreams", new Integer(streams));
         if (mediaPlayer != null) {
            if (this._fullscreen) {
               this._mediaPlayer.resizeAndRelocate(0, 0, Display.getWidth(), Display.getHeight());
            }

            if (this._queuedSeekTime > 0 && this._seekable) {
               mediaPlayer.seek((int)(this._queuedSeekTime / 1000));
               super._mediaTime = this._queuedSeekTime;
            }

            this._queuedSeekTime = -1;
            if (this._queuePlayback) {
               this._queuePlayback = false;
               this.starting();
               mediaPlayer.play();
               this.notifyListeners("started", new Long(this.getMediaTime()));
            }

            this._loaded = true;
         }
      }
   }

   @Override
   public void mediaStatusUpdate(int handle, int milliseconds) {
      if (milliseconds >= 0) {
         super._mediaTime = (long)milliseconds * 1000;
         this.notifyListenersTimeUpdate();
      } else {
         if (super._state >= 400) {
            super._state = 300;
            this.doDeallocate();
            if (this._duration > super._mediaTime) {
               super._mediaTime = this._duration;
            }

            Long mediaTimeObject = new Long(super._mediaTime);
            this.notifyListeners("endOfMedia", mediaTimeObject);
            this.doLoop();
         }
      }
   }

   @Override
   public void mediaAuthenticationRequired(int handle, Object uri, Object header) {
      this.notifyListeners("com.rim.authenticationRequired");
   }

   @Override
   public void setCredentials(String credentials) {
      if (this._mediaPlayer != null) {
         this._mediaPlayer.passCredentials(credentials);
      }
   }

   @Override
   public Object initDisplayMode(int mode, Object arg) {
      if (this._initDisplayCalled) {
         throw new IllegalStateException("initDisplayMode already called successfully.");
      }

      Object obj = null;
      switch (mode) {
         case -1:
            throw new IllegalArgumentException("Invalid display mode");
         case 0:
            if (arg != null) {
               if (!(arg instanceof String)) {
                  throw new IllegalArgumentException("arg must be null or a java.lang.String");
               }

               if (arg.equals("javax.microedition.lcdui.Item")) {
                  this._controller = PlayerRegistry.getMMAPIConnector().getMediaItem(this, null, this._videoWidth, this._videoHeight);
                  obj = this._controller.getComponent();
               } else {
                  if (!arg.equals("net.rim.device.api.ui.Field")) {
                     throw new IllegalArgumentException("GUI primitive type not supported");
                  }

                  this._controller = PlayerRegistry.getMMAPIConnector().getMediaField(this, this._videoWidth, this._videoHeight);
                  obj = this._controller.getComponent();
               }
            } else {
               this._controller = PlayerRegistry.getMMAPIConnector().getMediaItem(this, null, this._videoWidth, this._videoHeight);
               obj = this._controller.getComponent();
            }
            break;
         case 1:
         default:
            if (arg instanceof Canvas) {
               this._controller = PlayerRegistry.getMMAPIConnector().setMediaCanvas((Canvas)arg, this);
            } else {
               if (!(arg instanceof Field)) {
                  throw new IllegalArgumentException("arg must not be null and must be a javax.microedition.lcdui.Canvas");
               }

               if (!PlayerRegistry.hasInternalMMAPIAccess()) {
                  throw new IllegalArgumentException("arg must not be null and must be a javax.microedition.lcdui.Canvas");
               }

               if (arg instanceof LcduiPlayerController) {
                  this._controller = (LcduiPlayerController)arg;
                  this._controller.setPlayer(this);
               }
            }
      }

      this._initDisplayCalled = true;
      if (arg instanceof Canvas) {
         this.setVisible(false);
      }

      this._displayMode = mode;
      return obj;
   }

   @Override
   public void setDisplayLocation(int x, int y) {
      this.checkInitDisplayed();
      if (this._displayMode == 1) {
         this._videoX = MathUtilities.clamp(0, x, Display.getWidth());
         this._videoY = MathUtilities.clamp(0, y, Display.getHeight());
         this.fitDimensionsToScreen();
         if (this._mediaPlayer != null) {
            this._mediaPlayer.resizeAndRelocate(this._videoX + this._offsetX, this._videoY + this._offsetY, this._videoWidth, this._videoHeight);
         }

         if (this._controller != null) {
            this._controller.refresh();
         }
      }
   }

   @Override
   public void offset(int x, int y) {
      this.checkInitDisplayed();
      if (x != this._offsetX || y != this._offsetY) {
         this._offsetX = x;
         this._offsetY = y;
         this.fitDimensionsToScreen();
         if (this._mediaPlayer != null) {
            this._mediaPlayer.resizeAndRelocate(this._videoX + this._offsetX, this._videoY + this._offsetY, this._videoWidth, this._videoHeight);
         }
      }

      if (this._controller != null) {
         this._controller.refresh();
      }
   }

   @Override
   public void setOnScreen(boolean onScreen) {
      this._onScreen = onScreen;
      if (this._onScreen && this._resumeWhenOnScreen) {
         try {
            this._resumeWhenOnScreen = false;
            this.start();
         } finally {
            return;
         }
      }
   }

   @Override
   public void setPosition(XYRect rect) {
      this.checkInitDisplayed();
      this._offsetX = rect.x;
      this._offsetY = rect.y;
      this._videoWidth = MathUtilities.clamp(0, rect.width, Display.getWidth());
      this._videoHeight = MathUtilities.clamp(0, rect.height, Display.getHeight());
      this.fitDimensionsToScreen();
      if (this._mediaPlayer != null) {
         this._mediaPlayer.resizeAndRelocate(this._videoX + this._offsetX, this._videoY + this._offsetY, this._videoWidth, this._videoHeight);
      }

      if (this._controller != null) {
         this._controller.refresh();
      }
   }

   @Override
   public int getDisplayX() {
      return this._videoX;
   }

   @Override
   public int getDisplayY() {
      return this._videoY;
   }

   @Override
   public void setVisible(boolean visible) {
      this.checkInitDisplayed();
      if (visible) {
         if (super._state >= 200 && this._queuedByVideoControl) {
            this._queuedByVideoControl = false;

            try {
               this.start();
            } finally {
               return;
            }
         } else {
            this._queuedByVideoControl = false;
         }
      } else {
         if (super._state == 400) {
            label57:
            try {
               this.stop();
            } finally {
               break label57;
            }
         }

         this._queuedByVideoControl = true;
      }
   }

   @Override
   public void setDisplaySize(int width, int height) {
      this.checkInitDisplayed();
      if (width > 0 && height > 0) {
         this._videoWidth = MathUtilities.clamp(0, width, Display.getWidth());
         this._videoHeight = MathUtilities.clamp(0, height, Display.getHeight());
         this.fitDimensionsToScreen();
         if (this._mediaPlayer != null) {
            this._mediaPlayer.resizeAndRelocate(this._videoX + this._offsetX, this._videoY + this._offsetY, this._videoWidth, this._videoHeight);
         }

         if (this._controller != null) {
            this._controller.resizeComponent(this._videoWidth, this._videoHeight);
            this._controller.refresh();
         }
      } else {
         throw new IllegalArgumentException("Values must be positive.");
      }
   }

   @Override
   public void setDisplayFullScreen(boolean fullScreenMode) {
      this.checkInitDisplayed();
      boolean refresh = false;
      int newX;
      int newY;
      int newWidth;
      int newHeight;
      if (fullScreenMode) {
         newX = 0;
         newY = 0;
         newWidth = Display.getWidth();
         newHeight = Display.getHeight();
         this._fullscreen = true;
      } else {
         newX = this._videoX + this._offsetX;
         newY = this._videoY + this._offsetY;
         newWidth = this._videoWidth;
         newHeight = this._videoHeight;
         refresh = true;
         this._fullscreen = false;
      }

      if (this._mediaPlayer != null) {
         this._mediaPlayer.resizeAndRelocate(newX, newY, newWidth, newHeight);
      }

      if (this._controller != null && refresh) {
         this._controller.refresh();
      }
   }

   @Override
   public int getSourceWidth() {
      return this._contentWidth == -1 ? this._videoWidth : this._contentWidth;
   }

   @Override
   public int getSourceHeight() {
      return this._contentHeight == -1 ? this._videoHeight : this._contentHeight;
   }

   @Override
   public int getDisplayWidth() {
      this.checkInitDisplayed();
      return this._videoWidth;
   }

   @Override
   public int getDisplayHeight() {
      this.checkInitDisplayed();
      return this._videoHeight;
   }

   @Override
   public byte[] getSnapshot(String imageType) throws MediaException {
      throw new MediaException("VideoControl.getSnapshot not supported");
   }

   private void checkInitDisplayed() {
      if (!this._initDisplayCalled) {
         throw new IllegalStateException("initDisplayMode not called.");
      }
   }

   private void fitDimensionsToScreen() {
      if ((this._videoX + this._offsetX) % 2 != 0) {
         this._offsetX++;
      }

      int displayWidth = Display.getWidth();
      int displayHeight = Display.getHeight();
      int absoluteWidth = this._videoX + this._offsetX + this._videoWidth;
      int absoluteHeight = this._videoY + this._offsetY + this._videoHeight;
      if (absoluteWidth > displayWidth) {
         this._videoWidth -= absoluteWidth - displayWidth;
      }

      if (absoluteHeight > displayHeight) {
         this._videoHeight -= absoluteHeight - displayHeight;
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

   private void notifyListenersTimeUpdate() {
      if (this._timeEventRunnableID < 0 && super._listeners != null && super._app != null) {
         for (int lv = super._listeners.length - 1; lv >= 0; lv--) {
            PlayerListener pl = (PlayerListener)super._listeners[lv];
            Player player = this;
            this._timeEventRunnableID = super._app.invokeLater(new StreamingMediaPlayer$3(this, pl, player), 1, false);
         }
      }
   }

   private void setDuration(byte[] data, long fullLength) {
      if (data != null) {
         label45:
         try {
            switch (this._codec) {
               case 0:
                  this._duration = getWavDuration(new ByteArrayInputStream(data));
                  if (this._duration != -1) {
                     super._mediaTime = 0;
                  }

                  return;
               case 3:
               case 10:
                  this._duration = MpegDuration.getDuration(data, fullLength);
                  if (this._duration != -1) {
                     super._mediaTime = 0;
                  }

                  return;
               case 7:
                  this._duration = AmrDuration.getDuration(data, fullLength);
                  if (this._duration != -1) {
                     super._mediaTime = 0;
                  }

                  return;
            }
         } finally {
            break label45;
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
   protected void cleanUp() {
      super.cleanUp();
      this.doDeallocate();
      this._mediaBuffer = null;
   }
}
