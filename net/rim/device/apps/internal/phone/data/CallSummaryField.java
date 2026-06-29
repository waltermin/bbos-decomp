package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.quickcontact.QuickContactUtil;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberServices;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.VerticalSpacerField;

final class CallSummaryField extends VerticalFieldManager implements ListFieldCallback {
   private CallSummaryInfo _summaryInfo;
   private PhoneCallModelImpl _call;
   private int _phoneNumberCount;
   private ListField _listField;
   private int _numLines;
   static final int VERTICAL_SPACER_FIELD_HEIGHT = 2;
   static final int VERTICAL_PADDING = 4;
   private static final int NUM_LIST_FIELD_ROWS = 3;

   CallSummaryField(CallSummaryInfo summaryInfo, PhoneCallModelImpl callLog) {
      this._summaryInfo = summaryInfo;
      this._call = callLog;
      Field callerIDField = null;
      LabelField label = null;
      if (!this._summaryInfo.haveAddress()) {
         char sdKey = this._summaryInfo.getSpeedDialKey();
         if (sdKey != 0) {
            label = new LabelField(this._summaryInfo.getCallerIDString(), 64);
            label.setPosition(2);
            callerIDField = new SpeedDialCallerIDField(label, sdKey);
         }
      }

      if (callerIDField == null) {
         label = new LabelField(this._summaryInfo.getCallerIDString(), 64);
         label.setPosition(2);
         callerIDField = label;
      }

      this.add(new VerticalSpacerField(2));
      this.add(callerIDField);
      this._numLines++;
      if (!this._summaryInfo.displayCompanyName() && this._summaryInfo.getPersonName() != null) {
         String companyName = this._summaryInfo.getCompanyName();
         if (companyName != null) {
            label = new LabelField(companyName, 64);
            label.setPosition(2);
            this.add(label);
            this._numLines++;
         }
      }

      this.add(new VerticalSpacerField(2));
      this.add(new SeparatorField());
      this._phoneNumberCount = this._summaryInfo.getPhoneNumberCount();
      int numListRows;
      if (this._phoneNumberCount == 0) {
         numListRows = 2;
      } else {
         numListRows = 3;
      }

      if (PhoneUtilities.getAllLineIds().length > 1) {
         numListRows++;
      }

      this._numLines += numListRows;
      this._listField = new ListField(numListRows, 36028797018963968L);
      this._listField.setCallback(this);
      this.add(this._listField);
      this.update(callLog);
   }

   final int getNumLines() {
      return this._numLines;
   }

   final void update(PhoneCallModelImpl call) {
      this._call = call;
      this.invalidate();
   }

   private final void paintNumber(Graphics g, int y, int width) {
      CallerIDInfo cidi = (CallerIDInfo)this._call.getCallerIDInfo();
      PhoneNumberModel num = (PhoneNumberModel)cidi.getNumber();
      int type = num.getType();
      char speedDialKey = this._summaryInfo.getSpeedDialKey(type);
      if (speedDialKey != 0) {
         int height = g.getFont().getHeight();
         int keyWidth = height;
         int x = Display.getWidth() - keyWidth;
         ContextObject context = new ContextObject();
         context.put(9045827404276417370L, this);
         QuickContactUtil.paintHotkey(speedDialKey, g, x, 0, width, height, 5, context);
         width -= keyWidth - 2;
      }

      String numberString = this._summaryInfo.getNumberString(type);
      if (numberString != null) {
         numberString = PhoneNumberServices.convertForDisplayWithExtension(numberString, false);
         g.drawText(numberString, 0, y, 64, width);
      }
   }

   private final void paintCallType(Graphics g, int y, int width) {
      String callTypeString = MessageFormat.format(PhoneResources.getString(253), new String[]{this._call.getTypeString()});
      g.drawText(callTypeString, 0, y, 64, width);
   }

   private final void paintStatusField(Graphics g, int y, int width) {
      StringBuffer buf = null;
      byte type = this._call.getType();
      if (type != 5 && type != 6) {
         if (this._call.getElapsedTime() > 0) {
            buf = this._call.getDurationString(true);
         } else {
            int errorCode = this._call.getErrorCode();
            if (errorCode == 0) {
               buf = new StringBuffer();
               buf.append(PhoneResources.getString(169));
               buf.append(PhoneResources.getString(123));
            } else if (this._call.getType() == 1) {
               buf = new StringBuffer();
               int labelId = errorCode == 1 ? 169 : 170;
               buf.append(PhoneResources.getString(labelId));
               buf.append(PhoneUtilities.getCallFailureErrorString(errorCode));
            }
         }

         if (buf != null && buf.length() > 0) {
            g.drawText(buf, 0, buf.length(), 0, y, 64, width);
         }
      } else {
         buf = new StringBuffer();
         buf.append(PhoneResources.getString(6020));
         buf.append(this._call.getElapsedTime());
         g.drawText(buf, 0, buf.length(), 0, y, 64, width);
      }
   }

   private final void paintLineDescription(Graphics g, int y, int width) {
      g.drawText(PhoneUtilities.getLineDescription(this._call.getLineId()), 0, y, 64, width);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics g, int index, int y, int width) {
      if (this._phoneNumberCount == 0) {
         switch (index) {
            case -1:
               break;
            case 0:
            default:
               this.paintCallType(g, y, width);
               return;
            case 1:
               this.paintStatusField(g, y, width);
               return;
            case 2:
               this.paintLineDescription(g, y, width);
               return;
         }
      } else {
         switch (index) {
            case -1:
               break;
            case 0:
            default:
               this.paintNumber(g, y, width);
               return;
            case 1:
               this.paintCallType(g, y, width);
               return;
            case 2:
               this.paintStatusField(g, y, width);
               return;
            case 3:
               this.paintLineDescription(g, y, width);
         }
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return null;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return 0;
   }
}
