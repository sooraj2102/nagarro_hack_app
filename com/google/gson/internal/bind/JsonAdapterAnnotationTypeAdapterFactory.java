package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;

public final class JsonAdapterAnnotationTypeAdapterFactory
  implements TypeAdapterFactory
{
  private final ConstructorConstructor constructorConstructor;

  public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor paramConstructorConstructor)
  {
    this.constructorConstructor = paramConstructorConstructor;
  }

  public <T> TypeAdapter<T> create(Gson paramGson, TypeToken<T> paramTypeToken)
  {
    JsonAdapter localJsonAdapter = (JsonAdapter)paramTypeToken.getRawType().getAnnotation(JsonAdapter.class);
    if (localJsonAdapter == null)
      return null;
    return getTypeAdapter(this.constructorConstructor, paramGson, paramTypeToken, localJsonAdapter);
  }

  TypeAdapter<?> getTypeAdapter(ConstructorConstructor paramConstructorConstructor, Gson paramGson, TypeToken<?> paramTypeToken, JsonAdapter paramJsonAdapter)
  {
    Object localObject1 = paramConstructorConstructor.get(TypeToken.get(paramJsonAdapter.value())).construct();
    if ((localObject1 instanceof TypeAdapter));
    for (Object localObject2 = (TypeAdapter)localObject1; ; localObject2 = ((TypeAdapterFactory)localObject1).create(paramGson, paramTypeToken))
    {
      if (localObject2 != null)
        localObject2 = ((TypeAdapter)localObject2).nullSafe();
      return localObject2;
      if (!(localObject1 instanceof TypeAdapterFactory))
        break;
    }
    if (((localObject1 instanceof JsonSerializer)) || ((localObject1 instanceof JsonDeserializer)))
    {
      JsonSerializer localJsonSerializer;
      if ((localObject1 instanceof JsonSerializer))
      {
        localJsonSerializer = (JsonSerializer)localObject1;
        label107: if (!(localObject1 instanceof JsonDeserializer))
          break label147;
      }
      label147: for (JsonDeserializer localJsonDeserializer = (JsonDeserializer)localObject1; ; localJsonDeserializer = null)
      {
        localObject2 = new TreeTypeAdapter(localJsonSerializer, localJsonDeserializer, paramGson, paramTypeToken, null);
        break;
        localJsonSerializer = null;
        break label107;
      }
    }
    throw new IllegalArgumentException("@JsonAdapter value must be TypeAdapter, TypeAdapterFactory, JsonSerializer or JsonDeserializer reference.");
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory
 * JD-Core Version:    0.6.0
 */