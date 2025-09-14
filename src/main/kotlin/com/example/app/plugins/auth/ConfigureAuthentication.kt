package com.example.app.plugins.auth

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.app.plugins.routing.basicAuthEndpoint
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserHashedTableAuth
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.auth.bearer
import io.ktor.server.auth.digest
import io.ktor.server.auth.form
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.oauth
import io.ktor.server.response.respond
import io.ktor.util.getDigestFunction
import kotlinx.serialization.Serializable
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import kotlin.text.Charsets.UTF_8

const val AUTHENTICATION_BASIC = "my-basic-auth"
const val AUTHENTICATION_DIGEST = "my-digest-auth"
const val AUTHENTICATION_FORM = "my-form-auth"
const val AUTHENTICATION_BEARER = "my-bearer-auth"
const val AUTHENTICATION_JWT = "my-jwt-auth"
const val AUTHENTICATION_OAUTH = "my-oauth-auth"

/**
 * here we configure authentication,
 * ## Terms
 * * A principal is an entity that can be authenticated: a user, a computer, a service, etc.
 * In Ktor, various authentication providers might use different principals.
 * For example, the basic, digest, and form providers authenticate UserIdPrincipal
 * while the jwt provider verifies JWTPrincipal.
 * * A credential is a set of properties for a server to authenticate a principal:
 * a user/password pair, an API key, and so on.
 * For instance, the basic and form providers use UserPasswordCredential to validate a username and password
 * while jwt validates JWTCredential.
 * @see basicAuthEndpoint example usage of basic auth routing
 */
fun Application.configureAuthentication() {
    install(Authentication) {
        // basic auth provider
        basic(
            // this name is used to authenticate different routes
            name = AUTHENTICATION_BASIC
        ) {
            // provide information that the basic authentication scheme is used to protect a route
            realm = "Access to the '/' path"
            validate { credentials ->
                // validate, checks a specified credential and returns a principal (Any) or null if auth fails
                basicAuthHashedTable.authenticate(credentials)
//                if (credentials.name == "jetbrains" && credentials.password == "foobar") {
//                    UserIdPrincipal(credentials.name)
//                } else {
//                    null
//                }
            }
            skipWhen { call ->
                // called to skip the validation under certain conditions
                false
            }
        }
        digest(AUTHENTICATION_DIGEST) {
            realm = myRealm
            digestProvider { userName, realm ->
                digestAuthUserTable[userName]
            }

            validate { credentials ->
                if (credentials.userName.isNotEmpty()) {
                    CustomPrincipal(credentials.userName, credentials.realm)
                } else {
                    null
                }
            }
        }
        // form auth provider
        form(AUTHENTICATION_FORM) {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                if (credentials.name == "jetbrains" && credentials.password == "foobar") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized, "Credentials are not valid")
            }
        }
        // bearer auth provider
        bearer(AUTHENTICATION_BEARER) {
            realm = "Access to the '/' path"
            authenticate { tokenCredential ->
                if (tokenCredential.token == "abc123") {
                    UserIdPrincipal("jetbrains")
                } else {
                    null
                }
            }
        }
        val jwkProvider = JwkProviderBuilder("issuer")
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()
        jwt(AUTHENTICATION_JWT) {
            val secret = "secret" // environment.config.property("jwt.secret").getString()
            val issuer = "issuer" // environment.config.property("jwt.issuer").getString()
            val audience = "audience" // environment.config.property("jwt.audience").getString()
            val myRealm = "my realm" // environment.config.property("jwt.realm").getString()
            realm = myRealm
            // HS256 is used to sign the token
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            // RS256 is used to sign the token
            verifier(jwkProvider, issuer) {
                acceptLeeway(3)
            }
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
        oauth(AUTHENTICATION_OAUTH) {


        }
    }
}

@Serializable
private data class UserSession(val name: String, val count: Int)

/**
 * A digest function with the specified algorithm and salt provider
 */
private val basicAuthDigester = getDigestFunction("SHA-256") { salt ->
    "ktor${salt.length}"
}

/**
 * Initialize a new instance of UserHashedTableAuth and specify the following properties:
 * * Provide a table of usernames and hashed passwords using the table property.
 * * Assign a digest function to the digester property.
 */
private val basicAuthHashedTable = UserHashedTableAuth(
    table = mapOf(
        "jetbrains" to basicAuthDigester("foobar"),
        "admin" to basicAuthDigester("password")
    ),
    digester = basicAuthDigester
)

private fun getMd5Digest(str: String): ByteArray = MessageDigest
    .getInstance("MD5")
    .digest(str.toByteArray(UTF_8))

private const val myRealm = "Access to the '/' path"
private val digestAuthUserTable: Map<String, ByteArray> = mapOf(
    "jetbrains" to getMd5Digest("jetbrains:$myRealm:foobar"),
    "admin" to getMd5Digest("admin:$myRealm:password")
)

private data class CustomPrincipal(val userName: String, val realm: String)
