package net.rim.blackberry.api.stringpattern;

import net.rim.blackberry.api.menuitem.ApplicationMenuItem;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.StringPatternRepository;
import net.rim.device.internal.applicationcontrol.ApplicationControl;

public final class PatternRepository {
   public static final int PATTERN_TYPE_EXACT_MATCH = 0;
   public static final int PATTERN_TYPE_REGULAR_EXPRESSION = 1;

   private PatternRepository() {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void addPattern(ApplicationDescriptor application, String pattern, int patternType, ApplicationMenuItem[] menuItems) {
      assertPermission();
      checkApplication(application);
      if (pattern != null && pattern.length() != 0) {
         if (menuItems != null && menuItems.length != 0) {
            for (int i = 0; i < menuItems.length; i++) {
               if (menuItems[i] == null) {
                  throw new Object("menuItems cannot contain null entries");
               }

               String itemName = null;
               boolean var8 = false /* VF: Semaphore variable */;

               try {
                  var8 = true;
                  itemName = menuItems[i].toString();
                  var8 = false;
               } finally {
                  if (var8) {
                     throw new Object("menuItem.toString() throws an exception");
                  }
               }

               if (itemName == null || itemName.length() == 0) {
                  throw new Object("All toString() methods for ApplicationMenuItems must be non-null and must have length > 0");
               }
            }

            ExternalActiveFieldCookieFactory factory = ExternalActiveFieldCookieFactory.getInstance();
            ExternalStringPatternImpl sp;
            switch (patternType) {
               case -1:
                  throw new Object("Invalid patternType");
               case 0:
               default:
                  sp = new ExactMatchStringPatternImpl(pattern, factory.getNewID());
                  break;
               case 1:
                  sp = new RegularExpressionStringPatternImpl(pattern, factory.getNewID());
            }

            factory.addCookie(sp.getID(), new ExternalActiveFieldCookie(menuItems, application));
            StringPatternRepository.addPattern(sp);
         } else {
            throw new Object("menuItems cannot be null or zero-length");
         }
      } else {
         throw new Object("pattern cannot be null or zero-length");
      }
   }

   public static final void removePatterns(ApplicationDescriptor application) {
      assertPermission();
      checkApplication(application);
      long[] idsForDeletion = ExternalActiveFieldCookieFactory.getInstance().removeCookies(application);
      StringPatternRepository.removeExternalPatterns(idsForDeletion);
   }

   private static final void assertPermission() {
      ApplicationControl.assertIPCAllowed(true);
   }

   private static final void checkApplication(ApplicationDescriptor application) {
      if (application == null) {
         throw new Object("application is null");
      }

      if (application.getModuleHandle() != ApplicationDescriptor.currentApplicationDescriptor().getModuleHandle()) {
         throw new Object();
      }

      if (ControlledAccess.verifyRRISignature(application.getModuleHandle()) && !ControlledAccess.verifyRRISignatures(true)) {
         throw new Object();
      }
   }
}
