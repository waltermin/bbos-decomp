package net.rim.device.apps.internal.smartcard.datakey;

import net.rim.device.api.crypto.CryptoSmartCard;
import net.rim.device.api.crypto.CryptoToken;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.smartcard.AnswerToReset;
import net.rim.device.api.smartcard.SmartCardCapabilities;
import net.rim.device.api.smartcard.SmartCardFactory;
import net.rim.device.api.smartcard.SmartCardReaderSession;
import net.rim.device.api.smartcard.SmartCardSession;
import net.rim.device.api.util.Persistable;

public class DatakeyCryptoSmartCard extends CryptoSmartCard implements Persistable {
   private static ResourceBundle _rb = ResourceBundle.getBundle(-2744454300651253428L, "net.rim.device.apps.internal.resource.crypto.SmartCard");
   private static final byte[] DATAKEY_330_A = new byte[]{59, -1, 17, 0, 0, -127, 49, -2, 77, -128, 37, -96, 0, 0, 0, 86, 87, 68, 75, 51, 51, 48, 6, 0, -48};
   private static final byte[] DATAKEY_330_B = new byte[]{59, -1, 19, 0, 0, -127, 49, -2, 77, -128, 37, -96, 0, 0, 0, 86, 87, 68, 75, 51, 51, 48, 6, 0, -46};
   private static final AnswerToReset _datakey330ATRA = (AnswerToReset)(new Object(DATAKEY_330_A));
   private static final AnswerToReset _datakey330ATRB = (AnswerToReset)(new Object(DATAKEY_330_B));

   public static void libMain(String[] args) {
      try {
         SmartCardFactory.addSmartCard(new DatakeyCryptoSmartCard());
      } finally {
         return;
      }
   }

   @Override
   protected SmartCardSession openSessionImpl(SmartCardReaderSession readerSession) {
      return new DatakeyCryptoSmartCardSession(this, readerSession);
   }

   @Override
   protected boolean checkAnswerToResetImpl(AnswerToReset atr) {
      return _datakey330ATRA.equals(atr) || _datakey330ATRB.equals(atr);
   }

   @Override
   protected String getLabelImpl() {
      return _rb.getString(20);
   }

   @Override
   public String[] getAlgorithms() {
      return new String[]{"RSA", "DSA"};
   }

   @Override
   protected SmartCardCapabilities getCapabilitiesImpl() {
      return (SmartCardCapabilities)(new Object(2));
   }

   @Override
   public CryptoToken getCryptoToken(String algorithm) {
      if (algorithm.equals("RSA")) {
         return new DatakeyRSACryptoToken();
      } else if (algorithm.equals("DSA")) {
         return new DatakeyDSACryptoToken();
      } else {
         throw new Object();
      }
   }
}
