package net.rim.device.apps.internal.browser.cookie;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.internal.browser.util.DateCache;
import net.rim.device.apps.internal.browser.util.DomainUtilities;

public final class Cookie implements Persistable, EncryptableProvider {
   private Object _requestHost;
   private Object _requestURI;
   private Object _name;
   private Object _value;
   private Object _domain;
   private Object _path;
   private long _maxAgeMillis = -1;
   private long _timeCreatedMillis;
   private boolean _secure;

   public final boolean domainMatch(String hostnameA, String hostnameB) {
      if (!hostnameA.equals(hostnameB)
         || (!DomainUtilities.isIPAddress(hostnameA) || !DomainUtilities.isIPAddress(hostnameB))
            && (!DomainUtilities.isFQDN(hostnameA) || !DomainUtilities.isFQDN(hostnameB))
            && (!DomainUtilities.isPartialDN(hostnameA) || !DomainUtilities.isPartialDN(hostnameB))) {
         if (!DomainUtilities.isFQDN(hostnameA)) {
            return false;
         }

         if (hostnameA.length() + 1 == hostnameB.length() && hostnameB.charAt(0) == '.' && hostnameB.endsWith(hostnameA)) {
            return true;
         }

         if (!hostnameA.endsWith(hostnameB)) {
            return false;
         }

         int domainSubIndex = hostnameA.indexOf(hostnameB);
         if (domainSubIndex < 1) {
            return false;
         }

         if (hostnameB.startsWith(".")) {
            if (!DomainUtilities.isFQDN(hostnameB.substring(1))) {
               return false;
            }
         } else if (!DomainUtilities.isFQDN(hostnameB) || hostnameA.charAt(domainSubIndex - 1) != '.') {
            return false;
         }

         return true;
      } else {
         return true;
      }
   }

   public final String encode() {
      StringBuffer cookieBuffer = (StringBuffer)(new Object());
      if (this._name != null) {
         cookieBuffer.append(this.getName());
         cookieBuffer.append('=');
      }

      cookieBuffer.append(this.getValue());
      return cookieBuffer.toString();
   }

   public final String getRequestHost() {
      return PersistentContent.decodeString(this._requestHost);
   }

   public final String getRequestURI() {
      return PersistentContent.decodeString(this._requestURI);
   }

   public final String getName() {
      return PersistentContent.decodeString(this._name);
   }

   public final String getValue() {
      return PersistentContent.decodeString(this._value);
   }

   public final String getDomain() {
      if (this._domain == null) {
         return this.getRequestHost();
      }

      String domain = PersistentContent.decodeString(this._domain);
      domain = this.stripDoubleQuotes(domain);
      if (!domain.startsWith(".") && !DomainUtilities.isIPAddress(domain)) {
         domain = ((StringBuffer)(new Object())).append('.').append(domain).toString();
      }

      return domain;
   }

   public final String getPath() {
      if (this._path == null) {
         String requestUri = this.getRequestURI();
         int rightMostSlash = requestUri.lastIndexOf(47);
         return rightMostSlash >= 0 ? requestUri.substring(0, rightMostSlash + 1) : "/";
      } else {
         String path = PersistentContent.decodeString(this._path);
         return this.stripDoubleQuotes(path);
      }
   }

   public final boolean isSecure() {
      return this._secure;
   }

   public final long getTimeCreated() {
      return this._timeCreatedMillis;
   }

   public final long getMaxAge() {
      return this._maxAgeMillis;
   }

