package com.google.gson.internal;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

public final class $Gson$Types
{
  static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

  private $Gson$Types()
  {
    throw new UnsupportedOperationException();
  }

  public static GenericArrayType arrayOf(Type paramType)
  {
    return new GenericArrayTypeImpl(paramType);
  }

  public static Type canonicalize(Type paramType)
  {
    if ((paramType instanceof Class))
    {
      Class localClass = (Class)paramType;
      if (localClass.isArray());
      for (Object localObject = new GenericArrayTypeImpl(canonicalize(localClass.getComponentType())); ; localObject = localClass)
        return (Type)localObject;
    }
    if ((paramType instanceof ParameterizedType))
    {
      ParameterizedType localParameterizedType = (ParameterizedType)paramType;
      return new ParameterizedTypeImpl(localParameterizedType.getOwnerType(), localParameterizedType.getRawType(), localParameterizedType.getActualTypeArguments());
    }
    if ((paramType instanceof GenericArrayType))
      return new GenericArrayTypeImpl(((GenericArrayType)paramType).getGenericComponentType());
    if ((paramType instanceof WildcardType))
    {
      WildcardType localWildcardType = (WildcardType)paramType;
      return new WildcardTypeImpl(localWildcardType.getUpperBounds(), localWildcardType.getLowerBounds());
    }
    return (Type)paramType;
  }

  static void checkNotPrimitive(Type paramType)
  {
    if ((!(paramType instanceof Class)) || (!((Class)paramType).isPrimitive()));
    for (boolean bool = true; ; bool = false)
    {
      .Gson.Preconditions.checkArgument(bool);
      return;
    }
  }

  private static Class<?> declaringClassOf(TypeVariable<?> paramTypeVariable)
  {
    GenericDeclaration localGenericDeclaration = paramTypeVariable.getGenericDeclaration();
    if ((localGenericDeclaration instanceof Class))
      return (Class)localGenericDeclaration;
    return null;
  }

  static boolean equal(Object paramObject1, Object paramObject2)
  {
    return (paramObject1 == paramObject2) || ((paramObject1 != null) && (paramObject1.equals(paramObject2)));
  }

  public static boolean equals(Type paramType1, Type paramType2)
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

  public static Type getArrayComponentType(Type paramType)
  {
    if ((paramType instanceof GenericArrayType))
      return ((GenericArrayType)paramType).getGenericComponentType();
    return ((Class)paramType).getComponentType();
  }

  public static Type getCollectionElementType(Type paramType, Class<?> paramClass)
  {
    Type localType = getSupertype(paramType, paramClass, Collection.class);
    if ((localType instanceof WildcardType))
      localType = ((WildcardType)localType).getUpperBounds()[0];
    if ((localType instanceof ParameterizedType))
      return ((ParameterizedType)localType).getActualTypeArguments()[0];
    return Object.class;
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

  public static Type[] getMapKeyAndValueTypes(Type paramType, Class<?> paramClass)
  {
    if (paramType == Properties.class)
      return new Type[] { String.class, String.class };
    Type localType = getSupertype(paramType, paramClass, Map.class);
    if ((localType instanceof ParameterizedType))
      return ((ParameterizedType)localType).getActualTypeArguments();
    return new Type[] { Object.class, Object.class };
  }

  public static Class<?> getRawType(Type paramType)
  {
    if ((paramType instanceof Class))
      return (Class)paramType;
    if ((paramType instanceof ParameterizedType))
    {
      Type localType = ((ParameterizedType)paramType).getRawType();
      .Gson.Preconditions.checkArgument(localType instanceof Class);
      return (Class)localType;
    }
    if ((paramType instanceof GenericArrayType))
      return Array.newInstance(getRawType(((GenericArrayType)paramType).getGenericComponentType()), 0).getClass();
    if ((paramType instanceof TypeVariable))
      return Object.class;
    if ((paramType instanceof WildcardType))
      return getRawType(((WildcardType)paramType).getUpperBounds()[0]);
    if (paramType == null);
    for (String str = "null"; ; str = paramType.getClass().getName())
      throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + paramType + "> is of type " + str);
  }

