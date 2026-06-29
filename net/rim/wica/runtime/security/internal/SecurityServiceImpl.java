package net.rim.wica.runtime.security.internal;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.runtime.security.HandshakeException;
import net.rim.wica.runtime.security.HandshakeInfo;
import net.rim.wica.runtime.security.SecurityService;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.transport.handshake.VersionNotSupportedException;
import net.rim.wica.transport.security.Key;
import net.rim.wica.transport.security.KeyProvider;
import net.rim.wica.transport.security.SecurityProvider;
import net.rim.wica.transport.security.SequenceProvider;

public final class SecurityServiceImpl implements SecurityService, Serviceable, HandshakeHandler {
   private KeyProviderImpl _keyProvider;
   private SecurityProviderImpl _securityProvider = new SecurityProviderImpl();
   private SequenceProviderImpl _sequenceProvider;
   private EventService _eventService;
   private ServiceProvider _serviceProvider;
   private static final int SECURITY_MIN_VERSION;
   private static final int SECURITY_MAX_VERSION;
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$persistence$PersistenceService;

   @Override
   public final void setServices(ServiceProvider provider) {
      this._serviceProvider = provider;
      this._eventService = (EventService)provider.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      PersistenceService persistence = (PersistenceService)provider.getService(
         class$net$rim$wica$runtime$persistence$PersistenceService == null
            ? (class$net$rim$wica$runtime$persistence$PersistenceService = class$("net.rim.wica.runtime.persistence.PersistenceService"))
            : class$net$rim$wica$runtime$persistence$PersistenceService
      );
      this._keyProvider = new KeyProviderImpl(this._securityProvider, persistence);
      this._sequenceProvider = new SequenceProviderImpl(persistence);
   }

   @Override
   public final SecurityProvider getSecurityProvider() {
      return this._securityProvider;
   }

   @Override
   public final KeyProvider getKeyProvider() {
      return this._keyProvider;
   }

   @Override
   public final SequenceProvider getSequenceProvider() {
      return this._sequenceProvider;
   }

   @Override
   public final void register(HandshakeInfo params) {
      Logger.log("SHR");
      if (params.getSecurityVersion() == 0) {
         params.setSecurityVersion(this.getMaxSecurityVersion());
      }

      if (this.getMinSecurityVersion() > params.getSecurityVersion()) {
         throw new HandshakeException(new VersionNotSupportedException(), params);
      }

      this.createHandshake(params).register();
   }

   @Override
   public final void unregister(HandshakeInfo params) {
      Logger.log("SHU");
      this.createHandshake(params).unregister();
   }

   @Override
   public final int getMinSecurityVersion() {
      return Math.max(1, ITPolicy.getInteger(44, 2, 1));
   }

   @Override
   public final int getMaxSecurityVersion() {
      return 2;
   }

   @Override
   public final void registrationCompleted(HandshakeInfo params, Key[] keys) {
      Logger.log("SHR+");
      this._keyProvider.setSecurityInfo(params.getAGId(), params.getSecurityVersion(), keys, params.getResetState());
      this._sequenceProvider.add(params.getAGId(), params.getResetState());
      this._eventService.dispatchEvent(this, 403, params);
   }

   @Override
   public final void registrationFailed(HandshakeException e) {
      Logger.log(((StringBuffer)(new Object("SHR- "))).append(e.getCause()).toString());
      HandshakeInfo params = e.getHandshakeInfo();
      if (params.getSecurityVersion() > 1 && e.getCause() instanceof Object && "Hello".equals(e.getMessage())) {
         params.setSecurityVersion(1);

         try {
            this.register(params);
            return;
         } catch (HandshakeException ex) {
            e = ex;
         }
      }

      this._eventService.dispatchEvent(this, 404, e);
   }

   @Override
   public final void unregistrationCompleted(HandshakeInfo params) {
      Logger.log("SHU+");
      this._eventService.dispatchEvent(this, 405, params);
      this._keyProvider.wipeSecurityInfo(params.getAGId());
      this._sequenceProvider.remove(params.getAGId());
   }

   @Override
   public final void unregistrationFailed(HandshakeException e) {
      Logger.log(((StringBuffer)(new Object("SHU- "))).append(e.getCause()).toString());
      this._eventService.dispatchEvent(this, 406, e);
      this._keyProvider.cacheSecurityInfo(e.getHandshakeInfo().getAGId());
      this._sequenceProvider.remove(e.getHandshakeInfo().getAGId());
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final HandshakeProtocol createHandshake(HandshakeInfo params) {
      try {
         switch (params.getSecurityVersion()) {
            case 1:
               return new HandshakeProtocolV1(this, params, this._keyProvider.getPublicKey(), this._serviceProvider);
            default:
               return new HandshakeProtocolV2(this, params, this._serviceProvider);
         }
      } catch (Throwable var4) {
         throw new HandshakeException(e, params);
      }
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
