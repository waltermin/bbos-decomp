package net.rim.device.apps.internal.videorecorder;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.ScreenUiEngineAttachedListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.api.framework.file.ExplorerServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.MIMEContentVerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.OptionsChangeListener;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.mediarecorder.MediaRecorderScreen;
import net.rim.device.internal.camera.Camera;
import net.rim.device.internal.camera.CameraVideoListener;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.LEDEngine;
import net.rim.device.internal.ui.component.ProgressDialog;

final class VideoRecorderScreen extends MediaRecorderScreen implements CameraVideoListener, OptionsChangeListener, ScreenUiEngineAttachedListener {
   private VideoViewfinderField _viewfinder;
   private VideoRecordController _vrc;
   private ProgressDialog _progressDialog;
   private long _progressLastUpdated;
   private long _accumulatedTime;
   private long _recordStartTime;
   private int _timerID = -1;
   private VideoRecorderOptions _options;
   private int _flashMode = -1;
   private Runnable _delayedAction;
   private boolean _transcoded;
   private boolean _transcodeInProgress;
   private Verb[] _sendVerbs;
   private Verb _terminalVerb = null;
   private LEDEngine _led;
   private static final int RESET_PROGRESS = 0;
   private static final int DISMISS_PROGRESS = -1;
   private static final long PROGRESS_UPDATE_INTERVAL = 250L;
   private static final String MIME_TYPE = "video/3gpp";

   final void cleanup() {
      this.setLedState(false);
      if (this.isDisplayed()) {
         this.close();
      }

      try {
         if (Camera.isViewfinderActive()) {
            this._viewfinder.vfStop(true);
            return;
         }
      } finally {
         return;
      }
   }

   protected final char[] formatTime(int accumTime) {
      int time = accumTime / 1000;
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
         ctx = ShowVideoCameraApp.getAndClearVideoCameraContext();
      }

      if (ContextObject.getFlag(ctx, 74)) {
         this._vrc.setMmsMode(1);
         this._vrc.setVideoSaveDirectory((String)ContextObject.get(ctx, 2765042845091913199L));
      } else {
         this._vrc.setMmsMode(this._options.getVideoFormatIndex());
      }

      if (ContextObject.getFlag(ctx, 39)) {
         Object obj = ContextObject.get(ctx, -3185095355580406181L);
         if (obj instanceof Object) {
            this._terminalVerb = (Verb)obj;
         }
      } else {
         this._terminalVerb = null;
      }

