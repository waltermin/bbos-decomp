package net.rim.device.internal.media;

import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.TimeBase;
import javax.microedition.media.control.RecordControl;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.internal.system.MIDletSecurity;
import net.rim.device.internal.util.RingBuffer;
import net.rim.vm.TraceBack;

public class RecordPlayer implements Player, RecordControl, MediaStreamingCallback, StreamDataControl {
   private int _playerState;
   private int _recordState;
   private boolean _startRecordingOnCallToStart = false;
   private boolean _reset = false;
   private Object _lock = new Object();
   private OutputStream _stream;
   private FileConnection _file;
   private MediaStreamingManager$StreamingSession _streamingSession;
   private RingBuffer _ringBuffer;
   private int _codec;
   private RecordPlayer$RecordThread _thread;
   private Object[] _listeners;
   private String _mimeType;
   private int _loopCount;
   private int _maxSize;
   private TimeBase _timer;
   private long _startTime;
   private long _endTime;
   private int _audioSourceId = 8;
   private boolean _audioRouted = false;
   public static final String RECORD_COMMITTED = "net.rim.device.internal.media.recordCommitted";
   private static final int COMMITTED = 0;
   private static final int READY = 1;
   private static final int STANDBY = 2;
   private static final int RECORDING = 3;
   private static final int PREEMPTED = 4;
   private static final int AMR_HEADER_SIZE = 6;

   protected void notifyPlayerListeners(String event, Object eventData) {
      if (this._listeners != null) {
         for (int i = this._listeners.length - 1; i >= 0; i--) {
            try {
               ((PlayerListener)this._listeners[i]).playerUpdate(this, event, eventData);
            } finally {
               continue;
            }
         }
      }
   }

   public int getLoopCount() {
      return this._loopCount;
   }

   @Override
   public Object getKeyValueObject(String key) {
      return null;
   }

   @Override
   public String getKeyValue(String key) {
      return null;
   }

   @Override
   public String[] getKeys() {
      return null;
   }

   @Override
   public void setKeyValue(String key, Object value) {
      if (key.equals("mimetype")) {
         this._mimeType = (String)value;
         if (this._mimeType.equals("audio/basic")) {
            this._codec = 9;
            return;
         }

         if (this._mimeType.equals("audio/amr")) {
            this._codec = 7;
            return;
         }
      } else if (key.equals("audiosource")) {
         this._audioSourceId = (Integer)value;
      }
   }

   @Override
   public void realize() {
      if (this._playerState == 0) {
         throw new IllegalStateException();
      }

      if (this._playerState < 200) {
         this._ringBuffer = new RingBuffer(40000);
         this._ringBuffer.setReadEntirelySizeLimit(this._maxSize);
         this._playerState = 200;
      }
   }

   @Override
   public void prefetch() {
      if (this._playerState == 0) {
         throw new IllegalStateException();
      }

      if (this._playerState < 300) {
         if (this._playerState < 200) {
            this.realize();
         }

         this._playerState = 300;
      }
   }

   @Override
   public void start() {
      if (this._playerState == 0) {
         throw new IllegalStateException();
      }

      if (this._playerState < 400) {
         if (this._playerState < 200) {
            this.realize();
         }

         if (this._playerState < 300) {
            this.prefetch();
         }

         this._playerState = 400;
         if (this._startRecordingOnCallToStart) {
            this.startRecord();
            this._startRecordingOnCallToStart = false;
         }

         this.notifyPlayerListeners("started", new Long(this.getMediaTime()));
      }
   }

   @Override
   public void stop() {
      if (this._playerState == 0) {
         throw new IllegalStateException();
      }

      if (this._playerState >= 400) {
         this._playerState = 300;
         if (this._recordState == 3) {
            this._endTime = this._endTime + (this._timer.getTime() - this._startTime);
            this._startTime = this._endTime;
            if (this._streamingSession != null) {
               this._streamingSession.recordPause();
            }

            this._startRecordingOnCallToStart = true;
            this._recordState = 2;
            this.toggleAudioRouting(false);
         }

         this.notifyPlayerListeners("stopped", new Long(this.getMediaTime()));
      }
   }

