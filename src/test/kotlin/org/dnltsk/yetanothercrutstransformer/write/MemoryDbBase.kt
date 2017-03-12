package org.dnltsk.yetanothercrutstransformer.write

import org.junit.After
import org.junit.Before
import java.sql.Connection
import java.sql.DriverManager

open class MemoryDbBase {

    lateinit var memoryConn: Connection
    @Before
    fun setUp() {
        memoryConn = DriverManager.getConnection("jdbc:sqlite::memory:")
        memoryConn.autoCommit = false
    }

    @After
    fun tearDown() {
        memoryConn.close()
    }

}