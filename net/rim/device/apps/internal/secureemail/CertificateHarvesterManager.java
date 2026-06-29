package net.rim.device.apps.internal.secureemail;

import java.util.Enumeration;
import net.rim.device.api.ui.Field;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.properties.TransitoryMessagePropertiesModel;

public class CertificateHarvesterManager {
   private CertificateHarvester[] _activeCertificateHarvesters = new CertificateHarvester[0];
   private static SecureEmailCache _secureEmailCache = SecureEmailCache.getInstance();

   public static boolean addCertificateHarvester(CertificateHarvester newHarvester, Object context) {
      TransitoryMessagePropertiesModel messagePropertiesModel = (TransitoryMessagePropertiesModel)ContextObject.get(context, 32241034113959076L);
      if (messagePropertiesModel == null) {
         return false;
      }

      EmailMessageModel emailMessageModel = messagePropertiesModel.getEmailMessageModel();
      if (emailMessageModel == null) {
         return false;
      }

      CertificateHarvesterManager specificHarvesterManager = _secureEmailCache.getCertificateHarvesterManager(emailMessageModel);
      return specificHarvesterManager.addCertificateHarvesterInternal(newHarvester, messagePropertiesModel, context);
   }

   private synchronized boolean addCertificateHarvesterInternal(
      CertificateHarvester newHarvester, TransitoryMessagePropertiesModel messagePropertiesModel, Object context
   ) {
      if (this._activeCertificateHarvesters == null) {
         throw new Object("CertificateHarvesterManager already terminated");
      }

      int newHarvesterPriority = newHarvester.getPriority();
      long[] newHarvesterEncodingUIDs = newHarvester.getEncodingUIDs();
      if (newHarvesterEncodingUIDs != null && newHarvesterEncodingUIDs.length != 0) {
         Arrays.sort(newHarvesterEncodingUIDs, 0, newHarvesterEncodingUIDs.length);
         CertificateHarvester[] harvestersToRemove = new CertificateHarvester[0];
         int numActiveCertificateHarvesters = this._activeCertificateHarvesters.length;

         for (int i = 0; i < numActiveCertificateHarvesters; i++) {
            CertificateHarvester currentHarvester = this._activeCertificateHarvesters[i];
            int currentHarvesterPriority = currentHarvester.getPriority();
            long[] currentHarvesterEncodingUIDs = currentHarvester.getEncodingUIDs();
            Arrays.sort(currentHarvesterEncodingUIDs, 0, currentHarvesterEncodingUIDs.length);
            if (this.areSetsEqual(newHarvesterEncodingUIDs, currentHarvesterEncodingUIDs)) {
               if (newHarvesterPriority <= currentHarvesterPriority) {
                  return false;
               }

               Arrays.add(harvestersToRemove, currentHarvester);
            } else {
               if (this.isSubset(newHarvesterEncodingUIDs, currentHarvesterEncodingUIDs)) {
                  return false;
               }

               if (this.isSubset(currentHarvesterEncodingUIDs, newHarvesterEncodingUIDs)) {
                  if (newHarvesterPriority <= currentHarvesterPriority) {
                     return false;
                  }

                  Arrays.add(harvestersToRemove, currentHarvester);
               } else if (this.isIntersectionNonEmpty(currentHarvesterEncodingUIDs, newHarvesterEncodingUIDs)) {
                  return false;
               }
            }
         }

         for (CertificateHarvester harvesterToRemove : harvestersToRemove) {
            Arrays.remove(this._activeCertificateHarvesters, harvesterToRemove);
            harvesterToRemove.terminate();
         }

         EditorUsingRIMModelFactory emailEditorScreen = (EditorUsingRIMModelFactory)ContextObject.get(context, -6581931217101110672L);
         if (emailEditorScreen != null) {
            Enumeration e = emailEditorScreen.getFieldsFromEdit();

            while (e.hasMoreElements()) {
               Field field = (Field)e.nextElement();
               Object cookie = field.getCookie();
               if (cookie instanceof Object) {
                  newHarvester.recipientAdded((EmailHeaderModel)cookie, context);
               }
            }
         }

         newHarvester.setMessagePropertiesModel(messagePropertiesModel);
         Arrays.add(this._activeCertificateHarvesters, newHarvester);
         return true;
      } else {
         return false;
      }
   }

   public synchronized CertificateHarvester getCertificateHarvester(long encodingUID) {
      int numActiveCertificateHarvesters = this._activeCertificateHarvesters != null ? this._activeCertificateHarvesters.length : 0;

      for (int i = 0; i < numActiveCertificateHarvesters; i++) {
         CertificateHarvester currentCertificateHarvester = this._activeCertificateHarvesters[i];
         long[] currentEncodingUIDs = currentCertificateHarvester.getEncodingUIDs();
         if (Arrays.getIndex(currentEncodingUIDs, encodingUID) >= 0) {
            return currentCertificateHarvester;
         }
      }

      return null;
   }

   public synchronized void terminate() {
      int numActiveCertificateHarvesters = this._activeCertificateHarvesters != null ? this._activeCertificateHarvesters.length : 0;

      for (int i = 0; i < numActiveCertificateHarvesters; i++) {
         this._activeCertificateHarvesters[i].terminate();
      }
   }

   private boolean areSetsEqual(long[] setA, long[] setB) {
      int setALength = setA.length;
      if (setALength != setB.length) {
         return false;
      }

      for (int i = 0; i < setALength; i++) {
         if (setA[i] != setB[i]) {
            return false;
         }
      }

      return true;
   }

   private boolean isSubset(long[] setA, long[] setB) {
      int setALength = setA.length;
      int setBLength = setB.length;
      int setAIndex = 0;
      int setBIndex = 0;

      while (setAIndex < setALength) {
         if (setBIndex >= setBLength) {
            return false;
         }

         long elementA = setA[setAIndex];
         long elementB = setB[setBIndex];
         if (elementA < elementB) {
            return false;
         }

         if (elementA > elementB) {
            setBIndex++;
         } else {
            setAIndex++;
            setBIndex++;
         }
      }

      return true;
   }

   private boolean isIntersectionNonEmpty(long[] setA, long[] setB) {
      int setALength = setA.length;
      int setBLength = setB.length;
      int setAIndex = 0;
      int setBIndex = 0;

      while (setAIndex < setALength && setBIndex < setBLength) {
         long elementA = setA[setAIndex];
         long elementB = setB[setBIndex];
         if (elementA < elementB) {
            setAIndex++;
         } else {
            if (elementA <= elementB) {
               return true;
            }

            setBIndex++;
         }
      }

      return false;
   }
}
