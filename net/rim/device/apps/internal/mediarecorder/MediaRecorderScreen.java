package net.rim.device.apps.internal.mediarecorder;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MediaField;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.ScreenUiEngineAttachedListener;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ui.HintPollingThread;
import net.rim.device.apps.api.ui.HintPollingThread$HintProvider;
import net.rim.device.apps.api.ui.HintPopup;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.system.ActiveMedia;
import net.rim.device.internal.system.ActiveMediaObservable;
import net.rim.device.internal.ui.UiSettings;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.event.EventEngine;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.NodeImpl;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.internal.mediaengine.service.node.TextNode;
import net.rim.plazmic.mediaengine.MediaListener;
import net.rim.plazmic.mediaengine.MediaManager;
import net.rim.plazmic.mediaengine.MediaPlayer;

public class MediaRecorderScreen extends MainScreen implements MediaListener, ScreenUiEngineAttachedListener, HintPollingThread$HintProvider, ActiveMedia {
   private Field _banner;
   private MediaField _mediaField;
   private MediaPlayer _pmePlayer;
   private MediaManager _manager;
   private ModelInteractorImpl _model;
   private ModelInteractorImpl _modelRecord;
   private ModelInteractorImpl _modelControl;
   private String _hint;
   private TextNode _elapsedNode;
   private TextNode _filenameNode;
   private TextNode _durationNode;
   private TextNode _filesizeNode;
   private TextNode _mmsNode;
   protected boolean _recording;
   private static final String RECORD;
   private static final String PAUSE;
   private static final String RESUME;
   private static final String STOP;
   private static final String DELETE;
   private static final String FOLDER;
   private static final String PLAY;
   private static final String SEND;
   private static final String[] MEMORY = new String[]{
      "memory0", "memory10", "memory20", "memory30", "memory40", "memory50", "memory60", "memory70", "memory80", "memory90", "memory100"
   };
   private static final String[] FLASH_EVENT = new String[]{"flashOff", "flashOn", "flashAuto"};
   private static final String MMS_MODE;
   private static final String CE_RECORDING;
   private static final String CE_PAUSING;
   private static final String CE_DISABLE_RESUME;
   private static final String CE_ENABLE_RESUME;
   private static ResourceBundle _MediaRecorderBundle = ResourceBundle.getBundle(-6927834585541129670L, "net.rim.device.apps.internal.resource.MediaRecorder");

   public void updateMemoryRemainingIndicator(int percentageRemaining) {
      int index = MathUtilities.clamp(0, percentageRemaining / (MEMORY.length - 1), MEMORY.length - 1);
      this._modelRecord.trigger(107, this._modelRecord.getHandle(MEMORY[index]), null);
      this._mediaField.invalidate();
   }

   public void updateFlashIndicator(int flashMode) {
      if (this._model == this._modelRecord) {
         this._model.trigger(107, this._model.getHandle(FLASH_EVENT[flashMode]), null);
      }
   }

   protected void _getHintPosition(XYRect rect) {
      rect.translate(0, -rect.height);
   }

   public boolean canResume() {
      return true;
   }

   public void updateMmsModeIndicator(boolean mode) {
      String modeString = mode ? "MMS" : "";
      this._mmsNode.setString(modeString.toCharArray());
      this._mediaField.invalidate();
   }

   protected void activateRecord() {
      this.showPause();
      this._recording = true;
   }

   public boolean isRecording() {
      return this._recording;
   }

   public void activatePause() {
      this.showResume();
   }

   protected void activateResume() {
      this.showPause();
   }

   protected void activateStop() {
      this.showRecord();
      this._recording = false;
   }

   protected void activateDelete() {
      this.showRecord();
      this._recording = false;
   }

   protected void activateFolder() {
   }

   protected void activatePlay() {
      this.showRecord();
      this._recording = false;
   }

   protected void activateSend() {
      this.showRecord();
      this._recording = false;
   }

   protected void showPause() {
      if (this._model != this._modelRecord) {
         this.switchSkinModel(this._modelRecord, "record");
      }

      this._modelRecord.trigger(107, this._modelRecord.getHandle("pausing"), null);
   }