   @Override
   public void deallocate() {
      if (this._playerState == 0) {
         throw new IllegalStateException();
      }

      if (this._playerState >= 300) {
         if (this._playerState == 400) {
            label29:
            try {
               this.stop();
            } finally {
               break label29;
            }
         }

         this._playerState = 200;
      }
   }

   @Override
   public void close() {
      if (this._playerState != 0) {
         this.deallocate();

         label30:
         try {
            if (this._recordState != 0 && !this._reset) {
               this.commit();
            }
         } finally {
            break label30;
         }

         this.toggleAudioRouting(false);
         this._playerState = 0;
         this.notifyPlayerListeners("closed", null);
      }
   }

   @Override
   public void setTimeBase(TimeBase master) throws MediaException {
      if (this._playerState == 100 || this._playerState == 400 || this._playerState == 0) {
         throw new IllegalStateException();
      }

      if (master != this._timer) {
         throw new MediaException();
      }
   }

   @Override
   public TimeBase getTimeBase() {
      if (this._playerState != 0 && this._playerState != 100) {
         return this._timer;
      } else {
         throw new IllegalStateException("The Player is unrealized");
      }
   }

   @Override
   public long setMediaTime(long now) throws MediaException {
      if (this._playerState != 0 && this._playerState != 100) {
         throw new MediaException();
      } else {
         throw new IllegalStateException("The Player is unrealized");
      }
   }

   @Override
   public long getMediaTime() {
      if (this._playerState == 0) {
         throw new IllegalStateException();
      } else {
         return -1;
      }
   }

   @Override
   public int getState() {
      return this._playerState;
   }

   @Override
   public long getDuration() {
      if (this._playerState == 0) {
         throw new IllegalStateException();
      } else {
         return -1;
      }
   }

   @Override
   public String getContentType() {
      if (this._playerState != 0 && this._playerState != 100) {
         return this._mimeType;
      } else {
         throw new IllegalStateException("The Player is unrealized");
      }
   }

