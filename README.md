# Tson
> Tson is a lightweight annotation/json based "custom" de/serializer

With Tson you are be able to serialize your Java-Objects to a Json and vice versa

### Features

- Supports Arrays of Primtives,
   Collections (Lists, Sets, ...), 
   Maps.
- Has a Pretty-Json formatter
- Thread-Safe
- Does not need any other libraries
- Access for Private-Fields

### TODO
 - Automatic Empty-Constructor generation for newInstance
 - "skipIfEquals" for boolean values
 - Maybe ASM(Bytecode)-Based Reflection for faster De/Serialization (like ReflectASM)

##### 1. De/Serialization
```java
//Serialization of User-Object
Tson tson = TsonMapper.getSerializer(new User());
System.out.println(tson.toJsonPrettyString());

//Deserialization as Class
User user = TsonMapper.getDeserializer(tson).getAs(User.class);

//Deserialization as Instance
//(To override Field-Values of a existing Instance)
User userInstance = new User();
User user = TsonMapper.getDeserializer(tson).getAsInstance(userInstance);

//You can also fetch single datas from JSON
Tson deserializedTson = TsonMapper.getDeserializer(tson).getTson();
deserializedTson.getInt("userID");
deserializedTson.getArray("alias").asStringArray();
```

#### 2. Annotations
##### 2.1 Serialize fields by original or custom Field-Name
```java
public class User {

//Field userID will now be serialized with his fieldName and the value 0
@Serialize 
private int userID = 0;
//Output: {"userID":0}

//Field username will be serialized with name "User-Name" and the value sample
@Serialize("User-Name")
private String username = "sample";
//Output: {"User-Name":"sample"}
}
```

##### 2.2 Skip Serialization under certain condition
```java
public class User {

/*
The serialization of Field "userID" will now be skipped if the value is 0, it's a good option, if you want to safe unnecessary json size for unimportant values (its not only for numeric values you can also use it for strings)
*/
@Serialize(skipIfEquals = "0")
private int userID = 0;

//Field "username" will skipped if the value/object is null
@Serialize(skipNull = true)
private String username;

//Field "age" will skipped
@Serialize(skip = true)
private int age = 18;

//All Fields will be kept for deserialization
}
```

##### 2.3 Serialize ALL fields
```java
/*
If you annotate a class with @SerializeAll, you will serialize ALL fields in the class
(functions of the @Serialize annotation are retained)
if you want to ignore a field in a @SerializeAll annotated class you can use the @Ignore annotation on a Field
(transient fields and skip-options from @Serialize will be also considered)
 */
@SerializeAll
public class User {

private int userID = 0;

@Serialize("Name")
private String username = "User";

@Serialize(skipNull = true)
public String[] alias;

@Ignore
private int age = 18;
}
```

#### Benchmark (100.000 Operations)
##### Test-Class
```java
@SerializeAll
public class TestObject {
    public int testInt = Integer.MAX_VALUE;
    public long testLong = Long.MAX_VALUE;
    public boolean testBoolean = true;
    public char testChar = 't';
    public String testString = "String";
    public String[] stringArray = {"test", "string"};
}
```
##### Main-Class
```java
public class Main {

    private static final String json = "{\"testInt\":2147483647,\"testLong\":9223372036854775807,\"testBoolean\":true,\"testChar\":\"t\",\"testString\":\"String\",\"stringArray\":[\"test\",\"string\"]}";

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++)
            benchmark(false);
        for (int i = 0; i < 5; i++)
            benchmark(true);
    }

  private static void benchmark(boolean deserialize) {
        long before = System.currentTimeMillis();
        for (int i = 0; i < 100_000; i++) {
            if (deserialize)
                TsonMapper.getDeserializer(json).getAs(TestObject.class);
            else
                TsonMapper.getSerializer(new TestObject());
        }
        System.out.println("took: " + (System.currentTimeMillis() - before) + "ms.");
    }
}
```

```
Output:
//Serialization
took: 390ms. (Warm up)
took: 240ms.
took: 231ms.
took: 212ms.
took: 213ms.

//Deserialization
took: 331ms.
took: 198ms.
took: 189ms.
took: 230ms.
took: 114ms.
```

#### Notice

If your class has a Constructor with parameter, ensure that you add a Non-Argument (Zero Arg) Constructor to your class, if your class has no constructor you can skip this process.

##### Example:
```java
@SerializeAll
public class SampleClass {
  
  public boolean sampleBoolean;
  
  public  SampleClass(boolean sampleBoolean) {
         this.sampleBoolean = sampleBoolean;
  }
  
  public  SampleClass() {} //<== Needs to be added
}
```

```java
@SerializeAll
public class SampleClass {
  
  public boolean sampleBoolean;
  
  //Does not have to be added
}
```
