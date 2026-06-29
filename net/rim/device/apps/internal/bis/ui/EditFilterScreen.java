package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.data.Filter;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class EditFilterScreen extends AbstractFilterScreen {
   private Filter _filter;

   @Override
   public final void refresh(Hashtable screenParams) {
      this.eraseScreenData();
      Mailbox mailbox = ClientSessionState.getInstance().getMailboxToModify();
      this._filter = ClientSessionState.getInstance().getFilterToModify();
      this.setFilterId(this._filter.getId());
      String title = MessageFormat.format(ApplicationResources.getString(150), new Object[]{this._filter.getName()});
      this.setTitle(title);
      this.addHeaderInfo(this._filter.getName(), mailbox.getEmail());
      this.addFilterOperator(mailbox, this._filter.getOperator());
      this.addContainsArea(this._filter.getValue());
      this.addAction(this._filter.getSendAlert(), this._filter.getHeadersOnly(), this._filter.getLevelOne());
      Button cancelButton = new Button(ApplicationResources.getString(28));
      Button saveButton = new Button(ApplicationResources.getString(29));
      this.addButtonBarButtons(new Button[]{cancelButton, saveButton}, false, 1);
      LinkEvent cancelEvent = new LinkEvent(28, 43);
      this.attachEventToField(cancelButton, cancelEvent);
      CommandEvent saveEvent = new CommandEvent(
         29, 27, new String[]{"filtername", "sendalert", "headersonly", "levelone", "filteroperator", "filtervalue", "filterid"}
      );
      this.attachEventToField(saveButton, saveEvent);
      this.setDefaultEvent(saveEvent);
      this.setCloseEvent(cancelEvent);
      this.setHelp("217633.wml");
   }
}
