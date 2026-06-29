package net.rim.device.api.smartcard;

import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.vm.Array;

public final class SmartCardReaderFactory {
   private static final ResourceBundle _rb = ResourceBundle.getBundle(7215549882295292649L, "net.rim.device.internal.resource.SmartCard");
   private static final long ID = -1627428637371680177L;
   private static Vector _readers = ApplicationRegistry.getApplicationRegistry().getVector(-1627428637371680177L);

   private SmartCardReaderFactory() {
   }

   public static final boolean addReader(SmartCardReader newReader) {
      synchronized (_readers) {
         if (newReader == null) {
            throw new IllegalArgumentException();
         }

         int size = _readers.size();
         String newClassName = newReader.getClass().getName();

         for (int i = 0; i < size; i++) {
            SmartCardReader registeredReader = (SmartCardReader)_readers.elementAt(i);
            if (registeredReader.getClass().getName().equals(newClassName)) {
               return false;
            }
         }

         _readers.addElement(newReader);
         return true;
      }
   }

   public static final SmartCardReader[] getReaders() {
      synchronized (_readers) {
         boolean requireInsertionRemovalDetection = requireInsertionRemovalDetection();
         int num = 0;
         int size = _readers.size();
         SmartCardReader[] result = new SmartCardReader[size];

         for (int i = 0; i < size; i++) {
            SmartCardReader reader = (SmartCardReader)_readers.elementAt(i);
            if (!requireInsertionRemovalDetection || reader.isInsertionRemovalDetectable()) {
               result[num++] = reader;
            }
         }

         Array.resize(result, num);
         return result;
      }
   }

   public static final int getNumSmartCardReaders() {
      synchronized (_readers) {
         boolean requireInsertionRemovalDetection = requireInsertionRemovalDetection();
         int num = 0;

         for (int i = _readers.size() - 1; i >= 0; i--) {
            SmartCardReader reader = (SmartCardReader)_readers.elementAt(i);
            if (!requireInsertionRemovalDetection || reader.isInsertionRemovalDetectable()) {
               num++;
            }
         }

         return num;
      }
   }

   public static final SmartCardReader[] getInstalledReaders() {
      SmartCardReader[] readers = getReaders();
      int numRegisteredReaders = readers.length;
      SmartCardReader[] installedReaders = new SmartCardReader[numRegisteredReaders];
      int insertIndex = 0;

      for (int i = 0; i < numRegisteredReaders; i++) {
         try {
            SmartCardReader reader = readers[i];
            if (reader.isReaderPresent()) {
               installedReaders[insertIndex++] = reader;
            }
         } catch (SmartCardException var7) {
         }
      }

      Array.resize(installedReaders, insertIndex);
      return installedReaders;
   }

   public static final SmartCardReader chooseReader() {
      return chooseReader(true);
   }

   static final SmartCardReader chooseReader(boolean promptIfNoReaders) throws SmartCardCancelException, SmartCardNoReaderPresentException {
      if (getNumSmartCardReaders() <= 0) {
         throw new SmartCardNoReaderPresentException();
      }

      do {
         SmartCardReader[] installedReaders = getInstalledReaders();
         if (installedReaders.length > 0) {
            int numInstalledReaders = installedReaders.length;
            if (numInstalledReaders <= 1) {
               return installedReaders[0];
            }

            String[] choices = new String[numInstalledReaders];

            for (int i = 0; i < numInstalledReaders; i++) {
               choices[i] = installedReaders[i].getLabel();
            }

            int choice = BackgroundDialog.getChoice(_rb.getString(0), choices, 0, -2147483644);
            if (choice == -1) {
               throw new SmartCardCancelException();
            } else {
               return installedReaders[choice];
            }
         }

         if (!promptIfNoReaders) {
            return null;
         }
      } while (BackgroundDialog.getChoice(_rb.getString(1), CommonResource.getStringArray(10041), 0, -2147483644) == 0);

      throw new SmartCardCancelException();
   }

