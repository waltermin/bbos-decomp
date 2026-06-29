package net.rim.device.apps.internal.phone;

import java.util.Vector;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.apps.api.addressbook.DisplayPictureModel;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.ScaleBitmap;

final class CallContainer extends Field implements PersistentContentListener {
   private Object _calls;
   private int _flags;
   private StringBuffer _workBuf = (StringBuffer)(new Object());
   private boolean _pictureAvailable = false;
   private Bitmap _cachedBitmap = null;
   private String _cachedBitmapID = "";
   private static final boolean _charmScreen = PhoneUtilities.isCharm240x260Screen();
   private static final int MIN_CALL_STATUS_FONT_HEIGHT = 12;
   private static final int MIN_CONFERENCE_MEMBER_FONT = 12;
   private static final int CKJ_FUDGE_FACTOR = 2;
   static final int MAINTAIN_FONT = 2;
   private static final int BITMAP_PADDING = 4;
   private static int _minCallerIDFontSize;

   public CallContainer(Object calls) {
      this(calls, 0);
   }

   public CallContainer(Object calls, int flags) {
      this._calls = calls;
      this._flags = flags;
      PersistentContent.addWeakListener(this);
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (!visible && (Backlight.isEnabled() || (Display.getProperties() & 16384) == 0)) {
         this.removeDisconnectedCalls();
      }

      super.onVisibilityChange(visible);
   }

   private final void paintConferenceCall(Graphics g, ConferenceCall call, int y, XYRect rect) {
      int height = rect.height;
      Font oldFont = g.getFont();
      int heightUsedForStatus = this.paintCallStatus(g, call, y, rect);
      height -= heightUsedForStatus;
      y += heightUsedForStatus;
      int numMembers = call.memberCount();
      if (numMembers != 0) {
         int fontSize = height / numMembers;
         if (fontSize < _minCallerIDFontSize) {
            fontSize = _minCallerIDFontSize;
         }

         fontSize = Math.min(fontSize, heightUsedForStatus);
         if (fontSize == heightUsedForStatus) {
            fontSize -= 2;
         }

         Font font = oldFont.derive(1, fontSize);
         g.setFont(font);
         int numLinesAvailable = height / fontSize;
         int numCallsToDisplay = 0;
         if (numMembers <= numLinesAvailable) {
            numCallsToDisplay = numMembers;
         } else if (numMembers > 2) {
            numCallsToDisplay = numLinesAvailable - 1;
         } else {
            numCallsToDisplay = 1;
         }

         if (call.isHeld() && numCallsToDisplay == 1) {
            this._workBuf.setLength(0);
            this._workBuf.append('(');
            this._workBuf.append(PhoneResources.getString(113));
            this._workBuf.append(')');
            g.drawText(this._workBuf, 0, this._workBuf.length(), 0, y, 64, rect.width);
         } else {
            for (int i = 0; i < numCallsToDisplay; i++) {
               g.drawText(call.getMember(i).toString(), 0, y, 64, rect.width);
               y += fontSize;
            }

            if (numCallsToDisplay < numMembers && numMembers != 2) {
               g.drawText("...", 0, y, 0, rect.width);
            }
         }

         g.setFont(oldFont);
      }
   }

   private static final int getMaxCallStatusFontSize(int screenHeight) {
      return screenHeight > 260 ? 32 : 24;
   }

   private static final int getMinCallStatusFontSize(int screenHeight) {
      return screenHeight > 260 ? 12 : 10;
   }

   private final int getMinCallerIDFontSize() {
      return this._pictureAvailable ? 16 : _minCallerIDFontSize;
   }

   private final int paintCallStatus(Graphics g, LiveCall call, int y, XYRect rect) {
      Font oldFont = g.getFont();
      int height = rect.height;
      Font paintingFont = oldFont.derive(1);
      int fontSize = oldFont.getHeight();
      String statusString = call.getStatusString();
      if ((this._flags & 2) == 0) {
         int screenHeight = Display.getHeight();
         int minFontSize = getMinCallStatusFontSize(screenHeight);
         fontSize = screenHeight > 160 ? height >> 2 : height / 3;
         fontSize = Math.min(fontSize, getMaxCallStatusFontSize(screenHeight));
         String longestStatusString = call.longestStatusString();

         for (paintingFont = oldFont.derive(1, fontSize);
            paintingFont.getAdvance(longestStatusString) >= rect.width && fontSize > minFontSize;
            paintingFont = paintingFont.derive(1, fontSize)
         ) {
            fontSize -= 2;
         }
      }

      g.setFont(paintingFont);
      g.drawText(statusString, rect.x, y + 2, 64, rect.width);
      g.setFont(oldFont);
      return fontSize + 2;
   }

