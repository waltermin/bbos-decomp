package net.rim.device.apps.internal.mms.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;

public final class ComposeOptionsScreen extends MainScreen {
   private PriorityChoiceField _priorityField;
   private ReportOptionField _reportOptionField;
   private Hashtable _result;

   public ComposeOptionsScreen(MMSPayloadModel payload, int estimatedDataSize) {
      String title = EmailResources.getString(128);
      this.setTitle(new LabelField(title));
      this.addMenuItem(MenuItem.getPrefab(15));
      this._priorityField = new PriorityChoiceField(payload.getAttribute("x-mms-priority"));
      this._reportOptionField = new ReportOptionField(payload.getAttribute("x-mms-delivery-report"), payload.getAttribute("x-mms-read-report"));
      this.add(new TotalAttachmentDataSizeField(estimatedDataSize));
      this.add(this._priorityField);
      this.add(this._reportOptionField);
   }

   @Override
   public final void save() {
      this._result = new Hashtable();
      this._result.put("x-mms-priority", this._priorityField.getPriority());
      this._result.put("x-mms-delivery-report", this._reportOptionField.getDeliveryReport());
      this._result.put("x-mms-read-report", this._reportOptionField.getReadReport());
   }

   public final Hashtable getResult() {
      return this._result;
   }
}
