package net.rim.device.apps.internal.lbs;

import net.rim.device.api.bugreport.BugReport;
import net.rim.device.api.bugreport.BugReportable;

final class LBSApplication$LBSBugReportable implements BugReportable {
   private MapField _mapField;

   LBSApplication$LBSBugReportable(MapField mapField) {
      this._mapField = mapField;
   }

   @Override
   public final void populateBugInformation(BugReport bugReport) {
      StringBuffer buffer = bugReport.getDescription();
      int lat = this._mapField.getLatitude();
      int lon = this._mapField.getLongitude();
      int zoom = this._mapField.getZoom();
      buffer.append("\n\nContext:\n");
      String url = Utilities.createLbsUrl(lat, lon, zoom, null, null, null, null, null, null, null, null, null, null, null, null, null);
      buffer.append(url);
      buffer.append('\n');
   }
}
