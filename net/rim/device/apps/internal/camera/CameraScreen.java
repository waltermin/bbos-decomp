package net.rim.device.apps.internal.camera;

import java.util.Calendar;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MediaField;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.ScreenUiEngineAttachedListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.apps.api.framework.file.ExplorerServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.OptionsChangeListener;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.cldc.util.TimeService;
import net.rim.device.internal.camera.Camera;
import net.rim.device.internal.camera.CameraListener;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.io.file.MetaDataFile;
import net.rim.device.internal.io.file.RootRegister;
import net.rim.device.internal.ui.UiSettings;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.NodeImpl;
import net.rim.plazmic.internal.mediaengine.service.node.TextNode;
import net.rim.plazmic.mediaengine.MediaListener;
import net.rim.plazmic.mediaengine.MediaManager;
import net.rim.plazmic.mediaengine.MediaPlayer;

final class CameraScreen extends MainScreen implements MediaListener, OptionsChangeListener, CameraListener, ScreenUiEngineAttachedListener {
   private CameraScreen$PersistedCameraData _cameraData;
   private Field _banner;
   private ViewfinderField _viewfinder;
   private MediaField _mediaField;
   private MediaPlayer _pmePlayer;
   private MediaManager _manager;
   private ModelInteractorImpl _model;
   private TextNode _picsLeft;
   private TextNode _zoom;
   private boolean _zoomMode;
   private int _flashMode = -1;
   private int _nightMode = -1;
   private int _zoomCounter;
   private int _zoomCounterTimeStamp;
   private Verb _optionsVerb;
   private CameraOptions _options = CameraOptions.getOptions();
   private String[] _zoomStrings;
   private boolean _zoomInOn = true;
   private boolean _zoomOutOn = true;
   private Runnable _saveLater;
   private CameraPreviewScreen _preview;
   private String _path;
   private ContextObject _context;
   private Bitmap _thumbnailBitmap;
   private byte[] _jpegData;
   private Graphics _graphics;
   private int _viewfinderMode;
   private int timeIndex = 0;
   private long[] timeStamps = new long[6];
   private CalendarExtensions _calendar;
   private SimpleDateFormat _dateFormat;
   private String _modelName;
   private static final int PICTURE_COUNTER_MAX = 99999;
   private static final int PICTURE_COUNTER_STRING_LENGTH = Integer.toString(99999).length();
   private static final long CAMERA_DATA_KEY = 1361391055686298208L;
   private static final long FILE_HEADROOM = 256000L;
   private static final int PICTURES_LEFT_THRESHOLD = 200;
   private static final int PICTURES_LEFT_STRING_LENGTH = 3;
   private static final String PICTURES_LEFT_THRESHOLD_STRING = "200+";
   private static final String FILENAME_PREFIX = "IMG";
   private static final String FILENAME_SUFFIX = ".jpg";
   private static final String[] FLASH_EVENT = new String[]{"flashOff", "flashOn", "flashAuto"};
   private static final String[] NIGHTMODE_EVENT = new String[]{"nightmodeOff", "nightmodeOn"};
   private static final String PICSLEFT = "picsleft";
   private static final String TAKEPICTURE = "takepicture";
   private static final String ZOOM = "zoom";
   private static final int DEFAULT_MI_PRIORITY = Integer.MAX_VALUE;
   private static final long EVENTLOG_GUID = -2562843282228934904L;
   private static final int ZOOM_COUNTER_THRESHOLD = 2;
   private static CameraScreen _instance;
   private static final int TOGGLE_VIEWFINDER_MODE = -1;
   private static final int PERCENTAGE_SCALING_FACTOR = 1000;
   private static final int AVG_FILTER_COEFFICIENT = 4;
   private static final String TIMESTAMP_FORMAT = "yyyy:MM:dd HH:mm:ss";
   private static final int BACKLIGHT_TIMEOUT = 30;

