package net.rim.device.internal.media;

import java.io.InputStream;
import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.TimeBase;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;
import net.rim.device.api.media.control.AudioPathControl;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.Phone;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.internal.system.ActiveMedia;
import net.rim.device.internal.system.ActiveMediaObservable;
import net.rim.device.internal.system.ActiveMediaObserver;
import net.rim.device.internal.system.AlertPlayer;
import net.rim.vm.Process;
import net.rim.vm.WeakReference;

class PlayerImpl implements Player, AlertPlayer, StreamDataControl, ActiveMediaObserver, AudioPathControl {
   protected Object[] _listeners;
   protected String _type;
   protected int _currentLoopIteration;
   protected int _loopCount;
   protected long _mediaTime;
   private TimeBase _timeBase;
   protected int _state = 100;
   protected Application _app;
   private PlayerImpl$MyPhoneListener _phoneListener;
   protected boolean _phoneAudioActive;
   private ApplicationProcess _process;
   private Runnable _cleanupRunnable;
   protected boolean _notUnloadable;
   protected int _audioSourceId;
   private boolean _audioSourceAdded;
   private AudioRouter _audioRouter;
   private boolean _forcedActive;
   private boolean _starting = false;
   private int _playRunnableID = -1;
   private WeakReference _activeMedia;
   private int _killRunnableId = -1;
   private static final String FLAG_NOT_UNLOADABLE = "flag_not_unloadable";
   private static final String ACTIVE_MEDIA_SOURCE = "active_media_source";
   static final String INTERRUPTABLE_KEY = "interrupt_on_user_input";
   private static final int PLAY_DELAY = 1000;

   protected void cleanUp() {
      if (this._audioSourceAdded) {
         this._audioRouter.removeSource(this._audioSourceId);
         this._audioSourceAdded = false;
      }

      if (this._phoneListener != null) {
         this._phoneListener.deregister();
         this._phoneListener = null;
      }

      ActiveMediaObservable.removeListener(this);
      if (this._cleanupRunnable != null && this._process != null) {
         this._process.removeCleanupRunnable(this._cleanupRunnable);
         this._cleanupRunnable = null;
      }
   }

   protected synchronized void cancelKillRunnable() {
      if (this._killRunnableId != -1 && this._app != null) {
         this._app.cancelInvokeLater(this._killRunnableId);
         this._killRunnableId = -1;
      }
   }

   protected Application getApplication() {
      return this._app;
   }

   protected synchronized void kill() {
      label17:
      try {
         this.close();
      } finally {
         break label17;
      }

      this._killRunnableId = -1;
   }

   protected synchronized void startKillRunnable() {
      if (this._killRunnableId == -1 && this._app != null) {
         this._killRunnableId = this._app.invokeLater(new PlayerImpl$2(this), 2500, false);
      }
   }

   protected final void makeMediaAvailable() {
      if (!this._notUnloadable && !Phone.getInstance().isActive()) {
         this.notifyListeners("deviceAvailable", "");
      }
   }

   protected void starting() {
      if (!this._audioSourceAdded) {
         this._audioRouter.addSource(this._audioSourceId);
         this._audioSourceAdded = true;
      }
   }

   protected void stopped() {
      if (!this._forcedActive && this._audioSourceAdded) {
         this._audioRouter.removeSource(this._audioSourceId);
         this._audioSourceAdded = false;
      }
   }

   protected String getFullyQualifiedControlType(String controlType) {
      if (controlType.indexOf(46) == -1) {
         controlType = ((StringBuffer)(new Object("javax.microedition.media.control."))).append(controlType).toString();
      }

      return controlType;
   }

   protected final synchronized void makeMediaUnavailable(ActiveMedia param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/internal/media/PlayerImpl._notUnloadable Z
      // 04: ifeq 0c
      // 07: aload 0
      // 08: invokevirtual net/rim/device/internal/media/PlayerImpl.startKillRunnable ()V
      // 0b: return
      // 0c: aload 0
      // 0d: invokevirtual net/rim/device/internal/media/PlayerImpl.waitForStarting ()V
      // 10: aload 0
      // 11: aload 1
      // 12: invokevirtual net/rim/device/internal/media/PlayerImpl.doMakeMediaUnavailable (Lnet/rim/device/internal/system/ActiveMedia;)V
      // 15: return
      // 16: astore 2
      // 17: return
      // 18: astore 2
      // 19: return
      // try (6 -> 11): 12 null
      // try (6 -> 11): 14 null
   }

