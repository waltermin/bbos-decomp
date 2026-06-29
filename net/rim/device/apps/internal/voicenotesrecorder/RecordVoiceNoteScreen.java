package net.rim.device.apps.internal.voicenotesrecorder;

import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.AudioHeadsetListener;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.apps.api.framework.file.ExplorerServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.MIMEContentVerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.mediarecorder.MediaRecorderScreen;
import net.rim.device.apps.internal.mediarecorder.RenderScreen;
import net.rim.device.apps.internal.voicenotesrecorder.resource.RecordVoiceNotesResources;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.component.AnimatedBitmapField;
import net.rim.vm.PersistentInteger;

public final class RecordVoiceNoteScreen extends MediaRecorderScreen implements AudioHeadsetListener, PhoneEventListener {
   private VoiceRecordController _recordController;
   private LabelField _labelField;
   private String _paused;
   private String _recording;
   private String _record;
   private String _filename;
   private long _accumulatedTime;
   private long _recordStartTime;
   private int _timerID = -1;
   private long _timeLeft;
   private long _maxTime;
   private Bitmap _staticBitmap;
   private EncodedImage _animatedImage;
   private AnimatedBitmapField _imageField;
   private RecordVoiceNoteScreen$VoiceNoteStatusManager _vnManager = new RecordVoiceNoteScreen$VoiceNoteStatusManager(this);
   private int _nextFileNum;
   private Verb[] _sendVerbs;
   private Verb _terminalVerb = null;
   private boolean _autoPause = false;
   private boolean _missedCall = false;
   private Application _app = Application.getApplication();
   protected static final long NEXT_FILENUM_KEY;
   private static final int MAXTIME;
   private static final String AMR_MIME_TYPE;

   protected final char[] formatTime(long accumTime) {
      int time = (int)accumTime / 1000;
      int minutes = time / 60;
      int seconds = time % 60;
      String text = ((StringBuffer)(new Object())).append(minutes).append(":").toString();
      if (seconds < 10) {
         text = ((StringBuffer)(new Object())).append(text).append('0').append(seconds).toString();
      } else {
         text = ((StringBuffer)(new Object())).append(text).append(seconds).toString();
      }

      return text.toCharArray();
   }

   final void setContext(ContextObject ctx) {
      if (ctx == null) {
         ctx = ShowVoiceNotesRecorderApp.getVoiceNoteRecorderContext();
      }

      if (ContextObject.getFlag(ctx, 39)) {
         Object obj = ContextObject.get(ctx, -3185095355580406181L);
         if (obj instanceof Object) {
            this._terminalVerb = (Verb)obj;
            return;
         }
      } else {
         this._terminalVerb = null;
      }
   }

   protected final void activateStop(boolean browse) {
      this.stopRecording(true);
      if (browse) {
         ContextObject context = ShowVoiceNotesRecorderApp.getVoiceNoteRecorderContext();
         Object o = ContextObject.get(context, 4086083307293257364L);
         if (o instanceof Object) {
            this.close();
         } else {
            String path = FileUtilities.getDefaultPathForMIMEType("audio/amr");
            Verb browseVerb = ExplorerServices.getBrowseVerb(path, 132, null);
            browseVerb.invoke(null);
         }
      }

      super.activateStop();
   }

   @Override
   public final void headsetInserted(int type) {
   }

   @Override
   public final void headsetRemoved() {
   }

   @Override
   public final void headsetButtonUnclick(int button, int time) {
   }

   @Override
   public final void headsetButtonClick(int button, int time) {
      if (button == 0) {
         synchronized (this) {
            if (this.isRecording()) {
               this.activatePause();
            } else if (this._accumulatedTime == 0) {
               this.activateRecord();
            } else {
               this.activateResume();
            }
         }
      }
   }

