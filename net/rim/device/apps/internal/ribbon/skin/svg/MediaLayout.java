package net.rim.device.apps.internal.ribbon.skin.svg;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MediaField;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RunnableResolver;
import net.rim.device.apps.api.ribbon.indicators.IndicatorManager;
import net.rim.device.apps.api.utility.props.PropsChangeEventSubscription;
import net.rim.device.apps.api.utility.props.PropsChangeListener;
import net.rim.device.apps.api.utility.props.StringProps;
import net.rim.device.apps.internal.ribbon.components.PropsChangeListenerHelper;
import net.rim.device.apps.internal.ribbon.skin.svg.eventprovider.SkinEventProvider;
import net.rim.device.apps.internal.ribbon.skin.svg.eventprovider.SkinEventProviderFactory;
import net.rim.device.apps.internal.ribbon.skin.svg.manager.FiveSkinManager;
import net.rim.device.apps.internal.ribbon.skin.svg.manager.SkinManager;
import net.rim.device.apps.internal.ribbon.skin.svg.manager.TodaySkinManager;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.event.EventEngine;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.NodeImpl;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.internal.mediaengine.service.ModelInteractor;
import net.rim.plazmic.mediaengine.MediaManager;
import net.rim.plazmic.mediaengine.MediaPlayer;
import net.rim.vm.Array;
import net.rim.vm.MessageQueue;
import net.rim.vm.Process;

public class MediaLayout extends MediaField implements StringProps, PropsChangeEventSubscription, RibbonComponent$RibbonComponentChangeListener {
   private MediaLayout$ObjectReference _paintLock;
   PropsChangeListenerHelper _propsHelper = new PropsChangeListenerHelper();
   private MediaManager _mediaLoader;
   private MediaPlayer _player;
   ForeignObjectLoader _foLoader;
   private long _startDelay;
   private RunnableResolver _runnableResolver;
   private int _state;
   private int _focus = -1;
   private boolean _restart;
   private boolean _stopIfNotVisible;
   private boolean _onVisible;
   private WeakReference _applicationReference;
   private MediaLayout$ThreadSwitchingRunnable _invalidatingRunnable = new MediaLayout$ThreadSwitchingRunnable(this);
   public Object _media;
   private ModelInteractorImpl _modelInteractor;
   private MediaLayout$HistoryEntry[] _history;
   private String _title;
   private CustomFocusOrder _customFocusOrder;
   private SkinManager _skinManager;
   private SkinEventProvider _eventProvider;
   IntIntHashtable _keyMap = new IntIntHashtable();
   private static final long MEDIA_LAYOUT_LOCK_KEY = 8255869323709840319L;
   public static final long PRIVATE_FLAGS_KEY = 3089937493992571440L;
   public static final int NO_PAINTLOCK_FLAG = 1;
   public static final long DEFAULT_STYLE = 3476778963869630464L;
   public static final int TYPE_ZEN = 1;
   public static final int TYPE_TODAY = 2;
   public static final int TYPE_FIVE = 4;
   private static final int STOPPED = 0;
   private static final int STARTING = 1;
   private static final int STARTED = 2;
   private static final int NO_UPDATE_WATERMARK = 5;
   public static final int TITLE_ID = "title".hashCode();
   static final String PREFIX = "x-exec://";

   public void setStopIfNotVisible(boolean stopIfNotVisible) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public int getType() {
      int type = 0;
      if (this._skinManager instanceof TodaySkinManager) {
         return type | 2;
      }

      if (this._skinManager instanceof FiveSkinManager) {
         type |= 4;
      }

      return type;
   }

   FocusInteractor getMediaInteractor() {
      return super._focusInteractor;
   }

