package com_2is.egypt.wipegadmin.entites

import com.google.gson.Gson
import junit.framework.TestCase
import org.junit.Test

private const val TOKEN_RESPONSE = """{
 "access_token": "QORZCBI4fw9QT4SkZ6r90Qr-t2vvYHg9GNK22IVQ-HIOkoMTWV-4XBT6Ex-sYUPWPgF3W1LsUsoG5D4QZQKVDzfcR4YaPJ-93lwuwjsP5nWCB4d_jjptg9Jk3XG6KNyrghKWC8cgmFAO1dwv6zrjvz7KJwE67hhm6CVPoKiJuAwwYVPBGyk1x6X5MUoqhJhm1CNkMpuKKiiStBDwaRJozzsE2Gum_kg2UUEmACsu1ElIohVrlVKBTZTJG8iGoVQfIZnc62hI_eLOffBvHdxpAhmzNjhlkFWnICza58ptqQ2TmYaEscyAMgoelIau6R1u4WH-AnpVq5qTBTtaseZFnw",
    "token_type": "bearer",
    "expires_in": 2591999,
    "statusCode": "OK",
    "message": "Oracle server: Valid",
    ".issued": "Sun, 24 Jul 2021 10:34:33 GMT",
    ".expires": "Tue, 24 Aug 2021 10:34:33 GMT"
}"""

class ServerTokenTest : TestCase() {
    @Test
    fun `test parsing server token`() {
        //arrange
        val gson = Gson()
        //act
        val serverToken = gson.fromJson(TOKEN_RESPONSE, ServerToken::class.java)
        //assert
        assertEquals("OK", serverToken.statusCode)

    }
}