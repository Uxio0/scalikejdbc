package models

import scalikejdbc._
import org.joda.time.{ LocalDate, DateTime }

case class Member(
    id: Long,
    name: String,
    description: Option[String] = None,
    birthday: Option[LocalDate] = None,
    createdAt: DateTime) {

  def save(): Member = Member.save(this)

  def destroy(): Unit = Member.delete(this)

}

object Member {

  val tableName = "MEMBER"

  object columnNames {
    val id = "ID"
    val name = "NAME"
    val description = "DESCRIPTION"
    val birthday = "BIRTHDAY"
    val createdAt = "CREATED_AT"
    val all = Seq(id, name, description, birthday, createdAt)
  }

  val * = {
    import columnNames._
    (rs: WrappedResultSet) => Member(
      id = rs.long(id),
      name = rs.string(name),
      description = Option(rs.string(description)),
      birthday = Option(rs.date(birthday)).map(_.toLocalDate),
      createdAt = rs.timestamp(createdAt).toDateTime)
  }

  def find(id: Long): Option[Member] = {
    DB readOnly { implicit session =>
      SQL("""SELECT * FROM MEMBER WHERE ID = /*'id*/123""")
        .bindByName('id -> id).map(*).single.apply()
    }
  }

  def findAll(): List[Member] = {
    DB readOnly { implicit session =>
      SQL("""SELECT * FROM MEMBER""").map(*).list.apply()
    }
  }

  def countAll(): Long = {
    DB readOnly { implicit session =>
      SQL("""SELECT COUNT(1) FROM MEMBER""")
        .map(rs => rs.long(1)).single.apply().get
    }
  }

  def findBy(where: String, params: (Symbol, Any)*): List[Member] = {
    DB readOnly { implicit session =>
      SQL("""SELECT * FROM MEMBER WHERE """ + where)
        .bindByName(params: _*).map(*).list.apply()
    }
  }

  def countBy(where: String, params: (Symbol, Any)*): Long = {
    DB readOnly { implicit session =>
      SQL("""SELECT count(1) FROM MEMBER WHERE """ + where)
        .bindByName(params: _*).map(rs => rs.long(1)).single.apply().get
    }
  }

  def create(
    id: Long,
    name: String,
    description: Option[String] = None,
    birthday: Option[LocalDate] = None,
    createdAt: DateTime): Member = {
    DB localTx { implicit session =>
      SQL("""
        INSERT INTO MEMBER (
          ID,
          NAME,
          DESCRIPTION,
          BIRTHDAY,
          CREATED_AT
        ) VALUES (
          /*'id*/123,
          /*'name*/'abc',
          /*'description*/'xxx',
          /*'birthday*/'1980-04-06',
          /*'createdAt*/'2012-05-06 12:34:56' 
        )
      """)
        .bindByName(
          'id -> id,
          'name -> name,
          'description -> description,
          'birthday -> birthday,
          'createdAt -> createdAt
        ).update.apply()
      Member(
        id = id,
        name = name,
        description = description,
        birthday = birthday,
        createdAt = createdAt
      )
    }
  }

  def save(m: Member): Member = {
    DB localTx { implicit session =>
      SQL("""
        UPDATE 
          MEMBER
        SET 
          ID = /*'id*/123,
          NAME = /*'name*/'xxx',
          DESCRIPTION = /*'description*/'yyyy',
          BIRTHDAY = /*'birthday*/'1980-12-30',
          CREATED_AT = /*'createdAt*/'2012-05-04 12:23:34' 
        WHERE 
          ID = /*'id*/123
      """)
        .bindByName(
          'id -> m.id,
          'name -> m.name,
          'description -> m.description,
          'birthday -> m.birthday,
          'createdAt -> m.createdAt
        ).update.apply()
      m
    }
  }

  def delete(m: Member): Unit = {
    DB localTx { implicit session =>
      SQL("""DELETE FROM MEMBER WHERE ID = /*'id*/123""")
        .bindByName('id -> m.id).update.apply()
    }
  }

}

object MemberSQLTemplate {

  import Member._

  def find(): Option[Member] = {
    DB readOnly { implicit session =>
      SQL("""SELECT * FROM MEMBER WHERE ID = /*'id*/123""")
        .map(*).single.apply()
    }
  }

  def findAll(): List[Member] = {
    DB readOnly { implicit session =>
      SQL("""SELECT * FROM MEMBER""").map(*).list.apply()
    }
  }

  def countAll(): Long = {
    DB readOnly { implicit session =>
      SQL("""SELECT COUNT(1) FROM MEMBER""")
        .map(rs => rs.long(1)).single.apply().get
    }
  }

  def create(): Member = {
    DB localTx { implicit session =>
      SQL("""
        INSERT INTO MEMBER (
          ID,
          NAME,
          DESCRIPTION,
          BIRTHDAY,
          CREATED_AT
        ) VALUES (
          /*'id*/123,
          /*'name*/'abc',
          /*'description*/'xxx',
          /*'birthday*/'1980-04-06',
          /*'createdAt*/'2012-05-06 12:34:56'
        )
          """).update.apply()
    }
    Member.find(123).get
  }

  def save(): Member = {
    DB localTx { implicit session =>
      SQL("""
        UPDATE
          MEMBER
        SET
          ID = /*'id*/123,
          NAME = /*'name*/'xxx',
          DESCRIPTION = /*'description*/'yyyy',
          BIRTHDAY = /*'birthday*/'1980-12-30',
          CREATED_AT = /*'createdAt*/'2012-05-04 12:23:34'
        WHERE
          ID = /*'id*/123
          """).update.apply()
    }
    Member.find(123).get
  }

  def delete(): Unit = {
    DB localTx { implicit session =>
      SQL("""DELETE FROM MEMBER WHERE ID = /*'id*/123""")
        .update.apply()
    }
  }

}