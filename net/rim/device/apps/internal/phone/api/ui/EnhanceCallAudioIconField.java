package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.AudioRouterListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.EnhanceCallAudioServices;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.internal.ui.Image;

public final class EnhanceCallAudioIconField extends Field implements AudioRouterListener {
   private boolean _listening;
   private AudioRouter _audioRouter = AudioRouter.getInstance();

   public final void onCallInitiated() {
      this.invalidate();
   }

   public final void audioSinkRequested(int sink) {
   }

   @Override
   public final void audioSinkChanged() {
      this.invalidate();
   }

   @Override
   public final void audioSourceChanged() {
   }

   @Override
   public final void audioVolumeChanged(boolean remote) {
   }

   @Override
   public final void paint(Graphics graphics) {
      Image icon;
      if (this._audioRouter.getSink() == 0) {
         icon = EnhanceCallAudioServices.getInstance().getIconForCurrentState();
      } else {
         icon = EnhanceCallAudioServices.getInstance().getIconForState(0);
      }

      icon.paint(graphics, 0, 0, this.getPreferredWidth(), this.getPreferredHeight());
   }

   @Override
   public final int getPreferredHeight() {
      return this.getIconHeight();
   }

   @Override
   public final int getPreferredWidth() {
      return this.getIconWidth();
   }

   private final int getIconWidth() {
      return EnhanceCallAudioServices.getInstance().getIconWidth();
   }

   private final int getIconHeight() {
      return EnhanceCallAudioServices.getInstance().getIconHeight();
   }

   @Override
   public final void layout(int width, int height) {
      this.setExtent(this.getPreferredWidth(), this.getPreferredHeight());
   }

   private final LiveCall getCurrentCall() {
      return (LiveCall)VoiceServices.getVoiceApplication().getCurrentCall();
   }

   private final void stopListening() {
      this._listening = false;
      AudioRouter.removeListener(Application.getApplication(), this);
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (visible) {
         this.startListening();
         this.invalidate();
      } else {
         this.stopListening();
      }
   }

   private final void startListening() {
      if (!this._listening) {
         this._listening = true;
         AudioRouter.addListener(Application.getApplication(), this);
      }
   }
}
