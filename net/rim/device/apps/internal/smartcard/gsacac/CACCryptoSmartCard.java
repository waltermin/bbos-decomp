package net.rim.device.apps.internal.smartcard.gsacac;

import net.rim.device.api.crypto.CryptoSmartCard;
import net.rim.device.api.crypto.CryptoToken;
import net.rim.device.api.crypto.NoSuchAlgorithmException;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.smartcard.AnswerToReset;
import net.rim.device.api.smartcard.SmartCardCapabilities;
import net.rim.device.api.smartcard.SmartCardFactory;
import net.rim.device.api.smartcard.SmartCardReaderSession;
import net.rim.device.api.smartcard.SmartCardSession;
import net.rim.device.api.util.Persistable;

public class CACCryptoSmartCard extends CryptoSmartCard implements Persistable {
   private static ResourceBundle _rb = ResourceBundle.getBundle(-2744454300651253428L, "net.rim.device.apps.internal.resource.crypto.SmartCard");
   private static final byte[] GALACTIC = new byte[]{59, 125, 17, 0, 0, 0, 49, -128, 113, -114, 100, -122, -42, 1, 0, -127, -112, 0};
   private static final byte[] SCHLUMBERGER = new byte[]{59, 101, 0, 0, -100, 2, 2, 7, 2};
   private static final byte[] OBERTHUR = new byte[]{59, 127, 17, 0, 0, 0, 49, -64, 83, -54, -60, 1, 100, 82, -39, 4, 0, -126, -112, 0};
   private static final byte[] SAFENET = new byte[]{59, -1, 19, 0, 0, -127, 49, -2, 77, -128, 37, -96, 0, 0, 0, 86, 87, 51, 51, 48, 71, 51, 6, 0, -87};
   private static final byte[] GEMPLUS_GEMXPRESSO_64K = new byte[]{59, 107, 0, 0, -128, 101, -80, -125, 1, 4, 116, -125, 0, -112, 0};
   private static final byte[] AXALTO_ACCESS_64K = new byte[]{59, 117, 18, 0, 0, 41, 5, 1, 4, 1};
   private static final byte[] OBERTHUR_72K_DUAL_INTERFACE_CONTACT_LESS = new byte[]{
      59, -37, -106, 0, -128, 31, 3, 0, 49, -64, 100, 119, -29, 3, 0, -126, -112, 0, -63
   };
   private static final byte[] GEMALTO_ACCESS_64K_V2 = new byte[]{59, -107, -107, 64, -1, -82, 1, 3, 0, 0};
   private static final AnswerToReset _galacticATR = new AnswerToReset(GALACTIC);
   private static final AnswerToReset _schlumbergerATR = new AnswerToReset(SCHLUMBERGER);
   private static final AnswerToReset _oberthurATR = new AnswerToReset(OBERTHUR);
   private static final AnswerToReset _safeNetATR = new AnswerToReset(SAFENET);
   private static final AnswerToReset _gemplusGemxpresso64KATR = new AnswerToReset(GEMPLUS_GEMXPRESSO_64K);
   private static final AnswerToReset _axaltoAccess64KATR = new AnswerToReset(AXALTO_ACCESS_64K);
   private static final AnswerToReset _oberthur72KATR = new AnswerToReset(OBERTHUR_72K_DUAL_INTERFACE_CONTACT_LESS);
   private static final AnswerToReset _gemaltoAccess64KV2 = new AnswerToReset(GEMALTO_ACCESS_64K_V2);

   public static void libMain(String[] args) {
      CACCryptoSmartCard smartCard = new CACCryptoSmartCard();

      try {
         SmartCardFactory.addSmartCard(smartCard);
      } finally {
         return;
      }
   }

   @Override
   protected SmartCardSession openSessionImpl(SmartCardReaderSession readerSession) {
      return new CACCryptoSmartCardSession(this, readerSession);
   }

   @Override
   protected boolean checkAnswerToResetImpl(AnswerToReset atr) {
      return _schlumbergerATR.equals(atr)
         || _galacticATR.equals(atr)
         || _oberthurATR.equals(atr)
         || _safeNetATR.equals(atr)
         || _gemplusGemxpresso64KATR.equals(atr)
         || _axaltoAccess64KATR.equals(atr)
         || _oberthur72KATR.equals(atr)
         || _gemaltoAccess64KV2.equals(atr);
   }

   @Override
   protected String getLabelImpl() {
      return _rb.getString(2);
   }

   @Override
   public String[] getAlgorithms() {
      return new String[]{"RSA"};
   }

   @Override
   protected SmartCardCapabilities getCapabilitiesImpl() {
      return new SmartCardCapabilities(3);
   }

   @Override
   public CryptoToken getCryptoToken(String algorithm) throws NoSuchAlgorithmException {
      if (algorithm.equals("RSA")) {
         return new CACRSACryptoToken();
      } else {
         throw new NoSuchAlgorithmException();
      }
   }
}
