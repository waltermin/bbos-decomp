package net.rim.device.apps.api.framework.registration;

import java.io.InputStream;
import java.util.Vector;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.ConditionalVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.cldc.io.utility.URIDecoder;

public final class MIMEContentVerbRepository {
   private static final long CONTENT_VERB_REPOSITORY_GUID;

   private static final VerbRepository getVerbRepository() {
      return VerbRepository.getVerbRepository(1464325677521852362L);
   }

   public static final void register(Verb verb, String contentType) {
      getVerbRepository().register(verb, contentType.hashCode());
   }

   public static final void deregister(Verb verb, String contentType) {
      getVerbRepository().deregister(verb, contentType.hashCode());
   }

   public static final Verb[] getVerbs(String contentType) {
      return contentType == null ? null : getVerbRepository().getVerbs(contentType.hashCode());
   }

   public static final MenuItem[] getMenuItems(String mimeType, InputStream istream, String filename, ContextObject context) {
      Verb[] verbs = getVerbs(mimeType);
      if (verbs != null && verbs.length > 0) {
         context = ContextObject.clone(context);
         context.put(-4241241545455759532L, mimeType);
         context.put(5473606008898265655L, istream);
         if (filename == null) {
            String fullPathName = (String)context.get(2765042845091913199L);
            if (fullPathName != null) {
               filename = getName(fullPathName);
            }
         }

         if (filename != null) {
            context.put(-1188330299125235844L, filename);
            context.put(-4886909117188079897L, filename);
         }
      }

      return combineMenuItems(verbs, context);
   }

   private static final String getName(String filename) {
      if (filename.length() > 0) {
         int dirNamePos = filename.substring(0, filename.length() - 1).lastIndexOf(47);
         if (dirNamePos != -1) {
            filename = filename.substring(dirNamePos + 1, filename.length()).toString();
         }

         if (filename.toLowerCase().endsWith(".rem")) {
            filename = filename.substring(0, filename.length() - 4);
         }

         filename = URIDecoder.decode(filename, "UTF-8", false);
      }

      return filename;
   }

   public static final MenuItem[] getMenuItems(String mimeType, String fullPathFilename, ContextObject context) {
      Verb[] verbs = getVerbs(mimeType);
      if (verbs != null && verbs.length > 0) {
         context = ContextObject.castOrCreate(context);
         context.put(-4241241545455759532L, mimeType);
         context.put(2765042845091913199L, fullPathFilename);
         if (fullPathFilename != null) {
            String filename = getName(fullPathFilename);
            context.put(-1188330299125235844L, filename);
            context.put(-4886909117188079897L, filename);
         }
      }

      return combineMenuItems(verbs, context);
   }

   private static final MenuItem[] combineMenuItems(Verb[] verbs, ContextObject context) {
      MenuItem[] menuItems = null;
      if (verbs != null && verbs.length > 0) {
         Vector vector = (Vector)(new Object());

         for (int idx = verbs.length - 1; idx >= 0; idx--) {
            Verb verb = verbs[idx];
            if (!(verb instanceof ConditionalVerb) || ((ConditionalVerb)verb).canInvoke(context)) {
               if (verb instanceof Copyable) {
                  verb = (Verb)((Copyable)verb).copy();
               }

               VerbMenuItem verbMenuItem = new VerbMenuItem(null, verb.getOrdering(), 500, verb, context);
               vector.addElement(verbMenuItem);
            }
         }

         if (vector.size() > 0) {
            menuItems = new Object[vector.size()];
            vector.copyInto(menuItems);
         }
      }

      return menuItems;
   }
}
