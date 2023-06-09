package com.sevlow.rain.core.test;

import com.sevlow.rain.core.NotMatchStrategy;
import com.sevlow.rain.core.RainComponentManager;
import com.sevlow.rain.core.common.RainComponent;
import com.sevlow.rain.core.common.RainResult;
import com.sevlow.rain.core.test.bean.TestBean;
import com.sevlow.rain.core.test.bean.TestComponent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@SuppressWarnings("all")
@Slf4j
public class MainTest {

  public RainComponentManager buildRainComponentManager() {
    RainComponentManager rainComponentManager = RainComponentManager
//        .scanPackage("com.sevlow.rain")
        .runAt(MainTest.class)
        .notMatchStrategy(NotMatchStrategy.NULLABLE)
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
    return rainComponentManager;
  }

  @Test
  public void testRainComponentManager() {

    RainComponentManager rainComponentManager = buildRainComponentManager();
    rainComponentManager.launch();

    /**
     * RainComponentManager.get will try take one component ,
     * if null may throws RuntimeException or null , depend on notMatchStrategy
     */

    // get First
    TestBean tb1 = (TestBean) rainComponentManager.get(TestBean.class);
    log.info("get first tb1 : " + tb1);

    // only match by type and name
    TestBean tb2 = (TestBean) rainComponentManager.get(TestComponent.class, "world");
    TestBean tb3 = (TestBean) rainComponentManager.get(TestBean.class, "world");
    TestBean aaaTb = (TestBean) rainComponentManager.get(TestBean.class, "aaa");

    TestBean nullTab = (TestBean) rainComponentManager.get(TestBean.class, "notMatchName");

    log.info("aaaTb : " + aaaTb);
    log.info("nullTb : " + nullTab);
    log.info("get first tb2[testBean] : " + tb2);
    log.info("tb1 == tb2 : " + (tb1 == tb2));
    log.info("tb1 == tb3 : " + (tb1 == tb3));
    log.info("tb2 == tb3 : " + (tb2 == tb3));

    // list match by type , if not match will return empty list
    List<?> testBeanList = rainComponentManager.list(TestBean.class);
    log.info("test bean list : " + testBeanList);
  }


}
