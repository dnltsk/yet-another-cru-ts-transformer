package org.dnltsk.yetanothercrutstransformer

import com.google.inject.Guice
import com.google.inject.Inject
import org.slf4j.LoggerFactory

open class CruTsApplication @Inject constructor(
        val cruTsFileReader: CruTsFileReader
){

    companion object{
        val LOG = LoggerFactory.getLogger(this::class.java)

        @JvmStatic
        fun main(args : Array<String>) {
            val injector = Guice.createInjector(CruTsModule())
            val app = injector.getInstance(CruTsApplication::class.java)
            app.run()
        }
    }

    fun run(vararg args: String?) {
        LOG.info("Hello, CRU TS")
        LOG.info("file = "+cruTsFileReader.file)
    }


}