   protected void showResume() {
      if (this._model != this._modelControl) {
         this.switchSkinModel(this._modelControl, "resume");
      }
   }

   protected void enableResumeButton(boolean enable) {
      if (this._model == this._modelControl) {
         this._model.trigger(107, this._model.getHandle(enable ? "enableResume" : "disableResume"), null);
      }
   }

   protected void showRecord() {
      if (this._model != this._modelRecord) {
         this.switchSkinModel(this._modelRecord, "record");
      }

      this._model.trigger(107, this._model.getHandle("recording"), null);
   }

   public void updateFileSize(char[] filesizeString) {
      this._filesizeNode.setString(filesizeString);
      this._mediaField.invalidate();
   }

   public void updateElapsedTime(char[] formattedTime) {
      if (this._elapsedNode != null) {
         this._elapsedNode.setString(formattedTime);
         this._mediaField.invalidate();
      }
   }

   public void updateFileDuration(char[] formattedTime) {
      this._durationNode.setString(formattedTime);
      this._mediaField.invalidate();
   }

   public void updateFileName(char[] filenameString) {
      this._filenameNode.setString(filenameString);
      this._mediaField.invalidate();
   }

   @Override
   public void onScreenUiEngineAttached(Screen screen, boolean attached) {
      if (!attached) {
         Application app = this.getApplication();
         if (app != null) {
            app.invokeLater(new MediaRecorderScreen$1(this));
         }
      }
   }

   @Override
   public void mediaEvent(Object sender, int event, int eventParam, Object data) {
      if (event != 105) {
         if (event == 103) {
            String id = NodeImpl.getId(eventParam, this._model);
            String hintString = null;
            if ("resume".equals(id)) {
               int resId = this.canResume() ? 6 : 14;
               hintString = _MediaRecorderBundle.getString(resId);
            } else if ("stop".equals(id)) {
               hintString = _MediaRecorderBundle.getString(12);
            } else if ("delete".equals(id)) {
               hintString = _MediaRecorderBundle.getString(8);
            } else if ("folder".equals(id)) {
               hintString = _MediaRecorderBundle.getString(9);
            } else if ("play".equals(id)) {
               hintString = _MediaRecorderBundle.getString(10);
            } else if ("send".equals(id)) {
               hintString = _MediaRecorderBundle.getString(11);
            }

            if (hintString != null) {
               this._hint = hintString;
               HintPollingThread.reset();
            }
         }
      } else {
         if (this._hint != null) {
            this._hint = null;
         }

         String id = NodeImpl.getId(eventParam, this._model);
         if ("record".equals(id)) {
            if (this._recording) {
               this.activatePause();
               return;
            }

            this.activateRecord();
            return;
         }

         if ("resume".equals(id)) {
            this.activateResume();
            return;
         }

         if ("stop".equals(id)) {
            this.activateStop();
            return;
         }

         if ("delete".equals(id)) {
            this.activateDelete();
            return;
         }

         if ("folder".equals(id)) {
            this.activateFolder();
            return;
         }

         if ("play".equals(id)) {
            this.activatePlay();
            return;
         }

         if ("send".equals(id)) {
            this.activateSend();
            return;
         }
      }
   }

   @Override
   public void getHintPosition(XYRect rect) {
      HintPopup.transformToScreen(this._mediaField, rect);
      this._getHintPosition(rect);
   }

   @Override
   public Object getHint() {
      if (this._hint != null && this._model == this._modelControl && !this.isObscured()) {
         LabelField hint = (LabelField)(new Object(this._hint, 51539607616L));
         hint.setTag(ThemeUtilities.TAG_RECORDER_HINT);
         this._hint = null;
         return hint;
      } else {
         return null;
      }
   }

   @Override
   public boolean isAudioInUse() {
      return true;
   }

   @Override
   public int codecUsed() {
      return -1;
   }

   @Override
   public boolean isForce() {
      return true;
   }

   @Override
   public boolean isAlert() {
      return false;
   }

