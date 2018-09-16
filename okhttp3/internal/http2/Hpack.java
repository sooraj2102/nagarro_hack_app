package okhttp3.internal.http2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Source;

final class Hpack
{
  static final Map<ByteString, Integer> NAME_TO_FIRST_INDEX;
  private static final int PREFIX_4_BITS = 15;
  private static final int PREFIX_5_BITS = 31;
  private static final int PREFIX_6_BITS = 63;
  private static final int PREFIX_7_BITS = 127;
  static final Header[] STATIC_HEADER_TABLE;

  static
  {
    Header[] arrayOfHeader = new Header[61];
    arrayOfHeader[0] = new Header(Header.TARGET_AUTHORITY, "");
    arrayOfHeader[1] = new Header(Header.TARGET_METHOD, "GET");
    arrayOfHeader[2] = new Header(Header.TARGET_METHOD, "POST");
    arrayOfHeader[3] = new Header(Header.TARGET_PATH, "/");
    arrayOfHeader[4] = new Header(Header.TARGET_PATH, "/index.html");
    arrayOfHeader[5] = new Header(Header.TARGET_SCHEME, "http");
    arrayOfHeader[6] = new Header(Header.TARGET_SCHEME, "https");
    arrayOfHeader[7] = new Header(Header.RESPONSE_STATUS, "200");
    arrayOfHeader[8] = new Header(Header.RESPONSE_STATUS, "204");
    arrayOfHeader[9] = new Header(Header.RESPONSE_STATUS, "206");
    arrayOfHeader[10] = new Header(Header.RESPONSE_STATUS, "304");
    arrayOfHeader[11] = new Header(Header.RESPONSE_STATUS, "400");
    arrayOfHeader[12] = new Header(Header.RESPONSE_STATUS, "404");
    arrayOfHeader[13] = new Header(Header.RESPONSE_STATUS, "500");
    arrayOfHeader[14] = new Header("accept-charset", "");
    arrayOfHeader[15] = new Header("accept-encoding", "gzip, deflate");
    arrayOfHeader[16] = new Header("accept-language", "");
    arrayOfHeader[17] = new Header("accept-ranges", "");
    arrayOfHeader[18] = new Header("accept", "");
    arrayOfHeader[19] = new Header("access-control-allow-origin", "");
    arrayOfHeader[20] = new Header("age", "");
    arrayOfHeader[21] = new Header("allow", "");
    arrayOfHeader[22] = new Header("authorization", "");
    arrayOfHeader[23] = new Header("cache-control", "");
    arrayOfHeader[24] = new Header("content-disposition", "");
    arrayOfHeader[25] = new Header("content-encoding", "");
    arrayOfHeader[26] = new Header("content-language", "");
    arrayOfHeader[27] = new Header("content-length", "");
    arrayOfHeader[28] = new Header("content-location", "");
    arrayOfHeader[29] = new Header("content-range", "");
    arrayOfHeader[30] = new Header("content-type", "");
    arrayOfHeader[31] = new Header("cookie", "");
    arrayOfHeader[32] = new Header("date", "");
    arrayOfHeader[33] = new Header("etag", "");
    arrayOfHeader[34] = new Header("expect", "");
    arrayOfHeader[35] = new Header("expires", "");
    arrayOfHeader[36] = new Header("from", "");
    arrayOfHeader[37] = new Header("host", "");
    arrayOfHeader[38] = new Header("if-match", "");
    arrayOfHeader[39] = new Header("if-modified-since", "");
    arrayOfHeader[40] = new Header("if-none-match", "");
    arrayOfHeader[41] = new Header("if-range", "");
    arrayOfHeader[42] = new Header("if-unmodified-since", "");
    arrayOfHeader[43] = new Header("last-modified", "");
    arrayOfHeader[44] = new Header("link", "");
    arrayOfHeader[45] = new Header("location", "");
    arrayOfHeader[46] = new Header("max-forwards", "");
    arrayOfHeader[47] = new Header("proxy-authenticate", "");
    arrayOfHeader[48] = new Header("proxy-authorization", "");
    arrayOfHeader[49] = new Header("range", "");
    arrayOfHeader[50] = new Header("referer", "");
    arrayOfHeader[51] = new Header("refresh", "");
    arrayOfHeader[52] = new Header("retry-after", "");
    arrayOfHeader[53] = new Header("server", "");
    arrayOfHeader[54] = new Header("set-cookie", "");
    arrayOfHeader[55] = new Header("strict-transport-security", "");
    arrayOfHeader[56] = new Header("transfer-encoding", "");
    arrayOfHeader[57] = new Header("user-agent", "");
    arrayOfHeader[58] = new Header("vary", "");
    arrayOfHeader[59] = new Header("via", "");
    arrayOfHeader[60] = new Header("www-authenticate", "");
    STATIC_HEADER_TABLE = arrayOfHeader;
    NAME_TO_FIRST_INDEX = nameToFirstIndex();
  }

