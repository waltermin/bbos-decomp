package org.apache.oro.text.regexp;

final class OpCode {
   static final char _END = '\u0000';
   static final char _BOL = '\u0001';
   static final char _MBOL = '\u0002';
   static final char _SBOL = '\u0003';
   static final char _EOL = '\u0004';
   static final char _MEOL = '\u0005';
   static final char _SEOL = '\u0006';
   static final char _ANY = '\u0007';
   static final char _SANY = '\b';
   static final char _ANYOF = '\t';
   static final char _CURLY = '\n';
   static final char _CURLYX = '\u000b';
   static final char _BRANCH = '\f';
   static final char _BACK = '\r';
   static final char _EXACTLY = '\u000e';
   static final char _NOTHING = '\u000f';
   static final char _STAR = '\u0010';
   static final char _PLUS = '\u0011';
   static final char _ALNUM = '\u0012';
   static final char _NALNUM = '\u0013';
   static final char _BOUND = '\u0014';
   static final char _NBOUND = '\u0015';
   static final char _SPACE = '\u0016';
   static final char _NSPACE = '\u0017';
   static final char _DIGIT = '\u0018';
   static final char _NDIGIT = '\u0019';
   static final char _REF = '\u001a';
   static final char _OPEN = '\u001b';
   static final char _CLOSE = '\u001c';
   static final char _MINMOD = '\u001d';
   static final char _GBOL = '\u001e';
   static final char _IFMATCH = '\u001f';
   static final char _UNLESSM = ' ';
   static final char _SUCCEED = '!';
   static final char _WHILEM = '"';
   static final char _ANYOFUN = '#';
   static final char _NANYOFUN = '$';
   static final char _RANGE = '%';
   static final char _ALPHA = '&';
   static final char _BLANK = '\'';
   static final char _CNTRL = '(';
   static final char _GRAPH = ')';
   static final char _LOWER = '*';
   static final char _PRINT = '+';
   static final char _PUNCT = ',';
   static final char _UPPER = '-';
   static final char _XDIGIT = '.';
   static final char _OPCODE = '/';
   static final char _NOPCODE = '0';
   static final char _ONECHAR = '1';
   static final char _ALNUMC = '2';
   static final char _ASCII = '3';
   static final int[] _operandLength = new int[]{
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      2,
      2,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      1,
      1,
      1,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      -804913100,
      65536,
      65537,
      262148,
      458756,
      589831,
      655370,
      851980,
      983054,
      1114128,
      1245202,
      1376276,
      1507350,
      1638424,
      1769498,
      1900572,
      786433,
      12,
      2293794,
      2424868,
      2555942,
      2687016,
      2818090,
      2949164,
      3080238,
      3211312,
      3342386,
      -805044213,
      775162112,
      774909491,
      3420721,
      -805044199,
      1699878656,
      1918985587,
      1226860643,
      1867325550,
      1852795252,
      1685343264,
      46,
      -805044223,
      2,
      -804913127,
      524295,
      1179657,
      1441811,
      1572887,
      2293785,
      2490404,
      2621479,
      2752553,
      2883627,
      3014701,
      3145775,
      3276849,
      51,
      -804913144,
      851980,
      1114128,
      720906,
      2228250,
      -804651007,
      51,
      1064304896,
      -977993472,
      1881671680,
      1107820607,
      -989763739,
      134225510,
      1717990722,
      1124597771,
      1953369262,
      1992389,
      1684948232,
      1992389,
      1938312968,
      -313325568,
      473891611,
      234883584,
      355341824,
      204347182,
      128872307,
      773193843,
      1830956565,
      1919120225,
      204348777,
      7386215,
      1595236118,
      1935764770,
      493449827,
      -1268315041,
      1296650864,
      -1368427616,
      402682631,
      402704752,
      -313276048,
      473891611,
      778508544,
      1704354072,
      779032878,
      204396404,
      7386215,
      -1166786533,
      1881677312,
      1109262399,
      -989763739,
      503324262,
      1846324803,
      510051700,
      1850023424,
      510051684,
      290266624,
      510051734,
      1293811827,
      1769237621,
      6619468,
      1819626782,
      1694620532,
      -330097152,
      -313319936,
      473891611,
      1806509568,
      1742219264,
      -834666485,
      2037406585,
      -831373056,
      1062189689,
      -1569766715,
      9991012,
      1752392008,
      2120376692,
      -1777251072,
      1819636640,
      525140084,
      594352486,
      192155984,
      1308661870,
      424020957,
      9965933,
      -1016438193,
      -1502589184,
      189792262,
      290272620,
      1342180246,
      1295346699,
      1939904017,
      7629941,
      896273232,
      192155984,
      189792366,
      -313313940,
      473891611,
      1812680704,
      40935477,
      189792509,
      -1549388436,
      1342180315
   };
   static final char[] _opType = new char[]{
      '\u0000',
      '\u0001',
      '\u0001',
      '\u0001',
      '\u0004',
      '\u0004',
      '\u0004',
      '\u0007',
      '\u0007',
      '\t',
      '\n',
      '\n',
      '\f',
      '\r',
      '\u000e',
      '\u000f',
      '\u0010',
      '\u0011',
      '\u0012',
      '\u0013',
      '\u0014',
      '\u0015',
      '\u0016',
      '\u0017',
      '\u0018',
      '\u0019',
      '\u001a',
      '\u001b',
      '\u001c',
      '\u001d',
      '\u0001',
      '\f',
      '\f',
      '\u0000',
      '"',
      '#',
      '$',
      '%',
      '&',
      '\'',
      '(',
      ')',
      '*',
      '+',
      ',',
      '-',
      '.',
      '/',
      '0',
      '1',
      '2',
      '3',
      '\u000b',
      '퀄',
      'ऀ',
      '⸴',
      '⸳',
      '⸰',
      '㈱',
      '4',
      '\u0019',
      '퀄',
      'ᜀ',
      '敒',
      '敳',
      '牡',
      '档',
      '䤠',
      '\u206e',
      '潍',
      '楴',
      '湯',
      '䰠',
      '摴',
      '.',
      '\u0000',
      '\u0001',
      '퀄',
      '\u0002',
      '\u0000',
      '\u0019',
      '퀆',
      '\u0007',
      '\b',
      '\t',
      '\u0012',
      '\u0013',
      '\u0016',
      '\u0017',
      '\u0018',
      '\u0019',
      '#',
      '$',
      '&',
      '\'',
      '(',
      ')',
      '*',
      '+',
      ',',
      '-',
      '.',
      '/',
      '0'
   };
   static final char[] _opLengthVaries = new char[]{
      '\f', '\r', '\u0010', '\u0011', '\n', '\u000b', '\u001a', '"', '\u0001', '퀊', '3', '\u0000', 'Ā', '㽰', 'Ā', '억'
   };
   static final char[] _opLengthOne = new char[]{
      '\u0007',
      '\b',
      '\t',
      '\u0012',
      '\u0013',
      '\u0016',
      '\u0017',
      '\u0018',
      '\u0019',
      '#',
      '$',
      '&',
      '\'',
      '(',
      ')',
      '*',
      '+',
      ',',
      '-',
      '.',
      '/',
      '0',
      '1',
      '2',
      '3',
      '\u0000',
      '\b',
      '퀆',
      '\f',
      '\r',
      '\u0010',
      '\u0011',
      '\n',
      '\u000b',
      '\u001a',
      '"',
      '\u0001',
      '퀊',
      '3',
      '\u0000',
      'Ā',
      '㽰',
      'Ā',
      '억',
      'ࠀ',
      '瀨',
      '?',
      '䈈',
      '来',
      '씁'
   };
   static final int _NULL_OFFSET = -1;
   static final char _NULL_POINTER = '\u0000';

   private OpCode() {
   }

   static final int _getNextOffset(char[] program, int offset) {
      return program[offset + 1];
   }

   static final char _getArg1(char[] program, int offset) {
      return program[offset + 2];
   }

   static final char _getArg2(char[] program, int offset) {
      return program[offset + 3];
   }

   static final int _getOperand(int offset) {
      return offset + 2;
   }

   static final boolean _isInArray(char ch, char[] array, int start) {
      while (start < array.length) {
         if (ch == array[start++]) {
            return true;
         }
      }

      return false;
   }

   static final int _getNextOperator(int offset) {
      return offset + 2;
   }

   static final int _getPrevOperator(int offset) {
      return offset - 2;
   }

   static final int _getNext(char[] program, int offset) {
      if (program == null) {
         return -1;
      } else {
         int offs = _getNextOffset(program, offset);
         if (offs == 0) {
            return -1;
         } else {
            return program[offset] == 13 ? offset - offs : offset + offs;
         }
      }
   }

   static final boolean _isWordCharacter(char token) {
      return Util.isLetterOrDigit(token) || token == '_';
   }
}
