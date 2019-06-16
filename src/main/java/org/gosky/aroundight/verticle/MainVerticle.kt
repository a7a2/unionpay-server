package org.gosky.aroundight.verticle

import io.vertx.ext.web.RoutingContext
import io.vertx.reactivex.ext.mongo.MongoClient
import mu.KotlinLogging
import org.gosky.aroundight.ext.error
import org.gosky.aroundight.ext.success
import org.gosky.aroundight.service.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


/**
 * @Auther: guozhong
 * @Date: 2019-05-21 11:20
 * @Description:
 */
private val logger = KotlinLogging.logger {}

@Component
class MainVerticle : RestVerticle() {

    @Autowired
    private lateinit var mongo: MongoClient

    @Autowired
    private lateinit var orderService: OrderService

    override fun initRouter() {
        router.get("/health").handler { it.success("ok!") }

        router.post("/order").handler { createOrder(it) }

        router.get("/order-list").handler { getOrderList(it) }

        router.put("/order/paid").handler { }

        router.errorHandler(500) { routerContext ->
            logger.error { routerContext.failure().message }
            routerContext.error("error" to routerContext.failure().message)
        }
    }

    /**
     * 生成订单
     */
    private fun createOrder(context: RoutingContext) {

        val money = context.bodyAsJson.getString("money")

        orderService.createOrder(money.toBigDecimal())
                .subscribe { t: String? ->
                    context.success(t)
                }

    }

    /**
     * 获取订单列表
     */
    private fun getOrderList(context: RoutingContext) {
        orderService.getOrderList()
                .subscribe { t ->
                    context.success(t)
                }
    }

    /**
     * 修改支付状态 => 已支付
     */
    private fun orderStatusPaid(context: RoutingContext) {

        val randomMoney = context.bodyAsJson.getDouble("randomMoney")

        orderService.notifyPaid(randomMoney)
                .subscribe { t: String? ->
                    context.success(t)
                }

    }


}
