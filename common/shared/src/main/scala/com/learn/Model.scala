package com.learn

import java.util.UUID
import scala.util.Random
import zio.schema.{DeriveSchema, Schema}

object Model:
  case class DataItemID(uuid: UUID)

  enum Review:
    case good, normal, bad
  end Review

  case class DataItem(id: DataItemID, label: String, price: Double, count: Int, review: Review):
    def fullPrice: Double = price * count
  end DataItem

  object DataItem:
    def apply(): DataItem = DataItem(
      DataItemID(UUID.randomUUID()), Random.nextString(2), Random.nextDouble(), Random.nextInt(5) + 1, Review.fromOrdinal(Random.nextInt(3))
    )
  end DataItem

  type DataList = List[DataItem]

  // Schemas used for JSON codecs
  given Schema[DataItemID] = DeriveSchema.gen[DataItemID]
  given Schema[DataItem]   = DeriveSchema.gen[DataItem]
  given Schema[DataList]   = DeriveSchema.gen[DataList]
end Model
