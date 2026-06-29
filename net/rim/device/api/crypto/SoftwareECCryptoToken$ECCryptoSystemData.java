package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class SoftwareECCryptoToken$ECCryptoSystemData implements CryptoTokenCryptoSystemData, Persistable {
   private String _name;
   private int _fieldBitLength;
   private int _privateKeyLength;
   private static final long ID_EC_CRYPTOSYSTEM_EC160R1 = 4711484561127885927L;
   private static final long ID_EC_CRYPTOSYSTEM_EC163K1 = 8669050953022132342L;
   private static final long ID_EC_CRYPTOSYSTEM_EC163K2 = -2246274589386922826L;
   private static final long ID_EC_CRYPTOSYSTEM_EC163R2 = -4178625280477011559L;
   private static final long ID_EC_CRYPTOSYSTEM_EC192R1 = 2927509199855772890L;
   private static final long ID_EC_CRYPTOSYSTEM_EC224R1 = -5628643458668955280L;
   private static final long ID_EC_CRYPTOSYSTEM_EC233K1 = 5001962570927748793L;
   private static final long ID_EC_CRYPTOSYSTEM_EC233R1 = -2135086818618184682L;
   private static final long ID_EC_CRYPTOSYSTEM_EC239K1 = 2642104233314059064L;
   private static final long ID_EC_CRYPTOSYSTEM_EC256R1 = -5747743302585068881L;
   private static final long ID_EC_CRYPTOSYSTEM_EC283K1 = 4247389242964566074L;
   private static final long ID_EC_CRYPTOSYSTEM_EC283R1 = -5615505479846743283L;
   private static final long ID_EC_CRYPTOSYSTEM_EC384R1 = -5844504442993304878L;
   private static final long ID_EC_CRYPTOSYSTEM_EC409K1 = -1952929009240913506L;
   private static final long ID_EC_CRYPTOSYSTEM_EC409R1 = -5938788230129942524L;
   private static final long ID_EC_CRYPTOSYSTEM_EC521R1 = 1672301953462559163L;
   private static final long ID_EC_CRYPTOSYSTEM_EC571K1 = 6862931462943878845L;
   private static final long ID_EC_CRYPTOSYSTEM_EC571R1 = 4889255806911890676L;
   private static final byte[] EC160R1_PRIVATE_KEY = new byte[]{0, -4, 50, 1, -5, 18, 48, -12, 36, 127, -119, 57, -113, -85, -18, 8, 7, 1, 30, 124, -48};
   private static final byte[] EC160R1_PUBLIC_KEY = new byte[]{
      2, -117, 30, -112, -27, 121, -86, 111, 123, 80, -121, 92, -38, 83, -127, -114, 104, 64, -19, -86, -44
   };
   private static final byte[] EC160R1_SHARED_SECRET = new byte[]{-73, -126, 91, -62, -98, 18, 91, 114, -2, -98, -104, 8, 70, 35, 15, -119, -43, -108, 33, -66};
   private static final byte[] EC163K1_PRIVATE_KEY = new byte[]{
      2, 105, -1, 12, 127, 9, -55, 18, 116, -29, -101, -70, -59, -98, -128, 38, -23, -21, 112, 27, 51
   };
   private static final byte[] EC163K1_PUBLIC_KEY = new byte[]{
      3, 4, -110, -80, 102, -76, -9, -109, 18, 29, -11, 94, 107, 45, -55, -83, 29, 109, 18, -79, -112, 89
   };
   private static final byte[] EC163K1_SHARED_SECRET = new byte[]{
      0, -91, 41, 78, 63, -51, 113, -80, -2, -36, -112, 54, 99, -27, -14, 85, -89, 34, -110, -79, -96
   };
   private static final byte[] EC163K2_PRIVATE_KEY = new byte[]{1, 86, 8, 58, 56, 52, 105, 101, 50, 52, -72, -21, 74, 86, 32, 64, 87, -48, 33, -123, -32};
   private static final byte[] EC163K2_PUBLIC_KEY = new byte[]{2, 3, 28, 119, 83, -61, 107, 1, 65, 89, 7, -4, -93, 59, -47, -48, 122, 13, -72, 45, -80, -83};
   private static final byte[] EC163K2_SHARED_SECRET = new byte[]{1, -92, 50, 6, -51, 44, -59, 1, -45, 84, -119, -111, 41, 26, -13, 4, -36, 94, 20, -5, -117};
   private static final byte[] EC163R2_PRIVATE_KEY = new byte[]{3, 104, 96, -54, 27, -91, 121, 45, -115, 73, 38, 73, -123, 27, -76, 12, 53, 122, 55, 56, -60};
   private static final byte[] EC163R2_PUBLIC_KEY = new byte[]{
      3, 5, 59, 24, 44, -23, -119, 54, -92, -77, -13, 72, -95, -113, 48, 64, 71, 88, 123, 122, -52, 62
   };
   private static final byte[] EC163R2_SHARED_SECRET = new byte[]{
      5, -102, -50, 101, 37, 15, 46, -122, 26, 117, 86, -86, 66, 51, -124, -2, 89, -44, 31, -32, 79
   };
   private static final byte[] EC192R1_PRIVATE_KEY = new byte[]{
      82, -106, 98, -71, 39, 107, -90, 14, -80, 4, -75, -114, -6, -97, 101, -8, -123, -108, -109, 83, 53, -12, -65, -41
   };
   private static final byte[] EC192R1_PUBLIC_KEY = new byte[]{
      3, -5, -16, 82, -50, 1, -107, -69, 75, -34, -92, -43, -114, 74, 25, 68, -32, 123, -31, 119, 8, 90, 97, 119, -67
   };
   private static final byte[] EC192R1_SHARED_SECRET = new byte[]{
      -7, -58, 52, 94, 70, 81, 125, 17, -7, -100, 66, 40, -47, -30, -99, 89, 34, 14, 7, 83, -70, 32, 35, 20
   };
   private static final byte[] EC224R1_PRIVATE_KEY = new byte[]{
      -17, 47, -40, 28, -85, 37, -120, -22, 61, 2, -42, 63, -90, -19, -47, -109, -34, 94, 6, -79, 41, -116, 58, 106, -45, -107, 114, 104
   };
   private static final byte[] EC224R1_PUBLIC_KEY = new byte[]{
      3, 118, 58, 1, 63, -28, 12, 127, 112, 112, -77, -70, -15, -82, -18, -103, 123, 96, -100, -125, 68, 12, 68, -7, -42, -95, -65, 28, -126
   };
   private static final byte[] EC224R1_SHARED_SECRET = new byte[]{
      71, 114, 115, -25, -6, 114, -1, 78, 87, -84, -113, 29, -104, 50, 103, 14, 40, -13, -4, 105, 0, -91, -15, -25, 111, -26, 101, -35
   };
   private static final byte[] EC233K1_PRIVATE_KEY = new byte[]{
      47, 11, -101, -66, -13, 49, 31, 88, 5, -54, 109, 104, -94, -64, -64, -125, 2, 60, 37, 26, -95, 74, -116, 112, -77, -115, 52, -84, 31
   };
   private static final byte[] EC233K1_PUBLIC_KEY = new byte[]{
      2, 1, 31, 96, 119, -48, -65, 70, -73, -124, -120, -67, -89, -109, 51, 102, -125, -87, -59, -78, -3, 105, -100, 87, 101, 120, 127, -22, 106, -17, 89
   };
   private static final byte[] EC233K1_SHARED_SECRET = new byte[]{
      0, 42, 6, 1, 16, -25, 99, 21, 126, -5, 41, -50, -41, -110, 45, 121, 83, 68, -108, 103, 54, 20, -117, -44, -76, 8, -76, -55, -5, -40
   };
   private static final byte[] EC233R1_PRIVATE_KEY = new byte[]{
      0, 109, 123, 120, 36, -45, -104, -26, 44, -14, 35, 17, 79, -71, 57, -87, -26, -7, -85, 40, -100, 25, 107, -117, 97, -51, -30, 109, 77, 68
   };
   private static final byte[] EC233R1_PUBLIC_KEY = new byte[]{
      2, 1, -87, 1, -78, -57, 27, 111, -101, -119, -57, -70, -61, -11, -1, 119, 114, -63, 20, 101, -42, 73, 26, 72, 101, -61, -17, -28, 11, 75, -118
   };
   private static final byte[] EC233R1_SHARED_SECRET = new byte[]{
      0, 16, -37, 94, -8, 71, 28, 26, -100, 125, -126, 19, -86, 96, -69, -91, 90, 100, 91, 78, -61, -124, -44, 121, -2, 114, -49, -30, 66, 122
   };
   private static final byte[] EC239K1_PRIVATE_KEY = new byte[]{
      24, 70, 60, 15, -4, 75, 97, 26, -3, 84, -119, 106, -71, -79, 5, 110, -63, 73, 32, -110, -15, -76, -49, 3, 97, 123, 25, 12, -46, 38
   };
   private static final byte[] EC239K1_PUBLIC_KEY = new byte[]{
      2, 49, 29, -73, 11, -57, 78, -88, 59, -68, 36, 12, 40, 110, 124, -62, -80, -34, 39, -54, -90, -104, -25, -90, 62, -16, -101, 100, 95, 51, 108
   };
   private static final byte[] EC239K1_SHARED_SECRET = new byte[]{
      44, -65, -8, -49, 36, -14, 122, -92, 127, -96, -92, -60, 38, -104, 77, -23, -1, 112, 116, 70, 30, 41, 45, 75, 2, -46, -25, 75, -70, 9
   };
   private static final byte[] EC256R1_PRIVATE_KEY = new byte[]{
      -11, -124, 26, 83, -126, -99, 20, -23, -74, -120, 20, 100, 49, 38, 101, 9, -53, 97, -107, 1, -105, 34, 27, -54, 93, 116, -5, -35, 119, -3, -116, -104
   };
   private static final byte[] EC256R1_PUBLIC_KEY = new byte[]{
      2, -72, 96, 44, 67, 27, -23, 85, -75, -85, 27, 90, -51, 8, -79, 112, 114, -65, 125, 79, -19, 43, -97, 60, -11, -78, -62, -75, -128, -57, 28, 91, 87
   };
   private static final byte[] EC256R1_SHARED_SECRET = new byte[]{
      -43, -126, -70, -113, -74, -45, 109, -124, 0, 114, 118, -107, 28, -41, 35, -26, 52, -25, 55, -85, -30, 30, 74, 68, -79, 71, -72, -29, 23, 26, -35, -69
   };
   private static final byte[] EC283K1_PRIVATE_KEY = new byte[]{
      1,
      114,
      -125,
      51,
      -118,
      3,
      114,
      101,
      -13,
      -108,
      -58,
      -117,
      -118,
      37,
      66,
      102,
      2,
      -70,
      -89,
      -109,
      0,
      81,
      99,
      -46,
      91,
      70,
      61,
      -20,
      -52,
      -120,
      83,
      -39,
      88,
      38,
      -5,
      -113
   };
   private static final byte[] EC283K1_PUBLIC_KEY = new byte[]{
      2,
      3,
      -61,
      -96,
      72,
      108,
      -127,
      -13,
      -39,
      42,
      44,
      85,
      73,
      119,
      117,
      79,
      -94,
      -122,
      29,
      11,
      -11,
      -93,
      69,
      -69,
      120,
      48,
      -9,
      109,
      105,
      -42,
      -26,
      13,
      -119,
      74,
      -28,
      -42,
      85
   };
   private static final byte[] EC283K1_SHARED_SECRET = new byte[]{
      4,
      116,
      67,
      -66,
      9,
      55,
      66,
      -112,
      -119,
      -54,
      68,
      -86,
      57,
      109,
      -28,
      29,
      -19,
      -9,
      37,
      17,
      86,
      -128,
      56,
      -50,
      23,
      -11,
      -51,
      58,
      -84,
      -110,
      -1,
      18,
      -76,
      -82,
      -31,
      -78
   };
   private static final byte[] EC283R1_PRIVATE_KEY = new byte[]{
      1,
      -24,
      -80,
      -71,
      28,
      -92,
      34,
      51,
      -102,
      -37,
      53,
      75,
      31,
      -95,
      87,
      -4,
      12,
      -19,
      -102,
      -112,
      18,
      -87,
      95,
      -34,
      -86,
      -35,
      40,
      -24,
      -92,
      59,
      45,
      64,
      69,
      -73,
      39,
      -18
   };
   private static final byte[] EC283R1_PUBLIC_KEY = new byte[]{
      3,
      7,
      -65,
      26,
      -63,
      42,
      86,
      68,
      79,
      58,
      -39,
      28,
      -14,
      109,
      -72,
      61,
      78,
      65,
      -9,
      -49,
      -14,
      5,
      96,
      5,
      44,
      -76,
      99,
      -1,
      102,
      26,
      -40,
      -66,
      37,
      123,
      -45,
      23,
      25
   };
   private static final byte[] EC283R1_SHARED_SECRET = new byte[]{
      1,
      39,
      -14,
      25,
      -99,
      0,
      30,
      -86,
      16,
      -94,
      52,
      -51,
      -4,
      -114,
      -70,
      -33,
      60,
      116,
      67,
      -7,
      82,
      87,
      -84,
      35,
      26,
      -42,
      121,
      29,
      -14,
      -89,
      -31,
      -18,
      -106,
      -113,
      45,
      90
   };
   private static final byte[] EC384R1_PRIVATE_KEY = new byte[]{
      -15,
      106,
      -36,
      -62,
      -108,
      -106,
      87,
      -106,
      48,
      119,
      48,
      -58,
      -83,
      -45,
      26,
      -91,
      -93,
      82,
      113,
      -86,
      -69,
      -91,
      114,
      47,
      12,
      122,
      86,
      75,
      80,
      0,
      -48,
      55,
      -51,
      93,
      -123,
      -82,
      32,
      -83,
      37,
      17,
      120,
      -56,
      -11,
      111,
      97,
      67,
      -48,
      -116
   };
   private static final byte[] EC384R1_PUBLIC_KEY = new byte[]{
      3,
      46,
      91,
      -39,
      54,
      124,
      -74,
      -121,
      20,
      -37,
      -52,
      -31,
      -105,
      -66,
      20,
      -86,
      -113,
      21,
      122,
      110,
      5,
      -73,
      34,
      68,
      63,
      -83,
      44,
      -14,
      -46,
      -18,
      22,
      36,
      123,
      28,
      -32,
      46,
      30,
      98,
      -31,
      58,
      -103,
      18,
      -37,
      114,
      -61,
      -110,
      -59,
      87,
      -27
   };
   private static final byte[] EC384R1_SHARED_SECRET = new byte[]{
      -84,
      64,
      8,
      -89,
      6,
      17,
      63,
      -93,
      -107,
      -40,
      108,
      -102,
      -30,
      -57,
      19,
      86,
      -72,
      4,
      33,
      -17,
      -128,
      75,
      118,
      -119,
      114,
      6,
      -41,
      -110,
      -88,
      42,
      -67,
      -29,
      91,
      26,
      -118,
      -96,
      -113,
      -127,
      -103,
      5,
      55,
      -76,
      82,
      84,
      19,
      14,
      -94,
      90
   };
   private static final byte[] EC409K1_PRIVATE_KEY = new byte[]{
      22,
      -109,
      104,
      -76,
      71,
      -108,
      -56,
      117,
      -54,
      -104,
      -76,
      -2,
      -91,
      51,
      -2,
      -84,
      -80,
      77,
      -120,
      -106,
      -125,
      98,
      -109,
      -6,
      5,
      119,
      68,
      7,
      75,
      -19,
      29,
      -74,
      98,
      -76,
      -61,
      -13,
      -9,
      16,
      14,
      -2,
      -17,
      -20,
      83,
      51,
      43,
      3,
      -110,
      -10,
      -79,
      -85,
      -109
   };
   private static final byte[] EC409K1_PUBLIC_KEY = new byte[]{
      3,
      0,
      -38,
      120,
      -9,
      63,
      53,
      44,
      79,
      118,
      104,
      -15,
      49,
      106,
      114,
      -109,
      49,
      45,
      114,
      -93,
      -102,
      -12,
      -75,
      -36,
      122,
      49,
      57,
      49,
      30,
      43,
      73,
      -98,
      33,
      47,
      118,
      114,
      37,
      -77,
      -127,
      89,
      82,
      -66,
      40,
      99,
      -40,
      -68,
      13,
      63,
      -60,
      49,
      114,
      119,
      -102
   };
   private static final byte[] EC409K1_SHARED_SECRET = new byte[]{
      0,
      121,
      101,
      -44,
      -18,
      -50,
      45,
      -52,
      -100,
      123,
      -20,
      -91,
      -64,
      119,
      109,
      -76,
      43,
      52,
      51,
      -8,
      -41,
      101,
      -37,
      -39,
      -91,
      80,
      8,
      -111,
      38,
      -42,
      26,
      50,
      -71,
      -55,
      48,
      12,
      -116,
      30,
      78,
      25,
      -69,
      104,
      -27,
      -43,
      87,
      70,
      104,
      33,
      25,
      -97,
      -42,
      116
   };
   private static final byte[] EC409R1_PRIVATE_KEY = new byte[]{
      0,
      22,
      102,
      -23,
      -72,
      83,
      63,
      -17,
      117,
      118,
      56,
      -29,
      23,
      -118,
      39,
      -100,
      -36,
      -15,
      79,
      109,
      91,
      11,
      98,
      -62,
      -35,
      51,
      -23,
      47,
      -47,
      27,
      108,
      -23,
      -11,
      -30,
      -89,
      95,
      46,
      -70,
      -5,
      62,
      -69,
      100,
      39,
      -34,
      32,
      110,
      78,
      -115,
      -116,
      -49,
      -33,
      -61
   };
   private static final byte[] EC409R1_PUBLIC_KEY = new byte[]{
      2,
      0,
      -111,
      55,
      -16,
      -101,
      -112,
      113,
      -62,
      99,
      -49,
      -9,
      69,
      -88,
      -70,
      60,
      31,
      31,
      43,
      -90,
      -30,
      -90,
      73,
      69,
      -79,
      -16,
      2,
      -125,
      103,
      4,
      -25,
      25,
      -47,
      -61,
      -92,
      -108,
      -75,
      21,
      -47,
      59,
      -73,
      118,
      56,
      108,
      -92,
      114,
      -57,
      -52,
      -38,
      -87,
      109,
      -97,
      63
   };
   private static final byte[] EC409R1_SHARED_SECRET = new byte[]{
      1,
      -45,
      -111,
      120,
      52,
      -112,
      123,
      51,
      -115,
      -40,
      38,
      18,
      114,
      -6,
      -45,
      -80,
      15,
      -34,
      7,
      -19,
      28,
      103,
      107,
      -12,
      100,
      46,
      103,
      41,
      -40,
      53,
      -27,
      -11,
      -100,
      -58,
      110,
      -37,
      105,
      17,
      57,
      -53,
      -108,
      117,
      94,
      40,
      -39,
      -105,
      -112,
      40,
      -35,
      64,
      -94,
      -123
   };
   private static final byte[] EC521R1_PRIVATE_KEY = new byte[]{
      1,
      91,
      22,
      -72,
      -34,
      54,
      -100,
      115,
      -36,
      -113,
      106,
      109,
      -87,
      -91,
      44,
      78,
      54,
      -24,
      11,
      123,
      124,
      49,
      -89,
      79,
      -40,
      -16,
      -1,
      35,
      54,
      75,
      66,
      -57,
      115,
      102,
      64,
      -107,
      -116,
      73,
      80,
      98,
      0,
      127,
      94,
      -8,
      71,
      26,
      -117,
      16,
      -119,
      -9,
      121,
      58,
      44,
      37,
      -94,
      51,
      123,
      29,
      -74,
      46,
      24,
      55,
      14,
      56,
      -50,
      22
   };
   private static final byte[] EC521R1_PUBLIC_KEY = new byte[]{
      2,
      1,
      81,
      29,
      44,
      120,
      -48,
      -33,
      99,
      99,
      80,
      -12,
      -53,
      -115,
      -127,
      9,
      -2,
      49,
      -123,
      9,
      98,
      -71,
      32,
      -88,
      22,
      -78,
      -113,
      74,
      -103,
      86,
      -96,
      -1,
      20,
      117,
      -90,
      50,
      -18,
      -41,
      -103,
      11,
      -125,
      120,
      -120,
      48,
      20,
      -63,
      3,
      64,
      -76,
      105,
      3,
      97,
      -73,
      36,
      -83,
      114,
      86,
      -68,
      106,
      -94,
      -37,
      7,
      2,
      -88,
      92,
      30,
      39
   };
   private static final byte[] EC521R1_SHARED_SECRET = new byte[]{
      1,
      -75,
      127,
      1,
      78,
      -58,
      126,
      -12,
      96,
      14,
      -107,
      -41,
      -24,
      110,
      -57,
      40,
      27,
      98,
      -106,
      -82,
      -104,
      60,
      -127,
      44,
      -109,
      -12,
      97,
      -110,
      70,
      -2,
      -118,
      111,
      -108,
      96,
      10,
      80,
      -111,
      -122,
      36,
      27,
      -54,
      -6,
      -115,
      85,
      76,
      -71,
      -41,
      -31,
      -51,
      86,
      -10,
      43,
      68,
      -52,
      82,
      -41,
      -4,
      -64,
      -43,
      -93,
      37,
      -67,
      93,
      -38,
      28,
      1
   };
   private static final byte[] EC571K1_PRIVATE_KEY = new byte[]{
      0,
      49,
      91,
      -109,
      1,
      -33,
      -89,
      23,
      -56,
      -114,
      124,
      -87,
      -78,
      68,
      -113,
      103,
      121,
      -86,
      39,
      5,
      95,
      -71,
      -47,
      -26,
      28,
      110,
      -122,
      -109,
      34,
      -41,
      82,
      6,
      -70,
      -62,
      50,
      35,
      -2,
      -124,
      0,
      81,
      3,
      3,
      89,
      -29,
      -120,
      -48,
      -65,
      110,
      -20,
      -20,
      -8,
      102,
      115,
      -99,
      -106,
      125,
      126,
      -44,
      -25,
      -126,
      54,
      -84,
      -49,
      88,
      98,
      14,
      -55,
      76,
      -123,
      -88,
      51,
      -25
   };
   private static final byte[] EC571K1_PUBLIC_KEY = new byte[]{
      3,
      6,
      -38,
      -51,
      -46,
      -93,
      -101,
      16,
      -35,
      27,
      -98,
      103,
      -125,
      -110,
      28,
      -101,
      66,
      23,
      93,
      87,
      3,
      83,
      -120,
      79,
      79,
      -87,
      1,
      -40,
      51,
      -112,
      -48,
      -85,
      -83,
      -24,
      -66,
      -60,
      112,
      13,
      87,
      76,
      -25,
      -110,
      71,
      -5,
      -27,
      29,
      101,
      -78,
      79,
      68,
      -22,
      38,
      -17,
      110,
      -35,
      -32,
      -33,
      70,
      107,
      5,
      -7,
      101,
      -66,
      29,
      -6,
      -54,
      12,
      -125,
      15,
      -31,
      94,
      91,
      78
   };
   private static final byte[] EC571K1_SHARED_SECRET = new byte[]{
      5,
      -126,
      -25,
      -7,
      -107,
      -26,
      -69,
      37,
      -60,
      -39,
      50,
      -5,
      5,
      -7,
      -9,
      35,
      36,
      29,
      -28,
      35,
      -41,
      111,
      54,
      -89,
      -87,
      111,
      -100,
      -63,
      -124,
      -66,
      -12,
      48,
      -105,
      120,
      -18,
      9,
      11,
      -60,
      25,
      33,
      -105,
      -74,
      -108,
      -16,
      -88,
      52,
      -47,
      64,
      -122,
      33,
      85,
      -23,
      86,
      -24,
      -127,
      -128,
      105,
      98,
      62,
      -44,
      -5,
      -67,
      72,
      102,
      -62,
      -12,
      -106,
      -59,
      124,
      70,
      70,
      65
   };
   private static final byte[] EC571R1_PRIVATE_KEY = new byte[]{
      0,
      -60,
      -55,
      -55,
      77,
      -106,
      -54,
      72,
      78,
      28,
      -43,
      -66,
      -52,
      -55,
      -128,
      -109,
      -42,
      -104,
      81,
      105,
      17,
      -39,
      66,
      -107,
      -10,
      -121,
      10,
      -33,
      -26,
      56,
      -109,
      -103,
      -27,
      60,
      -73,
      -52,
      36,
      -98,
      22,
      16,
      -111,
      -72,
      74,
      75,
      109,
      -88,
      78,
      -65,
      -45,
      -95,
      91,
      47,
      10,
      -104,
      125,
      -20,
      -30,
      50,
      -90,
      -98,
      86,
      18,
      74,
      -41,
      -63,
      -99,
      -1,
      -102,
      -32,
      18,
      5,
      31
   };
   private static final byte[] EC571R1_PUBLIC_KEY = new byte[]{
      2,
      6,
      92,
      -16,
      -12,
      35,
      -115,
      -60,
      -69,
      49,
      -1,
      101,
      33,
      123,
      -34,
      70,
      -126,
      -53,
      72,
      27,
      -15,
      104,
      -21,
      104,
      -76,
      56,
      97,
      -82,
      121,
      -80,
      109,
      -121,
      90,
      117,
      5,
      -118,
      59,
      -87,
      95,
      -109,
      79,
      -13,
      -115,
      -26,
      -39,
      -123,
      59,
      -25,
      19,
      -41,
      107,
      -98,
      70,
      58,
      -65,
      -43,
      38,
      -98,
      -108,
      -118,
      2,
      -57,
      -11,
      -47,
      94,
      52,
      101,
      63,
      114,
      62,
      93,
      112,
      2
   };
   private static final byte[] EC571R1_SHARED_SECRET = new byte[]{
      6,
      -121,
      -41,
      5,
      23,
      -96,
      72,
      -45,
      83,
      33,
      104,
      -53,
      -5,
      12,
      11,
      -56,
      -15,
      -112,
      21,
      -117,
      -52,
      -114,
      122,
      -122,
      -101,
      -22,
      58,
      -126,
      -41,
      -127,
      -121,
      -29,
      -88,
      63,
      -92,
      77,
      29,
      57,
      57,
      40,
      -60,
      -94,
      106,
      -20,
      45,
      -35,
      15,
      -53,
      -59,
      8,
      123,
      117,
      40,
      -85,
      -82,
      10,
      42,
      -88,
      -86,
      -36,
      10,
      106,
      -20,
      -93,
      95,
      -9,
      -47,
      -50,
      -81,
      45,
      -33,
      59
   };
   private static final long[] IDS = new long[]{
      4711484561127885927L,
      8669050953022132342L,
      -2246274589386922826L,
      -4178625280477011559L,
      2927509199855772890L,
      -5628643458668955280L,
      5001962570927748793L,
      -2135086818618184682L,
      2642104233314059064L,
      -5747743302585068881L,
      4247389242964566074L,
      -5615505479846743283L,
      -5844504442993304878L,
      -1952929009240913506L,
      -5938788230129942524L,
      1672301953462559163L,
      6862931462943878845L,
      4889255806911890676L,
      9126810120284798986L,
      12831621743250L,
      -8119883923002753004L,
      -9027022681462332651L,
      -784654447060653150L,
      -2464388553769746424L,
      -3457638406897397504L,
      -6683604601724714836L,
      6202520887886665877L,
      -8541556626121751368L,
      -2036424552995158414L,
      403496095500081755L,
      6530797885822514231L,
      -4441812881490313196L,
      619419764440896158L,
      -4746348887423376570L,
      -942845798741966592L,
      -7146768748820762206L,
      -2542840331892870373L,
      -7258170961458700419L,
      -2746739194685441231L,
      -4383013297266471606L,
      -7639250391934148825L,
      -1147387436413813222L,
      2571314010282016678L,
      -1760794251456167598L,
      5989477752984045544L,
      7932255717994828531L,
      4794785443441016966L,
      1319709811312162329L,
      1240095099913125050L,
      8194186908688877729L,
      1051271906946503540L,
      246352162105133756L,
      8994463438791149945L,
      170298252047059818L,
      5860747827149395317L,
      6323869187752796077L,
      8128088806197676643L,
      5748880413877178802L,
      8053623704577499224L,
      8875889796307029819L,
      -1443277109140063870L,
      2932217352827684646L,
      -3136073911467403094L,
      3363861584727539984L,
      705126321039898333L,
      -1605188940442920343L,
      -3457638473601384628L,
      -8904228107192728875L,
      -1863409303034629632L,
      4920779501739697972L,
      -4909739328856897615L,
      3466820462913519632L,
      7200597122842600842L,
      -3457638606780415521L,
      -3457638493654613790L,
      -1546945055148199953L,
      -7795188181710273987L,
      7654584627361111774L,
      -3457638406002928173L,
      -7613451079377982735L,
      -6549689964749097168L,
      3418977292564648611L,
      4021714812103195148L,
      1235584028661603789L,
      -8300060027474556808L,
      5988244845834272800L,
      7211539222502874498L,
      114104890231694897L,
      -2451237626001546601L,
      -3457638061598507657L,
      -2432616878042979849L,
      -5091398310096552757L,
      -9013326855663545223L,
      7458531891642914809L,
      5717835099640650065L,
      5311011152384717286L,
      1375331046985527574L,
      -2186840064581316681L,
      2922128435792451132L,
      8477660346351264736L,
      3828616085534633617L,
      4261532772557648226L,
      -8534231508667226932L,
      477352264023901096L,
      2690192143693548434L,
      3047195313716330852L,
      6788269314593259544L,
      2901053703534235974L,
      5982766170680189649L,
      -3457638063820037958L,
      2959556674720071677L,
      -1737003871323365550L,
      19403340781130230L,
      7576833267374956227L,
      4420840835126418757L,
      3511389089166940301L,
      4287945180526136310L,
      -5233069575570711168L,
      -1684452206116300437L,
      -6216287165441082620L,
      6285890503107411671L,
      -359145484617988377L,
      3819829970899039875L,
      8500036945435038548L,
      -941486692560556301L,
      -4106921320064154846L,
      -7441392447306596344L,
      -3457638579282226058L,
      -1L,
      72209795001811200L,
      -9027058289035852950L,
      5783543052073238689L,
      5459323725841288685L,
      45461204248838469L,
      -1346504522704131583L,
      45532331301183949L,
      55649586314376705L,
      2263344689738377476L,
      4049353253203297260L,
      4405159166451713024L,
      6804452967444972544L,
      3766220864923173888L,
      3621829699428746240L,
      -1771229754835794944L
   };
   private static final byte[][] PRIVATE_KEYS = new byte[][]{
      EC160R1_PRIVATE_KEY,
      EC163K1_PRIVATE_KEY,
      EC163K2_PRIVATE_KEY,
      EC163R2_PRIVATE_KEY,
      EC192R1_PRIVATE_KEY,
      EC224R1_PRIVATE_KEY,
      EC233K1_PRIVATE_KEY,
      EC233R1_PRIVATE_KEY,
      EC239K1_PRIVATE_KEY,
      EC256R1_PRIVATE_KEY,
      EC283K1_PRIVATE_KEY,
      EC283R1_PRIVATE_KEY,
      EC384R1_PRIVATE_KEY,
      EC409K1_PRIVATE_KEY,
      EC409R1_PRIVATE_KEY,
      EC521R1_PRIVATE_KEY,
      EC571K1_PRIVATE_KEY,
      EC571R1_PRIVATE_KEY
   };
   private static final byte[][] PUBLIC_KEYS = new byte[][]{
      EC160R1_PUBLIC_KEY,
      EC163K1_PUBLIC_KEY,
      EC163K2_PUBLIC_KEY,
      EC163R2_PUBLIC_KEY,
      EC192R1_PUBLIC_KEY,
      EC224R1_PUBLIC_KEY,
      EC233K1_PUBLIC_KEY,
      EC233R1_PUBLIC_KEY,
      EC239K1_PUBLIC_KEY,
      EC256R1_PUBLIC_KEY,
      EC283K1_PUBLIC_KEY,
      EC283R1_PUBLIC_KEY,
      EC384R1_PUBLIC_KEY,
      EC409K1_PUBLIC_KEY,
      EC409R1_PUBLIC_KEY,
      EC521R1_PUBLIC_KEY,
      EC571K1_PUBLIC_KEY,
      EC571R1_PUBLIC_KEY
   };
   private static final byte[][] SHARED_SECRETS = new byte[][]{
      EC160R1_SHARED_SECRET,
      EC163K1_SHARED_SECRET,
      EC163K2_SHARED_SECRET,
      EC163R2_SHARED_SECRET,
      EC192R1_SHARED_SECRET,
      EC224R1_SHARED_SECRET,
      EC233K1_SHARED_SECRET,
      EC233R1_SHARED_SECRET,
      EC239K1_SHARED_SECRET,
      EC256R1_SHARED_SECRET,
      EC283K1_SHARED_SECRET,
      EC283R1_SHARED_SECRET,
      EC384R1_SHARED_SECRET,
      EC409K1_SHARED_SECRET,
      EC409R1_SHARED_SECRET,
      EC521R1_SHARED_SECRET,
      EC571K1_SHARED_SECRET,
      EC571R1_SHARED_SECRET
   };

   public static final void selfTest(String name, boolean checkRegistry) {
      int index = getIndex(name);
      if (index == -1) {
         throw new IllegalArgumentException();
      }

      if (checkRegistry) {
         long ID = IDS[index];
         ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
         Object instance = appRegistry.getOrWaitFor(ID);
         if (instance == null) {
            selfTest(name, index);
            appRegistry.put(ID, appRegistry);
            return;
         }
      } else {
         selfTest(name, index);
      }
   }

   private static final void selfTest(String name, int index) {
      byte[] privateKey = PRIVATE_KEYS[index];
      byte[] publicKey = PUBLIC_KEYS[index];
      byte[] sharedSecret = SHARED_SECRETS[index];
      byte[] testSecret = new byte[sharedSecret.length];
      Certicom.assertAccessAllowed();
      NativeEC.generateDHSharedSecretNoCofactor(name, privateKey, publicKey, testSecret, 0);
      if (!Arrays.equals(testSecret, sharedSecret)) {
         throw new CryptoSelfTestError();
      }
   }

   private static final int getIndex(String name) {
      if (name.equals("EC160R1")) {
         return 0;
      } else if (name.equals("EC163K1")) {
         return 1;
      } else if (name.equals("EC163K2")) {
         return 2;
      } else if (name.equals("EC163R2")) {
         return 3;
      } else if (name.equals("EC192R1")) {
         return 4;
      } else if (name.equals("EC224R1")) {
         return 5;
      } else if (name.equals("EC233K1")) {
         return 6;
      } else if (name.equals("EC233R1")) {
         return 7;
      } else if (name.equals("EC239K1")) {
         return 8;
      } else if (name.equals("EC256R1")) {
         return 9;
      } else if (name.equals("EC283K1")) {
         return 10;
      } else if (name.equals("EC283R1")) {
         return 11;
      } else if (name.equals("EC384R1")) {
         return 12;
      } else if (name.equals("EC409K1")) {
         return 13;
      } else if (name.equals("EC409R1")) {
         return 14;
      } else if (name.equals("EC521R1")) {
         return 15;
      } else if (name.equals("EC571K1")) {
         return 16;
      } else {
         return name.equals("EC571R1") ? 17 : -1;
      }
   }

   public SoftwareECCryptoToken$ECCryptoSystemData(String name) throws UnsupportedCryptoSystemException {
      if (name == null) {
         throw new IllegalArgumentException();
      }

      if (name.equals("EC160R1")) {
         this._fieldBitLength = 160;
         this._privateKeyLength = 21;
      } else if (name.equals("EC163K1")) {
         this._fieldBitLength = 163;
         this._privateKeyLength = 21;
      } else if (name.equals("EC163K2")) {
         this._fieldBitLength = 163;
         this._privateKeyLength = 21;
      } else if (name.equals("EC163R2")) {
         this._fieldBitLength = 163;
         this._privateKeyLength = 21;
      } else if (name.equals("EC192R1")) {
         this._fieldBitLength = 192;
         this._privateKeyLength = 24;
      } else if (name.equals("EC224R1")) {
         this._fieldBitLength = 224;
         this._privateKeyLength = 28;
      } else if (name.equals("EC233K1")) {
         this._fieldBitLength = 233;
         this._privateKeyLength = 29;
      } else if (name.equals("EC233R1")) {
         this._fieldBitLength = 233;
         this._privateKeyLength = 30;
      } else if (name.equals("EC239K1")) {
         this._fieldBitLength = 239;
         this._privateKeyLength = 30;
      } else if (name.equals("EC256R1")) {
         this._fieldBitLength = 256;
         this._privateKeyLength = 32;
      } else if (name.equals("EC283K1")) {
         this._fieldBitLength = 283;
         this._privateKeyLength = 36;
      } else if (name.equals("EC283R1")) {
         this._fieldBitLength = 283;
         this._privateKeyLength = 36;
      } else if (name.equals("EC384R1")) {
         this._fieldBitLength = 384;
         this._privateKeyLength = 48;
      } else if (name.equals("EC409K1")) {
         this._fieldBitLength = 409;
         this._privateKeyLength = 51;
      } else if (name.equals("EC409R1")) {
         this._fieldBitLength = 409;
         this._privateKeyLength = 52;
      } else if (name.equals("EC521R1")) {
         this._fieldBitLength = 521;
         this._privateKeyLength = 66;
      } else if (name.equals("EC571K1")) {
         this._fieldBitLength = 571;
         this._privateKeyLength = 72;
      } else {
         if (!name.equals("EC571R1")) {
            throw new UnsupportedCryptoSystemException();
         }

         this._fieldBitLength = 571;
         this._privateKeyLength = 72;
      }

      this._name = name;
      selfTest(this._name, true);
   }

   public final String getAlgorithm() {
      return "EC";
   }

   public final String getName() {
      return this._name;
   }

   public final int getBitLength() {
      return this._fieldBitLength;
   }

   public final int getFieldLength() {
      return this._fieldBitLength + 7 >>> 3;
   }

   public final byte[] getGroupOrder() {
      byte[] order = new byte[this.getPrivateKeyLength()];
      NativeEC.getGroupOrder(this.getName(), order);
      return order;
   }

   public final byte[] getA() {
      byte[] a = new byte[this.getPublicKeyLength(true) - 1];
      NativeEC.getA(this.getName(), a);
      return a;
   }

   public final byte[] getB() {
      byte[] b = new byte[this.getPublicKeyLength(true) - 1];
      NativeEC.getB(this.getName(), b);
      return b;
   }

   public final byte[] getCofactor() {
      byte[] coFactor = new byte[this.getPrivateKeyLength()];
      NativeEC.getCofactor(this.getName(), coFactor);
      return coFactor;
   }

   public final byte[] getFieldReductor() {
      byte[] fieldReductor = new byte[this.getPublicKeyLength(true) - 1];
      NativeEC.getFieldReductor(this.getName(), fieldReductor);
      return fieldReductor;
   }

   public final byte[] getBasePoint() {
      byte[] basePoint = new byte[this.getPublicKeyLength(true)];
      NativeEC.getBasePoint(this.getName(), basePoint);
      return basePoint;
   }

   public final int getPublicKeyLength(boolean compress) {
      int fieldLength = this.getFieldLength();
      return compress ? 1 + fieldLength : 1 + fieldLength * 2;
   }

   public final int getPrivateKeyLength() {
      return this._privateKeyLength;
   }

   @Override
   public final int hashCode() {
      return this._name.hashCode();
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof SoftwareECCryptoToken$ECCryptoSystemData)) {
         return false;
      }

      SoftwareECCryptoToken$ECCryptoSystemData other = (SoftwareECCryptoToken$ECCryptoSystemData)obj;
      return this._name.equals(other._name);
   }
}