   public final boolean isExpired(boolean endOfSession) {
      boolean expired = false;
      if (this._maxAgeMillis < 0) {
         if (endOfSession) {
            return true;
         }
      } else if (this._maxAgeMillis == 0 || System.currentTimeMillis() > this._timeCreatedMillis + this._maxAgeMillis) {
         expired = true;
      }

      return expired;
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._requestHost = PersistentContent.reEncode(this._requestHost, compress, encrypt);
      this._requestURI = PersistentContent.reEncode(this._requestURI, compress, encrypt);
      this._name = PersistentContent.reEncode(this._name, compress, encrypt);
      this._value = PersistentContent.reEncode(this._value, compress, encrypt);
      this._domain = PersistentContent.reEncode(this._domain, compress, encrypt);
      this._path = PersistentContent.reEncode(this._path, compress, encrypt);
      return null;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._requestHost, compress, encrypt)
         && PersistentContent.checkEncoding(this._requestURI, compress, encrypt)
         && PersistentContent.checkEncoding(this._name, compress, encrypt)
         && PersistentContent.checkEncoding(this._value, compress, encrypt)
         && PersistentContent.checkEncoding(this._domain, compress, encrypt)
         && PersistentContent.checkEncoding(this._path, compress, encrypt);
   }

   public Cookie(String requestHost, String requestURI, String cookieString) {
      cookieAssert(DomainUtilities.isFQDN(requestHost) || DomainUtilities.isPartialDN(requestHost) || DomainUtilities.isIPAddress(requestHost), 1130984040);
      this._timeCreatedMillis = System.currentTimeMillis();
      requestHost = StringUtilities.toLowerCase(requestHost, 1701707776);
      int separatorIndex = cookieString.indexOf(59);
      if (separatorIndex < 0) {
         separatorIndex = cookieString.length();
      }

      int equalsIndex = cookieString.indexOf(61);
      if (equalsIndex >= 0 && equalsIndex < separatorIndex) {
         this._name = PersistentContent.encode(cookieString.substring(0, equalsIndex).trim(), false, true);
         this._value = PersistentContent.encode(cookieString.substring(equalsIndex + 1, separatorIndex).trim(), false, true);
      } else {
         this._value = PersistentContent.encode(cookieString.substring(0, separatorIndex).trim(), false, true);
      }

      while (separatorIndex < cookieString.length()) {
         int prevIndex = separatorIndex + 1;
         separatorIndex = cookieString.indexOf(59, prevIndex);
         if (separatorIndex < 0) {
            separatorIndex = cookieString.length();
         }

         equalsIndex = cookieString.indexOf(61, prevIndex);
         if (equalsIndex < 0 || equalsIndex > separatorIndex) {
            equalsIndex = separatorIndex;
         }

         String attribute = StringUtilities.toLowerCase(cookieString.substring(prevIndex, equalsIndex).trim(), 1701707776);
         if (!attribute.equals("domain")) {
            if (attribute.equals("path")) {
               this._path = PersistentContent.encode(cookieString.substring(equalsIndex + 1, separatorIndex).trim(), false, true);
            } else if (attribute.equals("max-age")) {
               this._maxAgeMillis = Integer.parseInt(this.stripDoubleQuotes(cookieString.substring(equalsIndex + 1, separatorIndex).trim())) * 1000;
               if (this._maxAgeMillis < 0) {
                  this._maxAgeMillis = 0;
               }
            } else if (attribute.equals("expires")) {
               String expires = this.stripDoubleQuotes(cookieString.substring(equalsIndex + 1, separatorIndex).trim());
               if (expires != null && expires.length() > 0) {
                  this._maxAgeMillis = DateCache.parse(expires) - this._timeCreatedMillis;
                  if (this._maxAgeMillis < 0) {
                     this._maxAgeMillis = 0;
                  }
               }
            } else if (attribute.equals("secure")) {
               this._secure = true;
            }
         } else {
            String domain = StringUtilities.toLowerCase(cookieString.substring(equalsIndex + 1, separatorIndex).trim(), 1701707776);
            this._domain = PersistentContent.encode(domain, false, true);
            domain = this.stripDoubleQuotes(domain);
            boolean startsWithDot = domain.startsWith(".");
            String domainCompare = startsWithDot ? domain.substring(1) : domain;
            cookieAssert(DomainUtilities.isFQDN(domainCompare), 1130980468);
            cookieAssert(this.hasRequiredSeparators(domainCompare), 1130983027);
            cookieAssert(this.domainMatch(requestHost, domain), 1130984045);
         }
      }

      this._requestHost = PersistentContent.encode(requestHost, false, true);
      if (requestURI.endsWith("/")) {
         requestURI = requestURI.substring(0, requestURI.length() - 1);
      }

      this._requestURI = PersistentContent.encode(requestURI, false, true);
   }

   private static final void cookieAssert(boolean value, int code) throws CookieException {
      if (!value) {
         throw new CookieException(code);
      }
   }

   private final String stripDoubleQuotes(String quotedString) {
      int len = quotedString.length();
      return len >= 2 && quotedString.charAt(0) == '"' && quotedString.charAt(len - 1) == '"' ? quotedString.substring(1, len - 1) : quotedString;
   }

   private final boolean hasRequiredSeparators(String domain) {
      int count = 0;
      int currentIndex = -1;

      while ((currentIndex = domain.indexOf(46, currentIndex + 1)) != -1) {
         count++;
      }

      if (count < 1) {
         return false;
      }

      int lastIndexB = domain.lastIndexOf(46);
      int lastIndexA = domain.lastIndexOf(46, lastIndexB - 1);
      return this.getNumRequiredDomainSeparators(domain, lastIndexA + 1, lastIndexB + 1) <= count;
   }

   private final int getNumRequiredDomainSeparators(String domain, int firstIndex, int secondIndex) {
      int countryValue = getAsInt(domain, secondIndex, domain.length() + 1);
      if (countryValue == 0) {
         return 1;
      }

      int valueLength = secondIndex - firstIndex - 1;
      if (valueLength > 4) {
         if (valueLength > 8) {
            switch (countryValue) {
               case 26226:
                  if ("chambagri".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("chirurgiens-dentistes".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("experts-comptables".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("geometre-expert".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("huissier-justice".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("pharmacien".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("veterinaire".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  return 1;
               case 27248:
                  if ("fukushima".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("yamanashi".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("hiroshima".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("yamaguchi".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("tokushima".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("kagoshima".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("kitakyushu".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("utsunomiya".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("takamatsu".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("matsuyama".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  return 1;
               case 28780:
                  if ("nieruchomosci".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("realestate".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("turystyka".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  return 1;
               case 29541:
                  if ("kommunalforbund".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("naturbruksgymn".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  return 1;
               case 29556:
                  if ("consulado".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  if ("embaixada".regionMatches(false, 0, domain, firstIndex, valueLength)) {
                     return 2;
                  }

                  return 1;
            }
         } else {
            long longValue = getAsLong(domain, firstIndex, secondIndex);
            switch (countryValue) {
               case 24949:
                  if (longValue == 8387229063930277231L) {
                     return 2;
                  }

                  return 1;
               case 25455:
                  if (longValue == 495874699877L) {
                     return 2;
                  }

                  return 1;
               case 26218:
                  if (longValue == 126870791090028L) {
                     return 2;
                  }

                  return 1;
               case 26226:
                  if (longValue == 27691691891057013L) {
                     return 2;
                  }

                  if (longValue == 123636630647653L) {
                     return 2;
                  }

                  if (longValue == 7018141417280074356L) {
                     return 2;
                  }

                  if (longValue == 27430012200446307L) {
                     return 2;
                  }

                  if (longValue == 107161302819188L) {
                     return 2;
                  }

                  if (longValue == 107161303999859L) {
                     return 2;
                  }

                  if (longValue == 444300883041L) {
                     return 2;
                  }

                  if (longValue == 30792254333610350L) {
                     return 2;
                  }

                  if (longValue == 7957707028316710259L) {
                     return 2;
                  }

                  return 1;
               case 26471:
                  if (longValue == 7020096306038072697L) {
                     return 2;
                  }

                  if (longValue == 7454976300335457657L) {
                     return 2;
                  }

                  return 1;
               case 26741:
                  if (longValue == 28554795667778401L) {
                     return 2;
                  }

                  if (longValue == 117026708353914L) {
                     return 2;
                  }

                  if (longValue == 508574328175L) {
                     return 2;
                  }

                  if (longValue == 418347377010L) {
                     return 2;
                  }

                  if (longValue == 7741527752429628527L) {
                     return 2;
                  }

                  if (longValue == 439956436333L) {
                     return 2;
                  }

                  if (longValue == 465490895219L) {
                     return 2;
                  }

                  if (longValue == 109270199266927L) {
                     return 2;
                  }

                  if (longValue == 444016190835L) {
                     return 2;
                  }

                  if (longValue == 469852514657L) {
                     return 2;
                  }

                  if (longValue == 495807591028L) {
                     return 2;
                  }

                  if (longValue == 448546497900L) {
                     return 2;
                  }

                  if (longValue == 128022144574565L) {
                     return 2;
                  }

                  if (longValue == 28554795667776353L) {
                     return 2;
                  }

                  if (longValue == 7597123289683353966L) {
                     return 2;
                  }

                  if (longValue == 125779919528301L) {
                     return 2;
                  }

                  if (longValue == 129142712066419L) {
                     return 2;
                  }

                  return 1;
               case 26989:
                  if (longValue == 119247152767855L) {
                     return 2;
                  }

                  if (longValue == 123610822763375L) {
                     return 2;
                  }

                  return 1;
               case 27237:
                  if (longValue == 116983944406393L) {
                     return 2;
                  }

                  return 1;
               case 27248:
                  if (longValue == 7525351611299357807L) {
                     return 2;
                  }

                  if (longValue == 107131205284457L) {
                     return 2;
                  }

                  if (longValue == 452974441573L) {
                     return 2;
                  }

                  if (longValue == 120299775420265L) {
                     return 2;
                  }

                  if (longValue == 418413900897L) {
                     return 2;
                  }

                  if (longValue == 8746392216443909217L) {
                     return 2;
                  }

                  if (longValue == 29663043224955753L) {
                     return 2;
                  }

                  if (longValue == 32773570042619753L) {
                     return 2;
                  }

                  if (longValue == 444351802721L) {
                     return 2;
                  }

                  if (longValue == 32476727873727841L) {
                     return 2;
                  }

                  if (longValue == 426953499233L) {
                     return 2;
                  }

                  if (longValue == 500085520751L) {
                     return 2;
                  }

                  if (longValue == 7737586999424546657L) {
                     return 2;
                  }

                  if (longValue == 31078148865094753L) {
                     return 2;
                  }

                  if (longValue == 128022126620001L) {
                     return 2;
                  }

                  if (longValue == 7598531798286563169L) {
                     return 2;
                  }

                  if (longValue == 440056640873L) {
                     return 2;
                  }

                  if (longValue == 121364625321583L) {
                     return 2;
                  }

                  if (longValue == 8316012686616783713L) {
                     return 2;
                  }

                  if (longValue == 418379950185L) {
                     return 2;
                  }

                  if (longValue == 495672977249L) {
                     return 2;
                  }

                  if (longValue == 461598848111L) {
                     return 2;
                  }

                  if (longValue == 478677134177L) {
                     return 2;
                  }

                  if (longValue == 448713942895L) {
                     return 2;
                  }

                  if (longValue == 8602274829646785889L) {
                     return 2;
                  }

                  if (longValue == 32773643258786409L) {
                     return 2;
                  }

                  if (longValue == 32484424337682021L) {
                     return 2;
                  }

                  if (longValue == 31361788807310689L) {
                     return 2;
                  }

                  if (longValue == 118066090440545L) {
                     return 2;
                  }

                  if (longValue == 435543436645L) {
                     return 2;
                  }

                  if (longValue == 461430286441L) {
                     return 2;
                  }

                  if (longValue == 28839552016673633L) {
                     return 2;
                  }

                  if (longValue == 7953752085158259561L) {
                     return 2;
                  }

                  if (longValue == 7743215399548712047L) {
                     return 2;
                  }

                  if (longValue == 7883966082260757353L) {
                     return 2;
                  }

                  if (longValue == 31361822982502241L) {
                     return 2;
                  }

                  if (longValue == 32476757872308847L) {
                     return 2;
                  }

                  if (longValue == 126879480963433L) {
                     return 2;
                  }

                  if (longValue == 8750330727240920417L) {
                     return 2;
                  }

                  if (longValue == 7737596895230520169L) {
                     return 2;
                  }

                  if (longValue == 121364626241889L) {
                     return 2;
                  }

                  if (longValue == 7737586999743313761L) {
                     return 2;
                  }

                  return 1;
               case 27506:
                  if (longValue == 495623042412L) {
                     return 2;
                  }

                  if (longValue == 30251342008641385L) {
                     return 2;
                  }

                  return 1;
               case 28001:
                  if (longValue == 482955588467L) {
                     return 2;
                  }

                  return 1;
               case 28257:
                  if (longValue == 32762613530324845L) {
                     return 2;
                  }

                  return 1;
               case 28282:
                  if (longValue == 469786129001L) {
                     return 2;
                  }

                  if (longValue == 126870791090028L) {
                     return 2;
                  }

                  return 1;
               case 28525:
                  if (longValue == 120351214630253L) {
                     return 2;
                  }

                  return 1;
               case 28780:
                  if (longValue == 444217257569L) {
                     return 2;
                  }

                  if (longValue == 120299373950049L) {
                     return 2;
                  }

                  if (longValue == 469852514657L) {
                     return 2;
                  }

                  if (longValue == 123624047075700L) {
                     return 2;
                  }

                  if (longValue == 495723505008L) {
                     return 2;
                  }

                  if (longValue == 126969625668705L) {
                     return 2;
                  }

                  if (longValue == 499851093865L) {
                     return 2;
                  }

                  if (longValue == 32773647519806317L) {
                     return 2;
                  }

                  if (longValue == 128034610242924L) {
                     return 2;
                  }

                  return 1;
               case 29295:
                  if (longValue == 495874699877L) {
                     return 2;
                  }

                  return 1;
               case 29541:
                  if (longValue == 482955588467L) {
                     return 2;
                  }

                  if (longValue == 482671228009L) {
                     return 2;
                  }

                  if (longValue == 422825782884L) {
                     return 2;
                  }

                  if (longValue == 30240338168738402L) {
                     return 2;
                  }

                  if (longValue == 118126322021752L) {
                     return 2;
                  }

                  if (longValue == 119165719507554L) {
                     return 2;
                  }

                  if (longValue == 119165719570786L) {
                     return 2;
                  }

                  return 1;
               case 29556:
                  if (longValue == 32476753644449125L) {
                     return 2;
                  }

                  if (longValue == 8102654602428117093L) {
                     return 2;
                  }

                  if (longValue == 495874699877L) {
                     return 2;
                  }

                  return 1;
               case 29806:
                  if (longValue == 111482141304180L) {
                     return 2;
                  }

                  if (longValue == 32773647519806317L) {
                     return 2;
                  }

                  return 1;
               case 30059:
                  if (longValue == 123623862526821L) {
                     return 2;
                  }

                  return 1;
               case 30309:
                  if (longValue == 495874699877L) {
                     return 2;
                  }

                  return 1;
               case 30318:
                  if (longValue == 114784635483240L) {
                     return 2;
                  }

                  return 1;
               case 31329:
                  if (longValue == 126870791090028L) {
                     return 2;
                  }

                  return 1;
            }
         }
      } else {
         switch (countryValue) {
            case 24931:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 24933:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 6516589:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                  case 7369327:
                  case 7562088:
                     return 2;
                  default:
                     return 1;
               }
            case 24937:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 7303782:
                     return 1;
                  case 7303783:
                  default:
                     return 2;
               }
            case 24946:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 6909556:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 24948:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 26486:
                  case 28530:
                  case 1886546294:
                     return 2;
                  default:
                     return 1;
               }
            case 24949:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 26980:
                  case 28538:
                  case 6386542:
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                  case 7304291:
                  case 1668247142:
                  case 1768842863:
                     return 2;
                  default:
                     return 1;
               }
            case 24954:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 25186:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 25189:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                     return 2;
                  default:
                     return 1;
               }
            case 25192:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 25197:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 25202:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24941:
                  case 26221:
                  case 29814:
                  case 6382701:
                  case 6382710:
                  case 6383474:
                  case 6386289:
                  case 6386292:
                  case 6386799:
                  case 6449519:
                  case 6450532:
                  case 6515053:
                  case 6516327:
                  case 6516340:
                  case 6516589:
                  case 6644590:
                  case 6644853:
                  case 6647399:
                  case 6648688:
                  case 6648931:
                  case 6648937:
                  case 6709618:
                  case 6712932:
                  case 6713204:
                  case 6714228:
                  case 6762802:
                  case 6776678:
                  case 6778742:
                  case 6909282:
                  case 6909540:
                  case 6909542:
                  case 6975346:
                  case 7103852:
                  case 7168372:
                  case 7169380:
                  case 7170412:
                  case 7173491:
                  case 7234932:
                  case 7237485:
                  case 7237492:
                  case 7238770:
                  case 7300207:
                  case 7303783:
                  case 7368807:
                  case 7369327:
                  case 7369571:
                  case 7369577:
                  case 7435116:
                  case 7497059:
                  case 7564391:
                  case 7565942:
                  case 7630192:
                  case 7631460:
                  case 7632242:
                  case 7759220:
                  case 8023143:
                  case 1668247408:
                     return 2;
                  default:
                     return 1;
               }
            case 25203:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 25441:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24930:
                  case 25187:
                  case 26467:
                  case 28002:
                  case 28258:
                  case 28262:
                  case 28268:
                  case 28275:
                  case 28276:
                  case 28277:
                  case 28526:
                  case 28773:
                  case 29027:
                  case 29547:
                  case 31083:
                     return 2;
                  default:
                     return 1;
               }
            case 25451:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 25455:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 25454:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 24936:
                  case 25194:
                  case 25457:
                  case 26468:
                  case 26483:
                  case 26488:
                  case 26490:
                  case 26722:
                  case 26725:
                  case 26729:
                  case 26731:
                  case 26732:
                  case 26734:
                  case 27244:
                  case 27251:
                  case 27758:
                  case 28015:
                  case 28269:
                  case 28280:
                  case 29032:
                  case 29539:
                  case 29544:
                  case 29550:
                  case 29560:
                  case 29802:
                  case 29815:
                  case 30826:
                  case 30842:
                  case 31086:
                  case 31338:
                  case 6516589:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 25455:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 6909556:
                  case 7170412:
                  case 7237485:
                  case 7303783:
                  case 7497059:
                  case 7824738:
                  case 1634890867:
                  case 1718186605:
                  case 1768842863:
                     return 2;
                  default:
                     return 1;
               }
            case 25458:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 25956:
                  case 26217:
                  case 26479:
                  case 28530:
                  case 29537:
                     return 2;
                  default:
                     return 1;
               }
            case 25461:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 25465:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 6516589:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 25707:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 25455:
                     return 2;
                  default:
                     return 1;
               }
            case 25711:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6386292:
                  case 6516589:
                  case 6644853:
                  case 6778722:
                  case 6778742:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                  case 7564388:
                  case 7824738:
                     return 2;
                  default:
                     return 1;
               }
            case 25722:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6386292:
                  case 6386547:
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                  case 7368556:
                     return 2;
                  default:
                     return 1;
               }
            case 25955:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6711662:
                  case 6778742:
                  case 7024946:
                  case 7169380:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 25957:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6711653:
                  case 7169380:
                  case 7303783:
                  case 7369321:
                     return 2;
                  default:
                     return 1;
               }
            case 25959:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6649198:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                  case 7562089:
                     return 2;
                  default:
                     return 1;
               }
            case 25970:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6644853:
                  case 6778742:
                  case 6909540:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 25971:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778722:
                  case 7237485:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 25972:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6449530:
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                  case 1768842863:
                  case 1851878757:
                     return 2;
                  default:
                     return 1;
               }
            case 26218:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 26980:
                  case 6516589:
                  case 6778742:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 26219:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 6516589:
                  case 6778742:
                  case 7234932:
                  case 7237485:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 26226:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 29805:
                  case 6513513:
                  case 6516589:
                  case 7237485:
                  case 7369316:
                  case 1634956143:
                  case 1735357814:
                  case 1886351988:
                     return 2;
                  default:
                     return 1;
               }
            case 26469:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                  case 7370356:
                     return 2;
                  default:
                     return 1;
               }
            case 26471:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 6909540:
                  case 7107684:
                  case 7234932:
                  case 7303783:
                  case 7562088:
                  case 1935766123:
                     return 2;
                  default:
                     return 1;
               }
            case 26484:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778722:
                  case 6909540:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 26485:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 26731:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 6906998:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 26741:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 25455:
                  case 29805:
                  case 7303783:
                  case 7562616:
                  case 842018864:
                  case 1651469428:
                  case 1667855481:
                  case 1718185069:
                  case 1768842863:
                  case 1852143475:
                  case 1886546294:
                  case 1936224112:
                  case 1937075305:
                  case 1937401208:
                     return 2;
                  default:
                     return 1;
               }
            case 26980:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 26479:
                  case 28530:
                  case 7170412:
                  case 7234932:
                     return 2;
                  default:
                     return 1;
               }
            case 26988:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 6906982:
                  case 7024946:
                  case 7234932:
                  case 7303783:
                  case 1836412521:
                     return 2;
                  default:
                     return 1;
               }
            case 26989:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 7234932:
                  case 7235939:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 26990:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 6776174:
                  case 6778742:
                  case 6909540:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                  case 7497075:
                  case 1718186605:
                     return 2;
                  default:
                     return 1;
               }
            case 27237:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 25455:
                  case 6778742:
                  case 6909540:
                  case 7107684:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 27247:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 27248:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 24932:
                  case 25455:
                  case 25956:
                  case 26479:
                  case 26482:
                  case 27751:
                  case 28261:
                  case 28530:
                  case 6778742:
                  case 7170405:
                  case 7234932:
                  case 7303783:
                  case 1734960757:
                  case 1802461797:
                  case 1851880033:
                  case 1869182049:
                  case 1935763297:
                     return 2;
                  default:
                     return 1;
               }
            case 27496:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                  case 7366002:
                     return 2;
                  default:
                     return 1;
               }
            case 27506:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 26479:
                  case 28261:
                  case 28530:
                  case 28773:
                  case 29285:
                     return 2;
                  default:
                     return 1;
               }
            case 27511:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 27745:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 27746:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 27747:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 27766:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 26980:
                  case 6386542:
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                  case 1668247142:
                     return 2;
                  default:
                     return 1;
               }
            case 27769:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 28001:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 28011:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516588:
                     return 1;
                  case 6516589:
                  default:
                     return 2;
               }
            case 28013:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 28015:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 28020:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 29805:
                  case 30069:
                  case 6516589:
                  case 6644853:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 28024:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 28025:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 28257:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6384756:
                  case 6516589:
                  case 6518124:
                  case 6644853:
                  case 7234932:
                  case 7303783:
                  case 1970168173:
                     return 2;
                  default:
                     return 1;
               }
            case 28259:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 28263:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                  case 7562088:
                     return 2;
                  default:
                     return 1;
               }
            case 28265:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778722:
                  case 7234932:
                  case 7237485:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 28272:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 28282:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 6517353:
                  case 6776174:
                  case 6911849:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                  case 1734698347:
                  case 1735358068:
                     return 2;
                  default:
                     return 1;
               }
            case 28525:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 6449530:
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7169380:
                  case 7171940:
                  case 7234932:
                  case 7303783:
                  case 7369327:
                     return 2;
                  default:
                     return 1;
               }
            case 28769:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 6516589:
                  case 6644853:
                  case 6778722:
                  case 7234932:
                  case 7303783:
                  case 7564388:
                     return 2;
                  default:
                     return 1;
               }
            case 28773:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778722:
                  case 7170412:
                  case 7234932:
                  case 7237485:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 28775:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 6516589:
                  case 7234932:
                     return 2;
                  default:
                     return 1;
               }
            case 28779:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6449530:
                  case 6516589:
                  case 6644853:
                  case 6709613:
                  case 6778722:
                  case 6778731:
                  case 6778734:
                  case 6778736:
                  case 6778739:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                  case 7824738:
                     return 2;
                  default:
                     return 1;
               }
            case 28780:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 28771:
                  case 29805:
                  case 6383972:
                  case 6386797:
                  case 6449530:
                  case 6516589:
                  case 6644853:
                  case 6779757:
                  case 7170412:
                  case 7234932:
                  case 7237485:
                  case 7303783:
                  case 7497068:
                  case 7562616:
                  case 7565171:
                  case 1634169455:
                  case 1635087471:
                  case 1768842863:
                  case 1835100524:
                  case 1886546294:
                  case 1936224112:
                     return 2;
                  default:
                     return 1;
               }
            case 28787:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6644853:
                  case 6778742:
                  case 7367791:
                  case 7562595:
                     return 2;
                  default:
                     return 1;
               }
            case 28793:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 29025:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 29285:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 7237485:
                  case 1634956143:
                     return 2;
                  default:
                     return 1;
               }
            case 29295:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 28276:
                  case 29805:
                  case 6516589:
                  case 7237485:
                  case 7303783:
                  case 7497059:
                  case 7829367:
                  case 1634890867:
                  case 1718186605:
                  case 1768842863:
                     return 2;
                  default:
                     return 1;
               }
            case 29301:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 28784:
                  case 6516589:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 29537:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7169380:
                  case 7234932:
                  case 7303783:
                  case 7370082:
                  case 7562088:
                     return 2;
                  default:
                     return 1;
               }
            case 29538:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 29540:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7169380:
                  case 7234932:
                  case 7303783:
                  case 7562088:
                     return 2;
                  default:
                     return 1;
               }
            case 29541:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 26216:
                  case 28784:
                  case 29805:
                  case 6711414:
                  case 7303783:
                  case 1718121323:
                  case 1936943214:
                     return 2;
                  default:
                     return 1;
               }
            case 29543:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 7234932:
                  case 7303783:
                  case 7366002:
                     return 2;
                  default:
                     return 1;
               }
            case 29544:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 29556:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 29558:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778722:
                  case 7303783:
                  case 7497060:
                     return 2;
                  default:
                     return 1;
               }
            case 29561:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6778742:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 29800:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 26479:
                  case 28530:
                  case 7234932:
                     return 2;
                  default:
                     return 1;
               }
            case 29806:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6647411:
                  case 6711662:
                  case 6778742:
                  case 6909540:
                  case 7233908:
                  case 7234932:
                  case 7303783:
                  case 7499379:
                  case 7499381:
                  case 1768842863:
                  case 1768846444:
                  case 1919840884:
                     return 2;
                  default:
                     return 1;
               }
            case 29810:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6447731:
                  case 6516589:
                  case 6644853:
                  case 6776174:
                  case 6778742:
                  case 7024946:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 29812:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 25455:
                  case 25707:
                  case 29541:
                  case 6449530:
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                  case 7369327:
                  case 1768842863:
                  case 1851878757:
                     return 2;
                  default:
                     return 1;
               }
            case 29815:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6906998:
                  case 7234932:
                  case 7303783:
                  case 1735358053:
                     return 2;
                  default:
                     return 1;
               }
            case 30049:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6778742:
                  case 7234932:
                     return 2;
                  default:
                     return 1;
               }
            case 30055:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 26479:
                  case 28530:
                     return 2;
                  default:
                     return 1;
               }
            case 30059:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 28005:
                  case 6644853:
                  case 6778742:
                  case 7107684:
                  case 7171940:
                  case 7234932:
                  case 7235939:
                  case 7303783:
                  case 7367779:
                  case 7562088:
                     return 2;
                  default:
                     return 1;
               }
            case 30067:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6581865:
                  case 6710628:
                     return 2;
                  default:
                     return 1;
               }
            case 30073:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6780258:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 30309:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 25455:
                  case 6449506:
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 6909556:
                  case 7170412:
                  case 7234932:
                  case 7237485:
                  case 7303783:
                  case 7497059:
                  case 7628131:
                  case 7824738:
                  case 1634890867:
                  case 1718186605:
                  case 1768842863:
                     return 2;
                  default:
                     return 1;
               }
            case 30313:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 25455:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 30318:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 6449530:
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 6909556:
                  case 7234932:
                  case 7303783:
                  case 7369327:
                  case 1768842863:
                  case 1851878757:
                     return 2;
                  default:
                     return 1;
               }
            case 30325:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 26226:
                  case 6516589:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 30579:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 31077:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 6516589:
                  case 6644853:
                  case 6778742:
                  case 7170412:
                  case 7234932:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 31093:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 6644853:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 31329:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 6384756:
                  case 7102839:
                  case 7170412:
                  case 7234932:
                  case 7824738:
                  case 1667855481:
                     return 2;
                  default:
                     return 1;
               }
            case 31351:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 24931:
                  case 25455:
                  case 6778742:
                  case 7303783:
                     return 2;
                  default:
                     return 1;
               }
            case 7234932:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 25701:
                     return 2;
                  default:
                     return 1;
               }
            case 1634889825:
               switch (getAsInt(domain, firstIndex, secondIndex)) {
                  case 1697723955:
                     return 1;
                  case 1697723956:
                  default:
                     return 2;
               }
         }
      }

      return 1;
   }

   private static final int getAsInt(String str, int fromIndex, int secondIndex) {
      if (str == null) {
         return 0;
      } else {
         int length = secondIndex - fromIndex - 1;
         if (length <= 4 && length > 0) {
            int pos = fromIndex;
            return (length == 4 ? (str.charAt(pos++) & 0xFF) << 24 : 0)
               | (length >= 3 ? (str.charAt(pos++) & 0xFF) << 16 : 0)
               | (length >= 2 ? (str.charAt(pos++) & 0xFF) << 8 : 0)
               | str.charAt(pos++) & 0xFF;
         } else {
            return 0;
         }
      }
   }

   private static final long getAsLong(String str, int fromIndex, int secondIndex) {
      if (str == null) {
         return 0;
      } else {
         int length = secondIndex - fromIndex - 1;
         if (length <= 8 && length > 0) {
            int pos = fromIndex;
            return (length == 8 ? (long)(str.charAt(pos++) & 0xFF) << 56 : 0)
               | (length >= 7 ? (long)(str.charAt(pos++) & 0xFF) << 48 : 0)
               | (length >= 6 ? (long)(str.charAt(pos++) & 0xFF) << 40 : 0)
               | (length >= 5 ? (long)(str.charAt(pos++) & 0xFF) << 32 : 0)
               | (length >= 4 ? (long)(str.charAt(pos++) & 0xFF) << 24 : 0)
               | (length >= 3 ? (long)(str.charAt(pos++) & 0xFF) << 16 : 0)
               | (length >= 2 ? (long)(str.charAt(pos++) & 0xFF) << 8 : 0)
               | str.charAt(pos++) & 0xFF;
         } else {
            return 0;
         }
      }
   }
}
