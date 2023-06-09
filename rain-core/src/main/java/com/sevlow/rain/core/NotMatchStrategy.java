package com.sevlow.rain.core;

/**
 * 用于设置 RainComponentManager get/list 未能匹配上 component 时选择抛出异常还是直接返回null策略
 *
 * @author einsitang
 */
public enum NotMatchStrategy {

  /**
   * 没匹配上则直接抛出异常
   */
  THROWS_EXCEPTION(),
  /**
   * 没匹配上返回null
   */
  NULLABLE()

}
