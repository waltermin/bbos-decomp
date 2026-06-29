package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class AddFilterCall implements XMLCall, BISServiceConstants {
   private String _filterName;
   private Boolean _sendAlert;
   private Boolean _levelOne;
   private Boolean _headersOnly;
   private String _filterOperator;
   private String _filterValue;

   public AddFilterCall(String filterName, Boolean sendAlert, Boolean levelOne, Boolean headersOnly, String filterOperator, String filterValue) {
      this._filterName = filterName;
      this._sendAlert = sendAlert;
      this._levelOne = levelOne;
      this._headersOnly = headersOnly;
      this._filterOperator = filterOperator;
      this._filterValue = filterValue;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "filter");
      XMLUtils.writeSimpleElement(ostream, "name", this._filterName);
      XMLUtils.writeSimpleElement(ostream, "sendAlert", this._sendAlert);
      XMLUtils.writeSimpleElement(ostream, "levelOne", this._levelOne);
      XMLUtils.writeSimpleElement(ostream, "headersOnly", this._headersOnly);
      XMLUtils.writeSimpleElement(ostream, "filterOperator", this._filterOperator);
      XMLUtils.writeSimpleElement(ostream, "filterValue", this._filterValue);
      XMLUtils.endElement(ostream, "filter");
   }
}
