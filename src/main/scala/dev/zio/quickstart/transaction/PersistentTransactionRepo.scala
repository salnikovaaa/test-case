package dev.zio.quickstart.transaction

import io.getquill.context.ZioJdbc.DataSourceLayer
import io.getquill.{Escape, H2ZioJdbcContext}
import io.getquill.jdbczio.Quill
import io.getquill.*
import zio.*

import java.util.UUID
import javax.sql.DataSource

case class TransactionTable(uuid: UUID, src: String, dst: String, amount: Int)

case class PersistentTransactionRepo(ds: DataSource) extends TransactionRepo:
  val ctx = new H2ZioJdbcContext(Escape)

  import ctx._

  override def save(transaction: Transaction): Task[String] = {
    for
      id <- Random.nextUUID
      _ <- ctx.run {
        quote {
          query[TransactionTable].insertValue {
            lift(TransactionTable(id, transaction.src, transaction.dst, transaction.amount))
          }
        }
      }
    yield id.toString
  }.provide(ZLayer.succeed(ds))

  override def lookup(id: String): Task[Option[Transaction]] =
    ctx.run {
      quote {
        query[TransactionTable]
          .filter(p => p.uuid == lift(UUID.fromString(id)))
          .map(u => Transaction(u.src, u.dst, u.amount))
      }
    }.provide(ZLayer.succeed(ds)).map(_.headOption)

  override def transactions: Task[List[Transaction]] =
    ctx.run {
      quote {
        query[TransactionTable].map(u => Transaction(u.src, u.dst, u.amount))
      }
    }.provide(ZLayer.succeed(ds))

object PersistentTransactionRepo:
  def layer: ZLayer[Any, Throwable, PersistentTransactionRepo] =
    Quill.DataSource.fromPrefix("TransactionalCheckApp") >>>
      ZLayer.fromFunction(PersistentTransactionRepo(_))