   protected void doMakeMediaUnavailable(ActiveMedia media) {
      this.notifyListeners("deviceUnavailable", "");
      if (this._state == 400) {
         this.stop();
      }
   }

   protected void preStart() {
   }

   protected void doDeallocate() {
      throw null;
   }

   protected void notifyListeners(String event) {
      this.notifyListeners(event, null);
   }

   protected void notifyListeners(String event, Object eventData) {
      if (this._listeners != null && this._app != null) {
         for (int lv = this._listeners.length - 1; lv >= 0; lv--) {
            try {
               PlayerListener pl = (PlayerListener)this._listeners[lv];
               Player player = this;
               Runnable runnable = new PlayerImpl$1(this, pl, player, event, eventData);
               this._app.invokeLater(runnable);
            } finally {
               continue;
            }
         }
      }
   }

   protected void doStop() {
      throw null;
   }

   protected void read(InputStream _1) {
      throw null;
   }

   protected void doStart() {
      throw null;
   }

   protected void doRealize() {
   }

   protected int getStreamingChunkSize(int codec) {
      return 58000;
   }

   protected void setContentType(String type) {
      this._type = type;
   }

   void chkClosed(boolean unrealized) {
      if (this._state == 0 || unrealized && this._state == 100) {
         throw new Object(((StringBuffer)(new Object("The Player is "))).append(this._state == 0 ? "closed" : "unrealized").toString());
      }
   }

   protected void doDataSourceClose() {
      throw null;
   }

   protected synchronized void register() {
      this._process = (ApplicationProcess)Process.currentProcess();
      if (this._process != null) {
         this._cleanupRunnable = new PlayerImpl$CleanUpRunnable(this);
         this._process.addCleanupRunnable(this._cleanupRunnable);
      }

      this._app = Application.getApplication();
      this._phoneListener = new PlayerImpl$MyPhoneListener(this);
      ActiveMediaObservable.addListener(this);
   }