   private final void paintRedirectedCall(Graphics g, LiveCall call, int y, XYRect rect) {
      Font oldFont = g.getFont();
      int height = rect.height;
      int fontSize = height >> 2;
      Font paintingFont = oldFont.derive(1, fontSize);
      String origCallerID = call.getDisplayCallerIDInfo().toString();
      int stringId = call.isOutgoing() ? 6028 : 6029;
      String redirectedText = PhoneResources.getString(stringId);
      String newNumber = call.getRedirectedNumber();
      g.setFont(paintingFont);
      fontSize = this.paintCallStatus(g, call, y, rect);
      y += fontSize;
      String longest1 = PhoneUtilities.getLongestString(origCallerID, redirectedText);
      String longest = PhoneUtilities.getLongestString(longest1, newNumber);

      while (paintingFont.getAdvance(longest) >= rect.width && fontSize >= this.getMinCallerIDFontSize()) {
         paintingFont = paintingFont.derive(1, --fontSize);
      }

      g.setFont(paintingFont);
      g.drawText(origCallerID, rect.x, y, 64, rect.width);
      y += fontSize;
      paintingFont = paintingFont.derive(0, --fontSize);
      this.setFont(paintingFont);
      g.drawText(redirectedText, rect.x, y, 64, rect.width);
      y += fontSize;
      if (newNumber != null) {
         g.drawText(newNumber, rect.x, y, 64, rect.width);
      }

      g.setFont(oldFont);
   }

