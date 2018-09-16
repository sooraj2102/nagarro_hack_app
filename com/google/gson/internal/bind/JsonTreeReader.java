package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public final class JsonTreeReader extends JsonReader
{
  private static final Object SENTINEL_CLOSED;
  private static final Reader UNREADABLE_READER = new Reader()
  {
    public void close()
      throws IOException
    {
      throw new AssertionError();
    }

    public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws IOException
    {
      throw new AssertionError();
    }
  };
  private int[] pathIndices = new int[32];
  private String[] pathNames = new String[32];
  private Object[] stack = new Object[32];
  private int stackSize = 0;

  static
  {
    SENTINEL_CLOSED = new Object();
  }

  public JsonTreeReader(JsonElement paramJsonElement)
  {
    super(UNREADABLE_READER);
    push(paramJsonElement);
  }

  private void expect(JsonToken paramJsonToken)
    throws IOException
  {
    if (peek() != paramJsonToken)
      throw new IllegalStateException("Expected " + paramJsonToken + " but was " + peek() + locationString());
  }

  private String locationString()
  {
    return " at path " + getPath();
  }

  private Object peekStack()
  {
    return this.stack[(-1 + this.stackSize)];
  }

  private Object popStack()
  {
    Object[] arrayOfObject = this.stack;
    int i = -1 + this.stackSize;
    this.stackSize = i;
    Object localObject = arrayOfObject[i];
    this.stack[this.stackSize] = null;
    return localObject;
  }

  private void push(Object paramObject)
  {
    if (this.stackSize == this.stack.length)
    {
      Object[] arrayOfObject2 = new Object[2 * this.stackSize];
      int[] arrayOfInt = new int[2 * this.stackSize];
      String[] arrayOfString = new String[2 * this.stackSize];
      System.arraycopy(this.stack, 0, arrayOfObject2, 0, this.stackSize);
      System.arraycopy(this.pathIndices, 0, arrayOfInt, 0, this.stackSize);
      System.arraycopy(this.pathNames, 0, arrayOfString, 0, this.stackSize);
      this.stack = arrayOfObject2;
      this.pathIndices = arrayOfInt;
      this.pathNames = arrayOfString;
    }
    Object[] arrayOfObject1 = this.stack;
    int i = this.stackSize;
    this.stackSize = (i + 1);
    arrayOfObject1[i] = paramObject;
  }

  public void beginArray()
    throws IOException
  {
    expect(JsonToken.BEGIN_ARRAY);
    push(((JsonArray)peekStack()).iterator());
    this.pathIndices[(-1 + this.stackSize)] = 0;
  }

  public void beginObject()
    throws IOException
  {
    expect(JsonToken.BEGIN_OBJECT);
    push(((JsonObject)peekStack()).entrySet().iterator());
  }

  public void close()
    throws IOException
  {
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = SENTINEL_CLOSED;
    this.stack = arrayOfObject;
    this.stackSize = 1;
  }

  public void endArray()
    throws IOException
  {
    expect(JsonToken.END_ARRAY);
    popStack();
    popStack();
    if (this.stackSize > 0)
    {
      int[] arrayOfInt = this.pathIndices;
      int i = -1 + this.stackSize;
      arrayOfInt[i] = (1 + arrayOfInt[i]);
    }
  }

  public void endObject()
    throws IOException
  {
    expect(JsonToken.END_OBJECT);
    popStack();
    popStack();
    if (this.stackSize > 0)
    {
      int[] arrayOfInt = this.pathIndices;
      int i = -1 + this.stackSize;
      arrayOfInt[i] = (1 + arrayOfInt[i]);
    }
  }

  public String getPath()
  {
    StringBuilder localStringBuilder = new StringBuilder().append('$');
    int i = 0;
    if (i < this.stackSize)
    {
      if ((this.stack[i] instanceof JsonArray))
      {
        Object[] arrayOfObject2 = this.stack;
        i++;
        if ((arrayOfObject2[i] instanceof Iterator))
          localStringBuilder.append('[').append(this.pathIndices[i]).append(']');
      }
      while (true)
      {
        i++;
        break;
        if (!(this.stack[i] instanceof JsonObject))
          continue;
        Object[] arrayOfObject1 = this.stack;
        i++;
        if (!(arrayOfObject1[i] instanceof Iterator))
          continue;
        localStringBuilder.append('.');
        if (this.pathNames[i] == null)
          continue;
        localStringBuilder.append(this.pathNames[i]);
      }
    }
    return localStringBuilder.toString();
  }

  public boolean hasNext()
    throws IOException
  {
    JsonToken localJsonToken = peek();
    return (localJsonToken != JsonToken.END_OBJECT) && (localJsonToken != JsonToken.END_ARRAY);
  }

  public boolean nextBoolean()
    throws IOException
  {
    expect(JsonToken.BOOLEAN);
    boolean bool = ((JsonPrimitive)popStack()).getAsBoolean();
    if (this.stackSize > 0)
    {
      int[] arrayOfInt = this.pathIndices;
      int i = -1 + this.stackSize;
      arrayOfInt[i] = (1 + arrayOfInt[i]);
    }
    return bool;
  }

  public double nextDouble()
    throws IOException
  {
    JsonToken localJsonToken = peek();
    if ((localJsonToken != JsonToken.NUMBER) && (localJsonToken != JsonToken.STRING))
      throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + localJsonToken + locationString());
    double d = ((JsonPrimitive)peekStack()).getAsDouble();
    if ((!isLenient()) && ((Double.isNaN(d)) || (Double.isInfinite(d))))
      throw new NumberFormatException("JSON forbids NaN and infinities: " + d);
    popStack();
    if (this.stackSize > 0)
    {
      int[] arrayOfInt = this.pathIndices;
      int i = -1 + this.stackSize;
      arrayOfInt[i] = (1 + arrayOfInt[i]);
    }
    return d;
  }

  public int nextInt()
    throws IOException
  {
    JsonToken localJsonToken = peek();
    if ((localJsonToken != JsonToken.NUMBER) && (localJsonToken != JsonToken.STRING))
      throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + localJsonToken + locationString());
    int i = ((JsonPrimitive)peekStack()).getAsInt();
    popStack();
    if (this.stackSize > 0)
    {
      int[] arrayOfInt = this.pathIndices;
      int j = -1 + this.stackSize;
      arrayOfInt[j] = (1 + arrayOfInt[j]);
    }
    return i;
  }

  public long nextLong()
    throws IOException
  {
    JsonToken localJsonToken = peek();
    if ((localJsonToken != JsonToken.NUMBER) && (localJsonToken != JsonToken.STRING))
      throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + localJsonToken + locationString());
    long l = ((JsonPrimitive)peekStack()).getAsLong();
    popStack();
    if (this.stackSize > 0)
    {
      int[] arrayOfInt = this.pathIndices;
      int i = -1 + this.stackSize;
      arrayOfInt[i] = (1 + arrayOfInt[i]);
    }
    return l;
  }

  public String nextName()
    throws IOException
  {
    expect(JsonToken.NAME);
    Map.Entry localEntry = (Map.Entry)((Iterator)peekStack()).next();
    String str = (String)localEntry.getKey();
    this.pathNames[(-1 + this.stackSize)] = str;
    push(localEntry.getValue());
    return str;
  }

  public void nextNull()
    throws IOException
  {
    expect(JsonToken.NULL);
    popStack();
    if (this.stackSize > 0)
    {
      int[] arrayOfInt = this.pathIndices;
      int i = -1 + this.stackSize;
      arrayOfInt[i] = (1 + arrayOfInt[i]);
    }
  }

  public String nextString()
    throws IOException
  {
    JsonToken localJsonToken = peek();
    if ((localJsonToken != JsonToken.STRING) && (localJsonToken != JsonToken.NUMBER))
      throw new IllegalStateException("Expected " + JsonToken.STRING + " but was " + localJsonToken + locationString());
    String str = ((JsonPrimitive)popStack()).getAsString();
    if (this.stackSize > 0)
    {
      int[] arrayOfInt = this.pathIndices;
      int i = -1 + this.stackSize;
      arrayOfInt[i] = (1 + arrayOfInt[i]);
    }
    return str;
  }

  public JsonToken peek()
    throws IOException
  {
    if (this.stackSize == 0)
      return JsonToken.END_DOCUMENT;
    Object localObject = peekStack();
    if ((localObject instanceof Iterator))
    {
      boolean bool = this.stack[(-2 + this.stackSize)] instanceof JsonObject;
      Iterator localIterator = (Iterator)localObject;
      if (localIterator.hasNext())
      {
        if (bool)
          return JsonToken.NAME;
        push(localIterator.next());
        return peek();
      }
      if (bool)
        return JsonToken.END_OBJECT;
      return JsonToken.END_ARRAY;
    }
    if ((localObject instanceof JsonObject))
      return JsonToken.BEGIN_OBJECT;
    if ((localObject instanceof JsonArray))
      return JsonToken.BEGIN_ARRAY;
    if ((localObject instanceof JsonPrimitive))
    {
      JsonPrimitive localJsonPrimitive = (JsonPrimitive)localObject;
      if (localJsonPrimitive.isString())
        return JsonToken.STRING;
      if (localJsonPrimitive.isBoolean())
        return JsonToken.BOOLEAN;
      if (localJsonPrimitive.isNumber())
        return JsonToken.NUMBER;
      throw new AssertionError();
    }
    if ((localObject instanceof JsonNull))
      return JsonToken.NULL;
    if (localObject == SENTINEL_CLOSED)
      throw new IllegalStateException("JsonReader is closed");
    throw new AssertionError();
  }

  public void promoteNameToValue()
    throws IOException
  {
    expect(JsonToken.NAME);
    Map.Entry localEntry = (Map.Entry)((Iterator)peekStack()).next();
    push(localEntry.getValue());
    push(new JsonPrimitive((String)localEntry.getKey()));
  }

  public void skipValue()
    throws IOException
  {
    if (peek() == JsonToken.NAME)
    {
      nextName();
      this.pathNames[(-2 + this.stackSize)] = "null";
    }
    while (true)
    {
      int[] arrayOfInt = this.pathIndices;
      int i = -1 + this.stackSize;
      arrayOfInt[i] = (1 + arrayOfInt[i]);
      return;
      popStack();
      this.pathNames[(-1 + this.stackSize)] = "null";
    }
  }

  public String toString()
  {
    return getClass().getSimpleName();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.google.gson.internal.bind.JsonTreeReader
 * JD-Core Version:    0.6.0
 */