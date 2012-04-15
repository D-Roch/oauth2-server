package io.backchat.oauth2
package scentry

import org.scalatra._
import OAuth2Imports._

object ScentryAuthStore {

  trait ScentryAuthStore {
    def get: String
    def set(value: String)
    def invalidate
  }

  class CookieAuthStore(app: ⇒ ScalatraBase with CookieSupport, cookieOptions: CookieOptions = CookieOptions(path = "/")) extends ScentryAuthStore {

    //    private def cookieDomain = if (config.domain == ".localhost") "localhost" else config.domain
    //    implicit val cookieOptions = CookieOptions(secure = secureOnly, httpOnly = true, path = "/", domain = cookieDomain)
    //
    def get = app.cookies.get(Scentry.scentryAuthKey) getOrElse ""

    def set(value: String) {
      app.response.addHeader("Set-Cookie", Cookie(Scentry.scentryAuthKey, value)(cookieOptions).toCookieString)
    }

    def invalidate {
      app.response.addHeader("Set-Cookie", toCookieString(Scentry.scentryAuthKey, options = cookieOptions.copy(maxAge = 0)))
    }

    def toCookieString(name: String, value: String = "", options: CookieOptions = cookieOptions) = {
      val sb = new StringBuffer
      sb append name append "="
      sb append value

      if (cookieOptions.domain != "localhost") sb.append("; Domain=").append(cookieOptions.domain)

      val pth = options.path
      if (pth.nonBlank) sb append "; Path=" append (if (!pth.startsWith("/")) {
        "/" + pth
      } else { pth })

      if (options.maxAge > -1) sb append "; Max-Age=" append options.maxAge

      if (options.secure) sb append "; Secure"
      if (options.httpOnly) sb append "; HttpOnly"
      sb.toString
    }
  }

  class SessionAuthStore(session: ⇒ Session) extends ScentryAuthStore {

    def get: String = {
      session.get(Scentry.scentryAuthKey).map(_.asInstanceOf[String]).orNull
    }
    def set(value: String) {
      session(Scentry.scentryAuthKey) = value
    }
    def invalidate {
      session.invalidate()
    }
  }
}
