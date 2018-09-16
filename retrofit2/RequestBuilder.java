package retrofit2;

import java.io.IOException;
import okhttp3.FormBody.Builder;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.MultipartBody.Part;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;

final class RequestBuilder
{
  private static final char[] HEX_DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
  private static final String PATH_SEGMENT_ALWAYS_ENCODE_SET = " \"<>^`{}|\\?#";
  private final HttpUrl baseUrl;
  private RequestBody body;
  private MediaType contentType;
  private FormBody.Builder formBuilder;
  private final boolean hasBody;
  private final String method;
  private MultipartBody.Builder multipartBuilder;
  private String relativeUrl;
  private final Request.Builder requestBuilder;
  private HttpUrl.Builder urlBuilder;

  RequestBuilder(String paramString1, HttpUrl paramHttpUrl, String paramString2, Headers paramHeaders, MediaType paramMediaType, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.method = paramString1;
    this.baseUrl = paramHttpUrl;
    this.relativeUrl = paramString2;
    this.requestBuilder = new Request.Builder();
    this.contentType = paramMediaType;
    this.hasBody = paramBoolean1;
    if (paramHeaders != null)
      this.requestBuilder.headers(paramHeaders);
    if (paramBoolean2)
      this.formBuilder = new FormBody.Builder();
    do
      return;
    while (!paramBoolean3);
    this.multipartBuilder = new MultipartBody.Builder();
    this.multipartBuilder.setType(MultipartBody.FORM);
  }

  private static String canonicalizeForPath(String paramString, boolean paramBoolean)
  {
    int i = 0;
    int j = paramString.length();
    while (true)
    {
      int k;
      if (i < j)
      {
        k = paramString.codePointAt(i);
        if ((k < 32) || (k >= 127) || (" \"<>^`{}|\\?#".indexOf(k) != -1) || ((!paramBoolean) && ((k == 47) || (k == 37))))
        {
          Buffer localBuffer = new Buffer();
          localBuffer.writeUtf8(paramString, 0, i);
          canonicalizeForPath(localBuffer, paramString, i, j, paramBoolean);
          paramString = localBuffer.readUtf8();
        }
      }
      else
      {
        return paramString;
      }
      i += Character.charCount(k);
    }
  }

  private static void canonicalizeForPath(Buffer paramBuffer, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Buffer localBuffer = null;
    int i = paramInt1;
    if (i < paramInt2)
    {
      int j = paramString.codePointAt(i);
      if ((paramBoolean) && ((j == 9) || (j == 10) || (j == 12) || (j == 13)));
      while (true)
      {
        i += Character.charCount(j);
        break;
        if ((j < 32) || (j >= 127) || (" \"<>^`{}|\\?#".indexOf(j) != -1) || ((!paramBoolean) && ((j == 47) || (j == 37))))
        {
          if (localBuffer == null)
            localBuffer = new Buffer();
          localBuffer.writeUtf8CodePoint(j);
          while (!localBuffer.exhausted())
          {
            int k = 0xFF & localBuffer.readByte();
            paramBuffer.writeByte(37);
            paramBuffer.writeByte(HEX_DIGITS[(0xF & k >> 4)]);
            paramBuffer.writeByte(HEX_DIGITS[(k & 0xF)]);
          }
          continue;
        }
        paramBuffer.writeUtf8CodePoint(j);
      }
    }
  }

  void addFormField(String paramString1, String paramString2, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.formBuilder.addEncoded(paramString1, paramString2);
      return;
    }
    this.formBuilder.add(paramString1, paramString2);
  }

  void addHeader(String paramString1, String paramString2)
  {
    if ("Content-Type".equalsIgnoreCase(paramString1))
    {
      MediaType localMediaType = MediaType.parse(paramString2);
      if (localMediaType == null)
        throw new IllegalArgumentException("Malformed content type: " + paramString2);
      this.contentType = localMediaType;
      return;
    }
    this.requestBuilder.addHeader(paramString1, paramString2);
  }

  void addPart(Headers paramHeaders, RequestBody paramRequestBody)
  {
    this.multipartBuilder.addPart(paramHeaders, paramRequestBody);
  }

  void addPart(MultipartBody.Part paramPart)
  {
    this.multipartBuilder.addPart(paramPart);
  }

  void addPathParam(String paramString1, String paramString2, boolean paramBoolean)
  {
    if (this.relativeUrl == null)
      throw new AssertionError();
    this.relativeUrl = this.relativeUrl.replace("{" + paramString1 + "}", canonicalizeForPath(paramString2, paramBoolean));
  }

  void addQueryParam(String paramString1, String paramString2, boolean paramBoolean)
  {
    if (this.relativeUrl != null)
    {
      this.urlBuilder = this.baseUrl.newBuilder(this.relativeUrl);
      if (this.urlBuilder == null)
        throw new IllegalArgumentException("Malformed URL. Base: " + this.baseUrl + ", Relative: " + this.relativeUrl);
      this.relativeUrl = null;
    }
    if (paramBoolean)
    {
      this.urlBuilder.addEncodedQueryParameter(paramString1, paramString2);
      return;
    }
    this.urlBuilder.addQueryParameter(paramString1, paramString2);
  }

  Request build()
  {
    HttpUrl.Builder localBuilder = this.urlBuilder;
    HttpUrl localHttpUrl;
    Object localObject;
    label38: MediaType localMediaType;
    if (localBuilder != null)
    {
      localHttpUrl = localBuilder.build();
      localObject = this.body;
      if (localObject == null)
      {
        if (this.formBuilder == null)
          break label142;
        localObject = this.formBuilder.build();
      }
      localMediaType = this.contentType;
      if (localMediaType != null)
      {
        if (localObject == null)
          break label178;
        localObject = new ContentTypeOverridingRequestBody((RequestBody)localObject, localMediaType);
      }
    }
    while (true)
    {
      return this.requestBuilder.url(localHttpUrl).method(this.method, (RequestBody)localObject).build();
      localHttpUrl = this.baseUrl.resolve(this.relativeUrl);
      if (localHttpUrl != null)
        break;
      throw new IllegalArgumentException("Malformed URL. Base: " + this.baseUrl + ", Relative: " + this.relativeUrl);
      label142: if (this.multipartBuilder != null)
      {
        localObject = this.multipartBuilder.build();
        break label38;
      }
      if (!this.hasBody)
        break label38;
      localObject = RequestBody.create(null, new byte[0]);
      break label38;
      label178: this.requestBuilder.addHeader("Content-Type", localMediaType.toString());
    }
  }

  void setBody(RequestBody paramRequestBody)
  {
    this.body = paramRequestBody;
  }

  void setRelativeUrl(Object paramObject)
  {
    if (paramObject == null)
      throw new NullPointerException("@Url parameter is null.");
    this.relativeUrl = paramObject.toString();
  }

  private static class ContentTypeOverridingRequestBody extends RequestBody
  {
    private final MediaType contentType;
    private final RequestBody delegate;

    ContentTypeOverridingRequestBody(RequestBody paramRequestBody, MediaType paramMediaType)
    {
      this.delegate = paramRequestBody;
      this.contentType = paramMediaType;
    }

    public long contentLength()
      throws IOException
    {
      return this.delegate.contentLength();
    }

    public MediaType contentType()
    {
      return this.contentType;
    }

    public void writeTo(BufferedSink paramBufferedSink)
      throws IOException
    {
      this.delegate.writeTo(paramBufferedSink);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.RequestBuilder
 * JD-Core Version:    0.6.0
 */