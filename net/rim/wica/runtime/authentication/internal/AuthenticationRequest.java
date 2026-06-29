package net.rim.wica.runtime.authentication.internal;

import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.component.PopupDialogClosedListener;
import net.rim.wica.runtime.messaging.ReadableDataStream;
import net.rim.wica.runtime.ui.internal.AuthenticationDialog;

final class AuthenticationRequest implements PopupDialogClosedListener {
   private long _authenticationId;
   private int _attempt;
   private int _scheme;
   private long _wicletId;
   private int _type;
   private String _url;
   private String _service;
   private byte[] _challenge;
   private AuthenticationService _authenticationService;

   AuthenticationRequest(AuthenticationService authenticationService, ReadableDataStream stream) {
      this._attempt = stream.readInt();
      this._authenticationId = stream.readLong();
      this._scheme = stream.readInt();
      this._challenge = stream.readBlob();
      this._type = stream.readInt();
      this._url = stream.readString();
      this._service = stream.readString();
      this._wicletId = stream.readLong();
      this._authenticationService = authenticationService;
   }

   final long getAuthenticationId() {
      return this._authenticationId;
   }

   final int getAttempt() {
      return this._attempt;
   }

   final int getScheme() {
      return this._scheme;
   }

   final long getWicletId() {
      return this._wicletId;
   }

   final int getType() {
      return this._type;
   }

   final String getUrl() {
      return this._url;
   }

   final String getService() {
      return this._service;
   }

   final byte[] getChallenge() {
      return this._challenge;
   }

   @Override
   public final void dialogClosed(PopupDialog dialog, int closeReason) {
      this._authenticationService.completeRequest(this, (AuthenticationDialog)dialog);
   }
}
