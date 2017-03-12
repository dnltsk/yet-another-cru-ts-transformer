package org.dnltsk.yetanothercrutstransformer

import com.google.inject.Guice
import com.google.inject.Inject
import org.dnltsk.yetanothercrutstransformer.read.Parser
import org.dnltsk.yetanothercrutstransformer.write.GridDbService
import org.slf4j.LoggerFactory
import java.io.FileNotFoundException

open class Application @Inject constructor(
        val parser: Parser,
        val gridDbService: GridDbService
) {

    companion object {
        val LOG = LoggerFactory.getLogger(this::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val injector = Guice.createInjector(Module())
            val app = injector.getInstance(Application::class.java)
            app.run(*args)
        }
    }

    fun run(vararg args: String?) {
        LOG.info("# Yet Another CRU TS Transformer")
        if (args.isEmpty()){
            throw FileNotFoundException("input file not provided!")
        }
        val cruTs = parser.parse(filename = args.get(0))
        gridDbService.persist(cruTs.points)

    }


}