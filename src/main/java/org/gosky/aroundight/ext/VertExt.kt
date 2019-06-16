package org.gosky.aroundight.ext

import com.google.gson.Gson
import io.vertx.ext.web.RoutingContext

/**
 * @Auther: guozhong
 * @Date: 2019-05-21 16:17
 * @Description:
 */


fun RoutingContext.success(any: Any?) {
    this.response()
            .putHeader("content-type", "application/json")
            .end(Gson().toJson(any))
}

fun RoutingContext.error(any: Any?) {
    this.request()
            .response()
            .putHeader("content-type", "application/json")
            .setStatusCode(500)
            .end(Gson().toJson(any))
}