   @Override
   public final void phoneEventNotify(int eventId, int param1, Object param2) {
      switch (eventId) {
         case 1000:
            if (this.isRecording()) {
               this._app.invokeLater(new RecordVoiceNoteScreen$1(this));
               return;
            }
            break;
         case 2002:
            if (!this.isRecording() && this._autoPause && !VoiceServices.isPhoneActive()) {
               this._app.invokeLater(new RecordVoiceNoteScreen$2(this));
               return;
            }

            this._missedCall = true;
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         Audio.addListener(this._app, this);
         VoiceServices.addPhoneEventListener(this);
      } else {
         Audio.removeListener(this._app, this);
         VoiceServices.removePhoneEventListener(this);
         this.stopRecording(false);
      }
   }

   @Override
   public final void onExposed() {
      if (!this.isRecording() && this._autoPause && !VoiceServices.isPhoneActive()) {
         this.activateResume();
         this._autoPause = false;
      }
   }

   @Override
   public final Menu getMenu(int instance) {
      if (this.isRecording()) {
         this.activatePause();
         this._autoPause = true;
      }

      Menu menu = super.getMenu(instance);
      String path = FileUtilities.getDefaultPathForMIMEType("audio/amr");
      Verb browseVerb = ExplorerServices.getBrowseVerb(path, 132, null);
      menu.add((MenuItem)(new Object(RecordVoiceNotesResources.getString(8), 591106, 0, browseVerb, null)));
      return menu;
   }

   @Override
   public final void onObscured() {
      super.onObscured();
      if (this.isRecording() && !this._missedCall) {
         this.activatePause();
         this._autoPause = true;
      }
   }

