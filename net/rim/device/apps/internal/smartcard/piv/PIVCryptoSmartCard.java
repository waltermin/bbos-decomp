package net.rim.device.apps.internal.smartcard.piv;

import net.rim.device.api.crypto.CryptoSmartCard;
import net.rim.device.api.crypto.CryptoToken;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.smartcard.AnswerToReset;
import net.rim.device.api.smartcard.SmartCardCapabilities;
import net.rim.device.api.smartcard.SmartCardFactory;
import net.rim.device.api.smartcard.SmartCardReaderSession;
import net.rim.device.api.smartcard.SmartCardSession;
import net.rim.device.api.util.Persistable;

public class PIVCryptoSmartCard extends CryptoSmartCard implements Persistable {
   private static ResourceBundle _rb = ResourceBundle.getBundle(-2744454300651253428L, "net.rim.device.apps.internal.resource.crypto.SmartCard");
   private static final byte[] OBERTHUR = new byte[]{59, -37, -106, 0, -127, -79, -2, 69, 31, 3, -128, -7, -96, 0, 0, 3, 8, 0, 0, 16, 0, 24};
   private static final byte[] SAFE_NET = new byte[]{59, -1, 24, 0, 0, -127, 49, -2, 77, -128, 37, -96, 0, 0, 0, 86, 87, 68, 75, 52, 48, 48, 6, 0, -35};
   private static final AnswerToReset _oberthurATR = (AnswerToReset)(new Object(OBERTHUR));
   private static final AnswerToReset _safeNetATR = (AnswerToReset)(new Object(SAFE_NET));

   public static void libMain(String[] args) {
      PIVCryptoSmartCard smartCard = new PIVCryptoSmartCard();

      try {
         SmartCardFactory.addSmartCard(smartCard);
      } finally {
         return;
      }
   }

   @Override
   protected SmartCardSession openSessionImpl(SmartCardReaderSession readerSession) {
      return new PIVCryptoSmartCardSession(this, readerSession);
   }

   @Override
   protected boolean checkAnswerToResetImpl(AnswerToReset atr) {
      return _oberthurATR.equals(atr) || _safeNetATR.equals(atr);
   }

   @Override
   protected String getLabelImpl() {
      return "PIV";
   }

   @Override
   public String[] getAlgorithms() {
      return new String[]{"RSA"};
   }

   @Override
   protected SmartCardCapabilities getCapabilitiesImpl() {
      return (SmartCardCapabilities)(new Object(3));
   }

   @Override
   public CryptoToken getCryptoToken(String algorithm) {
      if (algorithm.equals("RSA")) {
         return new PIVRSACryptoToken();
      } else {
         throw new Object();
      }
   }
}
