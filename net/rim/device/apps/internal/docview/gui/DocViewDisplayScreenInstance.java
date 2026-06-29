package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class DocViewDisplayScreenInstance {
   private static final long AV_ACTIVEPART_ARRAY;
   private static final long AV_FORWARDSCREEN_ARRAY;

   public static final ActiveDisplayedPart getActivePartInstance(int messageID, int applicationID) {
      Object obj = ApplicationRegistry.getApplicationRegistry().get(-7709433243068286410L);
      if (obj instanceof ActiveDisplayedPart[]) {
         ActiveDisplayedPart[] partArray = (ActiveDisplayedPart[])obj;

         for (int i = 0; i < partArray.length; i++) {
            if (partArray[i]._messageID == messageID && partArray[i]._screen != null && partArray[i]._screen._applicationID == applicationID) {
               return partArray[i];
            }
         }
      }

      return null;
   }

   static final void removeActivePartInstance(int messageID, int applicationID) {
      Object obj = ApplicationRegistry.getApplicationRegistry().get(-7709433243068286410L);
      if (obj instanceof ActiveDisplayedPart[]) {
         ActiveDisplayedPart[] partArray = (ActiveDisplayedPart[])obj;

         for (int i = 0; i < partArray.length; i++) {
            if (partArray[i]._messageID == messageID && partArray[i]._screen != null && partArray[i]._screen._applicationID == applicationID) {
               Arrays.remove(partArray, partArray[i]);
               if (partArray.length == 0) {
                  ApplicationRegistry.getApplicationRegistry().remove(-7709433243068286410L);
                  return;
               }
               break;
            }
         }
      }
   }

   static final void putActivePartInstance(ActiveDisplayedPart instance) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   public static final ActiveDisplayedPart[] getActivePartInstances() {
      Object obj = ApplicationRegistry.getApplicationRegistry().get(-7709433243068286410L);
      return obj instanceof ActiveDisplayedPart[] ? (ActiveDisplayedPart[])obj : null;
   }

   public static final ForwardScreen getForwardScreenInstance(int messageID, int applicationID) {
      Object obj = ApplicationRegistry.getApplicationRegistry().get(2882202296741631652L);
      if (obj instanceof ForwardScreen[]) {
         ForwardScreen[] fwdScreens = (ForwardScreen[])obj;

         for (int i = 0; i < fwdScreens.length; i++) {
            if (fwdScreens[i]._messageID == messageID && fwdScreens[i]._applicationID == applicationID) {
               return fwdScreens[i];
            }
         }
      }

      return null;
   }

   static final void removeForwardScreenInstance(int messageID, int applicationID) {
      Object obj = ApplicationRegistry.getApplicationRegistry().get(2882202296741631652L);
      if (obj instanceof ForwardScreen[]) {
         ForwardScreen[] fwdScreens = (ForwardScreen[])obj;

         for (int i = 0; i < fwdScreens.length; i++) {
            if (fwdScreens[i]._messageID == messageID && fwdScreens[i]._applicationID == applicationID) {
               Arrays.remove(fwdScreens, fwdScreens[i]);
               if (fwdScreens.length == 0) {
                  ApplicationRegistry.getApplicationRegistry().remove(2882202296741631652L);
                  return;
               }
               break;
            }
         }
      }
   }

   static final void putForwardScreenInstance(ForwardScreen instance) {
      if (instance != null) {
         Object obj = ApplicationRegistry.getApplicationRegistry().get(2882202296741631652L);
         if (obj instanceof ForwardScreen[]) {
            ForwardScreen[] fwdScreens = (ForwardScreen[])obj;

            for (int i = 0; i < fwdScreens.length; i++) {
               if (fwdScreens[i]._messageID == instance._messageID && fwdScreens[i]._applicationID == instance._applicationID) {
                  Arrays.remove(fwdScreens, fwdScreens[i]);
                  break;
               }
            }

            Arrays.add((ForwardScreen[])obj, instance);
            return;
         }

         ForwardScreen[] fwdScreens = new ForwardScreen[]{instance};
         ApplicationRegistry.getApplicationRegistry().replace(2882202296741631652L, fwdScreens);
      }
   }

   public static final ForwardScreen[] getForwardScreenInstances() {
      Object obj = ApplicationRegistry.getApplicationRegistry().get(2882202296741631652L);
      return obj instanceof ForwardScreen[] ? (ForwardScreen[])obj : null;
   }
}
