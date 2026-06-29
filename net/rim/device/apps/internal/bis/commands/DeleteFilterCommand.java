package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.data.Filter;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class DeleteFilterCommand implements DomainCommand {
   public static final String PARAM_FILTER_ID = "filterid";
   public static final String PARAM_FILTER_NAME = "filtername";

   @Override
   public final DomainCommandResult run(Hashtable params) {
      String filterId = (String)params.get("filterid");
      String filterName = (String)params.get("filtername");
      Mailbox mailbox = ClientSessionState.getInstance().getMailboxToModify();
      Filter filter = mailbox.getFilter(filterId);
      if (filter == null) {
         throw new Object(((StringBuffer)(new Object("Unknown filter specified: "))).append(filterName).toString());
      }

      ClientSessionState.getInstance().setFilterToModify(filter);
      return new DomainCommandResult("success", null, null);
   }
}
