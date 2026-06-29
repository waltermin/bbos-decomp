package net.rim.device.api.smartcard;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.UserAuthenticator;
import net.rim.device.internal.system.Security;
import net.rim.vm.Persistence;
import net.rim.vm.TraceBack;

public final class SmartCardFactory {
   private static Vector _cards = ApplicationRegistry.getApplicationRegistry().getVector(-23670249000819764L);
   private static Hashtable _supportedATRs = ApplicationRegistry.getApplicationRegistry().getHashtable(8183956272243042995L);
   private static final ResourceBundle _rb = ResourceBundle.getBundle(7215549882295292649L, "net.rim.device.internal.resource.SmartCard");
   private static final long APPLICATION_REGISTRY_ID = -23670249000819764L;
   private static final long SUPPORTED_ATRS_APPLICATION_REGISTRY_ID = 8183956272243042995L;

   private SmartCardFactory() {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final boolean addSmartCard(SmartCard newCard) {
      synchronized (_cards) {
         if (newCard == null) {
            throw new Object();
         }

         boolean var9 = false /* VF: Semaphore variable */;

         try {
            var9 = true;
            Persistence.commit(newCard, true);
            var9 = false;
         } finally {
            if (var9) {
               throw new Object("SmartCard drivers registering with the Smart Card Framework must be Persistable");
            }
         }

         String name = newCard.getClass().getName();
         int size = _cards.size();

         for (int i = 0; i < size; i++) {
            SmartCard registeredCard = (SmartCard)_cards.elementAt(i);
            if (registeredCard.getClass().getName().equals(name)) {
               return false;
            }
         }

         _cards.addElement(newCard);
         UserAuthenticator userAuthenticator = GenericSmartCardUserAuthenticatorFacade.getSmartCardAuthenticator(newCard);
         if (userAuthenticator != null && Security.getInstance().registerUserAuthenticator(userAuthenticator)) {
            return true;
         }

         _cards.removeElement(newCard);
         return false;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final SmartCard chooseSmartCard() {
      SmartCardReader reader = null;

      while (reader == null) {
         reader = SmartCardReaderFactory.chooseReader(false);
         SmartCardReader[] readersToListenTo;
         String message;
         String[] messageParameters;
         if (reader == null) {
            readersToListenTo = SmartCardReaderFactory.getReaders();
            message = _rb.getString(3);
            messageParameters = new String[]{""};
         } else {
            readersToListenTo = new SmartCardReader[]{reader};
            message = _rb.getString(5);
            messageParameters = new Object[]{reader.getLabel()};
         }

         SmartCardPromptDialog.promptUserToInsertSmartCard(message, messageParameters, readersToListenTo);
      }

      SmartCardReaderSession readerSession = null;
      boolean var6 = false /* VF: Semaphore variable */;

      SmartCard var9;
      try {
         var6 = true;
         readerSession = reader.openSession();
         var9 = readerSession.getSmartCard();
         var6 = false;
      } finally {
         if (var6) {
            if (readerSession != null) {
               readerSession.close();
            }
         }
      }

      if (readerSession != null) {
         readerSession.close();
      }

      return var9;
   }

   public static final SmartCardSession getSmartCardSessionForced(SmartCard card) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return card.openSession(SmartCardReaderFactory.getReaderSession(card, null, true));
   }

   public static final SmartCardSession getSmartCardSession(SmartCard card) {
      return card.openSession(SmartCardReaderFactory.getReaderSession(card));
   }

   public static final SmartCardSession getSmartCardSession(AnswerToReset atr) {
      SmartCard card = getSmartCard(atr);
      if (card == null) {
         throw new SmartCardNoCardPresentException();
      } else {
         return getSmartCardSession(card);
      }
   }

   public static final SmartCardSession getSmartCardSession(SmartCardID smartCardID) {
      return getSmartCardSession(smartCardID, false);
   }

   public static final SmartCardSession getSmartCardSessionForced(SmartCardID smartCardID) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return getSmartCardSession(smartCardID, true);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final SmartCardSession getSmartCardSession(SmartCardID smartCardID, boolean forced) {
      if (smartCardID == null) {
         throw new Object();
      }

      SmartCard card = smartCardID.getSmartCard();
      SmartCardSession scSession = null;
      boolean found = false;

      SmartCardSession var7;
      while (true) {
         boolean var10;
         try {
            var10 = false;
            var10 = true;
            var10 = false;
            SmartCardReaderSession readerSession = SmartCardReaderFactory.getReaderSession(card, smartCardID.getLabel(), forced);
            scSession = card.openSession(readerSession);
            SmartCardID currentID = scSession.getSmartCardID();
            if (currentID != null && currentID.equals(smartCardID)) {
               found = true;
               var7 = scSession;
               var10 = false;
               break;
            }

            scSession.close();
            scSession = null;
            SmartCardPromptDialog.promptUserToInsertSmartCard(
               _rb.getString(8), new Object[]{smartCardID.getLabel(), card.getLabel()}, readerSession.getSmartCardReader(), true
            );
            var10 = false;
         } finally {
            if (var10) {
               if (scSession != null && !found) {
                  scSession.close();
               }
            }
         }

         if (scSession != null && !found) {
            scSession.close();
         }
      }

      if (scSession != null && !found) {
         scSession.close();
      }

      return var7;
   }

   public static final SmartCard getSmartCard(AnswerToReset atr) {
      synchronized (_cards) {
         int numRegisteredCards = _cards.size();

         for (int i = 0; i < numRegisteredCards; i++) {
            SmartCard card = (SmartCard)_cards.elementAt(i);
            if (card.checkAnswerToReset(atr) || isSupportedATR(card, atr)) {
               return card;
            }
         }

         return null;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final SmartCard getSmartCard() {
      SmartCardReaderSession readerSession = null;
      AnswerToReset atr = null;
      boolean var18 = false /* VF: Semaphore variable */;

      SmartCard smartCard;
      label245: {
         SmartCard var26;
         label255: {
            label239: {
               label238: {
                  try {
                     var18 = true;
                     readerSession = SmartCardReaderFactory.getReaderSession(null);
                     atr = readerSession.getAnswerToReset();
                     if (atr == null) {
                        smartCard = null;
                        var18 = false;
                        break label245;
                     }

                     smartCard = getSmartCard(atr);
                     if (smartCard != null) {
                        var26 = smartCard;
                        var18 = false;
                        break label255;
                     }

                     var18 = false;
                     break label238;
                  } catch (SmartCardException var21) {
                     var18 = false;
                  } finally {
                     if (var18) {
                        if (readerSession != null) {
                           readerSession.close();
                        }
                     }
                  }

                  if (readerSession != null) {
                     readerSession.close();
                  }
                  break label239;
               }

               if (readerSession != null) {
                  readerSession.close();
               }
            }

            int numRegisteredCards = _cards.size();

            for (int i = 0; i < numRegisteredCards; i++) {
               SmartCard card = (SmartCard)_cards.elementAt(i);
               SmartCardSession session = null;
               boolean var13 = false /* VF: Semaphore variable */;

               SmartCard var7;
               label216: {
                  label215: {
                     try {
                        var13 = true;
                        SmartCardReader reader = SmartCardReaderFactory.chooseReader(false);
                        if (reader != null) {
                           readerSession = reader.openSession();
                           session = card.openSession(readerSession);
                           if (session.testCardSupported()) {
                              addSupportedATR(card, atr);
                              var7 = card;
                              var13 = false;
                              break label216;
                           }

                           var13 = false;
                        } else {
                           var13 = false;
                        }
                        break label215;
                     } catch (SmartCardException var19) {
                        var13 = false;
                     } finally {
                        if (var13) {
                           if (session != null) {
                              session.close();
                           }
                        }
                     }

                     if (session != null) {
                        session.close();
                     }
                     continue;
                  }

                  if (session != null) {
                     session.close();
                  }
                  continue;
               }

               if (session != null) {
                  session.close();
               }

               return var7;
            }

            return null;
         }

         if (readerSession != null) {
            readerSession.close();
         }

         return var26;
      }

      if (readerSession != null) {
         readerSession.close();
      }

      return smartCard;
   }

   static final void addSupportedATR(SmartCard card, AnswerToReset atr) {
      synchronized (_supportedATRs) {
         Vector atrs = (Vector)_supportedATRs.get(card);
         if (atrs == null) {
            atrs = (Vector)(new Object(1));
         }

         atrs.addElement(atr);
         _supportedATRs.put(card, atrs);
      }
   }

   static final boolean isSupportedATR(SmartCard card, AnswerToReset atr) {
      synchronized (_supportedATRs) {
         Vector atrs = (Vector)_supportedATRs.get(card);
         return atrs == null ? false : atrs.contains(atr);
      }
   }

   public static final SmartCard[] getSmartCards() {
      synchronized (_cards) {
         int size = _cards.size();
         SmartCard[] result = new SmartCard[size];

         for (int i = 0; i < size; i++) {
            result[i] = (SmartCard)_cards.elementAt(i);
         }

         return result;
      }
   }

   public static final int getNumSmartCards() {
      return _cards.size();
   }
}
