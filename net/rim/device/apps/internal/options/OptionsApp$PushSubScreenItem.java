package net.rim.device.apps.internal.options;

import net.rim.device.apps.api.options.OptionsListItem;

final class OptionsApp$PushSubScreenItem extends OptionsListItem {
   long _groupId;
   private final OptionsApp this$0;

   public OptionsApp$PushSubScreenItem(OptionsApp _1, int displayId, long groupId) {
      super(net.rim.device.apps.internal.options.resources.OptionsResources.getString(displayId));
      this.this$0 = _1;
      this._groupId = groupId;
   }

   @Override
   protected final void open() {
      this.this$0.getGroup(this._groupId).pushScreen();
   }
}
