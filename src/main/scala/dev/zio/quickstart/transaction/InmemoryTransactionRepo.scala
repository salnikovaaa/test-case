package dev.zio.quickstart.transaction

import zio.{Random, Ref, UIO, ZLayer}

import scala.collection.mutable

case class InmemoryTransactionRepo(map: Ref[mutable.Map[String, Transaction]]) extends TransactionRepo:
  def save(transaction: Transaction): UIO[String] =
    for
      id <- Random.nextUUID.map(_.toString)
      _ <- map.updateAndGet(_ addOne(id, transaction))
    yield id

  def lookup(id: String): UIO[Option[Transaction]] =
    map.get.map(_.get(id))

  def transactions: UIO[List[Transaction]] =
    map.get.map(_.values.toList)

object InmemoryTransactionRepo {
  def layer: ZLayer[Any, Nothing, InmemoryTransactionRepo] =
    ZLayer.fromZIO(
      Ref.make(mutable.Map.empty[String, Transaction]).map(new InmemoryTransactionRepo(_))
    )
}
