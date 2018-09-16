package retrofit2;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.NoSuchElementException;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

final class Utils
{
  static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

  static ResponseBody buffer(ResponseBody paramResponseBody)
    throws IOException
  {
    Buffer localBuffer = new Buffer();
    paramResponseBody.source().readAll(localBuffer);
    return ResponseBody.create(paramResponseBody.contentType(), paramResponseBody.contentLength(), localBuffer);
  }

  static <T> T checkNotNull(T paramT, String paramString)
  {
    if (paramT == null)
      throw new NullPointerException(paramString);
    return paramT;
  }

  static void checkNotPrimitive(Type paramType)
  {
    if (((paramType instanceof Class)) && (((Class)paramType).isPrimitive()))
      throw new IllegalArgumentException();
  }

  private static Class<?> declaringClassOf(TypeVariable<?> paramTypeVariable)
  {
    GenericDeclaration localGenericDeclaration = paramTypeVariable.getGenericDeclaration();
    if ((localGenericDeclaration instanceof Class))
      return (Class)localGenericDeclaration;
    return null;
  }

  private static boolean equal(Object paramObject1, Object paramObject2)
  {
    return (paramObject1 == paramObject2) || ((paramObject1 != null) && (paramObject1.equals(paramObject2)));
  }

  static boolean equals(Type paramType1, Type paramType2)
  {
    int i = 1;
    int j;
    if (paramType1 == paramType2)
      j = i;
    boolean bool2;
    do
    {
      boolean bool1;
      do
      {
        while (true)
        {
          return j;
          if ((paramType1 instanceof Class))
            return paramType1.equals(paramType2);
          if ((paramType1 instanceof ParameterizedType))
          {
            boolean bool5 = paramType2 instanceof ParameterizedType;
            j = 0;
            if (!bool5)
              continue;
            ParameterizedType localParameterizedType1 = (ParameterizedType)paramType1;
            ParameterizedType localParameterizedType2 = (ParameterizedType)paramType2;
            if ((equal(localParameterizedType1.getOwnerType(), localParameterizedType2.getOwnerType())) && (localParameterizedType1.getRawType().equals(localParameterizedType2.getRawType())) && (Arrays.equals(localParameterizedType1.getActualTypeArguments(), localParameterizedType2.getActualTypeArguments())));
            while (true)
            {
              return i;
              i = 0;
            }
          }
          if ((paramType1 instanceof GenericArrayType))
          {
            boolean bool4 = paramType2 instanceof GenericArrayType;
            j = 0;
            if (!bool4)
              continue;
            GenericArrayType localGenericArrayType1 = (GenericArrayType)paramType1;
            GenericArrayType localGenericArrayType2 = (GenericArrayType)paramType2;
            return equals(localGenericArrayType1.getGenericComponentType(), localGenericArrayType2.getGenericComponentType());
          }
          if (!(paramType1 instanceof WildcardType))
            break;
          boolean bool3 = paramType2 instanceof WildcardType;
          j = 0;
          if (!bool3)
            continue;
          WildcardType localWildcardType1 = (WildcardType)paramType1;
          WildcardType localWildcardType2 = (WildcardType)paramType2;
          if ((Arrays.equals(localWildcardType1.getUpperBounds(), localWildcardType2.getUpperBounds())) && (Arrays.equals(localWildcardType1.getLowerBounds(), localWildcardType2.getLowerBounds())));
          while (true)
          {
            return i;
            i = 0;
          }
        }
        bool1 = paramType1 instanceof TypeVariable;
        j = 0;
      }
      while (!bool1);
      bool2 = paramType2 instanceof TypeVariable;
      j = 0;
    }
    while (!bool2);
    TypeVariable localTypeVariable1 = (TypeVariable)paramType1;
    TypeVariable localTypeVariable2 = (TypeVariable)paramType2;
    if ((localTypeVariable1.getGenericDeclaration() == localTypeVariable2.getGenericDeclaration()) && (localTypeVariable1.getName().equals(localTypeVariable2.getName())));
    while (true)
    {
      return i;
      i = 0;
    }
  }

  static Type getCallResponseType(Type paramType)
  {
    if (!(paramType instanceof ParameterizedType))
      throw new IllegalArgumentException("Call return type must be parameterized as Call<Foo> or Call<? extends Foo>");
    return getParameterUpperBound(0, (ParameterizedType)paramType);
  }