   public CustomFocusOrder getCustomFocusOrder() {
      return this._customFocusOrder;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void start() {
      this.getApplication();
      if (this._paintLock == null || this._paintLock.getLastSetValue() != this) {
         IndicatorManager iManager = IndicatorManager.getInstance();
         if (iManager != null) {
            iManager.resetIndicatorAreas();
         }
      }

      if (this._state != 0) {
         this.invalidate();
      } else {
         this._state = 1;
         Application application = Application.getApplication();
         synchronized (this._invalidatingRunnable) {
            this._applicationReference = new WeakReference(application);
            this._invalidatingRunnable._invalidatePending = false;
         }

         label64:
         try {
            if (this._restart) {
               this._player.setMediaTime(0);
            }

            if (this._startDelay == 0) {
               this._state = 2;
               this._player.start();
            } else {
               application.invokeLater(new MediaLayout$1(this), this._startDelay, false);
            }
         } catch (Throwable var7) {
            System.err.println(e);
            e.printStackTrace();
            EventLogger.logEvent(-7509200465648525729L, ("Exception starting MediaLayout: " + e.toString()).getBytes(), 2);
            break label64;
         }

         this.invalidate();
         if (this._focus == -1) {
            FocusInteractor mediaInteractor = this.getMediaInteractor();
            if (mediaInteractor != null) {
               this._focus = mediaInteractor.getItemInFocus();
            }
         }

         this.focusHotspot(this._focus);
      }
   }

   protected void stop() {
      if (this._state != 0) {
         if (this._state == 1) {
            this._state = 0;
         } else {
            this._state = 0;
            synchronized (this._invalidatingRunnable) {
               this._applicationReference = null;
               this._invalidatingRunnable._invalidatePending = false;
            }

            FocusInteractor mediaInteractor = this.getMediaInteractor();
            if (mediaInteractor != null) {
               this._focus = mediaInteractor.getItemInFocus();
            }

            this._player.stop();
         }
      }
   }

   void setSkinManager(SkinManager sm) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   protected void focusHotspot(int item) {
      FocusInteractor mediaInteractor = this.getMediaInteractor();
      if (mediaInteractor != null && item < mediaInteractor.getItemCount()) {
         int oldItem = mediaInteractor.getItemInFocus();
         if (item != oldItem) {
            mediaInteractor.setFocusToItem(item);
         }
      }
   }

   protected boolean activateItemInFocus() {
      FocusInteractor mediaInteractor = this.getMediaInteractor();
      return mediaInteractor != null ? mediaInteractor.activateItemInFocus() : false;
   }

   protected void activateHotspot(int item) {
      FocusInteractor mediaInteractor = this.getMediaInteractor();
      if (mediaInteractor != null && item < mediaInteractor.getItemCount()) {
         int oldItem = mediaInteractor.getItemInFocus();
         if (oldItem != item) {
            mediaInteractor.setFocusToItem(item);
         }

         mediaInteractor.activateItemInFocus();
         if (oldItem != item) {
            mediaInteractor.setFocusToItem(oldItem);
         }
      }
   }

   public void mapKey(char key, int focusId) {
      this._keyMap.put(key, focusId);
   }

   public void setStartDelay(long delay) {
      this._startDelay = delay;
   }

   public void setRestart(boolean restart) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public void set(long propID, String valueToSet) {
      if (propID == TITLE_ID && (valueToSet != null && !valueToSet.equals(this._title) || valueToSet == null && this._title != null)) {
         this._title = valueToSet;
         this._propsHelper.propChanged(TITLE_ID);
         this.invalidate();
      }
   }

   @Override
   public void addPropsChangeListener(PropsChangeListener listener) {
      this._propsHelper.addComponentForUpdate(listener);
   }

   @Override
   public void ribbonComponentChanged(RibbonComponent component) {
      this.invalidate();
   }

   @Override
   public String get(long propID, String defaultReturned) {
      return propID == TITLE_ID ? this._title : defaultReturned;
   }

   @Override
   protected void onObscured() {
      this._skinManager.onObscured();
      this._eventProvider.onObscured();
      super.onObscured();
   }

   @Override
   protected void onExposed() {
      this._skinManager.onExposed();
      this._eventProvider.onExposed();
      super.onExposed();
   }

   @Override
   protected void subpaint(Graphics graphics) {
      try {
         if (this._paintLock == null) {
            super.subpaint(graphics);
            return;
         }

         synchronized (this._paintLock) {
            if (this._onVisible) {
               this._paintLock.setValue(this);
               this._onVisible = false;
            } else if (this._paintLock.getValue() == null) {
               if (this._paintLock.getLastSetValue() != this) {
                  IndicatorManager iManager = IndicatorManager.getInstance();
                  if (iManager != null) {
                     iManager.resetIndicatorAreas();
                  }
               }

               this._paintLock.setValue(this);
            }

            if (this._paintLock.getValue() == this) {
               super.subpaint(graphics);
            }
         }
      } finally {
         return;
      }
   }

   @Override
   public void invalidate() {
      if (super._services != null) {
         EventEngine engine = super._services.getEngine();
         MessageQueue messageQueue = Process.currentProcess().getMessageQueue();
         if (this._modelInteractor == null || !engine.willProcessASAP() && messageQueue.getSize() < 5) {
            synchronized (this._invalidatingRunnable) {
               Application application = this.getApplication();
               if (application != null && !this._invalidatingRunnable._invalidatePending) {
                  this._invalidatingRunnable._invalidatePending = true;
                  if (this.isCorrectAppThread()) {
                     this._invalidatingRunnable.run();
                  } else {
                     application.invokeLater(this._invalidatingRunnable);
                  }
               }
            }
         } else {
            this._modelInteractor.triggerUpdate();
         }
      }
   }

   private void initialize(Object context) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      if (!ContextObject.castOrCreate(context).getPrivateFlag(3089937493992571440L, 1)) {
         this._paintLock = (MediaLayout$ObjectReference)ar.getOrWaitFor(8255869323709840319L);
         if (this._paintLock == null) {
            this._paintLock = new MediaLayout$ObjectReference(null);
            ar.put(8255869323709840319L, this._paintLock);
         }
      }

      this._runnableResolver = (RunnableResolver)ContextObject.get(context, -3669022226332202623L);
      this._mediaLoader = new MediaManager();
      this._foLoader = new ForeignObjectLoader();
      this._mediaLoader.setCustomResourceProvider(this._foLoader);
      this._foLoader.setChangeListener(this);
      this._foLoader._context = this;
      this._player = new MediaPlayer();
      this._player.setUI(this);
      this._player.addMediaListener(new MediaLayout$MyListener(this));
   }