   private final void paintSinglePartyCall(Graphics g, LiveCall call, int y, XYRect rect) {
      if (call instanceof Object) {
         PaintProvider pp = call;
         if (pp.paint(g, 0, y, rect.width, rect.height, null) != 0) {
            return;
         }
      }

      CallerIDInfo cidi = call.getDisplayCallerIDInfo();
      DisplayPictureModel dp = cidi.getDisplayPictureModel();
      Bitmap icon = null;
      String displayPhoneNumber = call.getDisplayPhoneNumber(false);
      if (dp != null) {
         if (this._cachedBitmap == null || !this._cachedBitmapID.equals(displayPhoneNumber)) {
            this._cachedBitmap = dp.getDisplayBitmap();
            this._cachedBitmapID = displayPhoneNumber;
         }

         icon = dp.getDisplayIcon();
      } else {
         this._cachedBitmap = null;
         this._cachedBitmapID = "";
      }

      if (this._cachedBitmap != null && !rect.isEmpty()) {
         if (this._cachedBitmap.getHeight() > rect.height - 8) {
            int width = Fixed32.div(this._cachedBitmap.getWidth(), Fixed32.div(Fixed32.toFP(this._cachedBitmap.getHeight()), Fixed32.toFP(rect.height - 8)));
            this._cachedBitmap = ScaleBitmap.scaleBitmap(this._cachedBitmap, width, rect.height - 8);
         }

         g.drawBitmap(4, 4, rect.width, rect.height, this._cachedBitmap, 0, 0);
         int widthUsedForImage = this._cachedBitmap.getWidth() + 8;
         rect = (XYRect)(new Object(rect.x + widthUsedForImage, rect.y, rect.width - widthUsedForImage, rect.height));
         this._pictureAvailable = true;
      } else {
         this._pictureAvailable = false;
      }

      if (call.getFlag(4096)) {
         this.paintRedirectedCall(g, call, y, rect);
      } else {
         Font oldFont = g.getFont();
         int height = rect.height;
         int heightUsedForStatus = this.paintCallStatus(g, call, y, rect);
         int fontSize = heightUsedForStatus;
         height -= heightUsedForStatus;
         y += heightUsedForStatus;
         Object cidiAddress = cidi.getAddress();
         String line1 = null;
         String line2 = null;
         String line3 = null;
         if (cidiAddress != null) {
            line1 = cidi.toString();
            if (PhoneUtilities.isHighResColourScreen()) {
               line2 = displayPhoneNumber;
               if (!cidi.isSpecial() && !cidi.isDefaultPhoneNumberType()) {
                  line3 = call.getPhoneNumberTypeString();
               }
            } else {
               line2 = call.getDisplayPhoneNumber(true);
            }
         } else {
            String friendlyName = cidi.getFriendlyName();
            if (friendlyName != null) {
               line1 = friendlyName;
               line2 = displayPhoneNumber;
            } else {
               line1 = displayPhoneNumber;
            }
         }

         if (line3 != null) {
            int lineHeight = height / 3;
            if (fontSize > lineHeight) {
               fontSize = lineHeight;
            }
         } else if (line2 != null) {
            int lineHeight = height >> 1;
            if (fontSize > lineHeight) {
               fontSize = lineHeight;
            }
         }

         Font paintingFont = oldFont.derive(1, fontSize);
         String longest = PhoneUtilities.getLongestString(line1, line2);
         if (line3 != null && line3.length() > longest.length()) {
            longest = line3;
         }

         while (paintingFont.getAdvance(longest) >= rect.width && fontSize >= this.getMinCallerIDFontSize()) {
            paintingFont = paintingFont.derive(1, --fontSize);
         }

         int heightNeeded = fontSize;
         if (line2 != null) {
            heightNeeded += fontSize;
         }

         if (line3 != null) {
            heightNeeded += fontSize;
         }

         int heightNotUsed = height - heightNeeded;
         y += Math.min(4, heightNotUsed);
         g.setFont(paintingFont);
         int line1Start = rect.x;
         int line1Width = rect.width;
         int adjustY = 0;
         if (icon != null) {
            int iconWidth = icon.getWidth();
            int iconHeight = icon.getHeight();
            g.drawBitmap(rect.x, y, iconWidth, iconHeight, icon, 0, 0);
            iconWidth += 2;
            line1Start += iconWidth;
            line1Width -= iconWidth;
            if (iconHeight > fontSize) {
               adjustY = iconHeight / 2 - fontSize / 2;
            }
         }

         g.drawText(line1, line1Start, y + adjustY, 64, line1Width);
         y += icon != null ? Math.max(fontSize, icon.getHeight()) : fontSize;
         if (cidi.isEmergencyCallCallerIDInfo()) {
            g.setFont(oldFont);
         } else {
            if (line2 != null) {
               g.drawText(line2, rect.x, y, 64, rect.width);
               y += fontSize;
            }

            if (line3 != null) {
               g.drawText(line3, rect.x, y, 64, rect.width);
               y += fontSize;
            }

            g.setFont(oldFont);
         }
      }
   }

   private final void paintCall(Graphics g, LiveCall call, int y, XYRect extent) {
      if (call instanceof ConferenceCall) {
         this.paintConferenceCall(g, (ConferenceCall)call, y, extent);
      } else {
         this.paintSinglePartyCall(g, call, y, extent);
      }
   }

   @Override
   protected final void paint(Graphics g) {
      XYRect extent = this.getExtent();
      if (this._calls instanceof Object) {
         this.paintCall(g, (LiveCall)this._calls, 0, extent);
      } else {
         if (this._calls instanceof Object) {
            Vector calls = (Vector)this._calls;
            int count = calls.size();
            switch (count) {
               case 0:
                  break;
               case 1:
               default:
                  this.paintCall(g, (LiveCall)calls.elementAt(0), 0, extent);
                  return;
               case 2:
                  int heightOver2 = extent.height - 2 >> 1;
                  int y1 = 0;
                  int y2 = heightOver2 + 2;
                  Font oldFont = g.getFont();
                  XYRect paintingRect = (XYRect)(new Object(extent.x, extent.y, extent.width, heightOver2));
                  LiveCall callA = (LiveCall)calls.elementAt(0);
                  LiveCall callB = (LiveCall)calls.elementAt(1);
                  int statusFontSize = paintingRect.height >> 1;
                  Font paintingFont = oldFont.derive(1, statusFontSize);
                  g.setFont(paintingFont);
                  if (callA instanceof ConferenceCall && callA.isActive()) {
                     g.drawText(PhoneResources.getString(300), 0, y1, 64, paintingRect.width);
                  } else {
                     g.drawText(callA.getStatusString(), 0, y1, 64, paintingRect.width);
                  }

                  if (callB instanceof ConferenceCall && callB.isActive()) {
                     g.drawText(PhoneResources.getString(300), 0, y2, 64, paintingRect.width);
                  } else {
                     g.drawText(callB.getStatusString(), 0, y2, 64, paintingRect.width);
                  }

                  y1 += statusFontSize;
                  y2 += statusFontSize;
                  paintingRect.height -= statusFontSize;
                  this.paintCallerID(g, callA, 0, y1, paintingRect, paintingFont, statusFontSize, true);
                  this.paintCallerID(g, callB, 0, y2, paintingRect, paintingFont, statusFontSize, true);
                  g.setFont(oldFont);
                  this._cachedBitmap = null;
                  this._cachedBitmapID = "";
            }
         }
      }
   }

