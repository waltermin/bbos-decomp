package net.rim.device.api.system;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.internal.system.RadioInternal;

public final class SMSPacketHeader extends SMSParameters implements RadioPacketHeader {
   private boolean _useDefaultProtocolId;
   private boolean _useDefaultMessageCoding;
   private boolean _useDefaultMessageClass;
   private boolean _userDataHeaderPresent;
   private boolean _useDefaultValidityPeriod;
   private boolean _useDefaultDeliveryPeriod;
   private boolean _statusReportRequest;
   private boolean _waitingIndValid;
   private boolean _waitingIndStore;
   private boolean _waitingIndActive;
   private int _waitingIndType;
   private int _numMessages;
   private int _id;
   private int _recordStatus;
   private long _timestamp;
   private boolean _fromSIM;
   private boolean _replyPath;
   public static final int WAITING_INDICATOR_TYPE_VOICEMAIL;
   public static final int WAITING_INDICATOR_TYPE_FAX;
   public static final int WAITING_INDICATOR_TYPE_EMAIL;
   public static final int WAITING_INDICATOR_TYPE_OTHER;
   public static final String[] INDICATOR_TYPE_NAMES = new String[]{"smsvoicemail", "smsfax", "smsemail", "smsother"};
   public static final int RECORD_STATUS_UNREAD;
   public static final int RECORD_STATUS_MS_ORIGINATED;
   public static final int RECORD_STATUS_REPORT_REQUESTED;
   public static final int RECORD_STATUS_REPORT_RECEIVED;
   public static final int SMS_ID_NOT_ON_SIM;
   private static int _maxPacketBits;
   private static final int SEGMENT_HEADER_BYTES;
   private static final int SEGMENT_HEADER_BITS;

   public SMSPacketHeader() {
      this.reset();
   }

   @Override
   public final void reset() {
      super.reset();
      this._useDefaultProtocolId = true;
      this._useDefaultMessageCoding = true;
      this._useDefaultMessageClass = true;
      this._userDataHeaderPresent = false;
      this._useDefaultValidityPeriod = true;
      this._useDefaultDeliveryPeriod = true;
      this._statusReportRequest = false;
      this._waitingIndValid = false;
      this._fromSIM = false;
      this._replyPath = false;
      this._timestamp = 0;
      _maxPacketBits = 0;
   }

   public final void setProtocolId(int protocolMeaning, int protocolId) {
      this._useDefaultProtocolId = false;
      super.setProtocolMeaning(protocolMeaning);
      super.setProtocolId(protocolId);
   }

   @Override
   public final void setMessageCoding(int messageCoding) {
      this._useDefaultMessageCoding = false;
      super.setMessageCoding(messageCoding);
   }

   @Override
   public final void setMessageClass(int messageClass) {
      this._useDefaultMessageClass = false;
      super.setMessageClass(messageClass);
   }

   public final boolean isUserDataHeaderPresent() {
      return this._userDataHeaderPresent;
   }

   public final void setUserDataHeaderPresent(boolean userDataHeaderPresent) {
      this._userDataHeaderPresent = userDataHeaderPresent;
   }

   @Override
   public final void setValidityPeriod(int validityPeriod) {
      this._useDefaultValidityPeriod = false;
      super.setValidityPeriod(validityPeriod);
   }

   public final boolean isValidityPeriodDefault() {
      return this._useDefaultValidityPeriod;
   }

   @Override
   public final void setDeliveryPeriod(int deliveryPeriod) {
      this._useDefaultDeliveryPeriod = false;
      super.setDeliveryPeriod(deliveryPeriod);
   }

   public final boolean isDeliveryPeriodDefault() {
      return this._useDefaultDeliveryPeriod;
   }

   public final boolean getStatusReportRequest() {
      return this._statusReportRequest;
   }

   public final void setStatusReportRequest(boolean statusReportRequest) {
      this._statusReportRequest = statusReportRequest;
   }