  static ByteString checkLowercase(ByteString paramByteString)
    throws IOException
  {
    int i = 0;
    int j = paramByteString.size();
    while (i < j)
    {
      int k = paramByteString.getByte(i);
      if ((k >= 65) && (k <= 90))
        throw new IOException("PROTOCOL_ERROR response malformed: mixed case name: " + paramByteString.utf8());
      i++;
    }
    return paramByteString;
  }

  private static Map<ByteString, Integer> nameToFirstIndex()
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap(STATIC_HEADER_TABLE.length);
    for (int i = 0; i < STATIC_HEADER_TABLE.length; i++)
    {
      if (localLinkedHashMap.containsKey(STATIC_HEADER_TABLE[i].name))
        continue;
      localLinkedHashMap.put(STATIC_HEADER_TABLE[i].name, Integer.valueOf(i));
    }
    return Collections.unmodifiableMap(localLinkedHashMap);
  }

  static final class Reader
  {
    Header[] dynamicTable = new Header[8];
    int dynamicTableByteCount = 0;
    int headerCount = 0;
    private final List<Header> headerList = new ArrayList();
    private final int headerTableSizeSetting;
    private int maxDynamicTableByteCount;
    int nextHeaderIndex = -1 + this.dynamicTable.length;
    private final BufferedSource source;

    Reader(int paramInt1, int paramInt2, Source paramSource)
    {
      this.headerTableSizeSetting = paramInt1;
      this.maxDynamicTableByteCount = paramInt2;
      this.source = Okio.buffer(paramSource);
    }

    Reader(int paramInt, Source paramSource)
    {
      this(paramInt, paramInt, paramSource);
    }

    private void adjustDynamicTableByteCount()
    {
      if (this.maxDynamicTableByteCount < this.dynamicTableByteCount)
      {
        if (this.maxDynamicTableByteCount == 0)
          clearDynamicTable();
      }
      else
        return;
      evictToRecoverBytes(this.dynamicTableByteCount - this.maxDynamicTableByteCount);
    }

    private void clearDynamicTable()
    {
      Arrays.fill(this.dynamicTable, null);
      this.nextHeaderIndex = (-1 + this.dynamicTable.length);
      this.headerCount = 0;
      this.dynamicTableByteCount = 0;
    }

    private int dynamicTableIndex(int paramInt)
    {
      return paramInt + (1 + this.nextHeaderIndex);
    }

    private int evictToRecoverBytes(int paramInt)
    {
      int i = 0;
      if (paramInt > 0)
      {
        for (int j = -1 + this.dynamicTable.length; (j >= this.nextHeaderIndex) && (paramInt > 0); j--)
        {
          paramInt -= this.dynamicTable[j].hpackSize;
          this.dynamicTableByteCount -= this.dynamicTable[j].hpackSize;
          this.headerCount = (-1 + this.headerCount);
          i++;
        }
        System.arraycopy(this.dynamicTable, 1 + this.nextHeaderIndex, this.dynamicTable, i + (1 + this.nextHeaderIndex), this.headerCount);
        this.nextHeaderIndex = (i + this.nextHeaderIndex);
      }
      return i;
    }

    private ByteString getName(int paramInt)
    {
      if (isStaticHeader(paramInt))
        return Hpack.STATIC_HEADER_TABLE[paramInt].name;
      return this.dynamicTable[dynamicTableIndex(paramInt - Hpack.STATIC_HEADER_TABLE.length)].name;
    }

    private void insertIntoDynamicTable(int paramInt, Header paramHeader)
    {
      this.headerList.add(paramHeader);
      int i = paramHeader.hpackSize;
      if (paramInt != -1)
        i -= this.dynamicTable[dynamicTableIndex(paramInt)].hpackSize;
      if (i > this.maxDynamicTableByteCount)
      {
        clearDynamicTable();
        return;
      }
      int j = evictToRecoverBytes(i + this.dynamicTableByteCount - this.maxDynamicTableByteCount);
      if (paramInt == -1)
      {
        if (1 + this.headerCount > this.dynamicTable.length)
        {
          Header[] arrayOfHeader = new Header[2 * this.dynamicTable.length];
          System.arraycopy(this.dynamicTable, 0, arrayOfHeader, this.dynamicTable.length, this.dynamicTable.length);
          this.nextHeaderIndex = (-1 + this.dynamicTable.length);
          this.dynamicTable = arrayOfHeader;
        }
        int m = this.nextHeaderIndex;
        this.nextHeaderIndex = (m - 1);
        this.dynamicTable[m] = paramHeader;
        this.headerCount = (1 + this.headerCount);
      }
      while (true)
      {
        this.dynamicTableByteCount = (i + this.dynamicTableByteCount);
        return;
        int k = paramInt + (j + dynamicTableIndex(paramInt));
        this.dynamicTable[k] = paramHeader;
      }
    }

    private boolean isStaticHeader(int paramInt)
    {
      return (paramInt >= 0) && (paramInt <= -1 + Hpack.STATIC_HEADER_TABLE.length);
    }

    private int readByte()
      throws IOException
    {
      return 0xFF & this.source.readByte();
    }

    private void readIndexedHeader(int paramInt)
      throws IOException
    {
      if (isStaticHeader(paramInt))
      {
        Header localHeader = Hpack.STATIC_HEADER_TABLE[paramInt];
        this.headerList.add(localHeader);
        return;
      }
      int i = dynamicTableIndex(paramInt - Hpack.STATIC_HEADER_TABLE.length);
      if ((i < 0) || (i > -1 + this.dynamicTable.length))
        throw new IOException("Header index too large " + (paramInt + 1));
      this.headerList.add(this.dynamicTable[i]);
    }

    private void readLiteralHeaderWithIncrementalIndexingIndexedName(int paramInt)
      throws IOException
    {
      insertIntoDynamicTable(-1, new Header(getName(paramInt), readByteString()));
    }

    private void readLiteralHeaderWithIncrementalIndexingNewName()
      throws IOException
    {
      insertIntoDynamicTable(-1, new Header(Hpack.checkLowercase(readByteString()), readByteString()));
    }

    private void readLiteralHeaderWithoutIndexingIndexedName(int paramInt)
      throws IOException
    {
      ByteString localByteString1 = getName(paramInt);
      ByteString localByteString2 = readByteString();
      this.headerList.add(new Header(localByteString1, localByteString2));
    }

    private void readLiteralHeaderWithoutIndexingNewName()
      throws IOException
    {
      ByteString localByteString1 = Hpack.checkLowercase(readByteString());
      ByteString localByteString2 = readByteString();
      this.headerList.add(new Header(localByteString1, localByteString2));
    }

    public List<Header> getAndResetHeaderList()
    {
      ArrayList localArrayList = new ArrayList(this.headerList);
      this.headerList.clear();
      return localArrayList;
    }

    int maxDynamicTableByteCount()
    {
      return this.maxDynamicTableByteCount;
    }

    ByteString readByteString()
      throws IOException
    {
      int i = readByte();
      if ((i & 0x80) == 128);
      int k;
      for (int j = 1; ; j = 0)
      {
        k = readInt(i, 127);
        if (j == 0)
          break;
        return ByteString.of(Huffman.get().decode(this.source.readByteArray(k)));
      }
      return this.source.readByteString(k);
    }

    void readHeaders()
      throws IOException
    {
      while (!this.source.exhausted())
      {
        int i = 0xFF & this.source.readByte();
        if (i == 128)
          throw new IOException("index == 0");
        if ((i & 0x80) == 128)
        {
          readIndexedHeader(-1 + readInt(i, 127));
          continue;
        }
        if (i == 64)
        {
          readLiteralHeaderWithIncrementalIndexingNewName();
          continue;
        }
        if ((i & 0x40) == 64)
        {
          readLiteralHeaderWithIncrementalIndexingIndexedName(-1 + readInt(i, 63));
          continue;
        }
        if ((i & 0x20) == 32)
        {
          this.maxDynamicTableByteCount = readInt(i, 31);
          if ((this.maxDynamicTableByteCount < 0) || (this.maxDynamicTableByteCount > this.headerTableSizeSetting))
            throw new IOException("Invalid dynamic table size update " + this.maxDynamicTableByteCount);
          adjustDynamicTableByteCount();
          continue;
        }
        if ((i == 16) || (i == 0))
        {
          readLiteralHeaderWithoutIndexingNewName();
          continue;
        }
        readLiteralHeaderWithoutIndexingIndexedName(-1 + readInt(i, 15));
      }
    }

    int readInt(int paramInt1, int paramInt2)
      throws IOException
    {
      int i = paramInt1 & paramInt2;
      if (i < paramInt2)
        return i;
      int j = paramInt2;
      int m;
      for (int k = 0; ; k += 7)
      {
        m = readByte();
        if ((m & 0x80) == 0)
          break;
        j += ((m & 0x7F) << k);
      }
      return j + (m << k);
    }
  }

  static final class Writer
  {
    private static final int SETTINGS_HEADER_TABLE_SIZE = 4096;
    private static final int SETTINGS_HEADER_TABLE_SIZE_LIMIT = 16384;
    Header[] dynamicTable = new Header[8];
    int dynamicTableByteCount = 0;
    private boolean emitDynamicTableSizeUpdate;
    int headerCount = 0;
    int headerTableSizeSetting;
    int maxDynamicTableByteCount;
    int nextHeaderIndex = -1 + this.dynamicTable.length;
    private final Buffer out;
    private int smallestHeaderTableSizeSetting = 2147483647;
    private final boolean useCompression;

    Writer(int paramInt, boolean paramBoolean, Buffer paramBuffer)
    {
      this.headerTableSizeSetting = paramInt;
      this.maxDynamicTableByteCount = paramInt;
      this.useCompression = paramBoolean;
      this.out = paramBuffer;
    }

    Writer(Buffer paramBuffer)
    {
      this(4096, true, paramBuffer);
    }

    private void adjustDynamicTableByteCount()
    {
      if (this.maxDynamicTableByteCount < this.dynamicTableByteCount)
      {
        if (this.maxDynamicTableByteCount == 0)
          clearDynamicTable();
      }
      else
        return;
      evictToRecoverBytes(this.dynamicTableByteCount - this.maxDynamicTableByteCount);
    }

    private void clearDynamicTable()
    {
      Arrays.fill(this.dynamicTable, null);
      this.nextHeaderIndex = (-1 + this.dynamicTable.length);
      this.headerCount = 0;
      this.dynamicTableByteCount = 0;
    }

    private int evictToRecoverBytes(int paramInt)
    {
      int i = 0;
      if (paramInt > 0)
      {
        for (int j = -1 + this.dynamicTable.length; (j >= this.nextHeaderIndex) && (paramInt > 0); j--)
        {
          paramInt -= this.dynamicTable[j].hpackSize;
          this.dynamicTableByteCount -= this.dynamicTable[j].hpackSize;
          this.headerCount = (-1 + this.headerCount);
          i++;
        }
        System.arraycopy(this.dynamicTable, 1 + this.nextHeaderIndex, this.dynamicTable, i + (1 + this.nextHeaderIndex), this.headerCount);
        Arrays.fill(this.dynamicTable, 1 + this.nextHeaderIndex, i + (1 + this.nextHeaderIndex), null);
        this.nextHeaderIndex = (i + this.nextHeaderIndex);
      }
      return i;
    }

    private void insertIntoDynamicTable(Header paramHeader)
    {
      int i = paramHeader.hpackSize;
      if (i > this.maxDynamicTableByteCount)
      {
        clearDynamicTable();
        return;
      }
      evictToRecoverBytes(i + this.dynamicTableByteCount - this.maxDynamicTableByteCount);
      if (1 + this.headerCount > this.dynamicTable.length)
      {
        Header[] arrayOfHeader = new Header[2 * this.dynamicTable.length];
        System.arraycopy(this.dynamicTable, 0, arrayOfHeader, this.dynamicTable.length, this.dynamicTable.length);
        this.nextHeaderIndex = (-1 + this.dynamicTable.length);
        this.dynamicTable = arrayOfHeader;
      }
      int j = this.nextHeaderIndex;
      this.nextHeaderIndex = (j - 1);
      this.dynamicTable[j] = paramHeader;
      this.headerCount = (1 + this.headerCount);
      this.dynamicTableByteCount = (i + this.dynamicTableByteCount);
    }

    void setHeaderTableSizeSetting(int paramInt)
    {
      this.headerTableSizeSetting = paramInt;
      int i = Math.min(paramInt, 16384);
      if (this.maxDynamicTableByteCount == i)
        return;
      if (i < this.maxDynamicTableByteCount)
        this.smallestHeaderTableSizeSetting = Math.min(this.smallestHeaderTableSizeSetting, i);
      this.emitDynamicTableSizeUpdate = true;
      this.maxDynamicTableByteCount = i;
      adjustDynamicTableByteCount();
    }

    void writeByteString(ByteString paramByteString)
      throws IOException
    {
      if ((this.useCompression) && (Huffman.get().encodedLength(paramByteString) < paramByteString.size()))
      {
        Buffer localBuffer = new Buffer();
        Huffman.get().encode(paramByteString, localBuffer);
        ByteString localByteString = localBuffer.readByteString();
        writeInt(localByteString.size(), 127, 128);
        this.out.write(localByteString);
        return;
      }
      writeInt(paramByteString.size(), 127, 0);
      this.out.write(paramByteString);
    }

    void writeHeaders(List<Header> paramList)
      throws IOException
    {
      if (this.emitDynamicTableSizeUpdate)
      {
        if (this.smallestHeaderTableSizeSetting < this.maxDynamicTableByteCount)
          writeInt(this.smallestHeaderTableSizeSetting, 31, 32);
        this.emitDynamicTableSizeUpdate = false;
        this.smallestHeaderTableSizeSetting = 2147483647;
        writeInt(this.maxDynamicTableByteCount, 31, 32);
      }
      int i = 0;
      int j = paramList.size();
      if (i < j)
      {
        Header localHeader = (Header)paramList.get(i);
        ByteString localByteString1 = localHeader.name.toAsciiLowercase();
        ByteString localByteString2 = localHeader.value;
        int k = -1;
        int m = -1;
        Integer localInteger = (Integer)Hpack.NAME_TO_FIRST_INDEX.get(localByteString1);
        if (localInteger != null)
        {
          m = 1 + localInteger.intValue();
          if ((m > 1) && (m < 8))
          {
            if (!Util.equal(Hpack.STATIC_HEADER_TABLE[(m - 1)].value, localByteString2))
              break label268;
            k = m;
          }
        }
        label167: int n;
        if (k == -1)
        {
          n = 1 + this.nextHeaderIndex;
          int i1 = this.dynamicTable.length;
          label188: if (n < i1)
          {
            if (!Util.equal(this.dynamicTable[n].name, localByteString1))
              break label314;
            if (!Util.equal(this.dynamicTable[n].value, localByteString2))
              break label294;
            k = n - this.nextHeaderIndex + Hpack.STATIC_HEADER_TABLE.length;
          }
        }
        if (k != -1)
          writeInt(k, 127, 128);
        while (true)
        {
          i++;
          break;
          label268: if (!Util.equal(Hpack.STATIC_HEADER_TABLE[m].value, localByteString2))
            break label167;
          k = m + 1;
          break label167;
          label294: if (m == -1)
            m = n - this.nextHeaderIndex + Hpack.STATIC_HEADER_TABLE.length;
          label314: n++;
          break label188;
          if (m == -1)
          {
            this.out.writeByte(64);
            writeByteString(localByteString1);
            writeByteString(localByteString2);
            insertIntoDynamicTable(localHeader);
            continue;
          }
          if ((localByteString1.startsWith(Header.PSEUDO_PREFIX)) && (!Header.TARGET_AUTHORITY.equals(localByteString1)))
          {
            writeInt(m, 15, 0);
            writeByteString(localByteString2);
            continue;
          }
          writeInt(m, 63, 64);
          writeByteString(localByteString2);
          insertIntoDynamicTable(localHeader);
        }
      }
    }

    void writeInt(int paramInt1, int paramInt2, int paramInt3)
    {
      if (paramInt1 < paramInt2)
      {
        this.out.writeByte(paramInt3 | paramInt1);
        return;
      }
      this.out.writeByte(paramInt3 | paramInt2);
      int i = paramInt1 - paramInt2;
      while (i >= 128)
      {
        int j = i & 0x7F;
        this.out.writeByte(j | 0x80);
        i >>>= 7;
      }
      this.out.writeByte(i);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.internal.http2.Hpack
 * JD-Core Version:    0.6.0
 */