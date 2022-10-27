package dev.zio.quickstart.transaction

import zhttp.http.*
import zio.*
import zio.stream.ZStream
import zio.json.*
import dev.zio.quickstart.other.AdditionalFunc.findIntoFile

object TransactionApp:

  def apply(): Http[TransactionRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      // POST /transactions -d '{"src": "1", "dst": "1", "amount" : 10}'
      case req@(Method.POST -> !! / "transactions") =>
        for
          u <- req.bodyAsString.map(_.fromJson[Transaction])
          r <- u match
            case Left(e) =>
              ZIO.debug(s"Failed to parse the input: $e").as(
                Response.text(e).setStatus(Status.BadRequest)
              )
            case Right(u) =>
              TransactionRepo.save(u)
                .map(id => Response.text(id))
        yield r

      // GET /transactions/:id
      case Method.GET -> !! / "transactions" / id =>
        TransactionRepo.lookup(id)
          .map {
            case Some(transaction) =>
              Response.json(transaction.toJson)
            case None =>
              Response.status(Status.NotFound)
          }
      // GET /transactions
      case Method.GET -> !! / "transactions" =>
        TransactionRepo.transactions.map(response => Response.json(response.toJson))



      //POST /transactional-check  -d '{"src": "1", "dst": "1", "amount" : 10}'
      case req@(Method.POST -> !! / "transactional-check") => {
        val filePath = "C:\\Users\\admin\\zio-quickstart-restful-webservice\\src\\main\\resources\\blacklist.txt"
        for
          u <- req.bodyAsString.map(_.fromJson[Transaction])
          q <- u match
            case Left(e) =>
              ZIO.debug(s"Failed to parse the input: $e").as(
                Response.text(e).setStatus(Status.BadRequest)
              )
            case Right(u) if (findIntoFile(u.src,u.dst,filePath)) => ZIO.succeed(Response.text("Cancel"))
            case _ => ZIO.succeed(Response.text("Success" ))
          
        yield q
      }



    }