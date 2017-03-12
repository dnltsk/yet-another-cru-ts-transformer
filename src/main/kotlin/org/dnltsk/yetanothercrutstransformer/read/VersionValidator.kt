package org.dnltsk.yetanothercrutstransformer.read

import com.google.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class VersionValidator {

    private val LOG = LoggerFactory.getLogger(this::class.java)

    companion object {
        val EXPECTED_VERSION = "CRU TS 2.1"
    }

    fun validateVersion(line: String): Boolean {
        val version = line.trim()
        LOG.info("version = $version")
        if (version != EXPECTED_VERSION) {
            LOG.warn("This parser expects version '$EXPECTED_VERSION' - results can be incorrect because you're using version '$version'")
            return false
        }
        return true
    }

}