package net.rim.device.apps.internal.phone;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.internal.media.StreamDataControl;
import net.rim.device.resources.Resource;

final class PreCallTunePlayer implements PlayerListener {
   private VoiceApp _voiceApp;
   private String _tuneName;
   private Player _player;
   private LiveCall _call;
   private byte[] _tune;
   private boolean _forceSpeaker;
   private int _oldSink;

   PreCallTunePlayer(VoiceApp app, String tuneResourceName, byte[] tune, LiveCall call, boolean forceSpeaker) {
      this._voiceApp = app;
      this._tuneName = tuneResourceName;
      this._call = call;
      this._tune = tune;
      this._forceSpeaker = forceSpeaker;
   }

   public final void startTune() {
      byte[] buffer = null;
      if (this._tune != null) {
         buffer = this._tune;
      } else {
         buffer = Resource.getResourceClass().getResource(this._tuneName);
      }

      InputStream is = new ByteArrayInputStream(buffer);
      String audioType = MIMETypeAssociations.getMIMEType(this._tuneName);
      AudioRouter.getInstance().addSource(6);

      try {
         this._player = Manager.createPlayer(is, audioType);
         if (this._player instanceof StreamDataControl) {
            ((StreamDataControl)this._player).setKeyValue("flag_not_unloadable", Boolean.TRUE);
         }

         if (this._forceSpeaker) {
            System.out.println("CMW: _forceSpeaker");
            AudioRouter audioRouter = AudioRouter.getInstance();
            this._oldSink = audioRouter.getSink();
            audioRouter.setSink(1);
         }

         this._player.addPlayerListener(this);
         this._player.start();
      } finally {
         System.out.println("PreCall tune failed to play.");
         this.onDone();
         return;
      }
   }

   private final void onDone() {
      AudioRouter.getInstance().removeSource(6);
      if (this._forceSpeaker) {
         AudioRouter.getInstance().setSink(this._oldSink);
      }

      this._voiceApp.preCallTuneFinished(this._call);
      this.stopListening();

      try {
         this._player.close();
      } finally {
         return;
      }
   }

   private final void stopListening() {
      try {
         this._player.removePlayerListener(this);
      } finally {
         return;
      }
   }

   @Override
   public final void playerUpdate(Player player, String event, Object eventData) {
      if (player == this._player && (event.equals("endOfMedia") || event.equals("stopped") || event.equals("error"))) {
         this.onDone();
      }
   }
}
