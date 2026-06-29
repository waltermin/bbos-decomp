package net.rim.plazmic.internal.contentpreview.device.apps;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.apps.internal.browser.pme.PMEBrowserField;
import net.rim.plazmic.internal.contentpreview.MishandleException;
import net.rim.plazmic.internal.contentpreview.device.dispatcherclient.DispatcherClient;
import net.rim.plazmic.internal.contentpreview.dispatcher.Dispatcher;
import net.rim.plazmic.internal.contentpreview.playback.PlaybackCommandHandler;
import net.rim.plazmic.internal.contentpreview.service.ServiceException;
import net.rim.plazmic.internal.mediaengine.MediaModel;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.util.MEUtilities;
import net.rim.plazmic.mediaengine.MediaException;
import net.rim.plazmic.mediaengine.MediaPlayer;

final class ContentPreviewPMEBrowserField extends PMEBrowserField implements PlaybackCommandHandler {
   private Timer _timer;
   public static final String rcsid;
   private static final String CONTROL_PANEL_SERVER_HOST;
   private static final boolean DEBUG;
   private static final long TIMER_INTERVAL;
   private static TimeSourceImpl _timeSource = new TimeSourceImpl();
   private static Dispatcher _dispatcherClient;
   private static PlaybackCommandServer _playbackCommandServer;
   private static ControlPanelClient _controlPanelClient;
   private static String _sessionName;

   ContentPreviewPMEBrowserField(InputConnection inputConnection, InputStream in, BrowserContentBaseImpl browserContent, long fieldStyle) {
      super(inputConnection, in, browserContent, fieldStyle);
      if (!initServices()) {
         throw new Object("Could not start services");
      }

      try {
         this.changeRate((float)1065353216);
      } catch (MishandleException var7) {
      }
   }

   static final boolean initServices() {
      if (_dispatcherClient == null) {
         _dispatcherClient = initDispatcherClient();
         if (_dispatcherClient != null) {
            _sessionName = initSessionName();
            if (_sessionName != null) {
               _playbackCommandServer = initPlaybackCommandServer();
               if (_playbackCommandServer != null) {
                  _controlPanelClient = initControlPanelClient();
                  if (_controlPanelClient != null) {
                     return true;
                  }

                  try {
                     _playbackCommandServer.stopService();
                  } catch (ServiceException var1) {
                  }

                  logError("device_init_control_panel_client_failed");
                  return false;
               } else {
                  logError("device_init_playback_command_server_failed");
                  return false;
               }
            } else {
               logError("device_init_session_name_failed");
               return false;
            }
         } else {
            logError("device_init_dispatcher_client_failed");
            return false;
         }
      } else {
         return true;
      }
   }

   private static final void handleMediaException(MediaException e) {
      logError("device_media_exception", new Object[]{e.toString()});
   }

   private static final void logMessage(String message, String[] data) {
      log(1, message, data);
   }

   private static final void logMessage(String message) {
      logMessage(message, new Object[0]);
   }

   private static final void logError(String message, String[] data) {
      log(3, message, data);
   }

   private static final void logError(String message) {
      logError(message, new Object[0]);
   }

   private static final void log(int type, String message, String[] data) {
      if (_dispatcherClient != null && _sessionName != null) {
         try {
            _dispatcherClient.log(_sessionName, type, message, data);
         } finally {
            return;
         }
      }
   }

   @Override
   public final void startPlayer() {
      if (this.getMediaPlayer() != null) {
         do {
            super.startPlayer();
         } while (this.getMediaPlayer().getState() == 1);

         try {
            if (_controlPanelClient != null) {
               _controlPanelClient.clientInfo(3, 0);
               _controlPanelClient.started();
            }
         } catch (MishandleException var2) {
         }

         this.startTimer();
      }
   }

   @Override
   public final void stopPlayer() {
      this.stopPlayer(true);
   }

   private final void stopPlayer(boolean sendTimeUpdate) {
      super.stopPlayer();

      try {
         if (_controlPanelClient != null) {
            _controlPanelClient.stopped();
         }
      } catch (MishandleException var3) {
      }

      this.stopTimer();
      if (sendTimeUpdate) {
         this.updateTime(this.getMediaPlayer().getMediaTime());
      }
   }

   private final void startTimer() {
      this.stopTimer();
      this._timer = (Timer)(new Object());
      TimerTask updateTime = new ContentPreviewPMEBrowserField$1(this);
      this._timer.scheduleAtFixedRate(updateTime, 0, 100);
   }

