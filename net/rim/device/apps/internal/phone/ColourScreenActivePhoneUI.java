package net.rim.device.apps.internal.phone;

import java.util.Vector;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.api.ui.ActivePhoneScreenHeader;
import net.rim.device.apps.internal.phone.api.ui.ActiveScreenMyNumberField;
import net.rim.device.apps.internal.phone.api.ui.DTMFEchoField;
import net.rim.device.apps.internal.phone.api.ui.EnhanceCallAudioIconField;
import net.rim.device.apps.internal.phone.api.ui.PhoneSystemStatusRibbonField;
import net.rim.device.apps.internal.phone.api.ui.ThemedBannerCache;
import net.rim.device.apps.internal.phone.api.ui.VolumeIndicator;

final class ColourScreenActivePhoneUI extends ActivePhoneUI {
   private CallContainer _callContainer;
   private ActivePhoneScreenHeader _header;
   private ActiveScreenMyNumberField _myNumberField;
   private VolumeIndicator _volumeIndicator;
   private EnhanceCallAudioIconField _ecaIconField;
   private int _flags;
   UiApplication _app = null;
   private static final int HEADER_FIELD = 0;
   private static final int MY_NUMBER_FIELD = 1;
   private static final int BANNER_FIELD = 2;
   private static final int DTMF_FIELD = 3;
   private static final int CALL_INFO = 4;
   private static final int VOLUME_INDICATOR = 5;
   private static final int ECA_FIELD = 6;
   static final int MY_NUMBER_FIELD_UNDER_HANDSET = 2;
   private static final String _phoneScreenFontFamilyName = "BBMillbank";
   private static final int _screenHeight = Display.getHeight();
   private static final int _screenWidth = Display.getHeight();
   private static ThemedBannerCache _activeBannerCache;

   private static final Field getCachedActiveBanner() {
      if (_activeBannerCache == null) {
         _activeBannerCache = new ColourScreenActivePhoneUI$1();
      }

      return _activeBannerCache.getBanner(false);
   }

   static final void initializeOnceOnSystemStart() {
      getCachedActiveBanner();
   }

   ColourScreenActivePhoneUI(UiApplication app, Screen screen, int flags) {
      super(app, screen);
      this._app = app;
      this._flags = flags;
   }

   @Override
   final void updateCalls(Vector calls) {
      this._callContainer.update(calls);
      LiveCall liveCall = (LiveCall)CallManager.getInstance().getCurrentCall();
      this.updateDTMFCallControlField(liveCall);
      if (liveCall != null) {
         int[] ids = liveCall.getCallIds();
         if (ids.length > 0) {
            this._myNumberField.updateCallId(ids[0]);
         } else {
            this._myNumberField.updateCallId(-1);
         }
      } else {
         this._myNumberField.updateCallId(-1);
      }
   }

   @Override
   final void setIncomingCall(LiveCall call) {
      this._callContainer.setIncomingCall(call);
      this.updateDTMFCallControlField(call);
   }

   private final void updateDTMFCallControlField(LiveCall activeCall) {
      Field field = this.getField(3);
      if (activeCall != null) {
         Field controlField = activeCall.getCallControlField(null);
         if (controlField != null) {
            this.insert(controlField, 3);
            this.delete(field);
            return;
         }
      }

      if (!(field instanceof DTMFEchoField)) {
         this.insert(this.createDTMFEchoField(), 3);
         this.delete(field);
      }
   }

   private final DTMFEchoField createDTMFEchoField() {
      return new DTMFEchoField();
   }

   @Override
   final void callerIDUpdated(int callId, Object callerIDInfo) {
      this._callContainer.callerIDUpdated();
      this._myNumberField.updateCallId(callId);
   }

   @Override
   final void ECAUpdated() {
      this.invalidate();
   }

