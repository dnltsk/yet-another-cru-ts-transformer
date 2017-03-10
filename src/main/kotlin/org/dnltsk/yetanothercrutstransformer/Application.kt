package org.dnltsk.yetanothercrutstransformer

import com.google.inject.Guice
import com.google.inject.Inject
import org.dnltsk.yetanothercrutstransformer.read.Reader
import org.slf4j.LoggerFactory

open class Application @Inject constructor(
        val reader: Reader
) {

    companion object {
        val LOG = LoggerFactory.getLogger(this::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val injector = Guice.createInjector(Module())
            val app = injector.getInstance(Application::class.java)
            app.run()
        }
    }

    fun run(vararg args: String?) {
        LOG.info("Hello, CRU TS")
        if (args.isEmpty()){
            LOG.error("input file not provided!")
            System.exit(0)
        }
        reader.read(args.get(0))
    }


}