      Object screen = ContextObject.get(ctx, -1477447097671931650L);
      if (screen instanceof Object) {
         ((Screen)screen).addScreenUiEngineAttachedListener(this);
      }
   }

   @Override
   public final void optionsChanged(int option) {
      switch (option) {
         case 1:
         default:
            this.setFlashMode(this._options.getFlashModeIndex());
            return;
         case 3:
            int[] format = this._options.getVideoFormat();
            this._viewfinder.setFormat(format[0], format[1], format[2]);
            this.setMmsMode(this._options.getVideoFormatIndex());
            return;
         case 5:
            ShowVideoCameraApp.setVideoCameraContext(null);
         case 0:
         case 2:
         case 4:
            return;
         case 6:
            this._viewfinder.setColourEffect(this._options.getColourEffect());
            this._viewfinder.vfRestart();
      }
   }

   @Override
   public final void recordComplete(int subMsg) {
      if (subMsg == 0 && !this._transcoded) {
         if (this._timerID != -1) {
            Application.getApplication().cancelInvokeLater(this._timerID);
            this._accumulatedTime = this._accumulatedTime + (InternalServices.getUptime() - this._recordStartTime);
            this._timerID = -1;
         }

         this._delayedAction = null;
         this.transcodeVideo();
         this.showResume();
      } else {
         if (subMsg == 1) {
            this.updateTranscodeProgress(-1);
            this._transcodeInProgress = false;
            if (this._transcoded) {
               this._vrc.cleanupStreamsAndFiles(false);
            }

            if (this._delayedAction != null) {
               Application.getApplication().invokeLater(this._delayedAction);
               this._delayedAction = null;
            }
         }
      }
   }

   @Override
   public final void setNightMode(int mode) {
   }

   @Override
   public final void recordStatusUpdate(int progress) {
      this.updateTranscodeProgress(progress);
   }

   @Override
   public final void recordError(int error) {
      switch (error) {
         case 13:
            this._vrc.fileAlreadyTranscoded();
            return;
         default:
            System.out.println(((StringBuffer)(new Object("VIDEO error:"))).append(error).toString());
            this.updateTranscodeProgress(-1);
            if (this._transcoded) {
               this._vrc.cleanupStreamsAndFiles(true);
            }

            if (this._delayedAction != null) {
               Application.getApplication().invokeLater(this._delayedAction);
               this._delayedAction = null;
            } else {
               this.showRecord();
            }

            this._transcodeInProgress = false;
            Dialog.alert(VideoRecorderResources.getString(25));
      }
   }

   @Override
   protected final void activateFolder() {
      this._delayedAction = new VideoRecorderScreen$1(this);
      this.transcodeVideo();
   }

   @Override
   protected final void activatePlay() {
      this._delayedAction = new VideoRecorderScreen$2(this);
      this.transcodeVideo();
   }

   @Override
   protected final void activateSend() {
      this._delayedAction = new VideoRecorderScreen$3(this);
      this.transcodeVideo();
   }

   @Override
   protected final void activateResume() {
      if (Phone.isPhoneActive()) {
         Dialog.alert(VideoRecorderResources.getString(26));
      } else if (!this._transcoded) {
         if (this._vrc.startRecord()) {
            if (this._timerID == -1) {
               this._recordStartTime = InternalServices.getUptime();
               this._timerID = Application.getApplication().invokeLater(new VideoRecorderScreen$TimerUpdateRunnable(this), 1000, true);
            }

            super.activateResume();
         } else {
            Dialog.alert(VideoRecorderResources.getString(21));
            this.transcodeVideo();
         }
      } else {
         this.activateStop();
      }
   }

   @Override
   protected final void activateStop() {
      if (this._timerID != -1) {
         Application.getApplication().cancelInvokeLater(this._timerID);
         this._accumulatedTime = 0;
         this._timerID = -1;
      }

      this._delayedAction = new VideoRecorderScreen$4(this);
      this.transcodeVideo();
   }

   private final void activateStop0() {
      this.runTerminalVerbIfApplicable();
      super.activateStop();
      this.resetIndicators();
   }

   private final void runTerminalVerbIfApplicable() {
      if (this._terminalVerb != null) {
         this._terminalVerb.invoke(this._vrc.getVideoFileName(true));
         this.close();
      }
   }

   @Override
   public final void close() {
      if (!this._transcodeInProgress) {
         this._vrc.exitController();
         this._options.removeOptionsChangeListener(this);
         super.close();
      }
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (c == 27) {
         if (super._recording) {
            this.activateStop();
            return true;
         }
      } else {
         if (c == '\b') {
            this.activateDelete();
            return true;
         }

         if (c == ' ') {
            if (!this.isRecording()) {
               this.setFlashMode(-1);
               return true;
            }

            return false;
         }
      }

      return super.keyChar(c, status, time);
   }

   private final ContextObject createContextForSendVerb() {
      String filename = this._vrc.getVideoFileName(true);
      String fileDisplayName = FileUtilities.getDisplayBaseName(filename);
      ContextObject contextObj = (ContextObject)(new Object());
      contextObj.put(2765042845091913199L, filename);
      contextObj.put(-4241241545455759532L, "video/3gpp");
      contextObj.put(-1188330299125235844L, fileDisplayName);
      contextObj.put(-4886909117188079897L, fileDisplayName);
      return contextObj;
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (visible) {
         AudioRouter.getInstance().addSource(7);
      } else {
         AudioRouter.getInstance().removeSource(7);
      }

      super.onVisibilityChange(visible);
   }

   @Override
   protected final void showRecord() {
      super.showRecord();
      this.updateVideoIndicators();
      this._transcoded = false;
      this.setLedState(false);
   }

   @Override
   public final boolean canResume() {
      return !this._transcoded;
   }

   @Override
   public final void onScreenUiEngineAttached(Screen screen, boolean attached) {
      if (!attached) {
         Application app = this.getApplication();
         if (app != null) {
            app.invokeLater(new VideoRecorderScreen$5(this));
         }
      }
   }

   @Override
   protected final void activateRecord() {
      if (Phone.isPhoneActive()) {
         Dialog.alert(VideoRecorderResources.getString(26));
      } else {
         if (this._viewfinder.isStarted()) {
            this._accumulatedTime = 0;
            this._transcoded = false;
            this._transcodeInProgress = false;
            if (this._flashMode != 0 && this.checkBatteryTooLow()) {
               this.setFlashMode(0);
            }

            if (this._vrc.startRecord()) {
               if (this._timerID == -1) {
                  this._recordStartTime = InternalServices.getUptime();
                  this._timerID = Application.getApplication().invokeLater(new VideoRecorderScreen$TimerUpdateRunnable(this), 1000, true);
               }

               super.activateRecord();
               return;
            }

            Dialog.alert(VideoRecorderResources.getString(21));
         }
      }
   }

   @Override
   public final Menu getMenu(int instance) {
      if (this.isRecording()) {
         this.activatePause();
      }

      ContextObject menuContext = (ContextObject)(new Object());
      menuContext.put(244, new Object(244387));
      SystemEnabledMenu menu = (SystemEnabledMenu)(new Object(menuContext, null));
      Menu.setTargetScreen(this);
      menu.setInstance(instance);
      this.makeMenuWithContext(menu, instance);
      String path = this._options.getDestinationFolder();
      Verb browseVerb = ExplorerServices.getBrowseVerb(path, 130, null);
      menu.add((MenuItem)(new Object(VideoRecorderResources.getString(19), 591106, 0, browseVerb, null)));
      menu.add((MenuItem)(new Object(new VideoRecorderScreen$OptionsVerb(), 0)));
      if (this.isRecording() || this._transcoded) {
         Verb[] sendVerbs = MIMEContentVerbRepository.getVerbs("video/3gpp");
         if (sendVerbs != null && sendVerbs.length > 0) {
            ContextObject context = this.createContextForSendVerb();

            for (int idx = 0; idx < sendVerbs.length; idx++) {
               Verb verb = new VideoRecorderScreen$TranscodeWrapperVerb(this, sendVerbs[idx]);
               menu.add((MenuItem)(new Object(null, verb.getOrdering(), 0, verb, context)));
            }
         }
      }

      return menu;
   }

   public VideoRecorderScreen() {
      this._vrc = VideoRecordController.getInstance();
      this._options = VideoRecorderOptions.getOptions();
      this._options.addOptionsChangeListener(this);
      this.setContext(ShowVideoCameraApp.getAndClearVideoCameraContext());
      int[] format;
      if (this._vrc.isMmsMode()) {
         format = this._options.getVideoFormat(1);
      } else {
         format = this._options.getVideoFormat();
      }

      this._viewfinder = new VideoViewfinderField(format[0], format[1], format[2], this._options.getColourEffect());
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(2305843022098595840L));
      hfm.add(this._viewfinder);
      this.add(hfm);
      this.setFlashMode(this._options.getFlashModeIndex());
      this.resetIndicators();
   }

   private final void transcodeVideo() {
      if (super._recording && !this._transcoded) {
         super._recording = false;
         this._transcoded = true;
         this._transcodeInProgress = true;
         this._vrc.stopRecord();
         this._vrc.transcodeVideo();
      } else {
         if (this._delayedAction != null) {
            Application.getApplication().invokeAndWait(this._delayedAction);
         }
      }
   }

   private final void updateTranscodeProgress(int progress) {
      if (progress == -1 && this._progressDialog != null) {
         this._progressDialog.dismiss();
         this._progressDialog = null;
      } else {
         if (progress >= 0) {
            long newTime = InternalServices.getUptime();
            if (newTime - this._progressLastUpdated > 250) {
               if (this._progressDialog == null) {
                  this._progressDialog = (ProgressDialog)(new Object(VideoRecorderResources.getString(4)));
                  this._progressDialog.show();
               }

               this._progressDialog.setProgress(progress);
               this._progressLastUpdated = newTime;
            }
         }
      }
   }

   @Override
   protected final void showResume() {
      super.showResume();
      this.updateFileName(this._vrc.getVideoFileName(false).toCharArray());
      this.updateFileSize(this._vrc.getVideoFileSize().toCharArray());
      this.updateFileDuration(this.formatTime((int)this._accumulatedTime));
      this.setLedState(false);
   }

   @Override
   protected final void showPause() {
      super.showPause();
      this.updateVideoIndicators();
      this.setLedState(true);
   }

   @Override
   public final void activatePause() {
      super.activatePause();
      if (this._timerID != -1) {
         Application.getApplication().cancelInvokeLater(this._timerID);
         this._accumulatedTime = this._accumulatedTime + (InternalServices.getUptime() - this._recordStartTime);
         this._timerID = -1;
      }

      this._vrc.pauseRecord();
      this.updateFileName(this._vrc.getVideoFileName(false).toCharArray());
      this.updateFileSize(this._vrc.getVideoFileSize().toCharArray());
      this.updateFileDuration(this.formatTime((int)this._accumulatedTime));
   }

   @Override
   protected final void activateDelete() {
      if (Dialog.ask(2) == 3) {
         this._vrc.cleanupStreamsAndFiles(true);
         super.activateDelete();
         this.resetIndicators();
      }
   }

   private final void resetIndicators() {
      this.updateElapsedTime(this.formatTime(0));
      this.updateMemoryRemainingIndicator(this._vrc.getPercentMemoryRemaining());
      this.updateVideoIndicators();
   }

   private final boolean checkBatteryTooLow() {
      return (DeviceInfo.getBatteryStatus() & 8192) != 0;
   }

   private final void setFlashMode(int flashMode) {
      if (flashMode < 0) {
         String[] names = VideoRecorderResources.getStringArray(9);
         flashMode = (this._flashMode + 1) % names.length;
      }

      if (flashMode != 0 && this.checkBatteryTooLow()) {
         flashMode = 0;
      }

      if (this._flashMode != flashMode) {
         this._flashMode = flashMode;
         this.updateFlashIndicator(flashMode);
         this._viewfinder.setFlashMode(flashMode);
         this._viewfinder.vfRestart();
      }
   }

   private final void setLedState(boolean state) {
      if (this._led == null) {
         this._led = LEDEngine.getInstance();
      }

      if (state) {
         this._led.setFlag(64);
      } else {
         this._led.clearFlag(64);
      }
   }

   private final void setMmsMode(int mode) {
      this._vrc.setMmsMode(mode);
      this.updateMmsModeIndicator(this._vrc.isMmsMode());
   }

   private final void updateVideoIndicators() {
      if (this._flashMode != -1) {
         this.updateFlashIndicator(this._flashMode);
      }

      if (this._vrc != null) {
         this.updateMmsModeIndicator(this._vrc.isMmsMode());
      }
   }
}
