package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.resources.MMSResources;

public final class MMSChangeStatusVerb extends Verb {
   private MMSMessageModel _message;
   private long _action;
   private int _resourceId;

   public MMSChangeStatusVerb(long action, MMSMessageModel message) {
      this(0, 0, action, message);
   }

   public MMSChangeStatusVerb(int menuOrdering, int resourceIdWithinMMSResources, long action, MMSMessageModel message) {
      super(menuOrdering);
      this._resourceId = resourceIdWithinMMSResources;
      this._action = action;
      this._message = message;
   }

   @Override
   public final Object invoke(Object context) {
      boolean wasSaved = this._message.isSaved();
      this._message.perform(this._action, context);
      if (this._message.isSaved() && !wasSaved) {
         MessageListOptions options = MessageListOptions.getOptions();
         if (options.isKeepSavedMessagesDurationDefinedByItPolicy()) {
            int durationFromItPolicy = options.getKeepSavedMessagesDuration();
            StringBuffer sb = new StringBuffer();
            sb.append(CommonResources.getString(9167));
            sb.append(" ");
            if (durationFromItPolicy == -1) {
               sb.append(CommonResources.getString(9145));
            } else {
               sb.append(durationFromItPolicy);
               sb.append(" ");
               sb.append(CommonResources.getString(9144));
            }

            Status.show(sb.toString());
         } else {
            Status.show(CommonResources.getString(5001));
         }

         Status.show(CommonResources.getString(5001));
      }

      return null;
   }

   @Override
   public final String toString() {
      return MMSResources.getString(this._resourceId);
   }
}
