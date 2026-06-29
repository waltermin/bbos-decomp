package net.rim.device.apps.internal.qm.peer;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.apps.internal.qm.resource.QmResources;

public final class VoiceNoteDialog extends PopupScreen implements FieldChangeListener, PlayerListener {
   private LabelField _stateField = (LabelField)(new Object("", 12884901888L));
   private LabelField _progressField = (LabelField)(new Object("", 12884901888L));
   private ButtonField _playField = (ButtonField)(new Object(QmResources.getString(59), 65536));
   private ButtonField _stopField = (ButtonField)(new Object(QmResources.getString(98), 65536));
   private ButtonField _sendField = (ButtonField)(new Object(QmResources.getString(101), 65536));
   private ButtonField _cancelField = (ButtonField)(new Object(PeerResources.getString(6), 65536));
   private FlowFieldManager _hfm = (FlowFieldManager)(new Object(12884901888L));
   boolean _result;
   boolean _sendOnKeyUp;
   private VoiceRecordController _recordController = new VoiceRecordController(15360, 15);
   Application _application;
   private long _accumulatedTime;
   private long _startTime;
   private int _timerID = -1;
   private Player _tunePlayer;
   static VoiceNoteDialog _instance = new VoiceNoteDialog();

   VoiceNoteDialog() {
      super((Manager)(new Object()));
      this.applyTheme();
      this.addTitle(QmResources.getString(0));
      this.add(this._stateField);
      this.add(this._progressField);
      this._playField.setChangeListener(this);
      this._stopField.setChangeListener(this);
      this._sendField.setChangeListener(this);
      this._cancelField.setChangeListener(this);
      this._hfm.add(this._stopField);
      this.add(this._hfm);
      this._recordController.setListener(this);
   }

   public final void addTitle(String title) {
      if (title != null && title.length() > 0) {
         this.add((Field)(new Object(title)));
         this.add((Field)(new Object()));
      }
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      int size = Ui.convertSize(FontRegistry.DEFAULT_SIZE, 3, 0);
      this.setFont(null);
      Font font = this.getFont();
      if (font.getHeight() > size && size > 0) {
         this.setFont(font.derive(font.getStyle(), size));
      }
   }

   public final boolean validate() {
      return true;
   }

   final void init() {
      this._stateField.setText(QmResources.getString(106));
      this._progressField.setText("0 %");
      this._hfm.deleteAll();
      this._hfm.add(this._stopField);
      this.updateLayout();
   }

   public static final boolean doModal() {
      return doModal(false);
   }

   public static final boolean doModal(boolean sendOnKeyUp) {
      _instance.init();
      _instance._sendOnKeyUp = sendOnKeyUp;
      _instance._application = Application.getApplication();
      Ui.getUiEngine().pushModalScreen(_instance);
      return _instance._result;
   }

   @Override
   protected final void onDisplay() {
      super.onDisplay();
      this.record();
   }

   public static final byte[] getVoiceClip() {
      return _instance._recordController.getVoiceClip();
   }

   public final void close(boolean result) {
      if (!result || this.validate()) {
         this._result = result;
         this.close();
      }
   }

