package org.dnltsk.yetanothercrutstransformer

import com.google.inject.Guice
import com.google.inject.Inject
import org.dnltsk.yetanothercrutstransformer.read.Parser
import org.dnltsk.yetanothercrutstransformer.write.DbService
import org.slf4j.LoggerFactory

open class Application @Inject constructor(
        val parser: Parser,
        val dbService: DbService
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

    internal fun run(vararg args: String?) {
        LOG.info("----------------------------------")
        LOG.info("  Yet Another CRU TS Transformer ")
        LOG.info("----------------------------------")
        if (args.isEmpty()){
            throw IllegalArgumentException("input file not provided!")
        }
        val cruTs = parser.parse(filename = args.get(0))
        dbService.persist(cruTs)

    }


}