  static Type getGenericSupertype(Type paramType, Class<?> paramClass1, Class<?> paramClass2)
  {
    if (paramClass2 == paramClass1)
      return paramType;
    if (paramClass2.isInterface())
    {
      Class[] arrayOfClass = paramClass1.getInterfaces();
      int i = 0;
      int j = arrayOfClass.length;
      while (i < j)
      {
        if (arrayOfClass[i] == paramClass2)
          return paramClass1.getGenericInterfaces()[i];
        if (paramClass2.isAssignableFrom(arrayOfClass[i]))
          return getGenericSupertype(paramClass1.getGenericInterfaces()[i], arrayOfClass[i], paramClass2);
        i++;
      }
    }
    if (!paramClass1.isInterface())
      while (paramClass1 != Object.class)
      {
        Class localClass = paramClass1.getSuperclass();
        if (localClass == paramClass2)
          return paramClass1.getGenericSuperclass();
        if (paramClass2.isAssignableFrom(localClass))
          return getGenericSupertype(paramClass1.getGenericSuperclass(), localClass, paramClass2);
        paramClass1 = localClass;
      }
    return paramClass2;
  }

  static Type getParameterUpperBound(int paramInt, ParameterizedType paramParameterizedType)
  {
    Type[] arrayOfType = paramParameterizedType.getActualTypeArguments();
    if ((paramInt < 0) || (paramInt >= arrayOfType.length))
      throw new IllegalArgumentException("Index " + paramInt + " not in range [0," + arrayOfType.length + ") for " + paramParameterizedType);
    Type localType = arrayOfType[paramInt];
    if ((localType instanceof WildcardType))
      localType = ((WildcardType)localType).getUpperBounds()[0];
    return localType;
  }

