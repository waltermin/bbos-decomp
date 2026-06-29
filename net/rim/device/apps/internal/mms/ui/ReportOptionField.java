package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.apps.internal.mms.resources.MMSResources;

public final class ReportOptionField extends VerticalFieldManager {
   private BooleanChoiceField _delivery;
   private BooleanChoiceField _read;
   private String _deliveryReport;
   private String _readReport;

   public ReportOptionField(String deliveryReport, String readReport) {
      this._deliveryReport = deliveryReport;
      this._readReport = readReport;
      if (!MMSClientServiceBook.isLockedOption(64)) {
         this._delivery = (BooleanChoiceField)(new Object(
            ((StringBuffer)(new Object())).append(MMSResources.getString(71)).append(": ").toString(), 0, MMSUtilities.parseInt(deliveryReport, 129) == 128
         ));
         this.add(this._delivery);
      }

      if (!MMSClientServiceBook.isLockedOption(128)) {
         this._read = (BooleanChoiceField)(new Object(
            ((StringBuffer)(new Object())).append(MMSResources.getString(72)).append(": ").toString(), 0, MMSUtilities.parseInt(readReport, 129) == 128
         ));
         this.add(this._read);
      }
   }

   public final String getDeliveryReport() {
      if (this._delivery != null) {
         int choice = this._delivery.isAffirmative() ? 128 : 129;
         return Integer.toString(choice);
      } else {
         return this._deliveryReport;
      }
   }

   public final String getReadReport() {
      if (this._read != null) {
         int choice = this._read.isAffirmative() ? 128 : 129;
         return Integer.toString(choice);
      } else {
         return this._readReport;
      }
   }
}