  static Type getSupertype(Type paramType, Class<?> paramClass1, Class<?> paramClass2)
  {
    .Gson.Preconditions.checkArgument(paramClass2.isAssignableFrom(paramClass1));
    return resolve(paramType, paramClass1, getGenericSupertype(paramType, paramClass1, paramClass2));
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

  public static ParameterizedType newParameterizedTypeWithOwner(Type paramType1, Type paramType2, Type[] paramArrayOfType)
  {
    return new ParameterizedTypeImpl(paramType1, paramType2, paramArrayOfType);
  }

  public static Type resolve(Type paramType1, Class<?> paramClass, Type paramType2)
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
          localObject2 = arrayOf(localType8);
        }
      }
      if ((paramType2 instanceof GenericArrayType))
      {
        localObject1 = (GenericArrayType)paramType2;
        Type localType6 = ((GenericArrayType)localObject1).getGenericComponentType();
        Type localType7 = resolve(paramType1, paramClass, localType6);
        if (localType6 != localType7)
          return arrayOf(localType7);
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
        if (i != 0)
          return newParameterizedTypeWithOwner(localType4, ((ParameterizedType)localObject1).getRawType(), arrayOfType3);
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
          return supertypeOf(localType2);
      }
      if (arrayOfType2.length != 1)
        continue;
      Type localType1 = resolve(paramType1, paramClass, arrayOfType2[0]);
      if (localType1 != arrayOfType2[0])
        return subtypeOf(localType1);
    }
    return (Type)(Type)paramType2;
  }

  static Type resolveTypeVariable(Type paramType, Class<?> paramClass, TypeVariable<?> paramTypeVariable)
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

  public static WildcardType subtypeOf(Type paramType)
  {
    return new WildcardTypeImpl(new Type[] { paramType }, EMPTY_TYPE_ARRAY);
  }

  public static WildcardType supertypeOf(Type paramType)
  {
    return new WildcardTypeImpl(new Type[] { Object.class }, new Type[] { paramType });
  }

  public static String typeToString(Type paramType)
  {
    if ((paramType instanceof Class))
      return ((Class)paramType).getName();
    return paramType.toString();
  }

  private static final class GenericArrayTypeImpl
    implements GenericArrayType, Serializable
  {
    private static final long serialVersionUID;
    private final Type componentType;

    public GenericArrayTypeImpl(Type paramType)
    {
      this.componentType = .Gson.Types.canonicalize(paramType);
    }

    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof Serializable)) && (.Gson.Types.equals(this, (Serializable)paramObject));
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
      return .Gson.Types.typeToString(this.componentType) + "[]";
    }
  }

  private static final class ParameterizedTypeImpl
    implements ParameterizedType, Serializable
  {
    private static final long serialVersionUID;
    private final Type ownerType;
    private final Type rawType;
    private final Type[] typeArguments;

    public ParameterizedTypeImpl(Type paramType1, Type paramType2, Type[] paramArrayOfType)
    {
      int j;
      if ((paramType2 instanceof Class))
      {
        Class localClass = (Class)paramType2;
        if ((Modifier.isStatic(localClass.getModifiers())) || (localClass.getEnclosingClass() == null))
        {
          j = 1;
          boolean bool;
          if (paramType1 == null)
          {
            bool = false;
            if (j == 0);
          }
          else
          {
            bool = true;
          }
          .Gson.Preconditions.checkArgument(bool);
        }
      }
      else
      {
        if (paramType1 != null)
          break label154;
      }
      label154: for (Type localType = null; ; localType = .Gson.Types.canonicalize(paramType1))
      {
        this.ownerType = localType;
        this.rawType = .Gson.Types.canonicalize(paramType2);
        this.typeArguments = ((Type[])paramArrayOfType.clone());
        for (int i = 0; i < this.typeArguments.length; i++)
        {
          .Gson.Preconditions.checkNotNull(this.typeArguments[i]);
          .Gson.Types.checkNotPrimitive(this.typeArguments[i]);
          this.typeArguments[i] = .Gson.Types.canonicalize(this.typeArguments[i]);
        }
        j = 0;
        break;
      }
    }

    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof Serializable)) && (.Gson.Types.equals(this, (Serializable)paramObject));
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
      return Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode() ^ .Gson.Types.hashCodeOrZero(this.ownerType);
    }

    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(30 * (1 + this.typeArguments.length));
      localStringBuilder.append(.Gson.Types.typeToString(this.rawType));
      if (this.typeArguments.length == 0)
        return localStringBuilder.toString();
      localStringBuilder.append("<").append(.Gson.Types.typeToString(this.typeArguments[0]));
      for (int i = 1; i < this.typeArguments.length; i++)
        localStringBuilder.append(", ").append(.Gson.Types.typeToString(this.typeArguments[i]));
      return ">";
    }
  }

  private static final class WildcardTypeImpl
    implements WildcardType, Serializable
  {
    private static final long serialVersionUID;
    private final Type lowerBound;
    private final Type upperBound;

    public WildcardTypeImpl(Type[] paramArrayOfType1, Type[] paramArrayOfType2)
    {
      if (paramArrayOfType2.length <= i)
      {
        int k = i;
        .Gson.Preconditions.checkArgument(k);
        if (paramArrayOfType1.length != i)
          break label88;
        int n = i;
        label29: .Gson.Preconditions.checkArgument(n);
        if (paramArrayOfType2.length != i)
          break label99;
        .Gson.Preconditions.checkNotNull(paramArrayOfType2[0]);
        .Gson.Types.checkNotPrimitive(paramArrayOfType2[0]);
        if (paramArrayOfType1[0] != Object.class)
          break label94;
      }
      while (true)
      {
        .Gson.Preconditions.checkArgument(i);
        this.lowerBound = .Gson.Types.canonicalize(paramArrayOfType2[0]);
        this.upperBound = Object.class;
        return;
        int m = 0;
        break;
        label88: int i1 = 0;
        break label29;
        label94: int j = 0;
      }
      label99: .Gson.Preconditions.checkNotNull(paramArrayOfType1[0]);
      .Gson.Types.checkNotPrimitive(paramArrayOfType1[0]);
      this.lowerBound = null;
      this.upperBound = .Gson.Types.canonicalize(paramArrayOfType1[0]);
    }

    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof Serializable)) && (.Gson.Types.equals(this, (Serializable)paramObject));
    }

    public Type[] getLowerBounds()
    {
      if (this.lowerBound != null)
      {
        Type[] arrayOfType = new Type[1];
        arrayOfType[0] = this.lowerBound;
        return arrayOfType;
      }
      return .Gson.Types.EMPTY_TYPE_ARRAY;
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
        return "? super " + .Gson.Types.typeToString(this.lowerBound);
      if (this.upperBound == Object.class)
        return "?";
      return "? extends " + .Gson.Types.typeToString(this.upperBound);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.google.gson.internal..Gson.Types
 * JD-Core Version:    0.6.0
 */