package okhttp3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;

public final class MultipartBody extends RequestBody
{
  public static final MediaType ALTERNATIVE;
  private static final byte[] COLONSPACE;
  private static final byte[] CRLF;
  private static final byte[] DASHDASH;
  public static final MediaType DIGEST;
  public static final MediaType FORM;
  public static final MediaType MIXED = MediaType.parse("multipart/mixed");
  public static final MediaType PARALLEL;
  private final ByteString boundary;
  private long contentLength = -1L;
  private final MediaType contentType;
  private final MediaType originalType;
  private final List<Part> parts;

  static
  {
    ALTERNATIVE = MediaType.parse("multipart/alternative");
    DIGEST = MediaType.parse("multipart/digest");
    PARALLEL = MediaType.parse("multipart/parallel");
    FORM = MediaType.parse("multipart/form-data");
    COLONSPACE = new byte[] { 58, 32 };
    CRLF = new byte[] { 13, 10 };
    DASHDASH = new byte[] { 45, 45 };
  }

  MultipartBody(ByteString paramByteString, MediaType paramMediaType, List<Part> paramList)
  {
    this.boundary = paramByteString;
    this.originalType = paramMediaType;
    this.contentType = MediaType.parse(paramMediaType + "; boundary=" + paramByteString.utf8());
    this.parts = Util.immutableList(paramList);
  }

  static StringBuilder appendQuotedString(StringBuilder paramStringBuilder, String paramString)
  {
    paramStringBuilder.append('"');
    int i = 0;
    int j = paramString.length();
    if (i < j)
    {
      char c = paramString.charAt(i);
      switch (c)
      {
      default:
        paramStringBuilder.append(c);
      case '\n':
      case '\r':
      case '"':
      }
      while (true)
      {
        i++;
        break;
        paramStringBuilder.append("%0A");
        continue;
        paramStringBuilder.append("%0D");
        continue;
        paramStringBuilder.append("%22");
      }
    }
    paramStringBuilder.append('"');
    return paramStringBuilder;
  }

  private long writeOrCountBytes(BufferedSink paramBufferedSink, boolean paramBoolean)
    throws IOException
  {
    long l1 = 0L;
    Buffer localBuffer = null;
    if (paramBoolean)
    {
      localBuffer = new Buffer();
      paramBufferedSink = localBuffer;
    }
    int i = 0;
    int j = this.parts.size();
    if (i < j)
    {
      Part localPart = (Part)this.parts.get(i);
      Headers localHeaders = localPart.headers;
      RequestBody localRequestBody = localPart.body;
      byte[] arrayOfByte4 = DASHDASH;
      paramBufferedSink.write(arrayOfByte4);
      ByteString localByteString2 = this.boundary;
      paramBufferedSink.write(localByteString2);
      byte[] arrayOfByte5 = CRLF;
      paramBufferedSink.write(arrayOfByte5);
      if (localHeaders != null)
      {
        int k = 0;
        int m = localHeaders.size();
        while (k < m)
        {
          String str = localHeaders.name(k);
          paramBufferedSink.writeUtf8(str).write(COLONSPACE).writeUtf8(localHeaders.value(k)).write(CRLF);
          k++;
        }
      }
      MediaType localMediaType = localRequestBody.contentType();
      if (localMediaType != null)
        paramBufferedSink.writeUtf8("Content-Type: ").writeUtf8(localMediaType.toString()).write(CRLF);
      long l2 = localRequestBody.contentLength();
      if (l2 != -1L)
      {
        paramBufferedSink.writeUtf8("Content-Length: ").writeDecimalLong(l2).write(CRLF);
        label268: byte[] arrayOfByte6 = CRLF;
        paramBufferedSink.write(arrayOfByte6);
        if (!paramBoolean)
          break label324;
        l1 += l2;
      }
      while (true)
      {
        byte[] arrayOfByte7 = CRLF;
        paramBufferedSink.write(arrayOfByte7);
        i++;
        break;
        if (!paramBoolean)
          break label268;
        localBuffer.clear();
        return -1L;
        label324: localRequestBody.writeTo(paramBufferedSink);
      }
    }
    byte[] arrayOfByte1 = DASHDASH;
    paramBufferedSink.write(arrayOfByte1);
    ByteString localByteString1 = this.boundary;
    paramBufferedSink.write(localByteString1);
    byte[] arrayOfByte2 = DASHDASH;
    paramBufferedSink.write(arrayOfByte2);
    byte[] arrayOfByte3 = CRLF;
    paramBufferedSink.write(arrayOfByte3);
    if (paramBoolean)
    {
      l1 += localBuffer.size();
      localBuffer.clear();
    }
    return l1;
  }

