package io.github.mbannour.demo.results

import io.github.mbannour.result.{DeleteResult, InsertOneResult, UpdateResult}
import com.mongodb.client.result.{DeleteResult => JDeleteResult, InsertOneResult => JInsertOneResult, UpdateResult => JUpdateResult}
import org.bson.{BsonNull, BsonValue}

object MongoFakeResults {

  def acknowledgedDeleteOneResult(): DeleteResult = DeleteResult(new JDeleteResult {
    override def wasAcknowledged(): Boolean = true

    override def getDeletedCount: Long = 1L
  })

  def acknowledgedUpdateResult(): UpdateResult = UpdateResult(new JUpdateResult {
    override def wasAcknowledged(): Boolean = true

    override def getMatchedCount: Long = 1L

    override def getModifiedCount: Long = 1L

    override def getUpsertedId: BsonValue = new BsonNull
  })

  def acknowledgedInsertResult(): InsertOneResult = InsertOneResult(new JInsertOneResult {
    override def wasAcknowledged(): Boolean = true

    override def getInsertedId: BsonValue = new BsonNull
  })




}