   @Override
   final void addFields(Vector currentCalls, LiveCall newlyAnsweredCall, Object context) {
      CallManager callMgr = CallManager.getInstance();
      LiveCall currentCall = newlyAnsweredCall != null ? newlyAnsweredCall : (LiveCall)callMgr.getCurrentCall();
      this._header = new ActivePhoneScreenHeader(currentCall, this.getOwnerScreen());
      this.add(this._header);
      this._myNumberField = new ActiveScreenMyNumberField();
      this._app.addGlobalEventListener(this._myNumberField);
      this.add(this._myNumberField);
      Field banner = getCachedActiveBanner();
      if (banner == null) {
         banner = new PhoneSystemStatusRibbonField(this.getRibbonComponentFieldProvider(), 2);
      }

      this.add(banner);
      Object calls = this.getCallsToDisplay(currentCalls, newlyAnsweredCall);
      this.add(this.createDTMFEchoField());
      this._callContainer = new CallContainer(calls);
      this.add(this._callContainer);
      this._volumeIndicator = new VolumeIndicator(false, this.getOwnerScreen());
      this.add(this._volumeIndicator);
      this._ecaIconField = new EnhanceCallAudioIconField();
      this.add(this._ecaIconField);
   }

   private final boolean myNumberUnderHandset() {
      return (this._flags & 2) != 0;
   }

   @Override
   final DTMFEchoField getDTMFEchoField() {
      Field field = this.getField(3);
      return !(field instanceof DTMFEchoField) ? null : (DTMFEchoField)field;
   }

   @Override
   protected final void sublayout(int width, int height) {
      int count = this.getFieldCount();

      for (int i = 0; i < count; i++) {
         int layoutHeight = height;
         int layoutWidth = width;
         int x = 0;
         int y = 0;
         Field field = this.getField(i);
         switch (i) {
            case -1:
               break;
            case 0:
            default:
               layoutHeight = field.getPreferredHeight();
               break;
            case 1:
               if (this.myNumberUnderHandset()) {
                  y = this._header.getHandsetBmpBottom() + 4;
               } else {
                  x = width - field.getPreferredWidth();
                  y = getTxRxArrowsHeight() + this._volumeIndicator.getPreferredHeight() + 2;
               }
               break;
            case 2:
               layoutHeight = field.getPreferredHeight();
               y = height - layoutHeight;
               break;
            case 3:
               XYRect rect = this.getField(2).getExtent();
               int prefHeight = field.getPreferredHeight();
               layoutHeight = prefHeight;
               y = rect.y - prefHeight;
               break;
            case 4:
               XYRect headerRect = this.getField(0).getExtent();
               XYRect dtmfRect = this.getField(3).getExtent();
               XYRect myNumberRect = this.getField(1).getExtent();
               int screenHeight = Display.getHeight();
               if (screenHeight < 240) {
                  if (screenHeight > 160) {
                     y = myNumberRect.y;
                  } else {
                     y = this._header.getHandsetBmpBottom() + 16;
                  }

                  int limitingFieldWidth = this.getField(1).getPreferredWidth();
                  layoutWidth = width - limitingFieldWidth - 6;
                  layoutHeight = dtmfRect.y - headerRect.Y2() - 4;
               } else if (PhoneUtilities.isCharm240x260Screen()) {
                  if (screenHeight <= 260) {
                     y = myNumberRect.Y2() + 8;
                  } else {
                     y = myNumberRect.Y2() + 20;
                  }

                  layoutHeight = dtmfRect.y - y;
                  layoutWidth = width;
               } else {
                  x = 0;
                  y = this._header.getHandsetBmpBottom() + 16;
                  layoutWidth = width - this.getField(1).getPreferredWidth() - 8;
                  layoutHeight = dtmfRect.y - y;
               }
               break;
            case 5:
               x = width - field.getPreferredWidth();
               y = ActivePhoneScreenHeader.getTxRxArrowsHeight();
               break;
            case 6:
               x = width - field.getPreferredWidth() - this.getField(5).getPreferredWidth() - 3;
               y = ActivePhoneScreenHeader.getTxRxArrowsHeight();
         }

         this.setPositionChild(field, x, y);
         this.layoutChild(field, layoutWidth, layoutHeight);
      }

      this.setExtent(width, height);
   }

   private static final Bitmap getTxRxArrowsBmp() {
      Theme theme = ThemeManager.getActiveTheme();
      if (theme != null) {
         EncodedImage img = theme.getImage("osicon_txrx", true);
         if (img != null) {
            return img.getBitmap();
         }
      }

      return null;
   }

   private static final int getTxRxArrowsHeight() {
      Bitmap bmp = getTxRxArrowsBmp();
      return bmp != null ? bmp.getHeight() : 16;
   }

   @Override
   final void onCallInitiated() {
      this._header.onCallInitiated();
   }

   @Override
   public final void updateNumber() {
      this._myNumberField.update();
   }
}