   public final boolean isMessageWaitingGroup() {
      return this._waitingIndValid;
   }

   public final boolean isMessageWaitingStore() {
      return this._waitingIndStore;
   }

   public final boolean isMessageWaitingActive() {
      return this._waitingIndActive;
   }

   public final int getMessageWaitingType() {
      return this._waitingIndType;
   }

   public final void setMessageWaitingType(int waitingIndType) {
      this._waitingIndValid = true;
      this._waitingIndType = waitingIndType;
   }

   public final int getNumMessages() {
      return this._numMessages;
   }

   public final void setNumMessages(int numMessages) {
      this._waitingIndValid = true;
      this._numMessages = numMessages;
   }

   public final int getID() {
      return this._id;
   }

   public final long getTimestamp() {
      return this._timestamp;
   }

   public final int getRecordStatus() {
      return this._recordStatus;
   }

   public final boolean isFromSIMCard() {
      return this._fromSIM;
   }

   public final boolean isReplyPath() {
      return this._replyPath;
   }

   public static final boolean isMessageCodingSupported(int wafs, int messageCoding) {
      switch (messageCoding) {
         case -1:
         case 1:
         case 3:
            return false;
         case 0:
         case 2:
         case 6:
         default:
            if ((wafs & 1) != 0) {
               return true;
            }

            return false;
         case 4:
         case 5:
            return (wafs & 2) != 0;
      }
   }

   public static final boolean isSendSupported() {
      return RadioInfo.areWAFsSupported(3) ? ITPolicy.getBoolean(15, true) : false;
   }

   public static final boolean isSegmentationSupported() {
      return (RadioInfo.getActiveWAFs() & 1) != 0;
   }

   public static final int getBitsPerCharacter(int messageCoding) {
      switch (messageCoding) {
         case -1:
         case 3:
            throw new IllegalArgumentException();
         case 0:
         case 4:
         default:
            return 7;
         case 1:
         case 5:
            return 8;
         case 2:
         case 6:
            return 16;
      }
   }

   public static final int getBytesPerCharacter(int messageCoding) {
      switch (messageCoding) {
         case -1:
         case 3:
            throw new IllegalArgumentException();
         case 0:
         case 1:
         case 4:
         case 5:
         default:
            return 1;
         case 2:
         case 6:
            return 2;
      }
   }

   private static final int getMaxPacketBits() {
      if (_maxPacketBits == 0) {
         _maxPacketBits = RadioInternal.getMaxSMSPacketSize() * 8;
      }

      return _maxPacketBits;
   }

   public static final int getBitsPerSegment(int messageCoding) {
      return getBitsPerSegment(messageCoding, 0);
   }

   public static final int getBitsPerSegment(int messageCoding, int wmaUDHLength) {
      int dataBitsPerPacket = getMaxPacketBits() - 48 - wmaUDHLength * 8;
      if (messageCoding == 0) {
         dataBitsPerPacket -= 7 - (48 + wmaUDHLength * 8) % 7;
      }

      return dataBitsPerPacket;
   }

   public static final int getBitsPerSegmentCDMA(int messageCoding, int wmaUDHLength) {
      int dataBitsPerPacket = getMaxPacketBits() - wmaUDHLength * 8;
      if (messageCoding == 0) {
         dataBitsPerPacket -= 7 - wmaUDHLength * 8 % 7;
      }

      return dataBitsPerPacket;
   }

   public static final int getSegments(int characters, int messageCoding) {
      return getSegments(characters, messageCoding, 0);
   }

   public static final int getSegments(int characters, int messageCoding, int udhLength) {
      int maxPacketBits = getMaxPacketBits() - udhLength * 8;
      int bitsRequired = characters * getBitsPerCharacter(messageCoding);
      if (bitsRequired <= maxPacketBits) {
         return 1;
      }

      int bitsPerSegment = getBitsPerSegment(messageCoding, udhLength);
      int segments = bitsRequired / bitsPerSegment;
      if (bitsRequired % bitsPerSegment != 0) {
         segments++;
      }

      return segments;
   }