   @Override
   protected void onExposed() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   private void switchSkinModel(ModelInteractorImpl newModel, String nodeName) {
      label28:
      try {
         this._pmePlayer.stop();
         this._model = newModel;
         this._model.reset();
         this._pmePlayer.setMedia(this._model);
         this._pmePlayer.setMediaTime(0);
         this._pmePlayer.start();
      } finally {
         break label28;
      }

      Object services = this._pmePlayer.getServices();
      if (services instanceof Object) {
         MediaServices mediaServices = (MediaServices)services;
         FocusInteractor focus = (FocusInteractor)mediaServices.getService("FocusInteractor");
         focus.setFocusToItem(this._model.getHandle(nodeName));
         EventEngine engine = mediaServices.getEngine();
         if (engine != null) {
            engine.dispatchEvents();
         }
      }

      this._mediaField.invalidate();
   }

   @Override
   public int adjustVolume(int volumeLevelChange) {
      return -1;
   }

   public MediaRecorderScreen() {
      super(2814749767303168L);
      this.setTag(ThemeUtilities.TAG_RECORDER_SCREEN);
      this.getMainManager().setTag(ThemeUtilities.TAG_RECORDER_SCREEN);
      this._banner = RibbonBanner.getInstance().getStatusBanner(null, 3);
      this._banner.setTag(ThemeUtilities.TAG_RECORDER_BANNER);
      this._mediaField = (MediaField)(new Object(18014411397529600L));
      this._manager = (MediaManager)(new Object());
      this._pmePlayer = (MediaPlayer)(new Object());
      this._pmePlayer.setUI(this._mediaField);
      this._pmePlayer.setInternalMediaListener(this);

      label20:
      try {
         this._modelRecord = this.createModel(ThemeUtilities.TAG_RECORDER_RECORDBAR, "cod://net_rim_bb_mediarecorder/recorderbar.pme");
         this._modelControl = this.createModel(ThemeUtilities.TAG_RECORDER_CONTROLBAR, "cod://net_rim_bb_mediarecorder/controlbar.pme");
         this._model = this._modelRecord;
         this._pmePlayer.setMedia(this._model);
      } finally {
         break label20;
      }

      this._elapsedNode = (TextNode)this._modelRecord.getNode("elapsedtime");
      this._filenameNode = (TextNode)this._modelControl.getNode("filename");
      this._durationNode = (TextNode)this._modelControl.getNode("duration");
      this._filesizeNode = (TextNode)this._modelControl.getNode("filesize");
      this._mmsNode = (TextNode)this._modelRecord.getNode("mmsmode");
      this.setBanner(this._banner);
      this.setStatus(this._mediaField);
      this.showRecord();
   }

   @Override
   public Menu getMenu(int instance) {
      ContextObject menuContext = (ContextObject)(new Object());
      menuContext.put(244, new Object(32247));
      SystemEnabledMenu menu = (SystemEnabledMenu)(new Object(menuContext, null));
      Menu.setTargetScreen(this);
      menu.setInstance(instance);
      this.makeMenuWithContext(menu, instance);
      return menu;
   }

   @Override
   protected void onObscured() {
      Backlight.setTimeout(UiSettings.getBacklightTimeout());
      super.onObscured();
   }

   @Override
   protected void onVisibilityChange(boolean visible) {
      if (visible) {
         ActiveMediaObservable.setActive(this);

         label22:
         try {
            this._pmePlayer.start();
         } finally {
            break label22;
         }
      } else {
         ActiveMediaObservable.setInactive(this);
         this._pmePlayer.stop();
      }

      super.onVisibilityChange(visible);
   }

   private ModelInteractorImpl createModel(Tag tag, String defaultName) {
      String mediaName = null;
      Theme theme = ThemeManager.getActiveTheme();
      if (theme != null) {
         ThemeAttributeSet tas = theme.getAttributeSet(tag);
         if (tas != null) {
            mediaName = tas.getLayoutName();
         }
      }

      if (mediaName == null) {
         ThemeAttributeSet tas = this.getThemeAttributeSet();
         if (tas != null) {
            mediaName = tas.getLayoutName();
         }

         if (mediaName == null) {
            mediaName = defaultName;
         }
      }

      return (ModelInteractorImpl)this._manager.createMedia(mediaName);
   }
}
