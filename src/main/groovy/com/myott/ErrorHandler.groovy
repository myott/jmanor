package com.myott

import groovy.util.logging.Slf4j
import org.codehaus.groovy.runtime.StackTraceUtils
import ratpack.error.ServerErrorHandler
import ratpack.handling.Context

import static ratpack.groovy.Groovy.groovyMarkupTemplate

/**
 * Created by Matt on 1/18/15.
 */
@Slf4j
class ErrorHandler implements ServerErrorHandler {

  @Override
  void error(Context context, Throwable throwable) throws Exception {
    log.warn "Something went wrong", throwable
    context.with {
      render groovyMarkupTemplate("error.gtpl",
          title: 'Exception',
          exception: throwable,
          sanitizedException: StackTraceUtils.deepSanitize(throwable))
    }
  }
}
