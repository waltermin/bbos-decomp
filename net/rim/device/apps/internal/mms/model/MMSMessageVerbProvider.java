package net.rim.device.apps.internal.mms.model;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.ForwardAsVerb;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.verbs.MMSChangeStatusVerb;
import net.rim.device.apps.internal.mms.verbs.MMSForwardVerb;
import net.rim.device.apps.internal.mms.verbs.MMSOpenVerb;
import net.rim.device.apps.internal.mms.verbs.MMSReplyVerb;
import net.rim.device.apps.internal.mms.verbs.MMSRequestContentVerb;
import net.rim.device.apps.internal.mms.verbs.MMSResendVerb;
import net.rim.vm.Array;

final class MMSMessageVerbProvider {
   public static final Verb getVerbs(MMSMessageModelImpl message, Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      } else {
         Verb defaultVerb = null;
         if (ContextObject.getFlag(context, 37)) {
            defaultVerb = getMMSMessageVerbs(message, context, verbs);
            appendVerbs(verbs, VerbRepository.getVerbRepository(806618162841601920L).getVerbs(6099112494809837535L));
            return defaultVerb;
         } else {
            return getMessageListVerbs(message, context, verbs);
         }
      }
   }

   private static final Verb getMessageListVerbs(MMSMessageModelImpl message, Object context, Verb[] verbs) {
      Verb defaultVerb = null;
      getMMSMessageVerbs(message, context, verbs);
      int numberOfVerbsAdded = verbs.length;
      if (!MMSUtilities.isPhoneNumber(message.getPayload().getSender())) {
      }

      defaultVerb = new MMSOpenVerb(message);
      Array.resize(verbs, numberOfVerbsAdded + 1);
      verbs[numberOfVerbsAdded++] = defaultVerb;
      return defaultVerb;
   }

   private static final Verb getMMSMessageVerbs(MMSMessageModelImpl message, Object context, Verb[] verbs) {
      Verb defaultVerb = null;
      boolean inView = ContextObject.getFlag(context, 37);
      boolean defaultMenu = !ContextObject.getFlag(context, 81);
      boolean enableMMS = MMSUtilities.isITPolicyEnabled();
      Array.resize(verbs, 10);
      int numberOfVerbsAdded = 0;
      if (!message.isInbound()) {
         if (message.getStatus() != 67108863 && enableMMS && MMSUtilities.canSend() && !message.isDraft() && (defaultMenu || !message.isSuccessfullySent())) {
            defaultVerb = new MMSResendVerb(message);
            verbs[numberOfVerbsAdded++] = defaultVerb;
         }
      } else {
         if (enableMMS && MMSUtilities.canReply(message)) {
            defaultVerb = new MMSReplyVerb(message);
            verbs[numberOfVerbsAdded++] = defaultVerb;
         }

         if (enableMMS && MMSUtilities.canReplyToAll(message)) {
            verbs[numberOfVerbsAdded++] = new MMSReplyVerb(message, true);
         }

         if (defaultMenu) {
            if (message.isOpened()) {
               verbs[numberOfVerbsAdded++] = new MMSChangeStatusVerb(602450, 4, -8629311385729242560L, message);
            } else {
               verbs[numberOfVerbsAdded++] = new MMSChangeStatusVerb(602448, 5, 5803508244060051872L, message);
            }
         }

         if (inView && MMSUtilities.canRequestContent(message)) {
            defaultVerb = new MMSRequestContentVerb(message);
            verbs[numberOfVerbsAdded++] = defaultVerb;
         }
      }

      if (!message.isDraft()) {
         if (enableMMS && MMSUtilities.canForward(message)) {
            Verb forwardVerb = new MMSForwardVerb(message, false);
            if (message.isInbound() || message.isSuccessfullySent()) {
               defaultVerb = forwardVerb;
            }

            verbs[numberOfVerbsAdded++] = forwardVerb;
            if (defaultMenu) {
               ForwardAsVerb forwardAsVerb = new ForwardAsVerb(message);
               if (forwardAsVerb.canInvoke(null)) {
                  verbs[numberOfVerbsAdded++] = forwardAsVerb;
               }
            }
         }

         if (!message.isSaved() && defaultMenu) {
            verbs[numberOfVerbsAdded++] = new MMSChangeStatusVerb(602480, 6, -8570780006855731756L, message);
         }
      }

      Array.resize(verbs, numberOfVerbsAdded);
      return defaultVerb;
   }

   private static final void appendVerbs(Verb[] target, Verb[] source) {
      if (source != null && source.length > 0) {
         int tlen = target.length;
         int slen = source.length;
         Array.resize(target, tlen + slen);
         System.arraycopy(source, 0, target, tlen, slen);
      }
   }
}
