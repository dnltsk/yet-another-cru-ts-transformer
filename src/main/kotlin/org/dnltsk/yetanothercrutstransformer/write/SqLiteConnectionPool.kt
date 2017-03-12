package org.dnltsk.yetanothercrutstransformer.write

import com.google.inject.Singleton
import java.sql.Connection
import java.sql.DriverManager

@Singleton
class SqLiteConnectionPool {

    companion object {
        val TARGET_FILE = "cru-ts.sqlite"
    }

    fun getConnection(): Connection {
        return DriverManager.getConnection("jdbc:sqlite:$TARGET_FILE")
    }

}