   private final void paintCallerID(Graphics g, LiveCall call, int x, int y, XYRect rect, Font font, int maxFontSize, boolean multipleCalls) {
      String line1 = null;
      String line2 = null;
      CallerIDInfo cidi = call.getDisplayCallerIDInfo();
      Object cidiAddress = cidi.getAddress();
      line1 = call.getCallerIDDisplayString();
      if (line1 == null) {
         line1 = cidiAddress != null ? cidi.toString() : call.getDisplayPhoneNumber(false);
      }

      int lineCount = 1;
      if (cidi.getAddress() != null) {
         line2 = call.getDisplayPhoneNumber();
         lineCount++;
      }

      int fontSize = rect.height / lineCount;
      fontSize = Math.min(fontSize, maxFontSize);
      if (fontSize < _minCallerIDFontSize) {
         line2 = null;
         fontSize = maxFontSize;
      }

      if (call instanceof ConferenceCall && multipleCalls) {
         line1 = ((StringBuffer)(new Object("("))).append(PhoneResources.getString(113)).append(")").toString();
         fontSize -= 2;
      }

      font = font.derive(1, fontSize);
      g.setFont(font);
      g.drawText(line1, x, y, 64, rect.width);
      if (line2 != null) {
         y += fontSize;
         g.drawText(line2, x, y, 64, rect.width);
      }
   }

   @Override
   public final void layout(int width, int height) {
      this.setExtent(width, height);
   }

   final void callerIDUpdated() {
      this.invalidate();
   }

   final void update(Vector calls) {
      if (calls != null && calls.size() > 0) {
         this._calls = calls;
         this.invalidate();
      } else {
         this._calls = null;
         this._cachedBitmap = null;
         this._cachedBitmapID = "";
      }
   }

   final void setIncomingCall(LiveCall call) {
      if (call != null) {
         this._calls = call;
         this.invalidate();
      }
   }

   private final void removeDisconnectedCalls() {
      if (!(this._calls instanceof Object)) {
         if (this._calls instanceof Object) {
            Vector calls = (Vector)this._calls;

            for (int idx = calls.size() - 1; idx >= 0; idx--) {
               LiveCall call = (LiveCall)calls.elementAt(idx);
               if (call.getFlag(4)) {
                  calls.removeElementAt(idx);
               }
            }

            if (calls.size() == 0) {
               this._calls = null;
               this._cachedBitmap = null;
               this._cachedBitmapID = "";
            }
         }
      } else {
         LiveCall call = (LiveCall)this._calls;
         if (call.getFlag(4)) {
            this._calls = null;
            this._cachedBitmap = null;
            this._cachedBitmapID = "";
            return;
         }
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      if (!Phone.isPhoneActive()) {
         this._calls = null;
         this._cachedBitmap = null;
         this._cachedBitmapID = "";
      }
   }

   @Override
   public final void persistentContentStateChanged(int state) {
      if (!Phone.isPhoneActive()) {
         this._calls = null;
         this._cachedBitmap = null;
         this._cachedBitmapID = "";
      }
   }

   static {
      int height = Display.getHeight();
      if (height < 160) {
         _minCallerIDFontSize = 16;
      } else if (_charmScreen) {
         _minCallerIDFontSize = 32;
      } else {
         _minCallerIDFontSize = 18;
      }
   }
}
