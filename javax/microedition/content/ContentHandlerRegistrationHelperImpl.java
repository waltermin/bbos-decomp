package javax.microedition.content;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.internal.content.ContentHandlerRegistrationHelper;
import net.rim.device.internal.system.MIDletSecurity;

class ContentHandlerRegistrationHelperImpl extends ContentHandlerRegistrationHelper {
   protected ContentHandlerRegistrationHelperImpl() {
   }

   static void register() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ContentHandlerRegistrationHelperImpl instance = (ContentHandlerRegistrationHelperImpl)ar.get(-352407102385872585L);
      if (instance == null) {
         instance = new ContentHandlerRegistrationHelperImpl();
         ar.put(-352407102385872585L, instance);
      }
   }

   @Override
   public int verifyJadAttributes(Hashtable jadAttributes, boolean upgrade) {
      int handlerIndex = 1;
      if (!jadAttributes.containsKey("MicroEdition-Handler-" + handlerIndex)) {
         return 900;
      }

      Hashtable installedHandlers = new Hashtable();
      String handlerAttribute = (String)jadAttributes.get("MicroEdition-Handler-" + handlerIndex);
      if (handlerAttribute != null && handlerAttribute.length() != 0) {
         while (handlerAttribute != null) {
            int comma = handlerAttribute.indexOf(44);
            String classname = comma == -1 ? handlerAttribute.trim() : handlerAttribute.substring(0, comma).trim();
            int nameLen = classname.length();
            if (nameLen == 0) {
               return 939;
            }

            for (int i = 0; i < nameLen; i++) {
               if (classname.charAt(i) == ' ') {
                  return 939;
               }
            }

            handlerAttribute = comma == -1 ? null : handlerAttribute.substring(comma + 1);
            String[] types = new String[0];
            String[] suffixes = new String[0];
            String[] actions = new String[0];
            String[] locales = new String[0];
            handlerAttribute = parseHandlerValues(handlerAttribute, types);
            handlerAttribute = parseHandlerValues(handlerAttribute, suffixes);
            handlerAttribute = parseHandlerValues(handlerAttribute, actions);
            handlerAttribute = parseHandlerValues(handlerAttribute, locales);
            int numLocales = locales.length;
            if (numLocales > 0) {
               for (int i = 0; i < numLocales; i++) {
                  String rawLocalizedActions = (String)jadAttributes.get("MicroEdition-Handler-" + handlerIndex + "-" + locales[i]);
                  if (rawLocalizedActions == null || rawLocalizedActions.length() == 0) {
                     return 939;
                  }

                  StringTokenizer tokens = new StringTokenizer(rawLocalizedActions, ',');
                  int numActions = tokens.countTokens();
                  if (numActions != actions.length) {
                     return 939;
                  }
               }
            }

            String ID = (String)jadAttributes.get("MicroEdition-Handler-" + handlerIndex + "-" + "ID");
            if (ID != null && ID.length() != 0) {
               try {
                  RegistryImpl.verifyCharacters(ID);
               } catch (IllegalArgumentException iae) {
                  return 939;
               }
            } else {
               ID = (String)jadAttributes.get("MIDlet-Vendor") + "-" + (String)jadAttributes.get("MIDlet-Name") + "-" + classname;
               ID = ID.replace(' ', '_');
            }

            try {
               RegistryImpl.verifyID(ID, classname, upgrade);
               Enumeration e = installedHandlers.keys();

               while (e.hasMoreElements()) {
                  String currClassname = (String)e.nextElement();
                  if (classname.equals(currClassname)) {
                     return 938;
                  }

                  String currID = (String)installedHandlers.get(currClassname);
                  if (currID.startsWith(ID) || ID.startsWith(currID)) {
                     return 938;
                  }
               }
            } catch (ContentHandlerException che) {
               return 938;
            }

            installedHandlers.put(classname, ID);
            handlerAttribute = (String)jadAttributes.get("MicroEdition-Handler-" + ++handlerIndex);
         }

         return 900;
      } else {
         return 939;
      }
   }

   @Override
   public void registerContentHandlers(int moduleHandle) {
      int handlerIndex = 1;
      String handlerAttribute = ContentHandlerUtilities.getStringValue("MicroEdition-Handler-" + handlerIndex, moduleHandle);
      boolean accessGranted = false;

      while (handlerAttribute != null) {
         int comma = handlerAttribute.indexOf(44);
         String classname = comma == -1 ? handlerAttribute.trim() : handlerAttribute.substring(0, comma).trim();
         handlerAttribute = comma == -1 ? null : handlerAttribute.substring(comma + 1);
         String[] types = new String[0];
         String[] suffixes = new String[0];
         String[] actions = new String[0];
         String[] locales = new String[0];
         handlerAttribute = parseHandlerValues(handlerAttribute, types);
         handlerAttribute = parseHandlerValues(handlerAttribute, suffixes);
         handlerAttribute = parseHandlerValues(handlerAttribute, actions);
         handlerAttribute = parseHandlerValues(handlerAttribute, locales);
         int numLocales = locales.length;
         ActionNameMap[] actionnames = null;
         if (numLocales > 0) {
            actionnames = new ActionNameMap[numLocales];

            for (int i = 0; i < numLocales; i++) {
               String rawLocalizedActions = ContentHandlerUtilities.getStringValue("MicroEdition-Handler-" + handlerIndex + "-" + locales[i], moduleHandle);
               StringTokenizer tokens = new StringTokenizer(rawLocalizedActions, ',');
               int numActions = tokens.countTokens();
               String[] localizedActions = new String[numActions];

               for (int j = 0; j < numActions; j++) {
                  localizedActions[j] = tokens.nextToken().trim();
               }

               actionnames[i] = new ActionNameMap(actions, localizedActions, locales[i]);
            }
         }

         String ID = ContentHandlerUtilities.getStringValue("MicroEdition-Handler-" + handlerIndex + "-" + "ID", moduleHandle);
         if (ID == null || ID.length() == 0) {
            ID = ContentHandlerUtilities.getStringValue("MIDlet-Vendor", moduleHandle)
               + "-"
               + ContentHandlerUtilities.getStringValue("MIDlet-Name", moduleHandle)
               + "-"
               + classname;
            ID = ID.replace(' ', '_');
         }

         String[] accessAllowed = null;
         String rawAccess = ContentHandlerUtilities.getStringValue("MicroEdition-Handler-" + handlerIndex + "-" + "Access", moduleHandle);
         if (rawAccess != null) {
            StringTokenizer tokens = new StringTokenizer(rawAccess);
            accessAllowed = new String[tokens.countTokens()];

            for (int tokenIndex = 0; tokens.hasMoreTokens(); tokenIndex++) {
               accessAllowed[tokenIndex] = tokens.nextToken();
            }
         }

         handlerAttribute = ContentHandlerUtilities.getStringValue("MicroEdition-Handler-" + ++handlerIndex, moduleHandle);
         Class c = null;

         try {
            c = Class.forName(classname);
         } catch (ClassNotFoundException cnfe) {
            continue;
         }

         int installedModule = CodeModuleManager.getModuleHandleForClass(c);
         if (installedModule == moduleHandle) {
            ApplicationDescriptor application = ContentHandlerUtilities.findApplicationDescriptor(moduleHandle, classname);
            if (!accessGranted) {
               try {
                  MIDletSecurity.checkPermission(37);
               } catch (SecurityException se) {
                  return;
               }

               accessGranted = true;
            }

            try {
               RegistryImpl ri = RegistryImpl.getRegistryImpl();
               ri.registerInternal(classname, types, suffixes, actions, actionnames, ID, accessAllowed, application, moduleHandle);
            } catch (Exception var20) {
            }
         }
      }
   }

   @Override
   public void unregisterContentHandler(String classname) {
      RegistryImpl ri = RegistryImpl.getRegistryImpl();
      ri.unregisterInternal(classname);
   }

   @Override
   public void moduleUpgraded(String moduleName, int moduleHandle) {
      InvocationCleanupManager.getInstance().moduleUpgraded(moduleName, moduleHandle);
   }

   private static String parseHandlerValues(String s, String[] a) {
      if (s == null) {
         return null;
      }

      int comma = s.indexOf(44);
      String values;
      String result;
      if (comma == -1) {
         values = s.trim();
         result = null;
      } else {
         values = s.substring(0, comma).trim();
         result = s.substring(comma + 1);
      }

      StringTokenizer tokens = new StringTokenizer(values);

      while (tokens.hasMoreTokens()) {
         Arrays.add(a, tokens.nextToken());
      }

      return result;
   }
}
