package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class ActiveScreenMyNumberField extends Field implements GlobalEventListener {
   private String _myPhoneNumber;
   private String _myLabel;
   private int _myWidth;
   private String _dcPhoneNumber;
   private String _dcLabel;
   private int _dcWidth;
   private boolean _dontPaint;
   private boolean _formatNumber;
   private boolean _directConnectCall;
   private int _lineHeight;
   private int _callId = -1;
   static final long SHOW_UNKNOWN_DEVICE_NUMBER = 1039979742518454189L;
   private static Font _defaultFont;

   public ActiveScreenMyNumberField() {
      this(true);
   }

   public ActiveScreenMyNumberField(boolean formatNumber) {
      this._formatNumber = formatNumber;
      this.update();
   }

   public final void updateCallId(int callId) {
      this._callId = callId;
      this.update();
   }

   private final int getFontSize() {
      if (PhoneUtilities.isCharm240x260Screen()) {
         return 12;
      } else {
         return PhoneUtilities.isElectron320x240Screen() ? 14 : 10;
      }
   }

   public final void update() {
      this._dontPaint = false;
      _defaultFont = Font.getDefault().derive(0, this.getFontSize());
      this._myPhoneNumber = PhoneUtilities.getDevicePhoneNumber(this._callId, this._formatNumber);
      this._myLabel = PhoneResources.getString(168);
      if (this._myPhoneNumber == null || this._myPhoneNumber.length() == 0) {
         if (PhoneUtilities.getDebugFlag(1039979742518454189L)) {
            this._myPhoneNumber = PhoneResources.getString(138);
         } else {
            this._dontPaint = true;
         }
      }

      this._myWidth = Math.max(_defaultFont.getBounds(this._myPhoneNumber), _defaultFont.getBounds(this._myLabel));
      this.setExtent(this.getPreferredWidth(), this.getPreferredHeight());
      if (this._directConnectCall) {
         this._dcLabel = PhoneResources.getString(6044);
         this._dcPhoneNumber = ((StringBuffer)(new Object()))
            .append(DirectConnect.getId(1))
            .append("*")
            .append(DirectConnect.getId(2))
            .append('*')
            .append(DirectConnect.getId(0))
            .toString();
         this._dcWidth = Math.max(_defaultFont.getBounds(this._dcPhoneNumber), _defaultFont.getBounds(this._dcLabel));
      }

      this._lineHeight = _defaultFont.getHeight() + _defaultFont.getLeading();
      this.updateLayout();
   }

   @Override
   public final void layout(int width, int height) {
      this.setExtent(this.getPreferredWidth(), this.getPreferredHeight());
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (!this._dontPaint) {
         int y = 0;
         int x = 0;
         String label;
         String phoneNumber;
         if (this._directConnectCall) {
            label = this._dcLabel;
            phoneNumber = this._dcPhoneNumber;
            if (!PhoneUtilities.isCharm240x260Screen() && this._dcWidth < this._myWidth) {
               x = this._myWidth - this._dcWidth;
            }
         } else {
            label = this._myLabel;
            phoneNumber = this._myPhoneNumber;
            if (!PhoneUtilities.isCharm240x260Screen() && this._myWidth < this._dcWidth) {
               x = this._dcWidth - this._myWidth;
            }
         }

         Font oldFont = graphics.getFont();
         graphics.setFont(_defaultFont);
         graphics.drawText(label, x, 0);
         y += this._lineHeight;
         graphics.drawText(phoneNumber, x, y);
         graphics.setFont(oldFont);
      }
   }

   @Override
   public final int getPreferredWidth() {
      return DirectConnect.isSupported() ? Math.max(this._myWidth, this._dcWidth) + 2 : this._myWidth + 2;
   }

   @Override
   public final int getPreferredHeight() {
      return this._lineHeight * 2;
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      super.onVisibilityChange(visible);
      if (visible) {
         this._directConnectCall = isDirectConnectCall();
         this.update();
      }
   }

   private static final boolean isDirectConnectCall() {
      if (DirectConnect.isSupported()) {
         try {
            return DirectConnect.getActiveCallType() != 0;
         } finally {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4394903006263251010L) {
         this.update();
      }
   }
}
