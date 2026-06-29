package net.rim.device.internal.media;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.AudioRouterListener;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.PhoneCallListener;
import net.rim.vm.WeakReference;

class PlayerImpl$MyPhoneListener implements PhoneCallListener, AudioRouterListener, Runnable {
   private AudioRouter _audioRouter;
   private Application _playerApp;
   private WeakReference _wr;

   public void deregister() {
      this._playerApp.removeRadioListener(this);
      AudioRouter.removeListener(this._playerApp, this);
   }

   @Override
   public void callFailed(int callId, int error) {
      this.callEnding();
   }

   @Override
   public void callDisconnected(int callId) {
      this.callEnding();
   }

   @Override
   public void callInitiated(int callid) {
   }

   @Override
   public void callConnected(int callId) {
   }

   @Override
   public void callDelivered(int callId) {
   }

   @Override
   public void callManipulateFailed(int callId, int error) {
   }

   @Override
   public void callHeld(int callId) {
   }

   @Override
   public void callResumed(int callId) {
   }

   @Override
   public void callAdded(int callId) {
   }

   @Override
   public void callRemoved(int callId) {
   }

   @Override
   public void callTransferred(int status, int reason) {
   }

   @Override
   public void callTransferStateUpdated(int callId, int state) {
   }

   @Override
   public void callVoicePrivacyUpdated(int callId, boolean on) {
   }

   @Override
   public void callOTAStatusUpdated(int callId, int status) {
   }

   @Override
   public void callDisplayUpdated(int callId) {
   }

   @Override
   public void callWaiting(int callId) {
   }

   @Override
   public void dtmfData(int dtmf) {
   }

   @Override
   public void run() {
      PlayerImpl player = this.getPlayer();
      if (player != null) {
         player._playRunnableID = -1;
         if (!Phone.getInstance().isActive()) {
            player.makeMediaAvailable();
         }
      }
   }

   @Override
   public void callIncoming(int callId) {
      this.callStarting();
   }

   @Override
   public void audioVolumeChanged(boolean remote) {
   }

   @Override
   public void audioSinkChanged() {
   }

   @Override
   public void audioSourceChanged() {
      PlayerImpl player = this.getPlayer();
      if (player != null) {
         player.onAudioSourceChanged();
      }
   }

   private void callEnding() {
      PlayerImpl player = this.getPlayer();
      if (player != null) {
         player.onCallEnding();
      }
   }

   private void callStarting() {
      PlayerImpl player = this.getPlayer();
      if (player != null) {
         player.onCallStarting();
      }
   }

   public PlayerImpl$MyPhoneListener(PlayerImpl player) {
      this._wr = (WeakReference)(new Object(player));
      this._playerApp = player.getApplication();
      this._playerApp.addRadioListener(this);
      this._audioRouter = AudioRouter.getInstance();
      AudioRouter.addListener(this._playerApp, this);
   }

   private PlayerImpl getPlayer() {
      Object player = this._wr.get();
      if (!(player instanceof PlayerImpl)) {
         this.deregister();
         return null;
      } else {
         return (PlayerImpl)player;
      }
   }
}
