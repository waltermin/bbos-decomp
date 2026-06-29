package net.rim.device.apps.internal.bis.data;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

final class UserInfo$MailboxComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      Mailbox firstMailbox = (Mailbox)o1;
      Mailbox secondMailbox = (Mailbox)o2;
      return StringUtilities.compareToIgnoreCase(firstMailbox.getDescription(), secondMailbox.getDescription());
   }
}
