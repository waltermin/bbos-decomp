package net.rim.device.apps.api.phone;

public class PhoneNumberTypes {
   public static final int DEFAULT_NUMBER_TYPE = 0;
   public static final int WORK_NUMBER_TYPE = 1;
   public static final int WORK2_NUMBER_TYPE = 2;
   public static final int HOME_NUMBER_TYPE = 3;
   public static final int HOME2_NUMBER_TYPE = 4;
   public static final int MOBILE_NUMBER_TYPE = 5;
   public static final int PAGER_NUMBER_TYPE = 6;
   public static final int FAX_NUMBER_TYPE = 7;
   public static final int OTHER_NUMBER_TYPE = 8;
   public static final int DC_PRIVATE_TYPE = 9;
   public static final int DC_GROUP_TYPE = 10;
   public static final int DC_UFMI_TYPE = 11;
   public static final int IMODE_NUMBER_TYPE = 12;
   public static final int ALPHANUMERIC_NUMBER_TYPE = 13;
   private static final int[] BACKUP_RESTORE_TYPES = new int[]{
      0,
      1,
      10,
      2,
      11,
      3,
      4,
      5,
      12,
      6,
      7,
      8,
      9,
      13,
      -805044208,
      -16710144,
      -16447741,
      -16449790,
      -16187640,
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
      -805044212,
      1,
      0,
      0,
      -805044212,
      16842753,
      0,
      0,
      -805044223,
      2,
      -804651007,
      51,
      1866858752,
      -1574934772,
      1208025188,
      186346607,
      1870004480,
      290219371,
      1761869835,
      1850731551,
      1950361315,
      1957496320,
      524518073,
      -1153416596,
      1957496320
   };
   public static final char DTMF_PAUSE = ',';
   public static final char DTMF_STOP = '!';
   public static final char PHONE_NUMBER_EXT = 'x';
   public static final char DTMF_HASH = '#';
   public static final char DTMF_STAR = '*';

   public static final int mapType(int type, boolean toBackupRestoreType) {
      if (type >= 0 && type < BACKUP_RESTORE_TYPES.length) {
         if (toBackupRestoreType) {
            return BACKUP_RESTORE_TYPES[type];
         }

         for (int i = BACKUP_RESTORE_TYPES.length - 1; i >= 0; i--) {
            if (BACKUP_RESTORE_TYPES[i] == type) {
               return i;
            }
         }

         throw new Object();
      } else {
         throw new Object();
      }
   }
}