   @Override
   public void setLoopCount(int count) {
      if (this._playerState == 400 || this._playerState == 0) {
         throw new IllegalStateException();
      }

      if (count != 0 && count >= -1) {
         this._loopCount = count;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public Control getControl(String controlType) {
      if (this._playerState == 100 || this._playerState == 0) {
         throw new IllegalStateException();
      } else if ("RecordControl".equals(controlType) || "javax.microedition.media.control.RecordControl".equals(controlType)) {
         return this;
      } else {
         return "net.rim.device.api.media.control.AudioPathControl".equals(controlType)
            ? AudioRouter.getInstance().getAudioPathControl(this._audioSourceId)
            : null;
      }
   }

   @Override
   public void addPlayerListener(PlayerListener playerListener) {
      if (this._playerState == 0) {
         throw new IllegalStateException();
      }

      if (playerListener != null) {
         this._listeners = ListenerUtilities.addListener(this._listeners, playerListener);
      }
   }

   @Override
   public void removePlayerListener(PlayerListener playerListener) {
      if (this._playerState == 0) {
         throw new IllegalStateException();
      }

      if (playerListener != null) {
         this._listeners = ListenerUtilities.removeListener(this._listeners, playerListener);
      }
   }

   @Override
   public Control[] getControls() {
      if (this._playerState != 100 && this._playerState != 0) {
         Control audioPathControl = AudioRouter.getInstance().getAudioPathControl(this._audioSourceId);
         return new Control[]{this, audioPathControl};
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public void setRecordStream(OutputStream stream) {
      if (CodeModuleManager.isMidlet() && !ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)) {
         MIDletSecurity.checkPermission(18);
      }

      if (this._recordState > 0) {
         throw new IllegalStateException();
      }

      if (stream == null) {
         throw new IllegalArgumentException();
      }

      this._stream = stream;
      this._ringBuffer.clear();
      this._thread = new RecordPlayer$RecordThread(this);
      this._thread.start();
      this._startTime = 0;
      this._endTime = 0;
      this._recordState = 1;
   }

   @Override
   public void setRecordLocation(String param1) throws IOException, MediaException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: invokestatic net/rim/device/api/system/CodeModuleManager.isMidlet ()Z
      // 003: ifeq 017
      // 006: bipush 0
      // 007: invokestatic net/rim/vm/TraceBack.getCallingModule (I)I
      // 00a: bipush 51
      // 00c: invokestatic net/rim/device/api/system/ControlledAccess.verifyCodeModuleSignature (II)Z
      // 00f: ifne 017
      // 012: bipush 18
      // 014: invokestatic net/rim/device/internal/system/MIDletSecurity.checkPermission (I)V
      // 017: aload 0
      // 018: getfield net/rim/device/internal/media/RecordPlayer._recordState I
      // 01b: ifle 026
      // 01e: new java/lang/IllegalStateException
      // 021: dup
      // 022: invokespecial java/lang/IllegalStateException.<init> ()V
      // 025: athrow
      // 026: aload 1
      // 027: ifnonnull 032
      // 02a: new java/lang/IllegalArgumentException
      // 02d: dup
      // 02e: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 031: athrow
      // 032: bipush 2
      // 034: invokestatic net/rim/vm/TraceBack.getCallingModule (I)I
      // 037: istore 2
      // 038: aload 0
      // 039: iload 2
      // 03a: aload 1
      // 03b: invokestatic net/rim/device/internal/io/RIMConnector.open (ILjava/lang/String;)Ljavax/microedition/io/Connection;
      // 03e: checkcast javax/microedition/io/file/FileConnection
      // 041: putfield net/rim/device/internal/media/RecordPlayer._file Ljavax/microedition/io/file/FileConnection;
      // 044: goto 061
      // 047: astore 2
      // 048: new javax/microedition/media/MediaException
      // 04b: dup
      // 04c: aload 2
      // 04d: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 050: invokespecial javax/microedition/media/MediaException.<init> (Ljava/lang/String;)V
      // 053: athrow
      // 054: astore 2
      // 055: new javax/microedition/media/MediaException
      // 058: dup
      // 059: aload 2
      // 05a: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 05d: invokespecial javax/microedition/media/MediaException.<init> (Ljava/lang/String;)V
      // 060: athrow
      // 061: bipush 0
      // 062: istore 2
      // 063: aload 0
      // 064: getfield net/rim/device/internal/media/RecordPlayer._file Ljavax/microedition/io/file/FileConnection;
      // 067: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 06c: ifne 07a
      // 06f: aload 0
      // 070: getfield net/rim/device/internal/media/RecordPlayer._file Ljavax/microedition/io/file/FileConnection;
      // 073: invokeinterface javax/microedition/io/file/FileConnection.create ()V 1
      // 078: bipush 1
      // 079: istore 2
      // 07a: aload 0
      // 07b: getfield net/rim/device/internal/media/RecordPlayer._file Ljavax/microedition/io/file/FileConnection;
      // 07e: invokeinterface javax/microedition/io/file/FileConnection.isDirectory ()Z 1
      // 083: ifeq 0a7
      // 086: iload 2
      // 087: ifeq 093
      // 08a: aload 0
      // 08b: getfield net/rim/device/internal/media/RecordPlayer._file Ljavax/microedition/io/file/FileConnection;
      // 08e: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 093: aload 0
      // 094: getfield net/rim/device/internal/media/RecordPlayer._file Ljavax/microedition/io/file/FileConnection;
      // 097: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 09c: new java/io/IOException
      // 09f: dup
      // 0a0: ldc_w "Cannot record to directory"
      // 0a3: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 0a6: athrow
      // 0a7: aload 0
      // 0a8: getfield net/rim/device/internal/media/RecordPlayer._file Ljavax/microedition/io/file/FileConnection;
      // 0ab: invokeinterface javax/microedition/io/file/FileConnection.canWrite ()Z 1
      // 0b0: ifeq 0ce
      // 0b3: aload 0
      // 0b4: getfield net/rim/device/internal/media/RecordPlayer._file Ljavax/microedition/io/file/FileConnection;
      // 0b7: bipush 0
      // 0b8: i2l
      // 0b9: invokeinterface javax/microedition/io/file/FileConnection.truncate (J)V 3
      // 0be: aload 0
      // 0bf: aload 0
      // 0c0: getfield net/rim/device/internal/media/RecordPlayer._file Ljavax/microedition/io/file/FileConnection;
      // 0c3: invokeinterface javax/microedition/io/file/FileConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 0c8: putfield net/rim/device/internal/media/RecordPlayer._stream Ljava/io/OutputStream;
      // 0cb: goto 0e2
      // 0ce: aload 0
      // 0cf: getfield net/rim/device/internal/media/RecordPlayer._file Ljavax/microedition/io/file/FileConnection;
      // 0d2: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0d7: new java/io/IOException
      // 0da: dup
      // 0db: ldc_w "Cannot write to specified file"
      // 0de: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 0e1: athrow
      // 0e2: aload 0
      // 0e3: getfield net/rim/device/internal/media/RecordPlayer._ringBuffer Lnet/rim/device/internal/util/RingBuffer;
      // 0e6: invokevirtual net/rim/device/internal/util/RingBuffer.clear ()V
      // 0e9: aload 0
      // 0ea: new net/rim/device/internal/media/RecordPlayer$RecordThread
      // 0ed: dup
      // 0ee: aload 0
      // 0ef: invokespecial net/rim/device/internal/media/RecordPlayer$RecordThread.<init> (Lnet/rim/device/internal/media/RecordPlayer;)V
      // 0f2: putfield net/rim/device/internal/media/RecordPlayer._thread Lnet/rim/device/internal/media/RecordPlayer$RecordThread;
      // 0f5: aload 0
      // 0f6: getfield net/rim/device/internal/media/RecordPlayer._thread Lnet/rim/device/internal/media/RecordPlayer$RecordThread;
      // 0f9: invokevirtual java/lang/Thread.start ()V
      // 0fc: aload 0
      // 0fd: bipush 0
      // 0fe: i2l
      // 0ff: putfield net/rim/device/internal/media/RecordPlayer._startTime J
      // 102: aload 0
      // 103: bipush 0
      // 104: i2l
      // 105: putfield net/rim/device/internal/media/RecordPlayer._endTime J
      // 108: aload 0
      // 109: bipush 1
      // 10a: putfield net/rim/device/internal/media/RecordPlayer._recordState I
      // 10d: return
      // try (22 -> 31): 32 null
      // try (22 -> 31): 39 null
   }

   @Override
   public void startRecord() {
      if (this._recordState == 0) {
         throw new IllegalStateException();
      }

      if (this._recordState != 3) {
         if (this._playerState != 400) {
            this._startRecordingOnCallToStart = true;
         } else {
            this.toggleAudioRouting(true);
            if (this._recordState == 4) {
               this._ringBuffer.clear();
               this._thread = new RecordPlayer$RecordThread(this, true);
               this._thread.start();
               this._recordState = 1;
            }

            if (this._recordState == 1) {
               this._streamingSession = MediaStreamingManager.getInstance().recordStream(this, this._ringBuffer, this._codec, 100);
               if (this._streamingSession == null) {
                  this.notifyPlayerListeners("recordError", "Could not start recording");
                  return;
               }
            } else if (this._recordState == 2) {
               this._streamingSession.recordResume();
            }

            this._recordState = 3;
            this.notifyPlayerListeners("recordStarted", new Long(this._startTime));
            this._startTime = this._timer.getTime();
         }
      }
   }

   @Override
   public void stopRecord() {
      if (this._recordState != 4) {
         if (this._recordState == 3) {
            this._streamingSession.recordPause();
            this.toggleAudioRouting(false);
            this._recordState = 2;
         }

         if (this._recordState == 2) {
            if (this._startTime != this._endTime) {
               this._endTime = this._endTime + (this._timer.getTime() - this._startTime);
               this._startTime = this._endTime;
            }

            this.notifyPlayerListeners("recordStopped", new Long(this._endTime));
         }
      }
   }

   @Override
   public void commit() {
      synchronized (this._lock) {
         if (this._recordState == 4) {
            this._stream.close();
            if (this._file != null && this._file.isOpen()) {
               this._file.close();
            }

            this._recordState = 0;
            this.notifyPlayerListeners("net.rim.device.internal.media.recordCommitted", "Recording complete.");
         } else {
            if (this._recordState >= 2) {
               this._streamingSession.stop();
               this.toggleAudioRouting(false);
               if (this._recordState == 3) {
                  this._endTime = this._endTime + (this._timer.getTime() - this._startTime);
                  this.notifyPlayerListeners("recordStopped", new Long(this._endTime));
               }
            }

            this._recordState = 0;
         }
      }
   }

   @Override
   public int setRecordSizeLimit(int size) {
      if (size <= 0) {
         throw new IllegalArgumentException("size must be a non-zero integer");
      }

      this._maxSize = size;
      this._ringBuffer.setReadEntirelySizeLimit(this._maxSize);
      return this._maxSize;
   }

   @Override
   public void reset() throws IOException {
      if (this._recordState == 4) {
         if (this._file != null && this._file.isOpen()) {
            this._stream.flush();
            this._file.truncate(0);
            this._ringBuffer.clear();
            this._thread = new RecordPlayer$RecordThread(this);
            this._thread.start();
            this._recordState = 1;
         } else {
            throw new IOException("Unable to reset recording");
         }
      } else {
         if (this._recordState == 3) {
            this.stopRecord();
         }

         if (this._recordState > 0) {
            this._streamingSession.stop();
            this.toggleAudioRouting(false);
            this._reset = true;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void recordingDone(int reason, int data) {
      this._streamingSession = null;
      boolean var10 = false /* VF: Semaphore variable */;

      try {
         var10 = true;
         if (reason != 6148) {
            if (reason == 6150) {
               this.toggleAudioRouting(false);
               this._stream.close();
               this.notifyPlayerListeners("recordError", "Record stream failed.");
               this._recordState = 0;
               var10 = false;
            } else if (reason == 1000) {
               this.toggleAudioRouting(false);
               if (this._codec == 7) {
                  synchronized (this._lock) {
                     this._ringBuffer.close();

                     label177:
                     try {
                        this.stop();
                     } finally {
                        break label177;
                     }

                     this.notifyPlayerListeners("deviceUnavailable", null);
                     this._recordState = 4;
                     var10 = false;
                  }
               } else {
                  label174:
                  try {
                     this.stop();
                  } finally {
                     break label174;
                  }

                  this._stream.close();
                  if (this._file != null && this._file.isOpen()) {
                     this._file.close();
                  }

                  this.notifyPlayerListeners("net.rim.device.internal.media.recordCommitted", "Recording complete.");
                  this._recordState = 0;
                  var10 = false;
               }
            } else {
               this._stream.close();
               this._recordState = 0;
               if (this._file != null) {
                  if (this._file.isOpen()) {
                     this._file.close();
                     return;
                  }

                  var10 = false;
               } else {
                  var10 = false;
               }
            }
         } else {
            this._stream.close();
            if (this._reset) {
               if (this._file != null && this._file.isOpen()) {
                  this._file.truncate(0);
                  this._stream = this._file.openOutputStream();
                  this._ringBuffer.clear();
                  this._thread = new RecordPlayer$RecordThread(this);
                  this._thread.start();
                  this._recordState = 1;
               } else {
                  this.notifyPlayerListeners("recordError", "Could not reset recording. Current recording may not be valid.");
                  this._recordState = 0;
               }

               this._reset = false;
               var10 = false;
            } else {
               if (this._file != null && this._file.isOpen()) {
                  this._file.close();
               }

               this.notifyPlayerListeners("net.rim.device.internal.media.recordCommitted", "Recording complete.");
               this._recordState = 0;
               var10 = false;
            }
         }
      } finally {
         if (var10) {
            this.notifyPlayerListeners("recordError", "Error while saving file. Current recording may not be valid.");
            this._recordState = 0;
            return;
         }
      }
   }

   @Override
   public void streamingDone(int reason) {
   }

   @Override
   public boolean sessionSourceEnded() {
      return false;
   }

   @Override
   public boolean moreData() {
      return false;
   }

   @Override
   public void streamingSentAllData() {
   }

   private void toggleAudioRouting(boolean on) {
      if (on && !this._audioRouted) {
         AudioRouter.getInstance().addSource(this._audioSourceId);
         this._audioRouted = true;
      } else {
         if (!on && this._audioRouted) {
            AudioRouter.getInstance().removeSource(this._audioSourceId);
            this._audioRouted = false;
         }
      }
   }

   public RecordPlayer() {
      this._playerState = 100;
      this._recordState = 0;
      this._loopCount = -1;
      this._listeners = null;
      this._maxSize = Integer.MAX_VALUE;
      this._timer = Manager.getSystemTimeBase();
   }
}