  public String boundary()
  {
    return this.boundary.utf8();
  }

  public long contentLength()
    throws IOException
  {
    long l1 = this.contentLength;
    if (l1 != -1L)
      return l1;
    long l2 = writeOrCountBytes(null, true);
    this.contentLength = l2;
    return l2;
  }

  public MediaType contentType()
  {
    return this.contentType;
  }

  public Part part(int paramInt)
  {
    return (Part)this.parts.get(paramInt);
  }

  public List<Part> parts()
  {
    return this.parts;
  }

  public int size()
  {
    return this.parts.size();
  }

  public MediaType type()
  {
    return this.originalType;
  }

  public void writeTo(BufferedSink paramBufferedSink)
    throws IOException
  {
    writeOrCountBytes(paramBufferedSink, false);
  }

  public static final class Builder
  {
    private final ByteString boundary;
    private final List<MultipartBody.Part> parts = new ArrayList();
    private MediaType type = MultipartBody.MIXED;

    public Builder()
    {
      this(UUID.randomUUID().toString());
    }

    public Builder(String paramString)
    {
      this.boundary = ByteString.encodeUtf8(paramString);
    }

    public Builder addFormDataPart(String paramString1, String paramString2)
    {
      return addPart(MultipartBody.Part.createFormData(paramString1, paramString2));
    }

    public Builder addFormDataPart(String paramString1, String paramString2, RequestBody paramRequestBody)
    {
      return addPart(MultipartBody.Part.createFormData(paramString1, paramString2, paramRequestBody));
    }

    public Builder addPart(Headers paramHeaders, RequestBody paramRequestBody)
    {
      return addPart(MultipartBody.Part.create(paramHeaders, paramRequestBody));
    }

    public Builder addPart(MultipartBody.Part paramPart)
    {
      if (paramPart == null)
        throw new NullPointerException("part == null");
      this.parts.add(paramPart);
      return this;
    }

    public Builder addPart(RequestBody paramRequestBody)
    {
      return addPart(MultipartBody.Part.create(paramRequestBody));
    }

    public MultipartBody build()
    {
      if (this.parts.isEmpty())
        throw new IllegalStateException("Multipart body must have at least one part.");
      return new MultipartBody(this.boundary, this.type, this.parts);
    }

    public Builder setType(MediaType paramMediaType)
    {
      if (paramMediaType == null)
        throw new NullPointerException("type == null");
      if (!paramMediaType.type().equals("multipart"))
        throw new IllegalArgumentException("multipart != " + paramMediaType);
      this.type = paramMediaType;
      return this;
    }
  }

  public static final class Part
  {
    final RequestBody body;
    final Headers headers;

    private Part(Headers paramHeaders, RequestBody paramRequestBody)
    {
      this.headers = paramHeaders;
      this.body = paramRequestBody;
    }

    public static Part create(Headers paramHeaders, RequestBody paramRequestBody)
    {
      if (paramRequestBody == null)
        throw new NullPointerException("body == null");
      if ((paramHeaders != null) && (paramHeaders.get("Content-Type") != null))
        throw new IllegalArgumentException("Unexpected header: Content-Type");
      if ((paramHeaders != null) && (paramHeaders.get("Content-Length") != null))
        throw new IllegalArgumentException("Unexpected header: Content-Length");
      return new Part(paramHeaders, paramRequestBody);
    }

    public static Part create(RequestBody paramRequestBody)
    {
      return create(null, paramRequestBody);
    }

    public static Part createFormData(String paramString1, String paramString2)
    {
      return createFormData(paramString1, null, RequestBody.create(null, paramString2));
    }

    public static Part createFormData(String paramString1, String paramString2, RequestBody paramRequestBody)
    {
      if (paramString1 == null)
        throw new NullPointerException("name == null");
      StringBuilder localStringBuilder = new StringBuilder("form-data; name=");
      MultipartBody.appendQuotedString(localStringBuilder, paramString1);
      if (paramString2 != null)
      {
        localStringBuilder.append("; filename=");
        MultipartBody.appendQuotedString(localStringBuilder, paramString2);
      }
      String[] arrayOfString = new String[2];
      arrayOfString[0] = "Content-Disposition";
      arrayOfString[1] = localStringBuilder.toString();
      return create(Headers.of(arrayOfString), paramRequestBody);
    }

    public RequestBody body()
    {
      return this.body;
    }

    public Headers headers()
    {
      return this.headers;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.MultipartBody
 * JD-Core Version:    0.6.0
 */