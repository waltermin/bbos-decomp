package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.AudioRouterListener;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceApplication;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.internal.ui.IconCollection;

public final class PhoneReceiverBitmapField extends Field implements PhoneEventListener, AudioRouterListener {
   private boolean _muted;
   private boolean _listening;
   private int _callType;
   private AudioRouter _audioRouter;
   private static final int UNMUTED_ICON_INDEX;
   private static final int MUTED_ICON_INDEX;
   private static final int SPEAKERPHONE_UNMUTED_ICON_INDEX;
   private static final int SPEAKERPHONE_MUTED_ICON_INDEX;
   private static final int ACTIVE_DIRECT_CONNECT_ICON_INDEX;
   private static final int BLUETOOTH_UNMUTED_ICON_INDEX;
   private static final int BLUETOOTH_MUTED_ICON_INDEX;
   private static final int ICON_COUNT;
   private static final int DEFAULT_HEIGHT;
   private static final int DEFAULT_WIDTH;
   private static IconCollection _icons = IconCollection.get("net_rim_Phone_Receiver", 7);

   public final void audioSinkRequested(int sink) {
   }

   public final void onCallInitiated() {
      LiveCall call = (LiveCall)VoiceServices.getVoiceApplication().getCurrentCall();
      if (call != null) {
         this._muted = call.isMuted();
         this._callType = call.getCallType();
      } else {
         this._muted = false;
         this._callType = 0;
      }

      this.invalidate();
   }

   @Override
   public final void audioVolumeChanged(boolean remote) {
   }

   @Override
   public final void audioSinkChanged() {
      this.invalidate();
   }

   @Override
   public final void audioSourceChanged() {
   }

   @Override
   public final void phoneEventNotify(int eventId, int callId, Object context) {
      LiveCall call = null;
      switch (eventId) {
         case 1001:
         case 1002:
         case 1004:
         case 1007:
         case 150040:
            if (!(context instanceof LiveCall)) {
               call = (LiveCall)ContextObject.get(context, -6075010664073451177L);
            } else {
               call = (LiveCall)context;
            }

            if (call == null) {
               VoiceApplication voiceApp = VoiceServices.getVoiceApplication();
               if (voiceApp != null) {
                  call = (LiveCall)voiceApp.getCurrentCall();
               }
            }

            if (call == null) {
               return;
            } else {
               boolean muted = this._muted;
               this._muted = call.isMuted();
               if (muted != this._muted) {
                  this.invalidate();
               }
            }
      }
   }

   private final LiveCall getCurrentCall() {
      return (LiveCall)VoiceServices.getVoiceApplication().getCurrentCall();
   }

   @Override
   public final void paint(Graphics graphics) {
      LiveCall call = this.getCurrentCall();
      if (call != null) {
         Bitmap bmp = call.getActiveCallBitmap();
         if (bmp != null) {
            graphics.drawBitmap(0, 0, bmp.getWidth(), bmp.getHeight(), bmp, 0, 0);
            return;
         }
      }

      getIcons().paint(graphics, 0, 0, this.getPreferredWidth(), this.getPreferredHeight(), this.getIconIndex(), 0);
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
      return getIcons().getWidth(40, 40);
   }

   private final int getIconHeight() {
      return getIcons().getHeight(40, 40);
   }

   @Override
   public final void layout(int width, int height) {
      this.setExtent(this.getPreferredWidth(), this.getPreferredHeight());
   }

   private final int getIconIndex() {
      if (this._callType == 1) {
         return 4;
      }

      if (VoiceServices.getPhoneState() == 0) {
         return 0;
      }

      switch (this._audioRouter.getSink()) {
         case 0:
            if (this._muted) {
               return 1;
            }

            return 0;
         case 1:
         default:
            if (this._muted) {
               return 3;
            }

            return 2;
         case 2:
            return this._muted ? 6 : 5;
      }
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (visible) {
         this.startListening();
         LiveCall call = this.getCurrentCall();
         if (call != null) {
            boolean repaint = false;
            if (this._muted != call.isMuted()) {
               this._muted = !this._muted;
               repaint = true;
            }

            if (this._callType != call.getCallType()) {
               this._callType = call.getCallType();
               repaint = true;
            }

            if (repaint) {
               this.invalidate();
               return;
            }
         }
      } else {
         this.stopListening();
      }
   }

   public PhoneReceiverBitmapField(boolean muted) {
      this._muted = muted;
      this._audioRouter = AudioRouter.getInstance();
   }

   private static final IconCollection getIcons() {
      if (_icons == null) {
         _icons = IconCollection.get("net_rim_Phone_Receiver", 7);
      }

      return _icons;
   }

   private final void startListening() {
      if (!this._listening) {
         this._listening = true;
         VoiceServices.addPhoneEventListener(this);
         AudioRouter.addListener(Application.getApplication(), this);
      }
   }

   private final void stopListening() {
      this._listening = false;
      VoiceServices.removePhoneEventListener(this);
      AudioRouter.removeListener(Application.getApplication(), this);
   }

   public PhoneReceiverBitmapField() {
      this(false);
   }
}
