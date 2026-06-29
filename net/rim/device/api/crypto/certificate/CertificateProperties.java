package net.rim.device.api.crypto.certificate;

public class CertificateProperties {
   public static final long INCOMPLETE_CERTIFICATE_CHAIN = 1L;
   public static final long UNVERIFIED_CERTIFICATE_CHAIN = 2L;
   public static final long UNSUPPORTED_CERTIFICATE_CHAIN = 4L;
   public static final long UNTRUSTED_CERTIFICATE_CHAIN = 8L;
   public static final long IMPROPER_CERTIFICATE_CHAIN = 16L;
   public static final long WEAK_CERTIFICATE_CHAIN = 32L;
   public static final long WEAK_CERTIFICATE_CHAIN_DIGEST = 64L;
   public static final long INVALID_CERTIFICATE_PRESENT = 256L;
   public static final long UNKNOWN_CERTIFICATE_STATUS_PRESENT = 512L;
   public static final long REVOKED_CERTIFICATE_STATUS_PRESENT = 1024L;
   public static final long STALE_CERTIFICATE_STATUS = 2048L;
   protected static final long REVOCATION_REASON_BASE = 65536L;
   public static final long REVOCATION_REASON_UNSPECIFIED = 65536L;
   public static final long REVOCATION_REASON_KEY_COMPROMISE = 131072L;
   public static final long REVOCATION_REASON_CA_COMPROMISE = 262144L;
   public static final long REVOCATION_REASON_AFFILIATION_CHANGED = 524288L;
   public static final long REVOCATION_REASON_SUPERSEDED = 1048576L;
   public static final long REVOCATION_REASON_CESSATION_OF_OPERATION = 2097152L;
   public static final long REVOCATION_REASON_CERTIFICATE_HOLD = 4194304L;
   public static final long REVOCATION_REASON_REMOVE_FROM_CRL = 8388608L;
   public static final long REVOCATION_REASON_KEY_RETIRED = 16777216L;
   public static final long REVOCATION_REASON_USER_ID_INVALID = 33554432L;
   private static final long REVOCATION_REASONS = 67043328L;
   private static final long[] DEFAULT_PROPERTY_MASKS = new long[]{
      0L,
      2048L,
      2560L,
      2561L,
      2569L,
      2665L,
      2921L,
      67047273L,
      -1L,
      281482761601027L,
      -19241325442105216L,
      6681112509884238071L,
      4986650333094383814L,
      4949277363912784388L,
      -5309859595510315154L,
      -3497212945035871431L,
      2519355743581185242L,
      -7478667850944519410L,
      -8280139724619771058L,
      9203626808360806622L,
      8547815165735258709L,
      -2840458090114686713L,
      2493558009283206128L,
      -8350052237687961942L,
      1986693432473023698L,
      -7996408032032705846L,
      -3457638061731181474L,
      -2373573841753803485L,
      -1263802998974357680L,
      8093113065058007868L,
      7167957328424738691L,
      -7380353906130183769L,
      -4166697909008668970L,
      -7885118507828575110L,
      -5668085561317695529L,
      9043900041976131752L,
      -412702643676041590L,
      -7455544238665746233L,
      -8593150405769747932L,
      -2256328375746954403L,
      7834736723287595831L,
      -6311709754935427707L,
      2280910026751621589L,
      5937847871381700656L,
      -6055500861380576768L,
      5583698233016296748L,
      -6159040544578003602L,
      4245089183929703979L,
      -5075307775194835295L,
      -3457638577843093074L,
      1849573705804247089L,
      2969811906891874432L,
      -8879498174193550978L,
      -5978301132642570890L,
      8069105860954747468L,
      -8091179122740210253L,
      3123043033483209164L,
      -5097961589567306680L,
      1902060808813871308L,
      -7511651988648118590L,
      5425343464578927495L,
      7892672475876239102L,
      -4251203633763373094L,
      -2991731429124715881L,
      -5024616886264106114L,
      -4587871980611231061L,
      -2191044501481094902L,
      221153280575L,
      18932842905993267L,
      23154967561454418L,
      -3457638583843401134L,
      20061986658402881L
   };

   public static long selectBestProperties(long[] properties) {
      int index = selectBest(properties);
      return properties[index];
   }

   public static int selectBest(long[] properties) {
      int[] returnValues = select(properties, DEFAULT_PROPERTY_MASKS);
      return returnValues[0];
   }

   public static int[] select(long[] properties, long[] propertyMasks) {
      if (properties != null && properties.length != 0 && propertyMasks != null && propertyMasks.length != 0) {
         int[] returnValues = new int[2];
         int numProperties = properties.length;
         int numPropertyMasks = propertyMasks.length;

         for (int m = 0; m < numPropertyMasks; m++) {
            long currentMask = propertyMasks[m];

            for (int n = 0; n < numProperties; n++) {
               if ((properties[n] & (currentMask ^ -1)) == 0) {
                  returnValues[0] = n;
                  returnValues[1] = m;
                  return returnValues;
               }
            }
         }

         returnValues[0] = -1;
         returnValues[1] = -1;
         return returnValues;
      } else {
         throw new IllegalArgumentException();
      }
   }
}
