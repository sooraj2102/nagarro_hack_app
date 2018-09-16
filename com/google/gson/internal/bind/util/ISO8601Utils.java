package com.google.gson.internal.bind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class ISO8601Utils
{
  private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
  private static final String UTC_ID = "UTC";

  private static boolean checkOffset(String paramString, int paramInt, char paramChar)
  {
    return (paramInt < paramString.length()) && (paramString.charAt(paramInt) == paramChar);
  }

  public static String format(Date paramDate)
  {
    return format(paramDate, false, TIMEZONE_UTC);
  }

  public static String format(Date paramDate, boolean paramBoolean)
  {
    return format(paramDate, paramBoolean, TIMEZONE_UTC);
  }

  public static String format(Date paramDate, boolean paramBoolean, TimeZone paramTimeZone)
  {
    GregorianCalendar localGregorianCalendar = new GregorianCalendar(paramTimeZone, Locale.US);
    localGregorianCalendar.setTime(paramDate);
    int i = "yyyy-MM-ddThh:mm:ss".length();
    int j;
    int m;
    label56: StringBuilder localStringBuilder;
    char c;
    if (paramBoolean)
    {
      j = ".sss".length();
      int k = i + j;
      if (paramTimeZone.getRawOffset() != 0)
        break label335;
      m = "Z".length();
      localStringBuilder = new StringBuilder(k + m);
      padInt(localStringBuilder, localGregorianCalendar.get(1), "yyyy".length());
      localStringBuilder.append('-');
      padInt(localStringBuilder, 1 + localGregorianCalendar.get(2), "MM".length());
      localStringBuilder.append('-');
      padInt(localStringBuilder, localGregorianCalendar.get(5), "dd".length());
      localStringBuilder.append('T');
      padInt(localStringBuilder, localGregorianCalendar.get(11), "hh".length());
      localStringBuilder.append(':');
      padInt(localStringBuilder, localGregorianCalendar.get(12), "mm".length());
      localStringBuilder.append(':');
      padInt(localStringBuilder, localGregorianCalendar.get(13), "ss".length());
      if (paramBoolean)
      {
        localStringBuilder.append('.');
        padInt(localStringBuilder, localGregorianCalendar.get(14), "sss".length());
      }
      int n = paramTimeZone.getOffset(localGregorianCalendar.getTimeInMillis());
      if (n == 0)
        break label352;
      int i1 = Math.abs(n / 60000 / 60);
      int i2 = Math.abs(n / 60000 % 60);
      if (n >= 0)
        break label345;
      c = '-';
      label283: localStringBuilder.append(c);
      padInt(localStringBuilder, i1, "hh".length());
      localStringBuilder.append(':');
      padInt(localStringBuilder, i2, "mm".length());
    }
    while (true)
    {
      return localStringBuilder.toString();
      j = 0;
      break;
      label335: m = "+hh:mm".length();
      break label56;
      label345: c = '+';
      break label283;
      label352: localStringBuilder.append('Z');
    }
  }

  private static int indexOfNonDigit(String paramString, int paramInt)
  {
    for (int i = paramInt; i < paramString.length(); i++)
    {
      int j = paramString.charAt(i);
      if ((j < 48) || (j > 57))
        return i;
    }
    return paramString.length();
  }

  private static void padInt(StringBuilder paramStringBuilder, int paramInt1, int paramInt2)
  {
    String str = Integer.toString(paramInt1);
    for (int i = paramInt2 - str.length(); i > 0; i--)
      paramStringBuilder.append('0');
    paramStringBuilder.append(str);
  }

  public static Date parse(String paramString, ParsePosition paramParsePosition)
    throws ParseException
  {
    try
    {
      int i = paramParsePosition.getIndex();
      int j = i + 4;
      k = parseInt(paramString, i, j);
      if (checkOffset(paramString, j, '-'))
        j++;
      m = j + 2;
      n = parseInt(paramString, j, m);
      if (checkOffset(paramString, m, '-'))
      {
        i1 = m + 1;
        i2 = i1 + 2;
        i3 = parseInt(paramString, i1, i2);
        boolean bool1 = checkOffset(paramString, i2, 'T');
        if ((!bool1) && (paramString.length() <= i2))
        {
          GregorianCalendar localGregorianCalendar1 = new GregorianCalendar(k, n - 1, i3);
          paramParsePosition.setIndex(i2);
          return localGregorianCalendar1.getTime();
        }
        i4 = 0;
        i5 = 0;
        i6 = 0;
        i7 = 0;
        int i14;
        int i16;
        if (bool1)
        {
          int i9 = i2 + 1;
          int i10 = i9 + 2;
          i4 = parseInt(paramString, i9, i10);
          if (checkOffset(paramString, i10, ':'))
            i10++;
          i11 = i10 + 2;
          i6 = parseInt(paramString, i10, i11);
          if (!checkOffset(paramString, i11, ':'))
            break label917;
          i12 = i11 + 1;
          if (paramString.length() <= i12)
            break label904;
          int i13 = paramString.charAt(i12);
          if ((i13 == 90) || (i13 == 43) || (i13 == 45))
            break label904;
          i2 = i12 + 2;
          i7 = parseInt(paramString, i12, i2);
          if ((i7 > 59) && (i7 < 63))
            i7 = 59;
          boolean bool2 = checkOffset(paramString, i2, '.');
          i5 = 0;
          if (bool2)
          {
            i14 = i2 + 1;
            i15 = indexOfNonDigit(paramString, i14 + 1);
            i16 = Math.min(i15, i14 + 3);
            i17 = parseInt(paramString, i14, i16);
          }
        }
        switch (i16 - i14)
        {
        case 2:
          if (paramString.length() <= i2)
            throw new IllegalArgumentException("No time zone indicator");
        case 1:
        }
      }
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      int k;
      int n;
      int i3;
      int i4;
      int i6;
      localObject = localIndexOutOfBoundsException;
      if (paramString == null)
      {
        str1 = null;
        String str2 = ((Exception)localObject).getMessage();
        if ((str2 == null) || (str2.isEmpty()))
          str2 = "(" + localObject.getClass().getName() + ")";
        ParseException localParseException = new ParseException("Failed to parse date [" + str1 + "]: " + str2, paramParsePosition.getIndex());
        localParseException.initCause((Throwable)localObject);
        throw localParseException;
        i5 = i17 * 10;
        break label935;
        i5 = i17 * 100;
        break label935;
        c = paramString.charAt(i2);
        if (c != 'Z')
          break label942;
        TimeZone localTimeZone = TIMEZONE_UTC;
        int i8 = i2 + 1;
        String str4;
        String str5;
        do
        {
          GregorianCalendar localGregorianCalendar2 = new GregorianCalendar(localTimeZone);
          localGregorianCalendar2.setLenient(false);
          localGregorianCalendar2.set(1, k);
          localGregorianCalendar2.set(2, n - 1);
          localGregorianCalendar2.set(5, i3);
          localGregorianCalendar2.set(11, i4);
          localGregorianCalendar2.set(12, i6);
          localGregorianCalendar2.set(13, i7);
          localGregorianCalendar2.set(14, i5);
          paramParsePosition.setIndex(i8);
          return localGregorianCalendar2.getTime();
          String str3 = paramString.substring(i2);
          if (str3.length() >= 5);
          while (true)
          {
            i8 = i2 + str3.length();
            if ((!"+0000".equals(str3)) && (!"+00:00".equals(str3)))
              break label732;
            localTimeZone = TIMEZONE_UTC;
            break;
            str3 = str3 + "00";
          }
          str4 = "GMT" + str3;
          localTimeZone = TimeZone.getTimeZone(str4);
          str5 = localTimeZone.getID();
        }
        while ((str5.equals(str4)) || (str5.replace(":", "").equals(str4)));
        throw new IndexOutOfBoundsException("Mismatching time zone indicator: " + str4 + " given, resolves to " + localTimeZone.getID());
        throw new IndexOutOfBoundsException("Invalid time zone indicator '" + c + "'");
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      while (true)
      {
        int m;
        int i11;
        int i15;
        int i17;
        char c;
        localObject = localIllegalArgumentException;
        continue;
        String str1 = '"' + paramString + "'";
        continue;
        int i2 = i12;
        int i5 = 0;
        int i7 = 0;
        continue;
        int i12 = i11;
        continue;
        int i1 = m;
        continue;
        i5 = i17;
        i2 = i15;
        continue;
        if (c == '+')
          continue;
        if (c != '-')
          continue;
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      label904: label917: label935: label942: 
      while (true)
        label732: Object localObject = localNumberFormatException;
    }
  }

  private static int parseInt(String paramString, int paramInt1, int paramInt2)
    throws NumberFormatException
  {
    if ((paramInt1 < 0) || (paramInt2 > paramString.length()) || (paramInt1 > paramInt2))
      throw new NumberFormatException(paramString);
    int i;
    int j;
    if (paramInt1 < paramInt2)
    {
      i = paramInt1 + 1;
      int n = Character.digit(paramString.charAt(paramInt1), 10);
      if (n < 0)
        throw new NumberFormatException("Invalid number: " + paramString.substring(paramInt1, paramInt2));
      j = -n;
    }
    while (true)
    {
      if (i < paramInt2)
      {
        int k = i + 1;
        int m = Character.digit(paramString.charAt(i), 10);
        if (m < 0)
          throw new NumberFormatException("Invalid number: " + paramString.substring(paramInt1, paramInt2));
        j = j * 10 - m;
        i = k;
        continue;
      }
      return -j;
      i = paramInt1;
      j = 0;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.google.gson.internal.bind.util.ISO8601Utils
 * JD-Core Version:    0.6.0
 */