   public final void suspendViewfinder(boolean state) {
      if (state) {
         this._viewfinder.vfStop(true);
      } else {
         if (this.isUiEngineAttached() && !this.isObscured()) {
            this._viewfinder.vfStart(0);
            this.setNightMode(0);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void setSize(int[] size, int quality) {
      boolean var6 = false /* VF: Semaphore variable */;

      label17:
      try {
         var6 = true;
         Camera.setPictureResolution(size[0], size[1], quality);
         var6 = false;
      } finally {
         if (var6) {
            Application.getApplication().invokeLater(new CameraScreen$3(this));
            this._options.setImageSizeIndex(0);
            int[] picSize = this._options.getImageSize();
            Camera.setPictureResolution(picSize[0], picSize[1], 0);
            break label17;
         }
      }

      this._viewfinder.vfRestart();
   }

   final void setContext(ContextObject parameter) {
      if (parameter != null) {
         this._context = ContextObject.clone(parameter);
         this._path = (String)ContextObject.get(this._context, 2765042845091913199L);
         Object screen = ContextObject.get(this._context, -1477447097671931650L);
         if (screen instanceof Screen) {
            ((Screen)screen).addScreenUiEngineAttachedListener(this);
         }

         this.updatePicturesLeftCounter();
      }
   }

   @Override
   public final void onScreenUiEngineAttached(Screen screen, boolean attached) {
      if (!attached) {
         Application app = this.getApplication();
         if (app != null) {
            app.invokeLater(new CameraScreen$2(this));
         }
      }
   }

   @Override
   public final void setNightMode(int mode) {
      if (this._nightMode != mode) {
         this._nightMode = mode;
         this._model.trigger(107, this._model.getHandle(NIGHTMODE_EVENT[mode]), null);
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
            this._viewfinder.setWhiteBalance(this._options.getWhiteBalance());
            return;
         case 4:
         case 5:
            this.setSize(this._options.getImageSize(), this._options.getImageQuality());
            this.updatePicturesLeftCounter();
            return;
         case 6:
         case 7:
            this.updatePicturesLeftCounter();
            return;
         case 8:
            this.setViewfinderMode(this._options.getViewfinderMode());
            return;
         case 9:
            this.setColourEffect(this._options.getColourEffect());
         case 0:
         case 2:
      }
   }

   @Override
   public final void mediaEvent(Object sender, int event, int eventParam, Object data) {
      if (event == 105) {
         String id = NodeImpl.getId(eventParam, this._model);
         if ("takepicture".equals(id)) {
            this.takePicture();
         }
      }
   }

   @Override
   public final boolean keyControl(char c, int status, int time) {
      if (super.keyControl(c, status, time)) {
         return true;
      }

      int offset = 0;
      switch (c) {
         case '\u0080':
            if (CameraOptions.isViewfinderModeValid(1)) {
               this._options.setViewfinderMode(-1);
               return true;
            }

            return false;
         case '\u0081':
         case '\u0084':
            if (this._zoomMode) {
               offset = 1;
            }
            break;
         case '\u0082':
         case '\u0083':
            if (this._zoomMode) {
               offset = -1;
            }
            break;
         case '\u0096':
            offset = 2;
            break;
         case '\u0097':
            offset = -2;
            break;
         default:
            return false;
      }

      this.zoom(offset, time);
      return true;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 19) {
         this.takePicture();
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   public final Menu getMenu(int instance) {
      ContextObject menuContext = new ContextObject();
      menuContext.put(244, new Integer(244387));
      SystemEnabledMenu menu = new SystemEnabledMenu(menuContext, null);
      Menu.setTargetScreen(this);
      menu.setInstance(instance);
      this.makeMenuWithContext(menu, instance);
      return menu;
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      if (this._optionsVerb == null) {
         this._optionsVerb = new CameraOptionsVerb();
      }

      Verb browseVerb = ExplorerServices.getBrowseVerb(this.getPath(false), 128, null);
      menu.add(new VerbMenuItem(CameraMain._rb.getString(24), 591106, 0, browseVerb, null));
      menu.add(new VerbMenuItem(this._optionsVerb, Integer.MAX_VALUE));
      if (CameraOptions.isViewfinderModeValid(1)) {
         menu.add(new VerbMenuItem(new CameraScreen$FullscreenVerb(this), Integer.MAX_VALUE));
      }

      if (this._context == null) {
         Verb[] verbs = VerbRepository.getVerbRepository(50498946589467127L).getVerbs(-7287235942111338224L);
         if (verbs != null) {
            for (int i = 0; i < verbs.length; i++) {
               CameraScreen$InvokeAndExitWrapperVerb verb = new CameraScreen$InvokeAndExitWrapperVerb(this, verbs[i]);
               menu.add(new VerbMenuItem(null, verb.getOrdering(), Integer.MAX_VALUE, verb, null));
            }
         }
      }

      super.makeMenu(menu, instance);
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return this.takePicture();
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      if (this._zoomMode) {
         this.zoom(dy != 0 ? -dy : dx, time);
      }

      return this._zoomMode;
   }

   @Override
   protected final void onVisibilityChange(boolean attached) {
      if (attached) {
         this._viewfinder.vfStart(0);
         this.setNightMode(0);
         Backlight.setTimeout(30);

         label22:
         try {
            this._pmePlayer.start();
         } finally {
            break label22;
         }
      } else {
         this._viewfinder.vfStop(true);
         this._pmePlayer.stop();
      }

      super.onVisibilityChange(attached);
   }

   @Override
   protected final void onExposed() {
      this._viewfinder.vfResume();
      Backlight.setTimeout(30);
      super.onExposed();
      this.updatePicturesLeftCounter();
   }

   @Override
   protected final void onObscured() {
      this._viewfinder.vfPause();
      Backlight.setTimeout(UiSettings.getBacklightTimeout());
      super.onObscured();
   }

   private final String getPictureFilename(int pictureCounter) {
      return "IMG" + NumberUtilities.toString(pictureCounter, 10, PICTURE_COUNTER_STRING_LENGTH) + ".jpg";
   }

   private final String getPath(boolean displayMessage) {
      String path = this._path == null ? this._options.getDestinationFolder() : this._path;
      if (RootRegister.getInstance().isMassStorageActive() && path.startsWith("/SDCard/", 0)) {
         path = CameraOptions.getDefaultPath(0);
         if (displayMessage) {
            Application.getApplication().invokeLater(new CameraScreen$1(this));
         }
      }

      return path;
   }

   @Override
   public final void close() {
      PersistentObject.commit(this._cameraData);
      super.close();
   }

   @Override
   public final int adjustVolume(int volumeLevelChange) {
      return -1;
   }

   private final boolean takePicture() {
      if (!this._viewfinder.isStarted()) {
         return false;
      }

      this.resetTimestamp();
      boolean log = this._options.isDevOptionEnabled(4);
      if (this._flashMode != 0 && this.checkBatteryTooLow()) {
         this.setFlashMode(0);
      }

      if (this._viewfinder.vfTakePicture()) {
         this._jpegData = this.fetchJpegData();
      } else {
         this._jpegData = null;
      }

      this._calendar.setTimeLong(System.currentTimeMillis());
      if (log) {
         System.out.println("Take picture " + this.getTimestamp());
      }

      Application.getApplication().invokeLater(this._saveLater);
      if (!ContextObject.getFlag(this._context, 39)) {
         label59:
         try {
            if (this._jpegData == null) {
               this._preview.updatePreview();
            } else {
               this._preview.updatePreview((JPEGEncodedImage)EncodedImage.createEncodedImage(this._jpegData, 0, this._jpegData.length));
            }

            this._preview.displayMessage(CameraMain._rb.getString(30));
            if (log) {
               System.out.println("get preview " + this.getTimestamp());
            }

            UiApplication.getUiApplication().pushScreen(this._preview);
         } finally {
            break label59;
         }
      }

      this.setNightMode(0);
      return true;
   }

   private final void setColourEffect(int effect) {
      this._viewfinder.setColourEffect(effect);
      this._viewfinder.vfRestart();
   }

   private final void setFlashMode(int flashMode) {
      if (flashMode < 0) {
         String[] names = CameraMain._rb.getStringArray(20);
         flashMode = (this._flashMode + 1) % names.length;
      }

      if (flashMode != 0 && this.checkBatteryTooLow()) {
         flashMode = 0;
      }

      if (this._flashMode != flashMode) {
         this._flashMode = flashMode;
         this._model.trigger(107, this._model.getHandle(FLASH_EVENT[flashMode]), null);
         this._viewfinder.setFlashMode(flashMode);
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         return this.takePicture();
      } else if (key == ' ') {
         this.setFlashMode(-1);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   public static final CameraScreen getInstance() {
      if (_instance == null) {
         _instance = new CameraScreen(0);
      }

      return _instance;
   }

   private final void setViewfinderMode(int mode) {
      this._viewfinderMode = mode;
      if (mode == 0) {
         this.setBanner(this._banner);
         this.setStatus(this._mediaField);
      } else {
         this.setBanner(null);
         this.setStatus(null);
      }

      this._viewfinder.vfRestart();
   }

   private final boolean checkBatteryTooLow() {
      if ((DeviceInfo.getBatteryStatus() & 8192) != 0) {
         Application.getApplication().invokeLater(new CameraScreen$4(this));
         return true;
      } else {
         return false;
      }
   }

   private final void setZoomMode(boolean zoomMode) {
      this._zoomMode = zoomMode;
      this.updateZoomIndicator();
   }

   private final void updatePicturesLeftCounter() {
      long avail = 0;
      long numberPics = 0;

      label37:
      try {
         FileConnection file = (FileConnection)Connector.open(FileUtilities.makeFileURL(this.getPath(false)));
         avail = file.availableSize();
         file.close();
      } finally {
         break label37;
      }

      String counterString;
      if (avail >= 0) {
         avail -= 256000;
         if (avail < 0) {
            avail = 0;
         }

         int[] size = this._options.getImageSize();
         int quality = this._options.getImageQualityIndex();
         int estimatedFileSize = size[0] * size[1] * this._cameraData._avgImageSizeFactor[quality] / 1000;
         numberPics = avail / estimatedFileSize;
         if (numberPics > 200) {
            counterString = "200+";
         } else {
            counterString = NumberUtilities.toString(numberPics, 10, 3);
         }
      } else {
         counterString = "---";
      }

      this._picsLeft.setString(counterString.toCharArray());
   }

   private final void zoom(int offset, int time) {
      long dTime = time - this._zoomCounterTimeStamp;
      if (dTime >= 250 || this._zoomCounter < 2 && this._zoomCounter < -2) {
         if (dTime > 100) {
            this._zoomCounter = 0;
         }

         this._zoomCounterTimeStamp = time;
         if (offset * this._zoomCounter > 0) {
            this._zoomCounter += offset;
         } else {
            this._zoomCounter = offset;
         }

         if (offset != 0 && (this._zoomCounter >= 2 || this._zoomCounter >= -2) && this._viewfinder.adjustZoomLevel(offset < 0 ? -1 : 1)) {
            this.updateZoomIndicator();
         }
      }
   }

   private final void updateZoomIndicator() {
      int i = this._viewfinder.getZoomIndex();
      if (this._zoom != null) {
         this._zoom.setString(this._zoomStrings[i].toCharArray());
      }

      boolean canZoomIn = this._zoomMode && i != this._zoomStrings.length - 1;
      boolean canZoomOut = this._zoomMode && i != 0;
      if (canZoomIn != this._zoomInOn) {
         this._model.trigger(107, this._model.getHandle(canZoomIn ? "zoomInOn" : "zoomInOff"), null);
         this._zoomInOn = canZoomIn;
      }

      if (canZoomOut != this._zoomOutOn) {
         this._model.trigger(107, this._model.getHandle(canZoomOut ? "zoomOutOn" : "zoomOutOff"), null);
         this._zoomOutOn = canZoomOut;
      }
   }

   private final void createZoomStrings() {
      int[] zoomLevels = this._viewfinder.getZoomLevels();
      this._zoomStrings = new String[zoomLevels.length];
      int one2one = zoomLevels[0];

      for (int i = 0; i < zoomLevels.length; i++) {
         this._zoomStrings[i] = (zoomLevels[i] + one2one / 2) / one2one + "x";
      }
   }

   private CameraScreen(long style) {
      super(2814749767303168L);
      this.setTag(ThemeUtilities.TAG_CAMERA_CAPTURE);
      this.getMainManager().setTag(ThemeUtilities.TAG_CAMERA_CAPTURE);
      this._banner = RibbonBanner.getInstance().getStatusBanner(null, 3);
      this._banner.setTag(ThemeUtilities.TAG_CAMERA_BANNER);
      this._viewfinder = ViewfinderField.getInstance();
      this.add(this._viewfinder);
      String mediaName = null;
      Theme theme = ThemeManager.getActiveTheme();
      if (theme != null) {
         ThemeAttributeSet tas = theme.getAttributeSet(ThemeUtilities.TAG_CAMERA_CAPTURE);
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
            mediaName = "cod://net_rim_bb_camera/capturebar.pme";
         }
      }

      this._mediaField = new MediaField(18014398512627712L);
      this._manager = new MediaManager();
      this._pmePlayer = new MediaPlayer();
      this._pmePlayer.setUI(this._mediaField);

      label59:
      try {
         this._model = (ModelInteractorImpl)this._manager.createMedia(mediaName);
         this._pmePlayer.setMedia(this._model);
         this._pmePlayer.setInternalMediaListener(this);
      } finally {
         break label59;
      }

      this.setViewfinderMode(this._options.getViewfinderMode());
      this._zoom = (TextNode)this._model.getNode("zoom");
      this._picsLeft = (TextNode)this._model.getNode("picsleft");
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(1361391055686298208L);
      this._cameraData = (CameraScreen$PersistedCameraData)persistentObject.getContents();
      if (this._cameraData == null) {
         this._cameraData = new CameraScreen$PersistedCameraData();
         persistentObject.setContents(this._cameraData, 51, false);
         persistentObject.commit();
      }

      this.updatePicturesLeftCounter();
      this.createZoomStrings();
      this._viewfinder.setWhiteBalance(this._options.getWhiteBalance());
      this.setFlashMode(this._options.getFlashModeIndex());
      this.setNightMode(0);
      this._options.addOptionsChangeListener(this);
      EventLogger.register(-2562843282228934904L, "Camera", 2);
      this._saveLater = new CameraScreen$SavePicture(this);
      this._preview = new CameraPreviewScreen();
      this.setZoomMode(true);
      this._calendar = (CalendarExtensions)Calendar.getInstance();
      this._dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
      this._modelName = CommonResources.getString(9101) + " " + DeviceInfo.getDeviceName();
      this.setSize(this._options.getImageSize(), this._options.getImageQuality());
      this.setColourEffect(this._options.getColourEffect());
      int desiredThumbnailWidth = MetaDataFile.getDesiredThumbnailWidth();
      int desiredThumbnailHeight = MetaDataFile.getDesiredThumbnailHeight();
      int resizedThumbnailWidth = desiredThumbnailHeight * 4 / 3;
      int resizedThumbnailHeight = desiredThumbnailHeight;
      if (resizedThumbnailWidth > desiredThumbnailWidth) {
         resizedThumbnailWidth = desiredThumbnailWidth;
         resizedThumbnailHeight = desiredThumbnailWidth * 3 / 4;
      }

      this._thumbnailBitmap = new Bitmap(197, resizedThumbnailWidth, resizedThumbnailHeight);
      this._graphics = new Graphics(this._thumbnailBitmap);
      if ((Camera.getFeatures(0) & 256) != 0) {
         this.setManualFrequencyBanding();
      }
   }

   private final byte[] fetchJpegData() {
      String dateString = this._dateFormat.format((Calendar)this._calendar, new StringBuffer(19), null).toString();
      return Camera.getPicture(dateString, this._modelName);
   }

   private final void resetTimestamp() {
      this.timeIndex = 0;
      this.timeStamps[0] = System.currentTimeMillis();
   }

   private final long getTimestamp() {
      this.timeStamps[++this.timeIndex] = System.currentTimeMillis();
      return this.timeStamps[this.timeIndex] - this.timeStamps[this.timeIndex - 1];
   }

   private final void createEventLog(int imageSize) {
      if (this._options.isDevOptionEnabled(4)) {
         String logEntry = "";
         long totalTime = this.timeStamps[this.timeIndex] - this.timeStamps[0];

         for (int i = this.timeIndex; i > 0; i--) {
            this.timeStamps[i] = this.timeStamps[i] - this.timeStamps[i - 1];
            logEntry = " " + this.timeStamps[i] + logEntry;
         }

         logEntry = totalTime
            + " ="
            + logEntry
            + " S"
            + this._options.getImageSizeIndex()
            + " Q"
            + this._options.getImageQualityIndex()
            + " F"
            + this._flashMode
            + " M"
            + this._options.getMemoryType()
            + " Z"
            + this._viewfinder.getZoomIndex()
            + " V"
            + this._viewfinderMode
            + " C"
            + this._options.getColourEffect()
            + " N"
            + this._nightMode
            + " "
            + imageSize / 1000
            + "k #"
            + this._cameraData._pictureCounter
            + " "
            + this._cameraData._avgImageSizeFactor[this._options.getImageQualityIndex()];
         EventLogger.logEvent(-2562843282228934904L, logEntry.getBytes());
         System.out.println(logEntry);
      }
   }

   private final void setManualFrequencyBanding() {
      TimeService ts = TimeService.getTimeService();
      int tzid = ts.getTimeZoneID(ts.getDefaultTimeZoneID());
      int freqSetting;
      switch (tzid) {
         case 70:
         case 73:
         case 75:
         case 80:
         case 83:
         case 85:
         case 90:
         case 95:
         case 100:
         case 105:
         case 110:
         case 113:
         case 115:
         case 120:
         case 125:
         case 130:
         case 135:
         case 140:
         case 145:
         case 150:
         case 155:
         case 158:
         case 160:
         case 165:
         case 170:
         case 175:
         case 180:
         case 185:
         case 190:
         case 193:
         case 195:
         case 200:
         case 201:
         case 203:
         case 205:
         case 207:
         case 210:
         case 215:
         case 225:
         case 227:
         case 235:
         case 240:
         case 245:
         case 250:
         case 255:
         case 260:
         case 265:
         case 270:
         case 280:
         case 285:
         case 290:
         case 300:
         case 16448:
         case 16449:
         case 16450:
         case 16454:
         case 16455:
            freqSetting = 0;
            break;
         default:
            freqSetting = 1;
      }

      Camera.setOption(32, freqSetting);
      this._viewfinder.vfRestart();
   }
}
