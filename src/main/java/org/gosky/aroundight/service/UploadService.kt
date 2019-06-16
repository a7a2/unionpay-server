package org.gosky.aroundight.service

import io.reactivex.Single
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.FindOptions
import io.vertx.reactivex.ext.mongo.MongoClient
import mu.KotlinLogging
import org.gosky.aroundight.flow.OrderStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

/**
 * @Auther: guozhong
 * @Date: 2019-05-27 23:28
 * @Description:
 */

private val logger = KotlinLogging.logger {}

@Service
class OrderService {

    @Autowired
    private lateinit var mongo: MongoClient

    fun createOrder(money: BigDecimal): Single<String> {

        //生成订单号
        val orderSn = UUID.randomUUID().toString()

        //查询random money
        val smallestRandomMoney = mongo.rxFindWithOptions("order",
                JsonObject()
                        .put("randomMoney", JsonObject()
                                .put("\$gt", money.subtract(1.toBigDecimal()).toDouble())
                                .put("\$lt", money.toDouble())),
                FindOptions().setSort(JsonObject().put("randomMoney", 1)).setFields(JsonObject().put("randomMoney", 1)).setLimit(1))
        //生成随机立减金额
        val randomMoney = smallestRandomMoney
                .map {
                    logger.info { "smallestRandomMoney: $it" }
                    if (it.isEmpty()) {
                        return@map money
                    } else {
                        return@map it.first().getDouble("randomMoney").toBigDecimal()
                    }
                }
                .map { it.subtract(0.01.toBigDecimal()) }

        //生成订单
        val single = randomMoney
                .flatMap {
                    val currentTimeMillis = System.currentTimeMillis()
                    val document = JsonObject().put("orderSn", orderSn)
                            .put("money", money.toDouble())
                            .put("randomMoney", it.toDouble())
                            .put("status", OrderStatus.CREATED)
                            .put("createTime", currentTimeMillis)
                            .put("updateTime", currentTimeMillis)
                    return@flatMap mongo.rxSave("order", document).toSingle()
                }

        return single


    }
}
