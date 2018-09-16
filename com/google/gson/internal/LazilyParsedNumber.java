package com.google.gson.internal;

import java.io.ObjectStreamException;
import java.math.BigDecimal;

public final class LazilyParsedNumber extends Number
{
  private final String value;

  public LazilyParsedNumber(String paramString)
  {
    this.value = paramString;
  }

  private Object writeReplace()
    throws ObjectStreamException
  {
    return new BigDecimal(this.value);
  }

  public double doubleValue()
  {
    return Double.parseDouble(this.value);
  }

  public boolean equals(Object paramObject)
  {
    int i;
    if (this == paramObject)
      i = 1;
    boolean bool2;
    do
    {
      boolean bool1;
      do
      {
        return i;
        bool1 = paramObject instanceof LazilyParsedNumber;
        i = 0;
      }
      while (!bool1);
      LazilyParsedNumber localLazilyParsedNumber = (LazilyParsedNumber)paramObject;
      if (this.value == localLazilyParsedNumber.value)
        break;
      bool2 = this.value.equals(localLazilyParsedNumber.value);
      i = 0;
    }
    while (!bool2);
    return true;
  }

  public float floatValue()
  {
    return Float.parseFloat(this.value);
  }

  public int hashCode()
  {
    return this.value.hashCode();
  }

  public int intValue()
  {
    try
    {
      int i = Integer.parseInt(this.value);
      return i;
    }
    catch (NumberFormatException localNumberFormatException1)
    {
      try
      {
        long l = Long.parseLong(this.value);
        return (int)l;
      }
      catch (NumberFormatException localNumberFormatException2)
      {
      }
    }
    return new BigDecimal(this.value).intValue();
  }

  public long longValue()
  {
    try
    {
      long l = Long.parseLong(this.value);
      return l;
    }
    catch (NumberFormatException localNumberFormatException)
    {
    }
    return new BigDecimal(this.value).longValue();
  }

  public String toString()
  {
    return this.value;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.google.gson.internal.LazilyParsedNumber
 * JD-Core Version:    0.6.0
 */