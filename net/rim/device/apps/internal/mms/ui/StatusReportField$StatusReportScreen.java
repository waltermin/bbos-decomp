package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSStatusReport;
import net.rim.device.apps.internal.mms.resources.MMSResources;

class StatusReportField$StatusReportScreen extends MainScreen {
   private final StatusReportField this$0;

   public StatusReportField$StatusReportScreen(StatusReportField _1, MMSMessageModel message, Object context) {
      super(196608);
      this.this$0 = _1;
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
      vfm.deleteAll();
      if (_1._type == 74) {
         this.addDeliveryReports(vfm, message, context);
      } else {
         this.addReadReports(vfm, message, context);
      }

      this.add(vfm);
   }

   private void addDeliveryReports(VerticalFieldManager vfm, MMSMessageModel msg, Object ctx) {
      for (int i = msg.getDeliveryReportCount() - 1; i >= 0; i--) {
         MMSStatusReport report = msg.getDeliveryReport(i);
         if (report != null) {
            vfm.add(this.createField(report, ctx));
         }
      }
   }

   private void addReadReports(VerticalFieldManager vfm, MMSMessageModel msg, Object ctx) {
      for (int i = msg.getReadReportCount(); i >= 0; i--) {
         MMSStatusReport report = msg.getReadReport(i);
         if (report != null) {
            vfm.add(this.createField(report, ctx));
         }
      }
   }

   private Field createField(MMSStatusReport report, Object context) {
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object(36028797018963968L));
      if (report.getAddress() != null) {
         RIMModel address = StatusReportField.getAddressModel(report.getAddress());
         ContextObject ctx = ContextObject.clone(context);
         RecipientField f = new RecipientField("", address, ctx);
         f.setEditable(false);
         vfm.add(f);
      }

      DateField df = (DateField)(new Object(MMSResources.getString(81), report.getDate(), 36028801313931312L));
      df.setEditable(false);
      vfm.add(df);
      vfm.add(this.getStatusField(report.getStatus()));
      vfm.add((Field)(new Object()));
      return vfm;
   }

   private Field getStatusField(int id) {
      String str;
      if (this.this$0._type == 74) {
         switch (id) {
            case 127:
               str = " ";
               break;
            case 128:
            default:
               str = MMSResources.getString(82);
               break;
            case 129:
               str = MMSResources.getString(83);
               break;
            case 130:
               str = MMSResources.getString(85);
               break;
            case 131:
               str = MMSResources.getString(86);
               break;
            case 132:
               str = MMSResources.getString(128);
               break;
            case 133:
               str = MMSResources.getString(87);
               break;
            case 134:
               str = MMSResources.getString(84);
         }
      } else {
         switch (id) {
            case 127:
               str = " ";
               break;
            case 128:
            default:
               str = MMSResources.getString(75);
               break;
            case 129:
               str = MMSResources.getString(78);
         }
      }

      long style = 36028803461414912L;
      AutoTextEditField f = (AutoTextEditField)(new Object(MMSResources.getString(80), str, str.length(), style));
      return f;
   }
}
