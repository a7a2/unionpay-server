package org.gosky.aroundight.flow

/**
 * @Auther: guozhong
 * @Date: 2019-06-16 00:06
 * @Description:
 */
enum class OrderStatus(status: Int) {
    CREATED(0),
    PAID(1),
    EXPIRED(2)
}
