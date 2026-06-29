package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ArgUtils;

public final class AddressBookSetupCommand implements DomainCommand {
   @Override
   public final DomainCommandResult run(Hashtable params) {
      Boolean synchAddressBook = ArgUtils.getBooleanValue(params, "synch_address_book");
      ClientSessionState.getInstance().setSynchAddressBook(synchAddressBook);
      return new DomainCommandResult("success", null, null);
   }
}
