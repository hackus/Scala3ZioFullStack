package com.learn

import com.learn.Model._
import zio.*
import zio.http.*
import zio.http.Middleware.CorsConfig
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

object Main extends ZIOAppDefault:
  private var dataList = List(DataItem(), DataItem(), DataItem())
  private val getData =
    Endpoint(Method.GET / "data").out[DataList].implement(Handler.succeed(dataList))
  private val postData =
    Endpoint(Method.POST / "data").in[DataList].out[Unit].implement(Handler.fromFunction[DataList](dataList = _))
  private val app = Routes(getData, postData).toHttpApp @@
    Middleware.cors(CorsConfig().copy(allowedOrigin = _ => Option(Header.AccessControlAllowOrigin.All)))

  override val run: ZIO[Any, Throwable, Nothing] =
    Server.serve(app).provide(Server.defaultWithPort(9000))
end Main



//@main
//def main(): Unit = {
//  println("Hello world!")
//}