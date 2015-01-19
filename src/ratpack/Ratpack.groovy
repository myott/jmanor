import com.myott.ErrorHandler
import com.myott.TagDbCommandService
import com.myott.TagModule
import com.zaxxer.hikari.HikariConfig
import ratpack.error.ServerErrorHandler
import ratpack.groovy.markuptemplates.MarkupTemplatingModule
import ratpack.groovy.sql.SqlModule
import ratpack.hikari.HikariModule
import ratpack.hystrix.HystrixMetricsEventStreamHandler
import ratpack.hystrix.HystrixModule
import ratpack.jackson.JacksonModule
import ratpack.rx.RxRatpack

import static ratpack.groovy.Groovy.ratpack
import static ratpack.groovy.Groovy.groovyMarkupTemplate

ratpack {

  bindings {

    add new JacksonModule()
    add new HystrixModule().sse()
    add new SqlModule()
    add new TagModule()
    add new MarkupTemplatingModule()

    add(HikariModule) { HikariConfig c ->
      c.addDataSourceProperty("URL", "jdbc:h2:mem:dev;INIT=CREATE SCHEMA IF NOT EXISTS DEV")
      c.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource")
    }

    init { TagDbCommandService tagDbCommandService ->
      System.out.println("Initializing JManor . . .")
      RxRatpack.initialize()
      tagDbCommandService.init()
    }

    bind ServerErrorHandler, ErrorHandler
  }

  handlers {

    prefix("api") {

      get("help") {
        render groovyMarkupTemplate("help-doc.gtpl", title: "JManor API Documentation")
      }

      get("here") {
        print "foo"
      }
    }

    prefix("admin") {
      get("hystrix.stream", new HystrixMetricsEventStreamHandler());
    }

    assets "public"
  }
}