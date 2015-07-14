package sc.ala.http.mock

import java.util

/**
 * almost same as Array[Byte], but this allows easy equality
 */
final case class ArrayByte(value: Array[Byte]) {
  def ===(that: ArrayByte): Boolean = {
    util.Arrays.equals(this.value, that.value)
  }

  override def equals(that: Any): Boolean = that match {
    case array: ArrayByte => this.===(array)
    case array => false
  }

  override def hashCode: Int = util.Arrays.hashCode(value)

  def length: Int = value.length

  def copy(): ArrayByte = ArrayByte(value.clone())

  override def toString: String = {
    s"ArrayByte(length = ${value.length}, value = 0x${ArrayByte.byte2string(value, 32)})"
  }
}

object ArrayByte extends (Array[Byte] => ArrayByte) {
  def byte2string(bytes: Array[Byte], maxSize: Int): String = {
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
