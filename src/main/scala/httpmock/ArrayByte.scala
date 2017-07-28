package httpmock

import java.util

import akka.util.ByteString

/**
 * almost same as Array[Byte], but this allows easy equality
 */
final case class ArrayByte(value: ByteString) {
  def ===(that: ArrayByte): Boolean = {
    this.value == that.value
  }

  override def equals(that: Any): Boolean = that match {
    case array: ArrayByte => this.===(array)
    case array => false
  }

  override def hashCode: Int = value.hashCode()

  def length: Int = value.length

  def copy(): ArrayByte = ArrayByte(value)

  override def toString: String = {
    s"ArrayByte(length = ${value.length}, value = 0x${ArrayByte.byte2string(value, 32)})"
  }
}

object ArrayByte extends (ByteString => ArrayByte) {
  def byte2string(bytes: ByteString, maxSize: Int): String = {
    val builder = new java.lang.StringBuilder
    var i = 0
    val len = bytes.length min maxSize
    while(i < len){
      builder.append("%02x".format(bytes(i) & 0xff))
      i += 1
    }
    builder.toString
  }
}
