package bootstrap.liftweb

import java.util.TimeZone

import com.fustigatedcat.tuft.web.model.SquerylMode._
import com.mchange.v2.c3p0.ComboPooledDataSource
import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.http.provider._
import net.liftweb.sitemap.Loc.If
import net.liftweb.sitemap._
import net.liftweb.util.{LiftFlowOfControlException, LoanWrapper, Props}
import org.slf4j.LoggerFactory
import org.squeryl.adapters.MySQLAdapter
import org.squeryl.{Session, SessionFactory}
import net.liftweb.util.Helpers._

import scala.language.postfixOps

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {

  val services = List()

  val logger = LoggerFactory.getLogger(classOf[Boot])

  def setupConnectionPool = {
    val cpds = new ComboPooledDataSource()
    cpds.setDriverClass("com.mysql.jdbc.Driver")
    cpds.setJdbcUrl(Props.get("database.jdbc.url").openOrThrowException("database.jdbc.url Required"))
    cpds.setUser(Props.get("database.username").openOrThrowException("database.username Required"))
    cpds.setPassword(Props.get("database.password").openOrThrowException("database.password Required"))
    cpds.setMinPoolSize(Props.getInt("database.pool.min").openOr(5))
    cpds.setAcquireIncrement(5)
    cpds.setMaxPoolSize(Props.getInt("database.pool.max").openOr(20))
    cpds.setTestConnectionOnCheckout(true)
    cpds
  }

  def setupDatabase : Boot = {
    val dataSource = setupConnectionPool
    SessionFactory.concreteFactory = Some(() =>
      Session.create(dataSource.getConnection, new MySQLAdapter())
    )

    S.addAround(new LoanWrapper {
      override def apply[T](f: => T): T = {
        val resultOrExcept = inTransaction {
          Session.currentSession.setLogger(logger.debug)
          try {
            Right(f)
          } catch {
            case e: LiftFlowOfControlException => Left(e)
          }
        }

        resultOrExcept match {
          case Right(result) => result
          case Left(except) => throw except
        }
      }
    })
    this
  }

  def createIndexPage = {
    Menu("Home") / "index"
  }

  def createLoginPage = {
    Menu("Login") / "login"
  }

  def createStaticPages = {
    Menu("Static") / "static" / **
  }

  def setupSiteMap : Boot = {
    LiftRules.setSiteMapFunc(() => SiteMap(
      createIndexPage,
      createLoginPage,
      createStaticPages
    ))
    this
  }

  def createServices = {
    val isUserLoggedIn : PartialFunction[Req, Unit] = {
      case _ /* if LoggedInUser.is.isDefined */ =>
    }

    services.foreach(service => LiftRules.dispatch.append(isUserLoggedIn guard service))

    this
  }

  def setupMisc : Boot = {
    // where to search snippet
    LiftRules.addToPackages("com.fustigatedcat.tuft.web")

    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

//    LiftRules.loggedInTest = Full(() => LoggedInUserId.is.isDefined)
    LiftRules.loggedInTest = Full(() => false)

/*    LiftRules.earlyInStateful.append({
      case Full(r) => LoggedInUser(User.getByOptionalId(LoggedInUserId.get))
    }) */

    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

    LiftRules.htmlProperties.default.set((r : Req) => new Html5Properties(r.userAgent))

    this
  }

  def boot {
    setupDatabase.setupSiteMap.createServices.setupMisc
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}
