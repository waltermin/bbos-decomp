package net.rim.device.apps.internal.mms.model;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.verbs.MMSChangeStatusVerb;
import net.rim.device.apps.internal.mms.verbs.MMSForwardVerb;
import net.rim.device.apps.internal.mms.verbs.MMSReplyVerb;

final class MMSMessageHotKeyProvider {
   public static final Object invokeHotkey(MMSMessageModelImpl message, Object context, int hotkeyID) {
      Verb verb = null;
      boolean enableMMS = MMSUtilities.isITPolicyEnabled();
      switch (hotkeyID) {
         case 148:
            if (message.isInbound() && enableMMS && MMSUtilities.canReply(message)) {
               verb = new MMSReplyVerb(message);
            }
            break;
         case 150:
            if (enableMMS && MMSUtilities.canForward(message)) {
               verb = new MMSForwardVerb(message, false);
            }
            break;
         case 152:
            long action = message.isOpened() ? -8629311385729242560L : 5803508244060051872L;
            verb = new MMSChangeStatusVerb(action, message);
      }

      return verb != null ? verb.invoke(context) : null;
   }
}
