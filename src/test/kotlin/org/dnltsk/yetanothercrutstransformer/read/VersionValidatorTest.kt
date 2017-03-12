package org.dnltsk.yetanothercrutstransformer.read

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test

class VersionValidatorTest {

    val validator = VersionValidator()

    @Test
    fun version_21_is_passes() {
        val isValid = validator.validateVersion(VersionValidator.EXPECTED_VERSION)
        assertThat(isValid).isTrue()
    }

    @Test
    fun version_not_21_is_fails() {
        val isValid = validator.validateVersion("foo version")
        assertThat(isValid).isFalse()
    }
}