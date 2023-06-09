package com.sevlow.rain.core.handler;

import com.sevlow.rain.core.common.RainResult;

@FunctionalInterface
public interface TypeWithHandler {

  RainResult<?> handle(Class<?> type);

}
