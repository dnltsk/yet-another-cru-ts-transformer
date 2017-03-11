package org.dnltsk.yetanothercrutstransformer

import com.google.inject.Guice
import com.google.inject.Inject
import org.dnltsk.yetanothercrutstransformer.read.Parser
import org.slf4j.LoggerFactory
import java.io.FileNotFoundException

open class Application @Inject constructor(
        val parser: Parser
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
        LOG.info("# Yet Another CRU TS Transformer")
        if (args.isEmpty()){
            throw FileNotFoundException("input file not provided!")
        }
        parser.read(filename = args.get(0))
    }


}