package com.google.gson.internal.bind;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal..Gson.Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ReflectiveTypeAdapterFactory
  implements TypeAdapterFactory
{
  private final ConstructorConstructor constructorConstructor;
  private final Excluder excluder;
  private final FieldNamingStrategy fieldNamingPolicy;
  private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;

  public ReflectiveTypeAdapterFactory(ConstructorConstructor paramConstructorConstructor, FieldNamingStrategy paramFieldNamingStrategy, Excluder paramExcluder, JsonAdapterAnnotationTypeAdapterFactory paramJsonAdapterAnnotationTypeAdapterFactory)
  {
    this.constructorConstructor = paramConstructorConstructor;
    this.fieldNamingPolicy = paramFieldNamingStrategy;
    this.excluder = paramExcluder;
    this.jsonAdapterFactory = paramJsonAdapterAnnotationTypeAdapterFactory;
  }

  private BoundField createBoundField(Gson paramGson, Field paramField, String paramString, TypeToken<?> paramTypeToken, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool1 = Primitives.isPrimitive(paramTypeToken.getRawType());
    JsonAdapter localJsonAdapter = (JsonAdapter)paramField.getAnnotation(JsonAdapter.class);
    TypeAdapter localTypeAdapter = null;
    if (localJsonAdapter != null)
      localTypeAdapter = this.jsonAdapterFactory.getTypeAdapter(this.constructorConstructor, paramGson, paramTypeToken, localJsonAdapter);
    if (localTypeAdapter != null);
    for (boolean bool2 = true; ; bool2 = false)
    {
      if (localTypeAdapter == null)
        localTypeAdapter = paramGson.getAdapter(paramTypeToken);
      return new BoundField(paramString, paramBoolean1, paramBoolean2, paramField, bool2, localTypeAdapter, paramGson, paramTypeToken, bool1)
      {
        void read(JsonReader paramJsonReader, Object paramObject)
          throws IOException, IllegalAccessException
        {
          Object localObject = this.val$typeAdapter.read(paramJsonReader);
          if ((localObject != null) || (!this.val$isPrimitive))
            this.val$field.set(paramObject, localObject);
        }

        void write(JsonWriter paramJsonWriter, Object paramObject)
          throws IOException, IllegalAccessException
        {
          Object localObject1 = this.val$field.get(paramObject);
          if (this.val$jsonAdapterPresent);
          for (Object localObject2 = this.val$typeAdapter; ; localObject2 = new TypeAdapterRuntimeTypeWrapper(this.val$context, this.val$typeAdapter, this.val$fieldType.getType()))
          {
            ((TypeAdapter)localObject2).write(paramJsonWriter, localObject1);
            return;
          }
        }

        public boolean writeField(Object paramObject)
          throws IOException, IllegalAccessException
        {
          if (!this.serialized);
          do
            return false;
          while (this.val$field.get(paramObject) == paramObject);
          return true;
        }
      };
    }
  }

  static boolean excludeField(Field paramField, boolean paramBoolean, Excluder paramExcluder)
  {
    return (!paramExcluder.excludeClass(paramField.getType(), paramBoolean)) && (!paramExcluder.excludeField(paramField, paramBoolean));
  }

  private Map<String, BoundField> getBoundFields(Gson paramGson, TypeToken<?> paramTypeToken, Class<?> paramClass)
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    if (paramClass.isInterface());
    while (true)
    {
      return localLinkedHashMap;
      Type localType1 = paramTypeToken.getType();
      while (paramClass != Object.class)
      {
        Field[] arrayOfField = paramClass.getDeclaredFields();
        int i = arrayOfField.length;
        int j = 0;
        if (j < i)
        {
          Field localField = arrayOfField[j];
          boolean bool1 = excludeField(localField, true);
          boolean bool2 = excludeField(localField, false);
          if ((!bool1) && (!bool2));
          Object localObject;
          do
          {
            j++;
            break;
            localField.setAccessible(true);
            Type localType4 = paramTypeToken.getType();
            Type localType5 = localField.getGenericType();
            Type localType6 = .Gson.Types.resolve(localType4, paramClass, localType5);
            List localList = getFieldNames(localField);
            localObject = null;
            for (int k = 0; k < localList.size(); k++)
            {
              String str = (String)localList.get(k);
              if (k != 0)
                bool1 = false;
              BoundField localBoundField = (BoundField)localLinkedHashMap.put(str, createBoundField(paramGson, localField, str, TypeToken.get(localType6), bool1, bool2));
              if (localObject != null)
                continue;
              localObject = localBoundField;
            }
          }
          while (localObject == null);
          throw new IllegalArgumentException(localType1 + " declares multiple JSON fields named " + localObject.name);
        }
        Type localType2 = paramTypeToken.getType();
        Type localType3 = paramClass.getGenericSuperclass();
        paramTypeToken = TypeToken.get(.Gson.Types.resolve(localType2, paramClass, localType3));
        paramClass = paramTypeToken.getRawType();
      }
    }
  }

  private List<String> getFieldNames(Field paramField)
  {
    SerializedName localSerializedName = (SerializedName)paramField.getAnnotation(SerializedName.class);
    Object localObject;
    if (localSerializedName == null)
      localObject = Collections.singletonList(this.fieldNamingPolicy.translateName(paramField));
    while (true)
    {
      return localObject;
      String str = localSerializedName.value();
      String[] arrayOfString = localSerializedName.alternate();
      if (arrayOfString.length == 0)
        return Collections.singletonList(str);
      localObject = new ArrayList(1 + arrayOfString.length);
      ((List)localObject).add(str);
      int i = arrayOfString.length;
      for (int j = 0; j < i; j++)
        ((List)localObject).add(arrayOfString[j]);
    }
  }

  public <T> TypeAdapter<T> create(Gson paramGson, TypeToken<T> paramTypeToken)
  {
    Class localClass = paramTypeToken.getRawType();
    if (!Object.class.isAssignableFrom(localClass))
      return null;
    return new Adapter(this.constructorConstructor.get(paramTypeToken), getBoundFields(paramGson, paramTypeToken, localClass));
  }

  public boolean excludeField(Field paramField, boolean paramBoolean)
  {
    return excludeField(paramField, paramBoolean, this.excluder);
  }

  public static final class Adapter<T> extends TypeAdapter<T>
  {
    private final Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields;
    private final ObjectConstructor<T> constructor;

    Adapter(ObjectConstructor<T> paramObjectConstructor, Map<String, ReflectiveTypeAdapterFactory.BoundField> paramMap)
    {
      this.constructor = paramObjectConstructor;
      this.boundFields = paramMap;
    }

    public T read(JsonReader paramJsonReader)
      throws IOException
    {
      if (paramJsonReader.peek() == JsonToken.NULL)
      {
        paramJsonReader.nextNull();
        return null;
      }
      Object localObject = this.constructor.construct();
      try
      {
        paramJsonReader.beginObject();
        while (true)
        {
          if (!paramJsonReader.hasNext())
            break label111;
          String str = paramJsonReader.nextName();
          localBoundField = (ReflectiveTypeAdapterFactory.BoundField)this.boundFields.get(str);
          if ((localBoundField != null) && (localBoundField.deserialized))
            break;
          paramJsonReader.skipValue();
        }
      }
      catch (IllegalStateException localIllegalStateException)
      {
        while (true)
        {
          ReflectiveTypeAdapterFactory.BoundField localBoundField;
          throw new JsonSyntaxException(localIllegalStateException);
          localBoundField.read(paramJsonReader, localObject);
        }
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
      label111: paramJsonReader.endObject();
      return localObject;
    }

    public void write(JsonWriter paramJsonWriter, T paramT)
      throws IOException
    {
      if (paramT == null)
      {
        paramJsonWriter.nullValue();
        return;
      }
      paramJsonWriter.beginObject();
      try
      {
        Iterator localIterator = this.boundFields.values().iterator();
        while (localIterator.hasNext())
        {
          ReflectiveTypeAdapterFactory.BoundField localBoundField = (ReflectiveTypeAdapterFactory.BoundField)localIterator.next();
          if (!localBoundField.writeField(paramT))
            continue;
          paramJsonWriter.name(localBoundField.name);
          localBoundField.write(paramJsonWriter, paramT);
        }
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
      paramJsonWriter.endObject();
    }
  }

  static abstract class BoundField
  {
    final boolean deserialized;
    final String name;
    final boolean serialized;

    protected BoundField(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.name = paramString;
      this.serialized = paramBoolean1;
      this.deserialized = paramBoolean2;
    }

    abstract void read(JsonReader paramJsonReader, Object paramObject)
      throws IOException, IllegalAccessException;

    abstract void write(JsonWriter paramJsonWriter, Object paramObject)
      throws IOException, IllegalAccessException;

    abstract boolean writeField(Object paramObject)
      throws IOException, IllegalAccessException;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.google.gson.internal.bind.ReflectiveTypeAdapterFactory
 * JD-Core Version:    0.6.0
 */