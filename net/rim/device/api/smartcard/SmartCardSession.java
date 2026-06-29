package net.rim.device.api.smartcard;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.RichTextFieldUtilities;

public class SmartCardSession {
   private boolean _open;
   private boolean _loggedIn;
   private boolean _usedCachedPassword;
   private SmartCard _smartCard;
   private SmartCardReaderSession _readerSession;
   private SmartCardReader _reader;
   private SmartCardID _cardID;
   public static final byte INS_ERASE_BINARY = 14;
   public static final byte INS_VERIFY = 32;
   public static final byte INS_MANAGE_CHANNEL = 112;
   public static final byte INS_EXTERNAL_AUTHENTICATE = -126;
   public static final byte INS_GET_CHALLENGE = -124;
   public static final byte INS_INTERNAL_AUTHENTICATE = -120;
   public static final byte INS_SELECT_FILE = -92;
   public static final byte INS_READ_BINARY = -80;
   public static final byte INS_READ_RECORD = -78;
   public static final byte INS_GET_RESPONSE = -64;
   public static final byte INS_ENVELOPE = -62;
   public static final byte INS_GET_DATA = -54;
   public static final byte INS_WRITE_BINARY = -48;
   public static final byte INS_WRITE_RECORD = -46;
   public static final byte INS_UPDATE_BINARY = -42;
   public static final byte INS_PUT_DATA = -38;
   public static final byte INS_UPDATE_RECORD = -36;
   public static final byte INS_APPEND_RECORD = -30;
   private static int KEY_STORE_SECURITY_LEVEL_HIGH = 2;
   private static SmartCardCache _cache = SmartCardCache.getInstance();
   private static final ResourceBundle _rb = ResourceBundle.getBundle(7215549882295292649L, "net.rim.device.internal.resource.SmartCard");
   public static final int UNKNOWN_OPERATION = -1;
   public static final int NON_CRYPTO_OPERATION = 0;
   public static final int SIGN_OPERATION = 1;
   public static final int DECRYPT_OPERATION = 2;
   public static final int USER_AUTHENTICATION_OPERATION = 3;
   public static final int KEY_GENERATION_OPERATION = 4;

   protected SmartCardSession(SmartCard smartCard, SmartCardReaderSession readerSession) {
      if (readerSession == null) {
         throw new Object();
      }

      this._smartCard = smartCard;
      this._readerSession = readerSession;
      this._reader = readerSession.getSmartCardReader();
   }

   final void setOpen() {
      this._open = true;
   }

   public void closing() {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void close() {
      if (this._open) {
         boolean var15 = false /* VF: Semaphore variable */;

         try {
            var15 = true;
            this.closing();
            this.closeImpl();
            var15 = false;
         } finally {
            if (var15) {
               this._open = false;
               boolean var11 = false /* VF: Semaphore variable */;

               try {
                  var11 = true;
                  this._readerSession.close();
                  var11 = false;
               } finally {
                  if (var11) {
                     this._readerSession.closeSmartCardSession();
                  }
               }

               this._readerSession.closeSmartCardSession();
            }
         }

         this._open = false;
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            this._readerSession.close();
            var7 = false;
         } finally {
            if (var7) {
               this._readerSession.closeSmartCardSession();
            }
         }

         this._readerSession.closeSmartCardSession();
      }
   }

   protected void closeImpl() {
      throw null;
   }

   public final boolean isOpen() {
      return this._open;
   }

   protected final void sendAPDU(CommandAPDU commandAPDU, ResponseAPDU responseAPDU) {
      if (!this._open) {
         throw new SmartCardSessionClosedException();
      }

      this._readerSession.sendAPDU(commandAPDU, responseAPDU);
   }

   protected final void sendAPDUs(CommandAPDUGroup commandAPDUs, ResponseAPDUGroup responseAPDUs) {
      if (!this._open) {
         throw new SmartCardSessionClosedException();
      }

      this._readerSession.sendAPDUs(commandAPDUs, responseAPDUs);
   }

   public final synchronized void sendAPDUs(CommandAPDUGroup commandAPDUs, ResponseAPDU responseAPDU) {
      if (!this._open) {
         throw new SmartCardSessionClosedException();
      }

      this._readerSession.sendAPDUs(commandAPDUs, responseAPDU);
   }

   public final int getMaxLoginAttempts() {
      if (!this._open) {
         throw new SmartCardSessionClosedException();
      }

      int maxAttempts = _cache.getMaxLoginAttempts(this._reader);
      if (maxAttempts != -1) {
         return maxAttempts;
      }

      maxAttempts = this.getMaxLoginAttemptsImpl();
      _cache.setMaxLoginAttempts(this._reader, maxAttempts);
      return maxAttempts;
   }

   protected int getMaxLoginAttemptsImpl() {
      throw null;
   }

   public final int getRemainingLoginAttempts() {
      if (!this._open) {
         throw new SmartCardSessionClosedException();
      }

      int remainingAttempts = _cache.getRemainingLoginAttempts(this._reader);
      if (remainingAttempts != -1) {
         return remainingAttempts;
      }

      remainingAttempts = this.getRemainingLoginAttemptsImpl();
      _cache.setRemainingLoginAttempts(this._reader, remainingAttempts);
      return remainingAttempts;
   }

   protected int getRemainingLoginAttemptsImpl() {
      throw null;
   }

   public final boolean isLoggedIn() {
      return this._open && this._loggedIn;
   }