  static Class<?> getRawType(Type paramType)
  {
    if (paramType == null)
      throw new NullPointerException("type == null");
    if ((paramType instanceof Class))
      return (Class)paramType;
    if ((paramType instanceof ParameterizedType))
    {
      Type localType = ((ParameterizedType)paramType).getRawType();
      if (!(localType instanceof Class))
        throw new IllegalArgumentException();
      return (Class)localType;
    }
    if ((paramType instanceof GenericArrayType))
      return Array.newInstance(getRawType(((GenericArrayType)paramType).getGenericComponentType()), 0).getClass();
    if ((paramType instanceof TypeVariable))
      return Object.class;
    if ((paramType instanceof WildcardType))
      return getRawType(((WildcardType)paramType).getUpperBounds()[0]);
    throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + paramType + "> is of type " + paramType.getClass().getName());
  }

  static Type getSupertype(Type paramType, Class<?> paramClass1, Class<?> paramClass2)
  {
    if (!paramClass2.isAssignableFrom(paramClass1))
      throw new IllegalArgumentException();
    return resolve(paramType, paramClass1, getGenericSupertype(paramType, paramClass1, paramClass2));
  }

  static boolean hasUnresolvableType(Type paramType)
  {
    if ((paramType instanceof Class));
    while (true)
    {
      return false;
      if (!(paramType instanceof ParameterizedType))
        break;
      Type[] arrayOfType = ((ParameterizedType)paramType).getActualTypeArguments();
      int i = arrayOfType.length;
      for (int j = 0; j < i; j++)
        if (hasUnresolvableType(arrayOfType[j]))
          return true;
    }
    if ((paramType instanceof GenericArrayType))
      return hasUnresolvableType(((GenericArrayType)paramType).getGenericComponentType());
    if ((paramType instanceof TypeVariable))
      return true;
    if ((paramType instanceof WildcardType))
      return true;
    if (paramType == null);
    for (String str = "null"; ; str = paramType.getClass().getName())
      throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + paramType + "> is of type " + str);
  }

  static int hashCodeOrZero(Object paramObject)
  {
    if (paramObject != null)
      return paramObject.hashCode();
    return 0;
  }

  private static int indexOf(Object[] paramArrayOfObject, Object paramObject)
  {
    for (int i = 0; i < paramArrayOfObject.length; i++)
      if (paramObject.equals(paramArrayOfObject[i]))
        return i;
    throw new NoSuchElementException();
  }

  static boolean isAnnotationPresent(Annotation[] paramArrayOfAnnotation, Class<? extends Annotation> paramClass)
  {
    int i = paramArrayOfAnnotation.length;
    for (int j = 0; ; j++)
    {
      int k = 0;
      if (j < i)
      {
        if (!paramClass.isInstance(paramArrayOfAnnotation[j]))
          continue;
        k = 1;
      }
      return k;
    }
  }

  static Type resolve(Type paramType1, Class<?> paramClass, Type paramType2)
  {
    Object localObject1;
    while ((paramType2 instanceof TypeVariable))
    {
      TypeVariable localTypeVariable = (TypeVariable)paramType2;
      paramType2 = resolveTypeVariable(paramType1, paramClass, localTypeVariable);
      if (paramType2 != localTypeVariable)
        continue;
      localObject1 = paramType2;
    }
    while (true)
    {
      return localObject1;
      if (((paramType2 instanceof Class)) && (((Class)paramType2).isArray()))
      {
        Object localObject2 = (Class)paramType2;
        Class localClass = ((Class)localObject2).getComponentType();
        Type localType8 = resolve(paramType1, paramClass, localClass);
        if (localClass == localType8);
        while (true)
        {
          return localObject2;
          localObject2 = new GenericArrayTypeImpl(localType8);
        }
      }
      if ((paramType2 instanceof GenericArrayType))
      {
        localObject1 = (GenericArrayType)paramType2;
        Type localType6 = ((GenericArrayType)localObject1).getGenericComponentType();
        Type localType7 = resolve(paramType1, paramClass, localType6);
        if (localType6 != localType7)
          return new GenericArrayTypeImpl(localType7);
      }
      if ((paramType2 instanceof ParameterizedType))
      {
        localObject1 = (ParameterizedType)paramType2;
        Type localType3 = ((ParameterizedType)localObject1).getOwnerType();
        Type localType4 = resolve(paramType1, paramClass, localType3);
        if (localType4 != localType3);
        Type[] arrayOfType3;
        for (int i = 1; ; i = 0)
        {
          arrayOfType3 = ((ParameterizedType)localObject1).getActualTypeArguments();
          int j = 0;
          int k = arrayOfType3.length;
          while (j < k)
          {
            Type localType5 = resolve(paramType1, paramClass, arrayOfType3[j]);
            if (localType5 != arrayOfType3[j])
            {
              if (i == 0)
              {
                arrayOfType3 = (Type[])arrayOfType3.clone();
                i = 1;
              }
              arrayOfType3[j] = localType5;
            }
            j++;
          }
        }
        if (i == 0)
          continue;
        ParameterizedTypeImpl localParameterizedTypeImpl = new ParameterizedTypeImpl(localType4, ((ParameterizedType)localObject1).getRawType(), arrayOfType3);
        return localParameterizedTypeImpl;
      }
      if (!(paramType2 instanceof WildcardType))
        break;
      localObject1 = (WildcardType)paramType2;
      Type[] arrayOfType1 = ((WildcardType)localObject1).getLowerBounds();
      Type[] arrayOfType2 = ((WildcardType)localObject1).getUpperBounds();
      if (arrayOfType1.length == 1)
      {
        Type localType2 = resolve(paramType1, paramClass, arrayOfType1[0]);
        if (localType2 != arrayOfType1[0])
          return new WildcardTypeImpl(new Type[] { Object.class }, new Type[] { localType2 });
      }
      if (arrayOfType2.length != 1)
        continue;
      Type localType1 = resolve(paramType1, paramClass, arrayOfType2[0]);
      if (localType1 != arrayOfType2[0])
        return new WildcardTypeImpl(new Type[] { localType1 }, EMPTY_TYPE_ARRAY);
    }
    return (Type)(Type)paramType2;
  }

  private static Type resolveTypeVariable(Type paramType, Class<?> paramClass, TypeVariable<?> paramTypeVariable)
  {
    Class localClass = declaringClassOf(paramTypeVariable);
    if (localClass == null);
    Type localType;
    do
    {
      return paramTypeVariable;
      localType = getGenericSupertype(paramType, paramClass, localClass);
    }
    while (!(localType instanceof ParameterizedType));
    int i = indexOf(localClass.getTypeParameters(), paramTypeVariable);
    return ((ParameterizedType)localType).getActualTypeArguments()[i];
  }

  static String typeToString(Type paramType)
  {
    if ((paramType instanceof Class))
      return ((Class)paramType).getName();
    return paramType.toString();
  }

  static <T> void validateServiceInterface(Class<T> paramClass)
  {
    if (!paramClass.isInterface())
      throw new IllegalArgumentException("API declarations must be interfaces.");
    if (paramClass.getInterfaces().length > 0)
      throw new IllegalArgumentException("API interfaces must not extend other interfaces.");
  }

  private static final class GenericArrayTypeImpl
    implements GenericArrayType
  {
    private final Type componentType;

    GenericArrayTypeImpl(Type paramType)
    {
      this.componentType = paramType;
    }

    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof GenericArrayType)) && (Utils.equals(this, (GenericArrayType)paramObject));
    }

    public Type getGenericComponentType()
    {
      return this.componentType;
    }

    public int hashCode()
    {
      return this.componentType.hashCode();
    }

    public String toString()
    {
      return Utils.typeToString(this.componentType) + "[]";
    }
  }

  private static final class ParameterizedTypeImpl
    implements ParameterizedType
  {
    private final Type ownerType;
    private final Type rawType;
    private final Type[] typeArguments;

    ParameterizedTypeImpl(Type paramType1, Type paramType2, Type[] paramArrayOfType)
    {
      if ((paramType2 instanceof Class))
      {
        int m;
        if (paramType1 == null)
        {
          m = i;
          if (((Class)paramType2).getEnclosingClass() != null)
            break label56;
        }
        while (true)
        {
          if (m == i)
            break label62;
          throw new IllegalArgumentException();
          m = 0;
          break;
          label56: i = 0;
        }
      }
      label62: this.ownerType = paramType1;
      this.rawType = paramType2;
      this.typeArguments = ((Type[])paramArrayOfType.clone());
      Type[] arrayOfType = this.typeArguments;
      int k = arrayOfType.length;
      while (j < k)
      {
        Type localType = arrayOfType[j];
        if (localType == null)
          throw new NullPointerException();
        Utils.checkNotPrimitive(localType);
        j++;
      }
    }

    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof ParameterizedType)) && (Utils.equals(this, (ParameterizedType)paramObject));
    }

    public Type[] getActualTypeArguments()
    {
      return (Type[])this.typeArguments.clone();
    }

    public Type getOwnerType()
    {
      return this.ownerType;
    }

    public Type getRawType()
    {
      return this.rawType;
    }

    public int hashCode()
    {
      return Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode() ^ Utils.hashCodeOrZero(this.ownerType);
    }

    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(30 * (1 + this.typeArguments.length));
      localStringBuilder.append(Utils.typeToString(this.rawType));
      if (this.typeArguments.length == 0)
        return localStringBuilder.toString();
      localStringBuilder.append("<").append(Utils.typeToString(this.typeArguments[0]));
      for (int i = 1; i < this.typeArguments.length; i++)
        localStringBuilder.append(", ").append(Utils.typeToString(this.typeArguments[i]));
      return ">";
    }
  }

  private static final class WildcardTypeImpl
    implements WildcardType
  {
    private final Type lowerBound;
    private final Type upperBound;

    WildcardTypeImpl(Type[] paramArrayOfType1, Type[] paramArrayOfType2)
    {
      if (paramArrayOfType2.length > 1)
        throw new IllegalArgumentException();
      if (paramArrayOfType1.length != 1)
        throw new IllegalArgumentException();
      if (paramArrayOfType2.length == 1)
      {
        if (paramArrayOfType2[0] == null)
          throw new NullPointerException();
        Utils.checkNotPrimitive(paramArrayOfType2[0]);
        if (paramArrayOfType1[0] != Object.class)
          throw new IllegalArgumentException();
        this.lowerBound = paramArrayOfType2[0];
        this.upperBound = Object.class;
        return;
      }
      if (paramArrayOfType1[0] == null)
        throw new NullPointerException();
      Utils.checkNotPrimitive(paramArrayOfType1[0]);
      this.lowerBound = null;
      this.upperBound = paramArrayOfType1[0];
    }

    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof WildcardType)) && (Utils.equals(this, (WildcardType)paramObject));
    }

    public Type[] getLowerBounds()
    {
      if (this.lowerBound != null)
      {
        Type[] arrayOfType = new Type[1];
        arrayOfType[0] = this.lowerBound;
        return arrayOfType;
      }
      return Utils.EMPTY_TYPE_ARRAY;
    }

    public Type[] getUpperBounds()
    {
      Type[] arrayOfType = new Type[1];
      arrayOfType[0] = this.upperBound;
      return arrayOfType;
    }

    public int hashCode()
    {
      if (this.lowerBound != null);
      for (int i = 31 + this.lowerBound.hashCode(); ; i = 1)
        return i ^ 31 + this.upperBound.hashCode();
    }

    public String toString()
    {
      if (this.lowerBound != null)
        return "? super " + Utils.typeToString(this.lowerBound);
      if (this.upperBound == Object.class)
        return "?";
      return "? extends " + Utils.typeToString(this.upperBound);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     retrofit2.Utils
 * JD-Core Version:    0.6.0
 */