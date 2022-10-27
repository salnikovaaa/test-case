package dev.zio.quickstart.transaction

import zio.json.*


case class Transaction(src: String, dst: String, amount: Int)


object Transaction:
  given JsonEncoder[Transaction] =
    DeriveJsonEncoder.gen[Transaction]
  given JsonDecoder[Transaction] =
    DeriveJsonDecoder.gen[Transaction]
