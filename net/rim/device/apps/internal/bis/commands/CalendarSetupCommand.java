package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ArgUtils;

public final class CalendarSetupCommand implements DomainCommand {
   @Override
   public final DomainCommandResult run(Hashtable params) {
      Boolean synchCalendar = ArgUtils.getBooleanValue(params, "synch_calendar");
      ClientSessionState.getInstance().setSynchCalendar(synchCalendar);
      String infoMessage = null;
      return !ClientSessionState.getInstance().getSynchAddressBook() && !ClientSessionState.getInstance().getSynchCalendar()
         ? new DomainCommandResult("synchNotEnabled", null, null)
         : new DomainCommandResult("synchEnabled", null, null);
   }
}
