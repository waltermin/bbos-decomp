package net.rim.device.apps.internal.qm.peer;

import java.io.ByteArrayOutputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.RecordControl;

public final class VoiceRecordController {
   private int _maxRecordSize;
   private int _maxRecordTime;
   private Player _recordPlayer;
   private RecordControl _recordControl;
   private ByteArrayOutputStream _oStream;
   private PlayerListener _listener;
   private int _state = 0;
   private static final int STATE_STOPPED = 0;
   private static final int STATE_RECORDING = 1;
   private static final int STATE_PAUSED = 2;

   VoiceRecordController(int maxRecordSize, int maxRecordTime) {
      this._maxRecordSize = maxRecordSize;
      this._maxRecordTime = maxRecordTime;
   }

   public static final boolean isSupported() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: ldc_w "capture://audio"
      // 03: invokestatic javax/microedition/media/Manager.createPlayer (Ljava/lang/String;)Ljavax/microedition/media/Player;
      // 06: pop
      // 07: bipush 1
      // 08: ireturn
      // 09: astore 0
      // 0a: goto 0e
      // 0d: astore 0
      // 0e: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 11: ldc_w "Voice Notes not supported"
      // 14: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 17: bipush 0
      // 18: ireturn
      // try (0 -> 4): 5 null
      // try (0 -> 4): 7 null
   }

   public final byte[] getVoiceClip() {
      return this._oStream.toByteArray();
   }

   public final int getPercentFull(int time) {
      int size = this._oStream.size();
      if (size >= this._maxRecordSize) {
         this.stopRecording();
         return 100;
      } else if (time >= this._maxRecordTime) {
         this.stopRecording();
         return 100;
      } else {
         return Math.max(size * 100 / this._maxRecordSize, time * 100 / this._maxRecordTime);
      }
   }

   public final int size() {
      return this._oStream == null ? 0 : this._oStream.size();
   }

   public final void setListener(PlayerListener listener) {
      this._listener = listener;
   }

   public final void startRecording() throws MediaException {
      if (this._state == 0) {
         System.out.println("VoiceNotes.start");
         this._recordPlayer = Manager.createPlayer("capture://audio");
         if (this._recordPlayer == null) {
            throw new MediaException();
         }

         this._recordPlayer.realize();
         if (this._listener != null) {
            this._recordPlayer.addPlayerListener(this._listener);
         }

         this._recordControl = (RecordControl)this._recordPlayer.getControl("RecordControl");
         if (this._recordControl == null) {
            throw new MediaException();
         }

         this._oStream = new ByteArrayOutputStream();
         this._recordControl.setRecordStream(this._oStream);
         this._recordControl.setRecordSizeLimit(this._maxRecordSize);
         this._recordControl.startRecord();
         this._recordPlayer.start();
         this._state = 1;
      }
   }

   public final void stopRecording() {
      if (this._recordPlayer != null && this._state != 0) {
         System.out.println("VoiceNotes.stop");

         label26:
         try {
            this._recordControl.commit();
         } finally {
            break label26;
         }

         this._recordPlayer.close();
         this._state = 0;
      }
   }
}
