package net.rim.blackberry.api.phone;

import java.util.Vector;
import net.rim.device.apps.api.phone.VoiceApplication;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;

public final class Phone {
   private static VoiceApplication _voiceApp = VoiceServices.getVoiceApplication();

   private Phone() {
   }

   public static final PhoneCall getActiveCall() {
      assertPermission();
      Object o = _voiceApp.getCurrentCall();
      if (!(o instanceof Object)) {
         return null;
      }

      LiveCall lc = (LiveCall)o;
      return new PhoneCall(lc);
   }

   public static final PhoneCall getCall(int callid) {
      assertPermission();
      Vector v = _voiceApp.getCurrentCalls();

      for (int i = v.size() - 1; i >= 0; i--) {
         Object o = v.elementAt(i);
         if (o instanceof Object) {
            LiveCall lc = (LiveCall)o;
            if (lc.getCallId() == callid) {
               return new PhoneCall(lc);
            }
         }
      }

      LiveCall lc = (LiveCall)_voiceApp.getIncomingCall();
      return lc != null && lc.getCallId() == callid ? new PhoneCall(lc) : null;
   }

   public static final void addPhoneListener(PhoneListener pl) {
      assertPermission();
      PhoneApiListener pal = PhoneApiListener.getInstance();
      pal.addListener(pl);
   }

   public static final void removePhoneListener(PhoneListener pl) {
      assertPermission();
      PhoneApiListener pal = PhoneApiListener.getInstance();
      pal.removeListener(pl);
   }

   public static final String getDevicePhoneNumber(boolean format) {
      assertPermission();
      return PhoneUtilities.getDevicePhoneNumber(format);
   }

   private static final void assertPermission() {
      ApplicationControl.assertPhonePermitted(true, CommonResource.getBundle(), 10167);
   }
}
