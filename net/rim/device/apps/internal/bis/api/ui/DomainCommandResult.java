package net.rim.device.apps.internal.bis.api.ui;

import java.util.Hashtable;

public final class DomainCommandResult {
   private String _resultName;
   private String _error;
   private String _status;
   private Hashtable _paramsToForward;

   public DomainCommandResult(String resultName, String errorMessage, String statusMessage) {
      this._resultName = resultName;
      this._error = errorMessage;
      this._status = statusMessage;
   }

   public DomainCommandResult(String resultName, String errorMessage, String statusMessage, Hashtable paramsToForward) {
      this._resultName = resultName;
      this._error = errorMessage;
      this._status = statusMessage;
      this._paramsToForward = paramsToForward;
   }

   public final String getResultName() {
      return this._resultName;
   }

   public final String getError() {
      return this._error;
   }

   public final String getStatus() {
      return this._status;
   }

   public final void setStatus(String statusMessage) {
      this._status = statusMessage;
   }

   public final Hashtable getParamsToForward() {
      return this._paramsToForward;
   }
}