   private boolean allowCachedPassword(int operation) {
      switch (operation) {
         case -2:
            throw new Object();
         case -1:
            return false;
         case 0:
         default:
            return true;
         case 1:
            if (ITPolicy.getInteger(24, 45, 1) != KEY_STORE_SECURITY_LEVEL_HIGH) {
               return true;
            }

            return false;
         case 2:
            if (ITPolicy.getInteger(24, 46, 1) != KEY_STORE_SECURITY_LEVEL_HIGH) {
               return true;
            }

            return false;
         case 3:
            return false;
         case 4:
            return false;
      }
   }

   public final boolean login(String password, int operation) {
      if (!this._open) {
         throw new SmartCardSessionClosedException();
      }

      if (this._loggedIn) {
         if (this._usedCachedPassword && !this.allowCachedPassword(operation)) {
            throw new Object("Already logged in with a cached password but operation requires explicit password entry. Please close the session and try again");
         } else {
            return true;
         }
      } else {
         if (password == null && this.cachedLogin(operation)) {
            return true;
         }

         if (password == null) {
            throw new Object();
         }

         try {
            this._loggedIn = this.loginImpl(password);
            _cache.login(this._reader, this._loggedIn);
            if (this._loggedIn) {
               CachedPasswordManager manager = CachedPasswordManager.getInstance();
               if (manager != null) {
                  manager.put(this.getSmartCardID(), password);
               }

               Security.getInstance().setNumericPasswords(null, password);
            }
         } catch (SmartCardAccessDeniedException e) {
            _cache.login(this._reader, this._loggedIn);
            throw e;
         } catch (SmartCardLockedException e) {
            _cache.setRemainingLoginAttempts(this._reader, 0);
            throw e;
         }

         return this._loggedIn;
      }
   }

   protected boolean loginImpl(String _1) {
      throw null;
   }

   public final void loginPrompt(String accessReason, int operation) {
      this.loginPrompt(accessReason, operation, false);
   }

   public final void loginPrompt(String accessReason, int operation, boolean allowOnLockScreen) {
      if (allowOnLockScreen) {
         ControlledAccess.assertRRISignatures(true);
      }

      if (!this._open) {
         throw new SmartCardSessionClosedException();
      }

      if (this._loggedIn) {
         if (this._usedCachedPassword && !this.allowCachedPassword(operation)) {
            throw new Object("Already logged in with a cached password but operation requires explicit password entry. Please close the session and try again");
         }
      } else if (!this.cachedLogin(operation)) {
         while (true) {
            try {
               RichTextField labelField = this.getPromptField(accessReason, this._smartCard.getLabel());
               String password = SmartCardTicketDialog.getPassword(labelField, allowOnLockScreen);
               if (password != null && password.length() != 0) {
                  this._usedCachedPassword = false;
                  if (this.login(password, operation)) {
                     return;
                  }
               }
            } catch (SmartCardAccessDeniedException var6) {
            }
         }
      }
   }

   private final boolean cachedLogin(int operation) {
      if (!this.allowCachedPassword(operation)) {
         return false;
      }

      SmartCardID smartCardID = this.getSmartCardID();
      if (smartCardID != null) {
         CachedPasswordManager manager = CachedPasswordManager.getInstance();
         if (manager != null) {
            String cachedPassword = manager.get(this.getSmartCardID());
            if (cachedPassword != null) {
               this._usedCachedPassword = true;
               return this.login(cachedPassword, operation);
            }
         }
      }

      return false;
   }

   private RichTextField getPromptField(String accessReason, String smartCardLabel) {
      StringBuffer promptMessage = (StringBuffer)(new Object(MessageFormat.format(_rb.getString(16), new Object[]{smartCardLabel})));
      if (accessReason != null) {
         promptMessage.append(accessReason);
      }

      try {
         int remainingLoginAttempts = this.getRemainingLoginAttempts();
         if (remainingLoginAttempts == 0) {
            throw new SmartCardLockedException();
         }

         int maxLoginAttempts = this.getMaxLoginAttempts();
         if (maxLoginAttempts != -1 && remainingLoginAttempts != -1) {
            int passwordAttempt = maxLoginAttempts - remainingLoginAttempts + 1;
            promptMessage.append(' ');
            if (passwordAttempt == maxLoginAttempts) {
               promptMessage.append(_rb.getString(17));
            } else {
               promptMessage.append(
                  MessageFormat.format(_rb.getString(18), new Object[]{Integer.toString(passwordAttempt), Integer.toString(maxLoginAttempts)})
               );
            }
         }
      } catch (SmartCardUnsupportedOperationException e) {
         promptMessage.append(' ');
         promptMessage.append(_rb.getString(6));
      }

      return RichTextFieldUtilities.getBoldFormattedRichTextField(promptMessage.toString(), 9007199254740992L);
   }

   public final SmartCard getSmartCard() {
      return this._smartCard;
   }

   public final SmartCardReaderSession getSmartCardReaderSession() {
      return this._readerSession;
   }

   public final SmartCardID getSmartCardID() {
      if (!this._open) {
         throw new SmartCardSessionClosedException();
      }

      if (this._cardID != null) {
         return this._cardID;
      }

      this._cardID = _cache.getSmartCardID(this._reader);
      if (this._cardID != null) {
         return this._cardID;
      }

      this._cardID = this.getSmartCardIDImpl();
      if (this._cardID == null) {
         throw new SmartCardUnsupportedOperationException("Cannot retrieve card ID");
      }

      _cache.setSmartCardID(this._reader, this._cardID);
      return this._cardID;
   }

   protected SmartCardID getSmartCardIDImpl() {
      throw null;
   }

   public final boolean testCardSupported() {
      if (!this._open) {
         throw new SmartCardSessionClosedException();
      } else {
         return this.testCardSupportedImpl();
      }
   }

   protected boolean testCardSupportedImpl() {
      return false;
   }
}
