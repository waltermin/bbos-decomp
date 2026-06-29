package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.device.internal.media.MediaPlayerState;
import net.rim.device.internal.media.MediaPlayerStateInstance;

final class MediaBrowserField$MyMediaPlayerState extends MediaPlayerStateInstance {
   private boolean _mediaPlayerSet;
   private final MediaBrowserField this$0;

   MediaBrowserField$MyMediaPlayerState(MediaBrowserField _1) {
      this.this$0 = _1;
      MediaPlayerState.registerPlayer(this);
      if (_1._renderingOptions.getPropertyWithBooleanValue(9094571315961484757L, 5, false)) {
         MediaPlayerState.setMediaPlayer(this);
         this._mediaPlayerSet = true;
      }
   }

   final void destroy() {
      MediaPlayerState.deregisterPlayer(this);
      if (this._mediaPlayerSet) {
         MediaPlayerState.setMediaPlayer(null);
         this._mediaPlayerSet = false;
      }
   }

   @Override
   public final boolean isPlayerPlaying() {
      return this.this$0._player != null && this.this$0._player.getState() == 400;
   }

   @Override
   public final boolean isPlayerPaused() {
      return this.this$0._player != null && this.this$0._paused;
   }

   @Override
   public final String getPlayingURL() {
      return this.this$0._currentUrl == null ? null : this.this$0._currentUrl;
   }
}
