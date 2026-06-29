package net.rim.device.apps.internal.phone.api.livecall;

import net.rim.device.api.system.Phone;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.Out;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class LiveCallVerb extends Verb {
   private int _whatToDo;
   private LiveCall _call;
   private int _callId;
   public static final int END = 0;
   public static final int HOLD = 1;
   public static final int RESUME = 2;
   public static final int SWAP = 3;
   public static final int JOIN = 4;
   public static final int NOTES = 5;
   public static final int ECT = 6;
   public static final int MUTE = 7;
   public static final int UNMUTE = 8;
   public static final int PARK = 9;
   public static final int SENDTOVOICEMAIL = 10;
   private static final int[] RES_IDS = new int[]{
      402,
      401,
      403,
      408,
      427,
      425,
      6126,
      461,
      462,
      6303,
      6304,
      -804650988,
      1262,
      30234,
      31234,
      32234,
      3232,
      16204,
      30216,
      1230,
      1219,
      2231,
      4220,
      10234,
      2272,
      7214,
      7262,
      8262,
      2232,
      604,
      15234,
      1214,
      1866858752,
      -1574934772,
      1661010020,
      1300917516,
      1443568481,
      1703898223,
      426115328,
      1711341677,
      1970380911,
      16784229,
      38616944,
      -682312344
   };

   LiveCallVerb(LiveCall call, int whatToDo, int ordering) {
      super(ordering);
      this._whatToDo = whatToDo;
      this._call = call;
      this._callId = call.getCallId();
   }

   public final int getAction() {
      return this._whatToDo;
   }

   @Override
   public final Object invoke(Object parameter) {
      switch (this._whatToDo) {
         case -1:
            break;
         case 0:
         default:
            this._call.endByUser();
            return null;
         case 1:
            Out.p(1128352844, 1213156420);
            VoiceServices.holdCall();
            return null;
         case 2:
            Out.p(1128352844, 1380275029);
            VoiceServices.resumeCall();
            return null;
         case 3:
            if (CallTask.canExecute()) {
               CallTask.executeTask(new CallSwapper());
               return null;
            }
            break;
         case 4:
            VoiceServices.joinCalls();
            return null;
         case 5:
            VoiceServices.getVoiceApplication().editCallNotes();
            return null;
         case 6:
            VoiceServices.transferCall();
            return null;
         case 7:
         case 8:
            this._call.mute();
            return null;
         case 9:
            Phone.getInstance().parkCall(this._callId);
            return null;
         case 10:
            Phone.getInstance().sendToVoicemail(this._callId);
      }

      return null;
   }

   @Override
   public final String toString() {
      int id;
      switch (this._whatToDo) {
         case 6:
            id = RES_IDS[this._whatToDo];
            break;
         case 7:
         case 8:
         default:
            id = !this._call.isMuted() ? RES_IDS[7] : RES_IDS[8];
      }

      return PhoneResources.getString(id);
   }
}
