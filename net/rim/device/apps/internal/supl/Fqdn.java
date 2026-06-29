package net.rim.device.apps.internal.supl;

import net.rim.device.api.util.Arrays;

final class Fqdn implements SlpAddress {
   String domainName;
   static final byte FQDN_CHARSET_BIT_SIZE = 6;
   static final short FQDN_MIN_LENGTH = 1;
   static final short FQDN_MAX_LENGTH = 255;
   static final byte FQDN_LENGTH_BIT_SIZE = 8;
   static final char[] FQDN_ALPHABET = new char[]{
      '-',
      '.',
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      'A',
      'B',
      'C',
      'D',
      'E',
      'F',
      'G',
      'H',
      'I',
      'J',
      'K',
      'L',
      'M',
      'N',
      'O',
      'P',
      'Q',
      'R',
      'S',
      'T',
      'U',
      'V',
      'W',
      'X',
      'Y',
      'Z',
      'a',
      'b',
      'c',
      'd',
      'e',
      'f',
      'g',
      'h',
      'i',
      'j',
      'k',
      'l',
      'm',
      'n',
      'o',
      'p',
      'q',
      'r',
      's',
      't',
      'u',
      'v',
      'w',
      'x',
      'y',
      'z',
      '\u0005',
      '퀄',
      '挮',
      '扲',
      '\n',
      '\u0000',
      '\u0001',
      '퀊',
      '3',
      '\u0000',
      'ŭ',
      '퀄',
      '牣',
      '\u0600',
      'Ⲑ',
      '\u0000',
      'Ȁ',
      '\u0000',
      '\u0000',
      '謀',
      '꼝',
      '넰',
      '欈',
      'I',
      '\b',
      '\u0000',
      '\u0000',
      '\u0000',
      '\u0001',
      '\u0000',
      '\u0002',
      '\u0000',
      '\u0003',
      '\u0000',
      '\u0004',
      '\u0000',
      '\u0005',
      '\u0000',
      '\u0006',
      '\u0000',
      '\u0007',
      '\u0000',
      '\b',
      '3',
      '[',
      '\u009e',
      '¾',
      'ǹ',
      'Ĉ',
      '猢',
      'Ѐ',
      '匀',
      '灵',
      '獬',
      '✀',
      '伀',
      '䅍',
      '匠',
      '捥',
      '牵',
      '\u2065',
      '獕',
      '牥',
      '倠'
   };

   @Override
   public final void decode(Nibbler nib) {
      int length = nib.getBitsLarge(8) + 1;
      StringBuffer strBuf = new StringBuffer();

      for (int i = 0; i < length; i++) {
         strBuf.append(FQDN_ALPHABET[0xFF & nib.getBitsLarge(6)]);
      }

      this.domainName = strBuf.toString();
   }

   @Override
   public final void encode(Stuffer stuff) {
      stuff.putBits(1, 1);
      stuff.putBits(this.domainName.length() - 1, 8);

      for (int i = 0; i < this.domainName.length(); i++) {
         stuff.putBits(Arrays.getIndex(FQDN_ALPHABET, this.domainName.charAt(i)), 6);
      }
   }

   public final void setDomainName(String domainName) {
      this.domainName = domainName;
   }

   @Override
   public final String toString() {
      return this.domainName;
   }

   public final boolean equals(SlpAddress slpAddress) {
      boolean retVal = false;
      if (slpAddress instanceof Fqdn) {
         Fqdn fqdn = (Fqdn)slpAddress;
         retVal = this.domainName.equals(fqdn.domainName);
      }

      return retVal;
   }

   final void print() {
      System.out.println("Fqdn: " + this.domainName);
   }
}
