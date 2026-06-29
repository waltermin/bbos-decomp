package net.rim.device.apps.internal.voicenotesrecorder;

import java.io.ByteArrayOutputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.RecordControl;
import net.rim.device.internal.media.StreamDataControl;

final class VoiceRecordController {
   private Player _recordPlayer;
   private RecordControl _recordControl;
   private ByteArrayOutputStream _oStream;
   private int _state = 0;
   private static final int STATE_STOPPED;
   private static final int STATE_RECORDING;
   private static final int STATE_PAUSED;

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

   public final boolean isRecording() {
      return this._state == 1;
   }

   public final byte[] getVoiceClip() {
      return this._oStream.toByteArray();
   }

   public final int getSize() {
      return this._oStream == null ? 0 : this._oStream.size();
   }

   public final void startRecording() {
      if (this._state == 0) {
         System.out.println("VoiceNotes.start");
         this._recordPlayer = Manager.createPlayer("capture://audio");
         if (!(this._recordPlayer instanceof Object)) {
            throw new Object();
         }

         ((StreamDataControl)this._recordPlayer).setKeyValue("audiosource", new Object(2));
         this._recordPlayer.realize();
         this._recordControl = (RecordControl)this._recordPlayer.getControl("RecordControl");
         if (this._recordControl == null) {
            throw new Object();
         }

         this._oStream = (ByteArrayOutputStream)(new Object());
         this._recordControl.setRecordStream(this._oStream);
         this._recordControl.startRecord();
         this._recordPlayer.start();
         this._state = 1;
      }
   }

   public final void pauseRecording() {
      if (this._recordPlayer != null && this._state == 1) {
         System.out.println("VoiceNotes.pause");

         try {
            this._recordPlayer.stop();
            this._state = 2;
         } finally {
            return;
         }
      }
   }

   public final void resumeRecording() {
      if (this._recordPlayer != null && this._state == 2) {
         System.out.println("VoiceNotes.resume");

         try {
            this._recordPlayer.start();
            this._state = 1;
         } finally {
            return;
         }
      }
   }

   public final void stopRecording() {
      if (this._recordPlayer != null && this._state != 0) {
         System.out.println("VoiceNotes.stop");

         try {
            VoiceRecordController$RecordingCommittedListener committedListener = new VoiceRecordController$RecordingCommittedListener(this._recordPlayer);
            this._recordControl.commit();
            if (!committedListener.waitUntilCommitted()) {
            }

            this._recordPlayer.close();
            this._state = 0;
         } finally {
            return;
         }
      }
   }
}