   protected synchronized void waitForStarting() {
      try {
         while (this._starting) {
            this.wait();
         }
      } finally {
         return;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized void start() {
      this.chkClosed(false);
      boolean var5 = false /* VF: Semaphore variable */;

      label97: {
         try {
            var5 = true;
            if (!this._notUnloadable && (this._phoneAudioActive || this._audioRouter.isSourceAdded(0))) {
               throw new Object("Media cannot start while phone is active.");
            }

            if (this._audioRouter.isSourceAdded(1)) {
               throw new Object("Media cannot start while voice dailing is active.");
            }

            ActiveMedia activeMedia = ActiveMediaObservable.getActiveMedia();
            ActiveMedia myActiveMedia = (ActiveMedia)(this._activeMedia == null ? null : this._activeMedia.get());
            if (activeMedia != null && !activeMedia.isAlert() && activeMedia != myActiveMedia) {
               MediaLogger.logActiveMediaPlayFailed(activeMedia);
               throw new Object("Media cannot start while another media is active.");
            }

            this.waitForStarting();
            if (this._state >= 400) {
               var5 = false;
               break label97;
            }

            this.preStart();
            this._starting = true;
            if (this._state < 200) {
               this.realize();
            }

            if (this._state < 300) {
               this.prefetch();
            }

            this.doStart();
            this._state = 400;
            var5 = false;
         } finally {
            if (var5) {
               this._starting = false;
               this.notifyAll();
            }
         }

         this._starting = false;
         this.notifyAll();
         return;
      }

      this._starting = false;
      this.notifyAll();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void startAlert(int volume, int interruptible) {
      try {
         this.start();
      } catch (Throwable var5) {
         throw new Object(e.toString());
      }
   }

   @Override
   public final synchronized void stop() {
      this.chkClosed(false);
      this.waitForStarting();
      if (this._state >= 400) {
         this.doStop();
         this._state = 300;
         this.notifyListeners("stopped", new Object(this.getMediaTime()));
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void stopAlert() {
      try {
         this.stop();
      } catch (Throwable var3) {
         throw new Object(e.toString());
      }
   }

   @Override
   public final synchronized void deallocate() {
      this.chkClosed(false);
      this.waitForStarting();
      if (this._state < 300) {
         this.doDataSourceClose();
      } else {
         if (this._state == 400) {
            label25:
            try {
               this.stop();
            } finally {
               break label25;
            }
         }

         this.doDeallocate();
         this.doDataSourceClose();
         this._state = 200;
      }
   }

   @Override
   public final void setTimeBase(TimeBase master) {
      this.waitForStarting();
      if (this._state != 0 && this._state != 100 && this._state != 400) {
         this._timeBase = master;
      } else {
         throw new Object(" This method cannot be called  if the Player is in the UNREALIZED or CLOSED state");
      }
   }

   @Override
   public final synchronized void close() {
      if (this._state != 0) {
         this.deallocate();
         this.cleanUp();
         this._state = 0;
         this.notifyListeners("closed");
      }
   }

   @Override
   public synchronized long setMediaTime(long now) {
      this.chkClosed(true);
      return this._mediaTime;
   }

   @Override
   public final long getMediaTime() {
      this.chkClosed(false);
      return this._mediaTime;
   }

   @Override
   public final int getState() {
      return this._state;
   }

   @Override
   public long getDuration() {
      this.chkClosed(false);
      return -1;
   }

   @Override
   public final void setLoopCount(int count) {
      this.chkClosed(false);
      this.waitForStarting();
      if (this._state == 400) {
         throw new Object("setLoopCount");
      }

      if (count != 0 && count >= -1) {
         this._loopCount = count;
      } else {
         throw new Object("setLoopCount");
      }
   }

   @Override
   public void setKeyValue(String key, Object value) {
      if ("mimetype".equals(key)) {
         this.setContentType((String)value);
      } else if (!"locator".equals(key)) {
         if ("sourcestreams".equals(key)) {
            this.read((InputStream)(new Object((SourceStream)((Object[])value)[0])));
         } else if ("datasource".equals(key)) {
            SourceStream[] sources = ((DataSource)value).getStreams();
            this.read((InputStream)(new Object(sources[0])));
         } else if (!"flag_not_unloadable".equals(key)) {
            if ("active_media_source".equals(key)) {
               this._activeMedia = (WeakReference)(new Object(value));
            }
         } else {
            this._notUnloadable = Boolean.TRUE.equals(value) && ControlledAccess.verifyRRISignatures(true);
         }
      }
   }

   @Override
   public final void removePlayerListener(PlayerListener playerListener) {
      this.chkClosed(false);
      if (playerListener != null) {
         this._listeners = ListenerUtilities.removeListener(this._listeners, playerListener);
      }
   }

   @Override
   public final synchronized void realize() {
      this.chkClosed(false);
      if (this._state < 200) {
         this.register();
         this.doRealize();
         this._state = 200;
      }
   }

   @Override
   public final synchronized void prefetch() {
      this.chkClosed(false);
      if (this._state < 300) {
         if (this._state < 200) {
            this.realize();
         }

         this._state = 300;
      }
   }

   @Override
   public final TimeBase getTimeBase() {
      if (this._state == 0 || this._state == 100) {
         throw new Object(" This method cannot be called  if the Player is in the UNREALIZED or CLOSED state");
      } else {
         return this._timeBase != null ? this._timeBase : Manager.getSystemTimeBase();
      }
   }

   @Override
   public Object getKeyValueObject(String key) {
      return this;
   }

   @Override
   public void onChanged(ActiveMedia from, ActiveMedia to) {
      if (to != null) {
         this.makeMediaUnavailable(to);
      } else {
         if (from != null && to == null) {
            this.makeMediaAvailable();
         }
      }
   }

   @Override
   public String getKeyValue(String key) {
      return "";
   }

   @Override
   public String[] getKeys() {
      return new String[]{"author", "copyright", "date", "title", "locator", "mimetype"};
   }

   @Override
   public Control[] getControls() {
      this.chkClosed(true);
      return null;
   }

   @Override
   public Control getControl(String controlType) {
      this.chkClosed(true);
      return "net.rim.device.internal.StreamDataControl".equals(controlType) ? this : null;
   }

   @Override
   public String getContentType() {
      this.chkClosed(true);
      return this._type;
   }

   @Override
   public final void addPlayerListener(PlayerListener playerListener) {
      this.chkClosed(false);
      if (playerListener != null) {
         this._listeners = ListenerUtilities.addListener(this._listeners, playerListener);
      }
   }

   @Override
   public boolean isPathExplicitlySet() {
      return this._audioRouter.getAudioPathControl(this._audioSourceId).isPathExplicitlySet();
   }

   @Override
   public void setAudioPath(int newPath) {
      this._audioRouter.getAudioPathControl(this._audioSourceId).setAudioPath(newPath);
   }

   @Override
   public int getAudioPath() {
      return this._audioRouter.getAudioPathControl(this._audioSourceId).getAudioPath();
   }

   @Override
   public boolean canSwitchToPath(int path) {
      return this._audioRouter.getAudioPathControl(this._audioSourceId).canSwitchToPath(path);
   }

   @Override
   public void toggleSpeakerphone() {
      this._audioRouter.getAudioPathControl(this._audioSourceId).toggleSpeakerphone();
   }

   @Override
   public void resetAudioPath() {
      this._audioRouter.getAudioPathControl(this._audioSourceId).resetAudioPath();
   }

   @Override
   public void forceActive(boolean active) {
      this._forcedActive = active;
      if (!this._forcedActive && this._audioSourceAdded) {
         this._audioRouter.removeSource(this._audioSourceId);
         this._audioSourceAdded = false;
      } else {
         if (this._forcedActive && !this._audioSourceAdded) {
            this._audioRouter.addSource(this._audioSourceId);
            this._audioSourceAdded = true;
         }
      }
   }

   protected PlayerImpl() {
      this._audioRouter = AudioRouter.getInstance();
   }

   private void onAudioSourceChanged() {
      if (this._audioRouter.getActiveSource() == 0) {
         if (!this._phoneAudioActive) {
            this.onCallStarting();
            this._phoneAudioActive = true;
            return;
         }
      } else if (this._phoneAudioActive) {
         this._phoneAudioActive = false;
         this.makeMediaAvailable();
      }
   }

   private void onCallStarting() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/internal/media/PlayerImpl._playRunnableID I
      // 04: bipush -1
      // 06: if_icmpeq 1a
      // 09: aload 0
      // 0a: getfield net/rim/device/internal/media/PlayerImpl._app Lnet/rim/device/api/system/Application;
      // 0d: aload 0
      // 0e: getfield net/rim/device/internal/media/PlayerImpl._playRunnableID I
      // 11: invokevirtual net/rim/device/api/system/Application.cancelInvokeLater (I)V
      // 14: aload 0
      // 15: bipush -1
      // 17: putfield net/rim/device/internal/media/PlayerImpl._playRunnableID I
      // 1a: invokestatic net/rim/device/api/system/Alert.isSingleSharedAudioChannel ()Z
      // 1d: ifeq 26
      // 20: aload 0
      // 21: aconst_null
      // 22: invokevirtual net/rim/device/internal/media/PlayerImpl.makeMediaUnavailable (Lnet/rim/device/internal/system/ActiveMedia;)V
      // 25: return
      // 26: aload 0
      // 27: getfield net/rim/device/internal/media/PlayerImpl._notUnloadable Z
      // 2a: ifeq 32
      // 2d: aload 0
      // 2e: invokevirtual net/rim/device/internal/media/PlayerImpl.startKillRunnable ()V
      // 31: return
      // 32: aload 0
      // 33: ldc_w "deviceUnavailable"
      // 36: ldc_w ""
      // 39: invokevirtual net/rim/device/internal/media/PlayerImpl.notifyListeners (Ljava/lang/String;Ljava/lang/Object;)V
      // 3c: aload 0
      // 3d: invokevirtual net/rim/device/internal/media/PlayerImpl.waitForStarting ()V
      // 40: aload 0
      // 41: invokevirtual net/rim/device/internal/media/PlayerImpl.getState ()I
      // 44: sipush 400
      // 47: if_icmpne 52
      // 4a: aload 0
      // 4b: invokevirtual net/rim/device/internal/media/PlayerImpl.stop ()V
      // 4e: return
      // 4f: astore 1
      // 50: return
      // 51: astore 1
      // 52: return
      // try (28 -> 36): 37 null
      // try (28 -> 36): 39 null
   }

   private void onCallEnding() {
      if (this._notUnloadable) {
         try {
            this.cancelKillRunnable();
            this.close();
         } finally {
            return;
         }
      } else {
         if (!this._phoneAudioActive && this._phoneListener != null) {
            if (this._playRunnableID != -1) {
               this._app.cancelInvokeLater(this._playRunnableID);
            }

            this._playRunnableID = this._app.invokeLater(this._phoneListener, 1000, false);
         }
      }
   }
}