   public static final int getSegmentsCDMA(int characters, int messageCoding, int udhLength) {
      int maxPacketBits = getMaxPacketBits() - udhLength * 8;
      int bitsRequired = characters * getBitsPerCharacter(messageCoding);
      if (bitsRequired <= maxPacketBits) {
         return 1;
      }

      int bitsPerSegment = getBitsPerSegmentCDMA(messageCoding, udhLength);
      int segments = bitsRequired / bitsPerSegment;
      if (bitsRequired % bitsPerSegment != 0) {
         segments++;
      }

      return segments;
   }

   public static final int getCharacters(int segments, int messageCoding) {
      int bitsPerCharacter = getBitsPerCharacter(messageCoding);
      if (segments == 1) {
         return getMaxPacketBits() / bitsPerCharacter;
      }

      int bitsPerSegment = getBitsPerSegment(messageCoding);
      return bitsPerSegment * segments / bitsPerCharacter;
   }

   public static final boolean validateForMessageCoding(char c, int messageCoding) {
      switch (messageCoding) {
         case -1:
         case 3:
         case 4:
            return validateForASCIIMessageCoding(c);
         case 0:
         default:
            return validateForDefaultMessageCoding(c);
         case 1:
         case 5:
            return validateForISO8859MessageCoding(c);
         case 2:
         case 6:
            return validateForUCS2MessageCoding(c);
      }
   }

   public static final boolean validateForDefaultMessageCoding(char c) {
      switch (c) {
         case '\n':
         case '\f':
         case '\r':
         case ' ':
         case '!':
         case '"':
         case '#':
         case '$':
         case '%':
         case '&':
         case '\'':
         case '(':
         case ')':
         case '*':
         case '+':
         case ',':
         case '-':
         case '.':
         case '/':
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'A':
         case 'B':
         case 'C':
         case 'D':
         case 'E':
         case 'F':
         case 'G':
         case 'H':
         case 'I':
         case 'J':
         case 'K':
         case 'L':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'S':
         case 'T':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case 'Y':
         case 'Z':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '_':
         case 'a':
         case 'b':
         case 'c':
         case 'd':
         case 'e':
         case 'f':
         case 'g':
         case 'h':
         case 'i':
         case 'j':
         case 'k':
         case 'l':
         case 'm':
         case 'n':
         case 'o':
         case 'p':
         case 'q':
         case 'r':
         case 's':
         case 't':
         case 'u':
         case 'v':
         case 'w':
         case 'x':
         case 'y':
         case 'z':
         case '{':
         case '|':
         case '}':
         case '~':
         case '¡':
         case '£':
         case '¤':
         case '¥':
         case '§':
         case '¿':
         case 'Ä':
         case 'Å':
         case 'Æ':
         case 'Ç':
         case 'É':
         case 'Ñ':
         case 'Ö':
         case 'Ø':
         case 'Ü':
         case 'ß':
         case 'à':
         case 'ä':
         case 'å':
         case 'æ':
         case 'è':
         case 'é':
         case 'ì':
         case 'ñ':
         case 'ò':
         case 'ö':
         case 'ø':
         case 'ù':
         case 'ü':
         case 'Γ':
         case 'Δ':
         case 'Θ':
         case 'Λ':
         case 'Ξ':
         case 'Π':
         case 'Σ':
         case 'Φ':
         case 'Ψ':
         case 'Ω':
         case '€':
         case '⌣':
            return true;
         default:
            return false;
      }
   }

   public static final boolean validateForISO8859MessageCoding(char c) {
      return c <= 255;
   }

   public static final boolean validateForASCIIMessageCoding(char c) {
      return c <= 127;
   }

   public static final boolean validateForUCS2MessageCoding(char c) {
      return true;
   }
}