   private MediaLayout(Object context, InputStream inputStream, String contentType, long style, long contentStyle) {
      super(style, contentStyle);
      this.initialize(context);
      Object skin = this._mediaLoader.createResource(contentType, inputStream, null, null);
      this.setMedia(skin);
   }

   private Application getApplication() {
      Application application = null;
      WeakReference applicationReference = this._applicationReference;
      if (applicationReference != null) {
         application = (Application)applicationReference.get();
         if (application == null || !application.isAlive()) {
            this._applicationReference = null;
            this.stop();
         }
      }

      return application;
   }

   @Override
   protected void onVisibilityChange(boolean visible) {
      this._skinManager.onVisibilityChange(visible);
      this._eventProvider.onVisibilityChange(visible);
      this._onVisible = visible;
      if (visible) {
         this.start();
         super.onVisibilityChange(visible);
      } else {
         if (this._paintLock != null && this._paintLock.getValue() == this) {
            this._paintLock.setValue(null);
         }

         if (this._stopIfNotVisible) {
            this.stop();
         }

         super.onVisibilityChange(visible);
      }
   }

   @Override
   public void setServices(MediaServices services) {
      if (this._skinManager != null) {
         this._skinManager.setServices(services);
      }

      super.setServices(services);
   }

   private void setMedia(Object media) {
      this._media = media;
      if (media instanceof ModelInteractor) {
         this._modelInteractor = (ModelInteractorImpl)media;
      }

      if (this._skinManager == null) {
         if (this._modelInteractor != null && this._modelInteractor.getHandle("email_app") != -1) {
            String name = ThemeManager.getActiveName();
            if (name != null
               && (
                  name.indexOf("phantom_240x260_b") != -1
                     || name.indexOf("bbdimension_zenplus_240x260_b") != -1
                     || name.indexOf("bbdimension_todayplus_240x260_b") != -1
               )) {
               FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(3856995967469138522L);
               Factory factory = repos.getFactory("Today");
               if (factory != null) {
                  SkinManager skinManager = (SkinManager)factory.createInstance(null);
                  this.setSkinManager(skinManager);
               }
            }
         }

         if (this._skinManager == null) {
            this._skinManager = new SkinManager();
         }
      }

      label62:
      try {
         this._player.setMedia(media);
         if (super._services != null) {
            this._eventProvider = SkinEventProviderFactory.getFactory().createInstance();
            super._services.registerService("SkinEventProvider", this._eventProvider);
            EventEngine engine = super._services.getEngine();
            engine.setFreezeTimeWhenStopped(false);
         }
      } finally {
         break label62;
      }

      if (this._foLoader._customFocusOrder != null) {
         this._customFocusOrder = this._foLoader._customFocusOrder;
         this._customFocusOrder.setFocusInteractor(super._focusInteractor);
      }

      this._foLoader.resolveIds();
      this._skinManager.setMedia(media);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static MediaLayout create(Object context, String url, long style) {
      try {
         return new MediaLayout(context, url, style);
      } catch (Throwable var6) {
         System.err.println(e);
         e.printStackTrace();
         return null;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static MediaLayout create(Object context, String url, long style, long contentStyle) {
      try {
         return new MediaLayout(context, url, style, contentStyle);
      } catch (Throwable var8) {
         System.err.println(e);
         e.printStackTrace();
         return null;
      }
   }

   public static MediaLayout create(Object context, String url) {
      return create(context, url, getCreationStyle(context));
   }

   private void pushHistory(String url) {
      int i = this._history.length;
      Array.resize(this._history, i + 1);
      this._history[i] = new MediaLayout$HistoryEntry(url);
   }

   private String popHistory() {
      int i = this._history.length - 1;
      String ret = this._history[i - 1]._url;
      Array.resize(this._history, i);
      return ret;
   }

   private void loadLater(String url) {
      this.loadLater(url, -1, true);
   }

   private void loadLater(String url, int focusId) {
      this.loadLater(url, focusId, false);
   }

   private void loadLater(String url, int focusId, boolean pushHistory) {
      String localUrl = url;
      int localFocusId = focusId;
      boolean localPushHistory = pushHistory;
      synchronized (this._invalidatingRunnable) {
         Application application = this.getApplication();
         if (application != null) {
            application.invokeLater(new MediaLayout$2(this, localUrl, localFocusId, localPushHistory));
         }
      }
   }

   private void load(String url) {
      if (url.startsWith("x-exec://")) {
         Application app = this.getApplication();
         if (app != null) {
            app.invokeLater(new MediaLayout$3(this, url));
         }
      } else {
         this._mediaLoader.resolveURL(url);
         FocusInteractor i = super._focusInteractor;
         if (i != null) {
            this._history[this._history.length - 1]._focus = i.getItemInFocus();
         }

         this.loadLater(url);
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      return this.activateItemInFocus();
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      return this.activateItemInFocus();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static MediaLayout create(Object context, InputStream inputStream, String contentType) {
      try {
         return new MediaLayout(context, inputStream, contentType, getCreationStyle(context));
      } catch (Throwable var5) {
         System.err.println(e);
         e.printStackTrace();
         return null;
      }
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      if (this._skinManager.keyDown(keycode, time)) {
         return true;
      }

      if (Keypad.key(keycode) == 4098 && this._modelInteractor != null) {
         int focus = super._focusInteractor.getItemInFocus();
         String id = NodeImpl.getId(focus, this._modelInteractor);
         if ("hs_profile".equals(id)) {
            this.navigationMovement(0, 1, -1, -1);
         }
      }

      return super.keyDown(keycode, time);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         return status != 257 ? this.activateItemInFocus() : false;
      }

      int focusId = this._keyMap.get(key);
      if (focusId != -1) {
         this.activateHotspot(focusId);
      }

      return super.keyChar(key, status, time);
   }

   private MediaLayout(Object context, InputStream inputStream, String contentType, long style) {
      super(style);
      this.initialize(context);
      Object skin = this._mediaLoader.createResource(contentType, inputStream, null, null);
      this.setMedia(skin);
   }

   private MediaLayout(Object context, InputStream inputStream, String contentType) {
      this(context, inputStream, contentType, getCreationStyle(context));
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean isCorrectAppThread() {
      try {
         Application application = this.getApplication();
         if (application != null && application == Application.getApplication() && application.isEventThread()) {
            return true;
         }
      } catch (Throwable var3) {
         System.err.println(e);
         e.printStackTrace();
         return false;
      }

      return false;
   }

   private MediaLayout(Object context, String url, long style, long contentStyle) {
      super(style, contentStyle);
      this.initialize(context);
      this._history = new MediaLayout$HistoryEntry[1];
      this._history[0] = new MediaLayout$HistoryEntry(url);
      Object skin = this._mediaLoader.createMedia(url);
      this.setMedia(skin);
   }

   private static long getCreationStyle(Object context) {
      Long value = (Long)ContextObject.get(context, 265370977573465368L);
      return value == null ? 3476778963869630464L : value;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static MediaLayout create(Object context, InputStream inputStream, String contentType, long contentStyle) {
      try {
         return new MediaLayout(context, inputStream, contentType, getCreationStyle(context), contentStyle);
      } catch (Throwable var7) {
         System.err.println(e);
         e.printStackTrace();
         return null;
      }
   }

   private MediaLayout(Object context, String url, long style) {
      super(style);
      this.initialize(context);
      this._history = new MediaLayout$HistoryEntry[1];
      this._history[0] = new MediaLayout$HistoryEntry(url);
      Object skin = this._mediaLoader.createMedia(url);
      this.setMedia(skin);
   }

   @Override
   protected boolean trackwheelRoll(int amount, int status, int time) {
      if (this._customFocusOrder != null) {
         this._customFocusOrder.navigationMovement(0, amount);
         return true;
      } else {
         return super.trackwheelRoll(amount, status, time);
      }
   }

   @Override
   public boolean navigationMovement(int dx, int dy, int status, int time) {
      if (this._skinManager.navigationMovement(dx, dy, status, time)) {
         return true;
      }

      if (this._customFocusOrder != null) {
         int focus = super._focusInteractor.getItemInFocus();
         this._customFocusOrder.navigationMovement(dx, dy);
         int newFocus = super._focusInteractor.getItemInFocus();
         if (newFocus == focus && dx <= 0 && dy > 0) {
            String id = NodeImpl.getId(newFocus, this._modelInteractor);
            if (id != null && id.equals("hs_profile")) {
               this._customFocusOrder.navigationMovement(-1, 0);
            }
         }

         return true;
      } else {
         return super.navigationMovement(dx, dy, status, time);
      }
   }

   static void access$800(MediaLayout x0, int x1, int x2, int x3, int x4) {
      x0.invalidate(x1, x2, x3, x4);
   }
}
