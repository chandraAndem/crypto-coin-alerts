package com.alexitc.coinalerts.data.anorm.dao

import java.sql.Connection
import javax.inject.Inject

import anorm._
import com.alexitc.coinalerts.core.{Count, PaginatedQuery}
import com.alexitc.coinalerts.data.anorm.interpreters.FixedPriceAlertFilterSQLInterpreter
import com.alexitc.coinalerts.data.anorm.parsers.FixedPriceAlertParsers
import com.alexitc.coinalerts.models._

class FixedPriceAlertPostgresDAO @Inject() (sqlFilterInterpreter: FixedPriceAlertFilterSQLInterpreter) {

  import FixedPriceAlertParsers._

  def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId)(implicit conn: Connection): FixedPriceAlert = {
    SQL(
      """
        |INSERT INTO fixed_price_alerts
        |  (user_id, currency_id, is_greater_than, price, base_price)
        |VALUES
        |  ({user_id}, {currency_id}, {is_greater_than}, {price}, {base_price})
        |RETURNING
        |  fixed_price_alert_id, user_id, currency_id, is_greater_than, price, base_price
      """.stripMargin
    ).on(
      "user_id" -> userId.string,
      "currency_id" -> createAlertModel.exchangeCurrencyId.int,
      "is_greater_than" -> createAlertModel.isGreaterThan,
      "price" -> createAlertModel.price,
      "base_price" -> createAlertModel.basePrice
    ).as(parseFixedPriceAlert.single)
  }

  def markAsTriggered(alertId: FixedPriceAlertId)(implicit conn: Connection): Int = {
    SQL(
      """
        |UPDATE fixed_price_alerts
        |SET triggered_on = NOW()
        |WHERE triggered_on IS NULL AND
        |      fixed_price_alert_id = {fixed_price_alert_id}
      """.stripMargin
    ).on(
      "fixed_price_alert_id" -> alertId.long
    ).executeUpdate()
  }

  def findPendingAlertsForPrice(
      currencyId: ExchangeCurrencyId,
      currentPrice: BigDecimal)(
      implicit conn: Connection): List[FixedPriceAlertWithCurrency] = {

    SQL(
      """
        |SELECT fixed_price_alert_id, user_id, currency_id, is_greater_than, price, base_price,
        |       exchange, market, currency
        |FROM fixed_price_alerts INNER JOIN currencies USING (currency_id)
        |WHERE triggered_on IS NULL AND
        |      currency_id = {currency_id} AND
        |      (
        |        (is_greater_than = TRUE AND {current_price} >= price) OR
        |        (is_greater_than = FALSE AND {current_price} <= price)
        |      )
      """.stripMargin
    ).on(
      "currency_id" -> currencyId.int,
      "current_price" -> currentPrice
    ).as(parseFixedPriceAlertWithCurrency.*)
  }

  def getAlerts(
      conditions: FixedPriceAlertFilter.Conditions,
      query: PaginatedQuery)(
      implicit conn: Connection): List[FixedPriceAlertWithCurrency] = {

    val whereClause = sqlFilterInterpreter.toWhere(conditions)
    val namedParams = NamedParameter.string("offset" -> query.offset.int) ::
        NamedParameter.string("limit" -> query.limit.int) ::
        whereClause.params.map(param => NamedParameter.string(param))

    SQL(
      s"""
         |SELECT fixed_price_alert_id, user_id, currency_id, is_greater_than, price, base_price,
         |       exchange, market, currency
         |FROM fixed_price_alerts INNER JOIN currencies USING (currency_id)
         |${whereClause.sql}
         |ORDER BY fixed_price_alert_id
         |OFFSET {offset}
         |LIMIT {limit}
       """.stripMargin
    ).on(
      namedParams: _*
    ).as(parseFixedPriceAlertWithCurrency.*)
  }

  def countBy(conditions: FixedPriceAlertFilter.Conditions)(implicit conn: Connection): Count = {

    val whereClause = sqlFilterInterpreter.toWhere(conditions)
    val namedParams = whereClause.params.map { param =>
      NamedParameter.string(param)
    }

    val result = SQL(
      s"""
        |SELECT COUNT(*)
        |FROM fixed_price_alerts
        |${whereClause.sql}
      """.stripMargin
    ).on(
      namedParams: _*
    ).as(SqlParser.scalar[Int].single)

    Count(result)
  }
}
