# Rain

## about

`Rain` is a simple customize Java component lib without spring context

**simple , easy and tiny**

## how-to-use

### depend
- jdk `17` - code pass at jdk17

**maven**
```
# this repository not deploy yet
<dependency>
    <groupId>com.sevlow.rain</groupId>
    <artifactId>rain-core</artifactId>
    <version>${rain-core.version}</version>
</dependency>
```

### usage

```java
  // build RainComponentManager with builder  
  RainComponentManager rainComponentManager = RainComponentManager
        // base package scan
        .scanPackage("com.sevlow.rain")
        // primary source class 
        .runAt(MainTest.class)
        // not match strategy : nullable or thorw exception
        .notMatchStrategy(NotMatchStrategy.NULLABLE)
        // scan with annotation class , you can make component on here
        .withAnnotation(TestComponent.class, (Class<?> type) -> {
            TestComponent tc = type.getAnnotation(TestComponent.class);
            String hello = tc.hello();
            System.out.println("hello : " + hello);
            TestBean testBean = new TestBean();
            testBean.setHello(hello);
            return RainResult.match(testBean, hello);
        })
        .withAnnotation(RainComponent.class, (Class<?> type) -> {
            RainComponent rc = type.getAnnotation(RainComponent.class);
            String name = rc.name();
            TestBean testBean = new TestBean();
            testBean.setHello(name);
            return RainResult.match(testBean, name);
        })
        .build();

    // get First
    TestBean tb1 = (TestBean)rainComponentManager.get(TestBean.class);

    // get TestBean.class with name : testBean1
    TestBean tb3 = (TestBean)rainComponentManager.get(TestBean.class, "testBean1");
    TestBean tb4 = (TestBean)rainComponentManager.get(TestComponent.class, "testBean1");
    System.out.println(tb3 == tb4); // true
    System.out.println(tb1 == tb3); // true , because just one TestBean type list
    
    TestBean tb5 = (TestBean)rainComponentManager.get(TestBean.class,"notMatchName");
    System.out.println(tb5); // null , because "notMathName" with TestBean.class not found
    
    // list with class , if not found , will return empty list
    ist<?> testBeanList = rainComponentManager.list(TestBean.class);

```

> `rainComponentManager.get(...)` method will return nullable value that because build manager with `notMatchStrategy(NotMatchStrategy.NULLABLE)`

the sample of `TestComponent` and `TestBean` class like this 


TestComponent.java:
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface TestComponent {

  String name() default "";
}

```
TestBean.java
```java
@TestComponent(name = "testBean1")
public class TestBean implements Serializable {


  @Serial
  private static final long serialVersionUID = 5791151669064899755L;

  private String hello;
}

```

you can define customize `@Annotation` and setting `withAnnotation(Annotation.class,(Class<?> type)->{...})` handler to build your own component without spring framework