   public static final SmartCardReaderSession getReaderSession(SmartCard smartCard) {
      return getReaderSession(smartCard, null, false);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final SmartCardReaderSession getReaderSession(SmartCard smartCard, String smartCardIDLabel, boolean forced) throws SmartCardCancelException, SmartCardNoReaderPresentException {
      SmartCardReader[] registeredReaders = getReaders();
      int numRegisteredReaders = registeredReaders.length;
      if (numRegisteredReaders <= 0) {
         throw new SmartCardNoReaderPresentException();
      }

      SmartCardReaderSession readerSession = null;
      boolean found = false;

      while (true) {
         SmartCardReader[] installedReaders = new SmartCardReader[0];

         for (int i = 0; i < numRegisteredReaders; i++) {
            try {
               SmartCardReader reader = registeredReaders[i];
               if (!reader.isSmartCardPresent()) {
                  if (reader.isReaderPresent()) {
                     Array.resize(installedReaders, installedReaders.length + 1);
                     installedReaders[installedReaders.length - 1] = reader;
                  }
               } else {
                  Array.resize(installedReaders, installedReaders.length + 1);
                  installedReaders[installedReaders.length - 1] = reader;
                  boolean var19 = false /* VF: Semaphore variable */;

                  SmartCardReaderSession var30;
                  label371: {
                     SmartCardReaderSession index;
                     label372: {
                        SmartCardReaderSession var32;
                        label352: {
                           try {
                              label384: {
                                 var19 = true;
                                 if (forced) {
                                    readerSession = reader.openSessionForced();
                                 } else {
                                    readerSession = reader.openSession();
                                 }

                                 if (smartCard == null) {
                                    found = true;
                                    var30 = readerSession;
                                    var19 = false;
                                    break label371;
                                 }

                                 AnswerToReset readerATR = readerSession.getAnswerToReset();
                                 if (smartCard.checkAnswerToReset(readerATR) || SmartCardFactory.isSupportedATR(smartCard, readerATR)) {
                                    found = true;
                                    var32 = readerSession;
                                    var19 = false;
                                    break label352;
                                 }

                                 SmartCardSession cardSession = null;
                                 boolean var24 = false /* VF: Semaphore variable */;

                                 label342: {
                                    try {
                                       var24 = true;
                                       cardSession = smartCard.openSession(readerSession);
                                       if (cardSession.testCardSupported()) {
                                          SmartCardFactory.addSupportedATR(smartCard, readerATR);
                                          cardSession.close();
                                          found = true;
                                          if (forced) {
                                             readerSession = reader.openSessionForced();
                                          } else {
                                             readerSession = reader.openSession();
                                          }

                                          index = readerSession;
                                          var24 = false;
                                          break label342;
                                       }

                                       var24 = false;
                                    } finally {
                                       if (var24) {
                                          if (cardSession != null) {
                                             cardSession.close();
                                          }
                                       }
                                    }

                                    if (cardSession != null) {
                                       cardSession.close();
                                       var19 = false;
                                    } else {
                                       var19 = false;
                                    }
                                    break label384;
                                 }

                                 if (cardSession != null) {
                                    cardSession.close();
                                    var19 = false;
                                 } else {
                                    var19 = false;
                                 }
                                 break label372;
                              }
                           } finally {
                              if (var19) {
                                 if (readerSession != null && !found) {
                                    readerSession.close();
                                 }
                              }
                           }

                           if (readerSession != null && !found) {
                              readerSession.close();
                           }
                           continue;
                        }

                        if (readerSession != null && !found) {
                           readerSession.close();
                        }

                        return var32;
                     }

                     if (readerSession != null && !found) {
                        readerSession.close();
                     }

                     return index;
                  }

                  if (readerSession != null && !found) {
                     readerSession.close();
                  }

                  return var30;
               }
            } catch (SmartCardCancelException e) {
               throw e;
            } catch (SmartCardException var28) {
            }
         }

         String message;
         String[] messageParameters;
         if (smartCardIDLabel != null) {
            byte[] idBuffer = smartCardIDLabel.getBytes();
            int index = Arrays.getIndex(idBuffer, (byte)0);
            if (index != -1) {
               Array.resize(idBuffer, index);
               smartCardIDLabel = new String(idBuffer);
            }

            messageParameters = new String[]{smartCardIDLabel, smartCard != null ? smartCard.getLabel() : ""};
            if (installedReaders.length > 0) {
               message = _rb.getString(8);
            } else {
               message = _rb.getString(9);
            }
         } else if (installedReaders.length > 0) {
            message = _rb.getString(5);
            messageParameters = new String[]{""};
         } else {
            message = _rb.getString(3);
            messageParameters = new String[]{smartCard != null ? smartCard.getLabel() : ""};
         }

         SmartCardReader[] readersToListenTo;
         if (installedReaders.length == 0) {
            readersToListenTo = registeredReaders;
         } else {
            readersToListenTo = installedReaders;
         }

         SmartCardPromptDialog.promptUserToInsertSmartCard(message, messageParameters, readersToListenTo, true);
      }
   }

   private static final boolean requireInsertionRemovalDetection() {
      boolean forceSmartCardTwoFactorAuthentication = ITPolicy.getBoolean(24, 2, false);
      boolean lockOnSmartCardRemoval = ITPolicy.getBoolean(24, 1, false);
      return forceSmartCardTwoFactorAuthentication && lockOnSmartCardRemoval;
   }
}