   private final void stopTimer() {
      if (this._timer != null) {
         this._timer.cancel();
         this._timer = null;
      }
   }

   private final void updateTime(long time) {
      try {
         if (_controlPanelClient != null) {
            _controlPanelClient.sceneTimeChanged(time);
            return;
         }
      } catch (MishandleException var4) {
      }
   }

   private static final Dispatcher initDispatcherClient() {
      try {
         return new DispatcherClient();
      } finally {
         ;
      }
   }

   private static final String initSessionName() {
      int pin = DeviceInfo.getDeviceId();

      try {
         return _dispatcherClient.getSession(pin);
      } finally {
         ;
      }
   }

   private static final PlaybackCommandServer initPlaybackCommandServer() {
      try {
         int playbackCommandServerPort = _dispatcherClient.getPlaybackCommandPort(_sessionName);
         PlaybackCommandServer pcs = new PlaybackCommandServer(playbackCommandServerPort);
         pcs.startService();
         return pcs;
      } finally {
         ;
      }
   }

   private static final ControlPanelClient initControlPanelClient() {
      try {
         int controlPanelServerPort = _dispatcherClient.getControlPanelPort(_sessionName);
         ControlPanelClient cpc = new ControlPanelClient(
            _playbackCommandServer.getConnection(), "127.0.0.1", controlPanelServerPort, _playbackCommandServer.getPort()
         );
         cpc.startService();
         return cpc;
      } finally {
         ;
      }
   }

   @Override
   public final void onDisplay() {
      logMessage(((StringBuffer)(new Object("onDisplay("))).append(_playbackCommandServer.getPort()).append(")").toString());
      super.onDisplay();
      this.setMediaTime(0, false);
      ((MediaServices)this.getMediaPlayer().getServices()).getEngine().setTimeSource(_timeSource);
      if (_playbackCommandServer != null) {
         _playbackCommandServer.setHandler(this);
      }
   }

   @Override
   public final void onUndisplay() {
      logMessage(((StringBuffer)(new Object("onUndisplay("))).append(_playbackCommandServer.getPort()).append(")").toString());
      super.onUndisplay();
   }

   private final boolean isPlaying() {
      return this.getMediaPlayer().getState() == 2;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void setMediaTime(long time, boolean preserveState) {
      boolean wasPlaying = this.isPlaying();
      this.stopPlayer(false);

      label29:
      try {
         this.getMediaPlayer().setMediaTime(time);
         this.updateTime(time);
      } catch (Throwable var7) {
         handleMediaException(e);
         break label29;
      }

      if (preserveState && wasPlaying) {
         this.startPlayer();
      }
   }

   @Override
   public final synchronized void pause() {
      this.stopPlayer(true);
   }

   @Override
   public final synchronized void play() {
      this.startPlayer();
   }

   @Override
   public final synchronized void changeRate(float rate) {
      boolean wasPlaying = this.isPlaying();
      this.stopPlayer(false);
      _timeSource.setTimeFactor(rate);
      if (_controlPanelClient != null) {
         _controlPanelClient.rateChanged(_timeSource.getTimeFactor());
      }

      if (wasPlaying) {
         this.startPlayer();
      }
   }

   @Override
   public final synchronized void seek(long time) {
      this.setMediaTime(Math.max(time, 0), true);
   }

   @Override
   public final synchronized void seekFast(long requestTime, long targetTime) {
      _timeSource.offsetTime(targetTime - requestTime);
   }

   @Override
   public final synchronized void skip(long amount) {
      long currentTime = this.getMediaPlayer().getMediaTime();
      this.seek(currentTime + amount);
   }

   @Override
   public final synchronized void requestSceneInfo() {
      try {
         MediaModel model = this.getMediaModel();
         if (_controlPanelClient != null) {
            _controlPanelClient.sceneInfo(
               model.getVersionNumber(),
               model.getContentType(),
               model.getExternalResources(8),
               model.getExternalResources(32),
               model.getExternalResources(2),
               model.getExternalResources(1),
               model.getExternalResources(4)
            );
            return;
         }
      } catch (MishandleException var2) {
      }
   }

   private final MediaPlayer getMediaPlayer() {
      return super._player;
   }

   private final MediaModel getMediaModel() {
      return MEUtilities.getMediaModel(super._model);
   }
}
