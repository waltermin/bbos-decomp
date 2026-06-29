package net.rim.device.apps.internal.qm.peer;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.vm.Array;

final class ContentHandlerManager {
   private Hashtable _systemHandlers = (Hashtable)(new Object(2));
   private static final long GUID;
   private static ContentHandlerManager _instance;

   private ContentHandlerManager() {
   }

   static final ContentHandlerManager getInstance() {
      _instance = (ContentHandlerManager)ApplicationRegistry.getApplicationRegistry().get(5434758971540425216L);
      if (_instance == null) {
         _instance = new ContentHandlerManager();
         ApplicationRegistry.getApplicationRegistry().replace(5434758971540425216L, _instance);
      }

      return _instance;
   }

   final void registerSystemHandler(MessageHandler handler, String[] contentTypes) {
      synchronized (this._systemHandlers) {
         if (!this._systemHandlers.containsKey(handler) && this.validTypes(contentTypes)) {
            this._systemHandlers.put(handler, contentTypes);
         }
      }
   }

   final MessageHandler[] getSystemHandlers(String contentType) {
      MessageHandler[] handlers = new MessageHandler[0];
      Enumeration e = this._systemHandlers.keys();

      while (e.hasMoreElements()) {
         MessageHandler handler = (MessageHandler)e.nextElement();
         String[] contentTypes = (Object[])this._systemHandlers.get(handler);
         if (this.findMatch(contentType, contentTypes)) {
            Array.resize(handlers, handlers.length + 1);
            handlers[handlers.length - 1] = handler;
            break;
         }
      }

      return handlers.length > 0 ? handlers : null;
   }

   private final boolean validTypes(String[] contentTypes) {
      if (contentTypes == null) {
         return false;
      }

      for (int i = 0; i < contentTypes.length; i++) {
         String type = contentTypes[i];
         if (type == null) {
            return false;
         }

         int slash = type.indexOf(47);
         if (slash < 0) {
            return false;
         }

         if (type.substring(0, slash).length() < 1) {
            return false;
         }

         int wildcard = type.indexOf(42);
         if (wildcard >= 0 && (wildcard < slash || wildcard != type.length() - 1)) {
            return false;
         }
      }

      return true;
   }

   private final boolean findMatch(String contentType, String[] possibleMatches) {
      for (int i = 0; i < possibleMatches.length; i++) {
         int wildcard = possibleMatches[i].indexOf(42);
         if (wildcard >= 0) {
            if (contentType.startsWith(possibleMatches[i].substring(0, wildcard))) {
               return true;
            }
         } else if (contentType.equals(possibleMatches[i])) {
            return true;
         }
      }

      return false;
   }
}
