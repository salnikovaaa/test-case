package dev.zio.quickstart.transaction

import zio.*

trait TransactionRepo:

  def save(transaction: Transaction): Task[String]

  def lookup(id: String): Task[Option[Transaction]]

  def transactions: Task[List[Transaction]]


object TransactionRepo:
  def save(transaction: Transaction): ZIO[TransactionRepo, Throwable, String] =
    ZIO.serviceWithZIO[TransactionRepo](_.save(transaction))

  def lookup(id: String): ZIO[TransactionRepo, Throwable, Option[Transaction]] =
    ZIO.serviceWithZIO[TransactionRepo](_.lookup(id))

  def transactions: ZIO[TransactionRepo, Throwable, List[Transaction]] =
    ZIO.serviceWithZIO[TransactionRepo](_.transactions)

