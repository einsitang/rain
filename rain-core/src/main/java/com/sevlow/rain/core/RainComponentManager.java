package com.sevlow.rain.core;

import com.sevlow.rain.core.handler.TypeWithHandler;
import com.sevlow.rain.core.manager.DefaultRainComponentManager;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rain Component Manager
 *
 * @author einsitang
 */
public interface RainComponentManager {

  /**
   * launch manager to scan and build component
   */
  void launch();

  Object get(Class<?> type);

  Object get(Class<?> type, String name);

  List<?> list(Class<?> type);

  static Builder scanPackage(String packageName) {
    return Builder.emptyBuilder().scanPackage(packageName);
  }

  static Builder runAt(Class<?> getPrimarySource) {
    return Builder.emptyBuilder().runAt(getPrimarySource);
  }

  static Builder notMatchStrategy(NotMatchStrategy notMatchStrategy) {
    return Builder.emptyBuilder().notMatchStrategy(notMatchStrategy);
  }

  static Builder withAnnotation(Class<? extends Annotation> type, TypeWithHandler handler) {
    return Builder.emptyBuilder().withAnnotation(type, handler);
  }

  final class Builder {

    @SuppressWarnings("MagicNumber")
    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;

    private String packageName;

    private Class<?> primarySource;

    private NotMatchStrategy notMatchStrategy = NotMatchStrategy.NULLABLE;

    private final Map<Class<? extends Annotation>, TypeWithHandler> typeHandlerMap = new HashMap<>(
        DEFAULT_INITIAL_CAPACITY);

    private Builder() {
    }

    private static Builder emptyBuilder() {
      return new Builder();
    }

    public Builder scanPackage(String packageName) {
      this.packageName = packageName;
      return this;
    }

    public Builder runAt(Class<?> primarySource) {
      this.primarySource = primarySource;
      return this;
    }

    public Builder notMatchStrategy(NotMatchStrategy notMatchStrategy) {
      this.notMatchStrategy = notMatchStrategy;
      return this;
    }

    public Builder withAnnotation(Class<? extends Annotation> type, TypeWithHandler handler) {
      typeHandlerMap.put(type, handler);
      return this;
    }

    public RainComponentManager build() {
      return new DefaultRainComponentManager(this.packageName, this.primarySource,
          this.notMatchStrategy, this.typeHandlerMap);
    }

  }
}
