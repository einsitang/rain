package com.sevlow.rain.core.manager;


import com.sevlow.rain.core.NotMatchStrategy;
import com.sevlow.rain.core.RainComponentManager;
import com.sevlow.rain.core.RainScanner;
import com.sevlow.rain.core.common.RainResult;
import com.sevlow.rain.core.handler.TypeWithHandler;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * default Rain Component Manager
 *
 * @author einsitang
 */
@Slf4j
public class DefaultRainComponentManager implements RainComponentManager {

  private final String packageName;

  private final Class<?> primarySource;

  private final NotMatchStrategy notMatchStrategy;
  private final Map<Class<? extends Annotation>, TypeWithHandler> annotationHandlerMap;

  Map<Class<?>, List<RainResult<?>>> classBeanMap;

  public DefaultRainComponentManager(String packageName, Class<?> primarySource,
      NotMatchStrategy notMatchStrategy,
      Map<Class<? extends Annotation>, TypeWithHandler> annotationHandlerMap) {

    if (StringUtils.isBlank(packageName) && primarySource == null) {
      // packageName and primarySource can't both empty
      throw new IllegalArgumentException("packageName and primarySource can't both empty");
    }

    this.packageName = packageName;
    this.primarySource = primarySource;
    this.notMatchStrategy = notMatchStrategy;
    this.annotationHandlerMap = annotationHandlerMap;
  }

  @Override
  public void launch() {
    if (annotationHandlerMap == null) {
      classBeanMap = new HashMap<>();
      return;
    }
    classBeanMap = new HashMap<>(annotationHandlerMap.size());

    String pn;
    if (StringUtils.isNoneBlank(packageName)) {
      // scan by packageName
      pn = packageName;
    } else {
      // scan by primarySource
      pn = primarySource.getPackage().getName();
    }

    new RainScanner(pn).scan((String className) -> {
      Class<?> type;
      try {
        type = Class.forName(className);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }

      // 如果 type annotations 没有和 typeHandlerMap 中的任何 type 相交，则可以向下尝试取其 annotations
      if (!annotationHandlerMap.containsKey(type)) {
        // 尝试获取
        tryTypeHandler(type);
      }

    });
  }

  private void tryTypeHandler(Class<?> type) {
    Annotation[] annotations = type.getAnnotations();
    for (Annotation annotation : annotations) {

      TypeWithHandler handler = annotationHandlerMap.get(annotation.annotationType());
      if (handler != null) {
        RainResult<?> rainResult = handler.handle(type);
        if (!rainResult.isMatch()) {
          return;
        }
        Object obj = rainResult.getObj();
        if (!type.equals(obj.getClass())) {
          throw new RuntimeException(
              "obj [ " + obj.getClass() + " ] is not instance of type " + type.getName());
        }
        classBeanMap.computeIfAbsent(type, k -> new LinkedList<>()).add(rainResult);
        classBeanMap.computeIfAbsent(annotation.annotationType(), k -> new LinkedList<>())
            .add(rainResult);
        log.debug("找到 class : [ " + type.getName() + " ] 并加入豪华套餐");
      }

    }

  }

  @Override
  public Object get(Class<?> type) {

    List<RainResult<?>> list = classBeanMap.get(type);
    if (list == null || list.isEmpty()) {
      if (NotMatchStrategy.THROWS_EXCEPTION.equals(this.notMatchStrategy)) {
        throw new RuntimeException("not found type : [ " + type.getName() + " ]");
      }
      return null;
    }

    return list.get(0).getObj();
  }

  @Override
  public Object get(Class<?> type, String name) {

    List<RainResult<?>> list = classBeanMap.get(type);
    if (list.isEmpty()) {
      if (NotMatchStrategy.THROWS_EXCEPTION.equals(this.notMatchStrategy)) {
        throw new RuntimeException(
            "not found type : [ " + type.getName() + " ] with name [ " + name + " ]");
      }
      return null;
    }
    for (RainResult<?> result : list) {
      if (name.equals(result.getName())) {
        return (result.getObj());
      }
    }

    if (NotMatchStrategy.THROWS_EXCEPTION.equals(this.notMatchStrategy)) {
      throw new RuntimeException(
          "not found type : [ " + type.getName() + " ] with name [ " + name + " ]");
    }
    return null;
  }

  @Override
  public List<?> list(Class<?> type) {
    List<RainResult<?>> list = classBeanMap.get(type);
    if (list == null || list.isEmpty()) {
      return Collections.EMPTY_LIST;
    }
    return list.stream().map(RainResult::getObj).collect(Collectors.toList());
  }

}