   final void record() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: bipush 0
      // 02: i2l
      // 03: putfield net/rim/device/apps/internal/qm/peer/VoiceNoteDialog._accumulatedTime J
      // 06: aload 0
      // 07: getfield net/rim/device/apps/internal/qm/peer/VoiceNoteDialog._recordController Lnet/rim/device/apps/internal/qm/peer/VoiceRecordController;
      // 0a: invokevirtual net/rim/device/apps/internal/qm/peer/VoiceRecordController.startRecording ()V
      // 0d: return
      // 0e: astore 1
      // 0f: return
      // 10: astore 1
      // 11: return
      // try (4 -> 7): 8 null
      // try (4 -> 7): 10 null
   }

   final void onStart() {
      this._application.invokeLater(new VoiceNoteDialog$1(this));
   }

   @Override
   protected final boolean blockInputEvents(int event, int keycode) {
      return event == 515 && ConversationScreen.isPTTKey(keycode) ? false : super.blockInputEvents(event, keycode);
   }

   final void stop() {
      if (this._tunePlayer != null) {
         this._tunePlayer.close();
         this._tunePlayer = null;
      } else {
         this._recordController.stopRecording();
      }
   }

   final void onStop() {
      this._application.invokeLater(new VoiceNoteDialog$2(this));
   }

   final boolean isPlaying() {
      return this._tunePlayer != null;
   }

   final void play() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/qm/peer/VoiceNoteDialog._recordController Lnet/rim/device/apps/internal/qm/peer/VoiceRecordController;
      // 04: invokevirtual net/rim/device/apps/internal/qm/peer/VoiceRecordController.getVoiceClip ()[B
      // 07: astore 1
      // 08: aload 0
      // 09: new java/lang/Object
      // 0c: dup
      // 0d: aload 1
      // 0e: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 11: ldc_w "audio/amr"
      // 14: invokestatic javax/microedition/media/Manager.createPlayer (Ljava/io/InputStream;Ljava/lang/String;)Ljavax/microedition/media/Player;
      // 17: putfield net/rim/device/apps/internal/qm/peer/VoiceNoteDialog._tunePlayer Ljavax/microedition/media/Player;
      // 1a: aload 0
      // 1b: getfield net/rim/device/apps/internal/qm/peer/VoiceNoteDialog._tunePlayer Ljavax/microedition/media/Player;
      // 1e: ifnull 3c
      // 21: aload 0
      // 22: getfield net/rim/device/apps/internal/qm/peer/VoiceNoteDialog._tunePlayer Ljavax/microedition/media/Player;
      // 25: aload 0
      // 26: invokeinterface javax/microedition/media/Player.addPlayerListener (Ljavax/microedition/media/PlayerListener;)V 2
      // 2b: aload 0
      // 2c: getfield net/rim/device/apps/internal/qm/peer/VoiceNoteDialog._tunePlayer Ljavax/microedition/media/Player;
      // 2f: invokeinterface javax/microedition/media/Player.realize ()V 1
      // 34: goto 3c
      // 37: astore 2
      // 38: goto 3c
      // 3b: astore 2
      // 3c: aload 0
      // 3d: invokespecial net/rim/device/apps/internal/qm/peer/VoiceNoteDialog.startTune ()V
      // 40: return
      // try (4 -> 22): 23 null
      // try (4 -> 22): 25 null
   }

   private final void startTune() {
      if (this._tunePlayer != null) {
         try {
            this._tunePlayer.start();
         } finally {
            return;
         }
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.isDisplayed()) {
         if (field == this._stopField) {
            this.stop();
         } else if (field == this._playField) {
            this.play();
         } else if (field == this._sendField) {
            this.close(true);
         } else {
            if (field == this._cancelField) {
               this.close(false);
            }
         }
      }
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      if (ConversationScreen.isPTTKey(keycode)) {
         this.stop();
         this.close(true);
         return true;
      } else {
         return super.keyUp(keycode, time);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.stop();
         this.close(false);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   public final void playerUpdate(Player player, String event, Object eventData) {
      if (!event.equals("recordStarted") && !event.equals("started")) {
         if ((
               event.equals("recordStopped")
                  || event.equals("stopped")
                  || event.equals("closed")
                  || event.equals("stoppedAtTime")
                  || event.equals("endOfMedia")
                  || event.equals("error")
                  || event.equals("recordError")
            )
            && this._timerID != -1) {
            Application.getApplication().cancelInvokeLater(this._timerID);
            if (!this.isPlaying()) {
               this._accumulatedTime = this._accumulatedTime + (System.currentTimeMillis() - this._startTime);
            }

            this._timerID = -1;
            this.onStop();
         }
      } else if (this._timerID == -1) {
         this._startTime = System.currentTimeMillis();
         this._timerID = Application.getApplication().invokeLater(new VoiceNoteDialog$TimerUpdateRunnable(this), 1000, true);
         this.onStart();
         return;
      }
   }

   static final LabelField access$000(VoiceNoteDialog x0) {
      return x0._stateField;
   }

   static final LabelField access$100(VoiceNoteDialog x0) {
      return x0._progressField;
   }

   static final FlowFieldManager access$200(VoiceNoteDialog x0) {
      return x0._hfm;
   }

   static final ButtonField access$300(VoiceNoteDialog x0) {
      return x0._stopField;
   }

   static final void access$400(VoiceNoteDialog x0) {
      x0.updateLayout();
   }

   static final VoiceRecordController access$500(VoiceNoteDialog x0) {
      return x0._recordController;
   }

   static final ButtonField access$600(VoiceNoteDialog x0) {
      return x0._playField;
   }

   static final ButtonField access$700(VoiceNoteDialog x0) {
      return x0._sendField;
   }

   static final ButtonField access$800(VoiceNoteDialog x0) {
      return x0._cancelField;
   }

   static final void access$900(VoiceNoteDialog x0) {
      x0.updateLayout();
   }

   static final long access$1000(VoiceNoteDialog x0) {
      return x0._startTime;
   }

   static final long access$1100(VoiceNoteDialog x0) {
      return x0._accumulatedTime;
   }
}
