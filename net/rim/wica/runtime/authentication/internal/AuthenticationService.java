package net.rim.wica.runtime.authentication.internal;

import net.rim.device.api.system.Application;
import net.rim.wica.runtime.authentication.internal.credentialstore.CredentialStore;
import net.rim.wica.runtime.authentication.internal.credentialstore.Credentials;
import net.rim.wica.runtime.authentication.protocols.NTLM;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.management.ManagementService;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.MessageConsumer;
import net.rim.wica.runtime.messaging.MessageException;
import net.rim.wica.runtime.messaging.MessagingException;
import net.rim.wica.runtime.messaging.MessagingService;
import net.rim.wica.runtime.messaging.ReadableDataStream;
import net.rim.wica.runtime.messaging.WritableDataStream;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.service.Startable;
import net.rim.wica.runtime.ui.internal.AuthenticationDialog;

public final class AuthenticationService implements MessageConsumer, Startable, Serviceable {
   private MessagingService _messaging;
   private ManagementService _management;
   private CredentialStore _store;
   private static final int SCHEME_BASIC = 0;
   private static final int SCHEME_NTLM = 1;
   private static final int TYPE_PROXY = 1;
   private static final int TYPE_TARGET = 0;
   private static final String CLEAR_CREDENTIALS = "outClearCredentials";
   private static final String CLEAR_ALL_CREDENTIALS = "outClearAllCredentials";
   static Class class$net$rim$wica$runtime$messaging$MessagingService;
   static Class class$net$rim$wica$runtime$management$ManagementService;

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void completeRequest(AuthenticationRequest request, AuthenticationDialog authenticationDialog) {
      if (authenticationDialog.cancelled()) {
         this.sendAbort(request.getAuthenticationId());
      } else {
         String username = authenticationDialog.getUsername();
         String domain = authenticationDialog.getDomain();
         String password = authenticationDialog.getPassword();

         byte[] secretToken;
         try {
            secretToken = request.getScheme() == 1 ? NTLM.getLMHash(password) : password.getBytes("UTF-8");
         } catch (Throwable var9) {
            Logger.log(this.toString(), e.getMessage(), 2);
            return;
         }

         this.authenticate(request, username, domain, secretToken);
         this._store
            .storeCredentials(
               request.getWicletId(), request.getUrl(), request.getScheme(), username, domain, secretToken, authenticationDialog.rememberCredentials()
            );
      }
   }

   @Override
   public final void start() {
      this._messaging.registerSystemMessageConsumer(new int[]{512, -804651006, 803, 805}, this);
      this._messaging.registerServiceMessageConsumer("local://AuthenticationService", this);
   }

   @Override
   public final void stop() {
      this._messaging.deregisterSystemMessageConsumer(this);
      this._messaging.deregisterServiceMessageConsumer(this);
   }

   @Override
   public final Message processMessage(Message message) {
      if (message.getMessageCode() == 512) {
         this.processAuthenticationRequest(message);
         return null;
      }

      if (message.getMessageName().equals("outClearCredentials")) {
         this.processClearCredentialsRequest(message);
         return null;
      }

      if (message.getMessageName().equals("outClearAllCredentials")) {
         this.clearCredentials();
      }

      return null;
   }

   @Override
   public final void setServices(ServiceProvider provider) {
      this._messaging = (MessagingService)provider.getService(
         class$net$rim$wica$runtime$messaging$MessagingService == null
            ? (class$net$rim$wica$runtime$messaging$MessagingService = class$("net.rim.wica.runtime.messaging.MessagingService"))
            : class$net$rim$wica$runtime$messaging$MessagingService
      );
      this._management = (ManagementService)provider.getService(
         class$net$rim$wica$runtime$management$ManagementService == null
            ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
            : class$net$rim$wica$runtime$management$ManagementService
      );
      this._store = new CredentialStore(provider);
   }