   @Override
   public final int adjustVolume(int volumeLevelChange) {
      return this.isRecording() ? -1 : super.adjustVolume(volumeLevelChange);
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      switch (Keypad.key(keycode)) {
         case 19:
         case 21:
            if (this.isRecording()) {
               return 65536;
            }
            break;
         case 32:
         case 273:
            if (this.isRecording()) {
               this.activatePause();
            } else if (this._accumulatedTime > 0) {
               this.activateResume();
            } else if (Keypad.key(keycode) == 32) {
               this.activateRecord();
            }
      }

      return super.processKeyEvent(event, key, keycode, time);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final String stopRecording(boolean save) {
      this._imageField.stopAnimation();

      label65:
      try {
         this._recordController.stopRecording();
      } catch (Throwable var11) {
         System.out.println(((StringBuffer)(new Object("stopRecording() error: "))).append(e.toString()).toString());
         break label65;
      }

      this._labelField.setText(this._record);
      this._autoPause = false;
      this._missedCall = false;
      if (this._timerID != -1) {
         this._app.cancelInvokeLater(this._timerID);
         this._timerID = -1;
      }

      this._accumulatedTime = 0;
      if (save) {
         int nextFileNumHandle = PersistentInteger.getId(6471394709640816166L, 0);
         PersistentInteger.set(nextFileNumHandle, this._nextFileNum);

         label59:
         try {
            byte[] ba = this._recordController.getVoiceClip();
            FileConnection file = (FileConnection)Connector.open(this._filename);
            if (file.exists()) {
               file.delete();
            }

            file.create();
            OutputStream out = file.openOutputStream();
            out.write(ba);
            out.close();
            this.setDirty(false);
         } catch (Throwable var10) {
            System.out.println(((StringBuffer)(new Object("Error writing voice note: "))).append(e.toString()).toString());
            break label59;
         }
      }

      this._imageField.setBitmap(this._staticBitmap);
      return this._filename;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean startRecording() {
      if (this.phoneActive()) {
         return false;
      }

      try {
         this._recordController.startRecording();
      } catch (Throwable var3) {
         Dialog.alert(RecordVoiceNotesResources.getString(18));
         System.out.println(((StringBuffer)(new Object("startRecording() error: "))).append(e.toString()).toString());
         return false;
      }

      this._accumulatedTime = 0;
      if (this._timerID == -1) {
         this._recordStartTime = InternalServices.getUptime();
         this._timerID = this._app.invokeLater(new RecordVoiceNoteScreen$TimerUpdateRunnable(this), 1000, true);
      }

      this._labelField.setText(this._recording);
      this._filename = this.getNextAvailableFilename();
      this.updateFileName(FileUtilities.getDisplayBaseName(this._filename).toCharArray());
      return true;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void pauseRecording() {
      if (this.isRecording()) {
         int bytes;
         label41:
         try {
            this._recordController.pauseRecording();
            bytes = this._recordController.getSize();
         } catch (Throwable var5) {
            System.out.println(((StringBuffer)(new Object("pauseRecording() error: "))).append(e.toString()).toString());
            bytes = 0;
            break label41;
         }

         this._labelField.setText(this._paused);
         if (this._timerID != -1) {
            this._app.cancelInvokeLater(this._timerID);
            long additionalTime = InternalServices.getUptime() - this._recordStartTime;
            this._accumulatedTime += additionalTime;
            this._timeLeft -= additionalTime;
            this._timerID = -1;
         }

         this.updateFileDuration(this.formatTime(this._accumulatedTime));
         String size = ((StringBuffer)(new Object())).append(bytes).append("B").toString();
         if (bytes > 1024) {
            size = ((StringBuffer)(new Object())).append((bytes + 512) / 1024).append("K").toString();
         }

         if (bytes > 1048576) {
            size = ((StringBuffer)(new Object())).append((bytes + 524288) / 1024 / 1024).append("M").toString();
         }

         this.updateFileSize(size.toCharArray());
         this.updateFileName(FileUtilities.getDisplayBaseName(this._filename).toCharArray());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void resumeRecording() {
      if (!this.isRecording() && !this.phoneActive()) {
         try {
            this._recordController.resumeRecording();
         } catch (Throwable var3) {
            System.out.println(((StringBuffer)(new Object("resumeRecording() error: "))).append(e.toString()).toString());
            return;
         }

         this._labelField.setText(this._recording);
         if (this._timerID == -1) {
            this._recordStartTime = InternalServices.getUptime();
            this._timerID = this._app.invokeLater(new RecordVoiceNoteScreen$TimerUpdateRunnable(this), 1000, true);
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean isRecording() {
      try {
         return this._recordController.isRecording();
      } catch (Throwable var3) {
         System.out.println(((StringBuffer)(new Object("isRecording() error: "))).append(e.toString()).toString());
         return false;
      }
   }

   private final String getNextAvailableFilename() {
      String filename = null;
      FileConnection file = null;
      String location = FileUtilities.getDefaultPathForMIMEType("audio/amr");
      FileUtilities.ensureDirectoryExists(location);
      int nextFileNumHandle = PersistentInteger.getId(6471394709640816166L, 0);
      this._nextFileNum = PersistentInteger.get(nextFileNumHandle);
      if (this._nextFileNum == 0) {
         this._nextFileNum = 1;
      }

      String prefix = RecordVoiceNotesResources.getString(6);

      for (boolean fileExists = true; fileExists; this._nextFileNum++) {
         String num = NumberUtilities.toString(this._nextFileNum, 10, 5);
         filename = ((StringBuffer)(new Object())).append(location).append(prefix).append(" ").append(num).append(".amr").toString();
         fileExists = this.fileExists(filename);
      }

      if (file != null) {
         try {
            file.close();
            return filename;
         } finally {
            return filename;
         }
      } else {
         return filename;
      }
   }

   private final boolean recordingToMediaCard() {
      return this._filename.toLowerCase().startsWith("file:///sdcard");
   }

   private final boolean enoughSpaceAvail(long time) {
      if (this.recordingToMediaCard()) {
         if (time < 600000) {
            return false;
         }
      } else if (time < 30000) {
         return false;
      }

      return true;
   }

   @Override
   protected final void activateRecord() {
      super.activateRecord();
      if (!this.startRecording()) {
         super.activateStop();
      } else {
         this._autoPause = false;
         this._missedCall = false;
         this._maxTime = this.getTimeAvailable();
         if (this._maxTime > 3600000) {
            this._maxTime = 3600000;
         }

         this._timeLeft = this._maxTime;
         if (!this.enoughSpaceAvail(this._maxTime)) {
            this.stopRecording(false);
            super.activateStop();
            String prompt = RecordVoiceNotesResources.getString(17);
            Dialog.inform(prompt);
         } else {
            this._imageField.setImage(this._animatedImage);
            this._imageField.setMaximumLoopIterations(10000);
            this._imageField.startAnimation();
            this.setDirty(true);
         }
      }
   }

   @Override
   public final void activatePause() {
      super.activatePause();
      this._imageField.stopAnimation();
      this.pauseRecording();
      this._imageField.setBitmap(this._staticBitmap);
      this._autoPause = false;
      this._missedCall = false;
      this.runTerminalVerbIfApplicable();
   }

   @Override
   protected final void activateResume() {
      if (!this.phoneActive()) {
         super.activateResume();
         this._imageField.setImage(this._animatedImage);
         this._imageField.startAnimation();
         long availTime = this.getTimeAvailable() - this._accumulatedTime;
         if (availTime < this._timeLeft) {
            this._timeLeft = availTime;
         }

         this._maxTime = this._timeLeft + this._accumulatedTime;
         this.updateElapsedTime(this.formatTime(this._accumulatedTime + 500));
         this.resumeRecording();
      }
   }

   @Override
   public final void activateStop() {
      this.activateStop(true);
   }

   @Override
   public final void save() {
      this.stopRecording(true);
   }

   @Override
   protected final void activateDelete() {
      if (Dialog.ask(2) == 3) {
         super.activateDelete();
         this.stopRecording(false);
         this._labelField.setText(this._record);
         this._accumulatedTime = 0;
         this.updateElapsedTime(this.formatTime(0));
         this.setDirty(false);
      }
   }

   private final boolean fileExists(String filename) {
      FileConnection file = null;

      try {
         file = (FileConnection)Connector.open(filename);
      } finally {
         ;
      }

      boolean fileExists = false;
      if (file != null) {
         fileExists = file.exists();
      }

      return fileExists;
   }

   @Override
   protected final void activateFolder() {
      this.pauseRecording();
      Verb renameVerb = ExplorerServices.getRenameVerb(FileUtilities.getDisplayName(this._filename), null);
      if (renameVerb != null) {
         label35:
         try {
            FileConnection file = (FileConnection)Connector.open(this._filename);
            file.create();
            Object obj = renameVerb.invoke(this._filename);
            if (obj instanceof Object) {
               this._filename = (String)obj;
               this.updateFileName(FileUtilities.getDisplayBaseName(this._filename).toCharArray());
            }

            if (file.exists()) {
               file.delete();
            }

            file = (FileConnection)Connector.open(this._filename);
            if (file.exists()) {
               file.delete();
            }
         } finally {
            break label35;
         }
      }

      super.activateFolder();
   }

   @Override
   protected final void activatePlay() {
      super.activatePlay();
      String filename = this.stopRecording(true);
      this._labelField.setText(this._record);

      label20:
      try {
         FileConnection connection = (FileConnection)Connector.open(filename);
         RenderScreen renderScreen = (RenderScreen)(new Object(0));
         renderScreen.init(connection);
         renderScreen.doModal();
      } finally {
         break label20;
      }

      String path = FileUtilities.getDefaultPathForMIMEType("audio/amr");
      Verb browseVerb = ExplorerServices.getBrowseVerb(path, 132, null);
      browseVerb.invoke(path);
   }

   @Override
   protected final void activateSend() {
      if (this._sendVerbs != null && this._sendVerbs.length > 0) {
         int idx = 0;
         if (this._sendVerbs.length > 1) {
            String[] verbStrings = new Object[this._sendVerbs.length];
            int i = this._sendVerbs.length;

            while (--i >= 0) {
               verbStrings[i] = this._sendVerbs[i].toString();
            }

            Dialog pickVerb = (Dialog)(new Object(RecordVoiceNotesResources.getString(20), verbStrings, 0, true));
            pickVerb.setEscapeEnabled(true);
            pickVerb.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
            idx = pickVerb.doModal();
            if (idx == -1) {
               return;
            }
         }

         if (this._sendVerbs[idx] != null) {
            String filename = this.stopRecording(true);
            this._labelField.setText(this._record);
            ContextObject contextObj = (ContextObject)(new Object());
            contextObj.put(2765042845091913199L, filename);
            contextObj.put(-4241241545455759532L, "audio/amr");
            String fileDisplayName = FileUtilities.getDisplayBaseName(filename);
            contextObj.put(-1188330299125235844L, fileDisplayName);
            contextObj.put(-4886909117188079897L, fileDisplayName);
            this._sendVerbs[idx].invoke(contextObj);
            super.activateSend();
            return;
         }
      }

      Dialog.alert(RecordVoiceNotesResources.getString(21));
   }

   @Override
   public final void close() {
      this.stopRecording(false);
      super.close();
   }

   private final void runTerminalVerbIfApplicable() {
      if (this._terminalVerb != null) {
         label21:
         try {
            this.save();
         } finally {
            break label21;
         }

         this._terminalVerb.invoke(this._filename);
         this.close();
      }
   }

   private final boolean phoneActive() {
      if (VoiceServices.isPhoneActive()) {
         Dialog.alert(RecordVoiceNotesResources.getString(22));
         return true;
      } else {
         return false;
      }
   }

   public RecordVoiceNoteScreen() {
      this._recordController = new VoiceRecordController();
      this.setContext(ShowVoiceNotesRecorderApp.getVoiceNoteRecorderContext());
      this.getMainManager().setTag(Tag.create("media-library-screen"));
      this.setTag(Tag.create("media-list"));
      this._staticBitmap = RecordVoiceNotesResources.getVoiceNoteRecorderImage();
      this._animatedImage = RecordVoiceNotesResources.getAnimatedRecorderImage();
      this._imageField = (AnimatedBitmapField)(new Object(this._staticBitmap, 12884901888L));
      this._paused = RecordVoiceNotesResources.getString(11);
      this._recording = RecordVoiceNotesResources.getString(10);
      this._record = RecordVoiceNotesResources.getString(9);
      this._labelField = (LabelField)(new Object(this._record, 12884901952L));
      this._vnManager.add(this._imageField);
      this._vnManager.add(this._labelField);
      this.add(this._vnManager);
      this.updateMemoryRemainingIndicator(100);
      this.updateElapsedTime(this.formatTime(0));
      this.setDirty(false);
      this._sendVerbs = MIMEContentVerbRepository.getVerbs("audio/amr");
      this._app = Application.getApplication();
   }

   private final long getTimeAvailable() {
      long avail = 0;
      long time = 0;

      label36:
      try {
         char dirSlash = '/';
         int dirNameEnd = this._filename.lastIndexOf(dirSlash);
         String dir = this._filename.substring(0, dirNameEnd + 1);
         FileConnection file = (FileConnection)Connector.open(dir);
         avail = file.availableSize();
         file.close();
      } finally {
         break label36;
      }

      avail /= 1024;
      avail -= 500;
      if (avail > 0) {
         time = (long)(avail / 4609884578576439706L);
         if (!this.recordingToMediaCard()) {
            int maxDeviceTime = 90;
            if (time > maxDeviceTime) {
               time = maxDeviceTime;
            }
         }

         return time * 1000;
      } else {
         return 0;
      }
   }
}
