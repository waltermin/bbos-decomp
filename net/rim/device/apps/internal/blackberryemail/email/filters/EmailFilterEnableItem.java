package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.apps.api.ui.VerbMenuItem;

final class EmailFilterEnableItem extends VerbMenuItem {
   public EmailFilterEnableItem(EmailFilterCollectionListField listField) {
      super(EmailFilterEnableVerb.getInstance(listField), 0);
   }

   @Override
   public final int getPriority() {
      return this.getTarget().isMuddy() ? 100 + 1000 : 100 + 0;
   }
}
