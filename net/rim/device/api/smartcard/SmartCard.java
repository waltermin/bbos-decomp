package net.rim.device.api.smartcard;

import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;

public class SmartCard implements Persistable {
   protected SmartCard() {
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized SmartCardSession openSession(SmartCardReaderSession readerSession) {
      if (readerSession == null) {
         throw new Object();
      }

      readerSession.openSmartCardSession();
      SmartCardSession session = null;

      SmartCardException t;
      try {
         try {
            session = this.openSessionImpl(readerSession);
            session.setOpen();
            SmartCardCapabilities capabilities = this.getCapabilities();
            if (capabilities != null) {
               readerSession.negotiateProtocol(capabilities);
            }

            return session;
         } catch (SmartCardException var6) {
            t = var6;
         }
      } catch (Throwable var7) {
         if (session != null) {
            session.close();
         }

         readerSession.closeSmartCardSession();
         throw new SmartCardException(t.toString());
      }

      if (session != null) {
         session.close();
      }

      readerSession.closeSmartCardSession();
      throw t;
   }

   protected SmartCardSession openSessionImpl(SmartCardReaderSession _1) {
      throw null;
   }

   public final SmartCardCapabilities getCapabilities() {
      try {
         return this.getCapabilitiesImpl();
      } finally {
         ;
      }
   }

   protected SmartCardCapabilities getCapabilitiesImpl() {
      throw null;
   }

   public final boolean checkAnswerToReset(AnswerToReset atr) {
      if (atr == null) {
         throw new Object();
      }

      try {
         return this.checkAnswerToResetImpl(atr);
      } finally {
         ;
      }
   }

   protected boolean checkAnswerToResetImpl(AnswerToReset _1) {
      throw null;
   }

   public final String getLabel() {
      try {
         return this.getLabelImpl();
      } finally {
         ;
      }
   }

   protected String getLabelImpl() {
      throw null;
   }

   public final void displaySettings(Object context) {
      try {
         this.displaySettingsImpl(context);
      } finally {
         return;
      }
   }

   protected void displaySettingsImpl(Object context) {
   }

   public final boolean isDisplaySettingsAvailable(Object context) {
      try {
         return this.isDisplaySettingsAvailableImpl(context);
      } finally {
         ;
      }
   }

   protected boolean isDisplaySettingsAvailableImpl(Object context) {
      return false;
   }

   @Override
   public String toString() {
      String label = this.getLabel();
      return label != null ? label : super.toString();
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj
         ? true
         : StringUtilities.strEqual(this.toString(), obj.toString()) && StringUtilities.strEqual(this.getClass().getName(), obj.getClass().getName());
   }

   @Override
   public int hashCode() {
      StringBuffer buffer = (StringBuffer)(new Object(this.getClass().getName()));
      String label = this.getLabel();
      if (label != null) {
         buffer.append(label);
      }

      return buffer.toString().hashCode();
   }
}
