package org.scalatra
package oauth2

import org.json4s._
import java.nio.charset.Charset
import rl.UrlCodingUtils
import mojolly.inflector.Inflector
import org.apache.commons.codec.binary.Hex

class OAuthString(s: String) {
  def blankOption = if (isBlank) None else Some(s)
  def isBlank = s == null || s.trim.isEmpty
  def nonBlank = !isBlank
  def asCheckboxBool = s.toUpperCase(ENGLISH) match {
    case "ON" | "TRUE" | "OK" | "1" | "CHECKED" ⇒ true
    case _                                      ⇒ false
  }
  def urlEncode: String = { // Encoding comforming to RFC 3986
    UrlCodingUtils.urlEncode(s)
  }
  def urlEncode(charset: Charset): String = { // Encoding comforming to RFC 3986
    UrlCodingUtils.urlEncode(s, charset)
  }
  def formEncode: String = { // This gives the same output as java.net.URLEncoder
    UrlCodingUtils.urlEncode(s, spaceIsPlus = true)
  }
  def formEncode(charset: Charset): String = { // This gives the same output as java.net.URLEncoder
    UrlCodingUtils.urlEncode(s, charset, spaceIsPlus = true)
  }
  def urlDecode: String = {
    UrlCodingUtils.urlDecode(s, plusIsSpace = false)
  }

  def formDecode: String = { // This gives the same output as java.net.URLDecoder
    UrlCodingUtils.urlDecode(s, plusIsSpace = true)
  }
  def urlDecode(charset: Charset): String = {
    UrlCodingUtils.urlDecode(s, charset, plusIsSpace = false)
  }

  def formDecode(charset: Charset): String = { // This gives the same output as java.net.URLDecoder
    UrlCodingUtils.urlDecode(s, charset, plusIsSpace = true)
  }

  def %%(params: Map[String, String]) = Inflector.interpolate(s, params)

  def hexDecode() = Hex.decodeHex(s.toCharArray)
}

class OAuthJValue(json: JValue) {

  import OAuth2Imports.string2InflectorString

  def camelizeKeys = rewriteJsonAST(true)
  def snakizeKeys = rewriteJsonAST(false)

  private def rewriteJsonAST(camelize: Boolean): JValue = {
    json transformField {
      case JField(nm, x) if !nm.startsWith("_") ⇒ JField(if (camelize) nm.camelize else nm.underscore, x)
      case x                                    ⇒ x
    }
  }
}

class OAuth2ByteArray(arr: Array[Byte]) {
  def hexEncode(toLowerCase: Boolean = true) = new String(Hex.encodeHex(arr, toLowerCase))
}