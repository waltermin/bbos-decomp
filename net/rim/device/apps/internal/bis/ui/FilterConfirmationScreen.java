package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.data.Filter;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.internal.ui.component.AnimatedBitmapField;
import net.rim.device.internal.ui.component.HorizontalSpacerField;

public final class FilterConfirmationScreen extends BasicScreen {
   @Override
   public final void refresh(Hashtable screenParams) {
      Filter filter = ClientSessionState.getInstance().getFilterToModify();
      Mailbox mailboxToDelete = ClientSessionState.getInstance().getMailboxToModify();
      this.setTitle(ApplicationResources.getString(292));
      String deleteConfirmText = MessageFormat.format(ApplicationResources.getString(296), new String[]{filter.getName()});
      EncodedImage exclamationImage = ThemeManager.getActiveTheme().getImage("dialog_exclamation");
      AnimatedBitmapField exclamationIcon = new AnimatedBitmapField(exclamationImage, 1000, 0);
      HorizontalSpacerField hsf = new HorizontalSpacerField(5);
      this.addContentFieldRow(new Field[]{exclamationIcon, hsf, new LabelField(ApplicationResources.getString(293))});
      this.addContentLineBreak();
      this.addContentField(new LabelField(ApplicationResources.getString(294)));
      this.addContentLineBreak();
      this.addContentField(new LabelField(ApplicationResources.getString(295)));
      Button noButton = new Button(ApplicationResources.getString(31));
      Button yesButton = new Button(ApplicationResources.getString(32));
      this.addButtonBarButtons(new Button[]{noButton, yesButton}, false, 1);
      CommandEvent yesEvent = new CommandEvent(32, 27, new String[]{"filterconfirm"});
      this.attachEventToField(yesButton, yesEvent);
      BackEvent backEvent = new BackEvent(31);
      this.attachEventToField(noButton, backEvent);
      this.setDefaultEvent(yesEvent);
      this.setCloseEvent(backEvent);
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      inputMap.put("filterconfirm", "true");
      return true;
   }
}