   private final void processClearCredentialsRequest(Message m) {
      try {
         ReadableDataStream s = m.openReadableDataStream();
         long id = Long.parseLong(s.readString());
         this._store.clearCredentials(id, true);
      } catch (MessageException e) {
         Logger.log(this.toString(), ((StringBuffer)(new Object("Clear credentials failed: "))).append(e.getMessage()).toString(), 2);
      }
   }

   private final void processAuthenticationRequest(Message m) {
      AuthenticationRequest request;
      try {
         request = new AuthenticationRequest(this, m.openReadableDataStream());
      } catch (MessageException e) {
         Logger.log(this.toString(), ((StringBuffer)(new Object("Authentication failed: "))).append(e.getMessage()).toString(), 2);
         return;
      }

      if (!this.isSchemeSupported(request.getScheme())) {
         this.sendAbort(request.getAuthenticationId());
      } else {
         Object ticket = this._store.getAccessTicket();
         if (ticket == null) {
            this.sendAbort(request.getAuthenticationId());
         } else {
            Credentials credentials = (Credentials)(request.getAttempt() == 1
               ? this._store.getCredentials(request.getWicletId(), request.getUrl())
               : this._store.removeCredentials(request.getWicletId(), request.getUrl()));
            if (credentials == null || credentials.getScheme() != request.getScheme()) {
               this.promptForCredentials(request);
            } else if (request.getAttempt() > 1) {
               this.promptForCredentials(request, credentials.getUsername(), credentials.getDomain());
            } else {
               this.authenticate(request, credentials.getUsername(), credentials.getDomain(), credentials.getSecretToken());
            }
         }
      }
   }

   private final void promptForCredentials(AuthenticationRequest request) {
      this.promptForCredentials(request, null, null);
   }

   private final void promptForCredentials(AuthenticationRequest request, String username, String domain) {
      int scheme = request.getScheme() == 0 ? 1 : 2;
      AuthenticationDialog dialog = new AuthenticationDialog(
         username, domain, null, request.getService(), request.getUrl(), request.getType() == 1, scheme, 33554432
      );
      dialog.setPopupDialogClosedListener(request);
      Application.getApplication().invokeLater(new AuthenticationService$1(this, dialog));
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void authenticate(AuthenticationRequest request, String username, String domain, byte[] secretToken) {
      if (request.getScheme() == 0) {
         this.sendCredentials(request.getAuthenticationId(), username, domain, secretToken);
      } else {
         byte[] lmResponse;
         try {
            lmResponse = NTLM.getLMResponse(secretToken, request.getChallenge());
         } catch (Throwable var8) {
            Logger.log(this.toString(), ((StringBuffer)(new Object("Authentication failed: "))).append(e.getMessage()).toString(), 2);
            return;
         }

         this.sendCredentials(request.getAuthenticationId(), username, domain, lmResponse);
      }
   }

   private final void sendAbort(long authenticationId) {
      Message m = this._messaging.createMessageInstance();
      m.setMessageCode(514);
      WritableDataStream ws = m.openWritableDataStream();
      ws.writeLong(authenticationId);
      this.sendAuthenticationResponse(m);
   }

   private final void sendCredentials(long authenticationId, String username, String domain, byte[] response) {
      Message m = this._messaging.createMessageInstance();
      m.setMessageCode(513);
      WritableDataStream ws = m.openWritableDataStream();
      ws.writeLong(authenticationId);
      ws.writeBlob(response);
      ws.writeString(username);
      ws.writeString(domain);
      this.sendAuthenticationResponse(m);
   }

   private final void sendAuthenticationResponse(Message m) {
      m.setDestinationType(0);
      m.setAGID(this._management.getRuntimeInfo().getDefaultAGInfo().getAgID());

      try {
         this._messaging.sendMessage(m);
      } catch (MessagingException e) {
         Logger.log(this.toString(), ((StringBuffer)(new Object("Authentication failed: "))).append(e.getMessage()).toString(), 2);
      }
   }

   private final void clearCredentials() {
      this._store.clearCredentials(true);
   }

   private final boolean isSchemeSupported(int scheme) {
      return scheme == 0 || scheme == 1;
   }

   @Override
   public final String toString() {
      return "MDS Runtime Authentication Service";
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
