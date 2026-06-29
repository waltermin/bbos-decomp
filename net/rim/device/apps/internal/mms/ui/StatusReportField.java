package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.resources.MMSResources;

final class StatusReportField extends VerticalFieldManager {
   private MMSMessageModel _message;
   private int _type;
   private int _reportCount;
   private Object _context;

   public StatusReportField(MMSMessageModel message, int labelID, Object context) {
      this._type = labelID;
      this._message = message;
      this._context = context;
      this._reportCount = this._type == 74 ? this._message.getDeliveryReportCount() : this._message.getReadReportCount();
      this.deleteAll();
      EditField field = new EditField(MMSResources.getString(this._type), " " + this._reportCount);
      field.setEditable(false);
      this.add(field);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this._reportCount > 0 && menu instanceof SystemEnabledMenu) {
         SystemEnabledMenu systemMenu = (SystemEnabledMenu)menu;
         Verb showDetailsVerb = new StatusReportField$1(this, 16978432, MMSResources.getResourceBundle(), 70);
         systemMenu.add(showDetailsVerb);
         systemMenu.setDefault(showDetailsVerb);
      }
   }

   private static final RIMModel getAddressModel(String str) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Factory phoneNumberFactory = (Factory)ar.waitFor(3797587162219887872L);
      Factory emailAddressFactory = (Factory)ar.waitFor(-2985347935260258684L);
      ContextObject context = new ContextObject();
      int pos = str.indexOf("/TYPE");
      if (pos >= 0) {
         String globalPhoneNumber = str.substring(0, pos).trim();
         context.put(253, globalPhoneNumber);
         return (RIMModel)phoneNumberFactory.createInstance(context);
      } else {
         context.put(253, str);
         return (RIMModel)emailAddressFactory.createInstance(context);
      }
   }
}
