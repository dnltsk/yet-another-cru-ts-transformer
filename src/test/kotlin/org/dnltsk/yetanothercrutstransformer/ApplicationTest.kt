package org.dnltsk.yetanothercrutstransformer

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.AssertionsForClassTypes
import org.dnltsk.yetanothercrutstransformer.MockedModels.Companion.SAMPLE_CRU_TS
import org.dnltsk.yetanothercrutstransformer.read.Parser
import org.dnltsk.yetanothercrutstransformer.write.DbService
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class ApplicationTest {

    @Mock
    lateinit var parser: Parser

    @Mock
    lateinit var dbService: DbService

    @InjectMocks
    lateinit var application: Application

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(parser.parse(any())).thenReturn(SAMPLE_CRU_TS)
    }

    @Test
    fun metadata_and_grid_are_passed_correctly() {
        application.run(GoldenTestData.sampleCruTsPrePath())
        verify(parser).parse(any())
        verify(dbService).persist(eq(SAMPLE_CRU_TS))
    }

    @Test
    fun unprovided_file_argument_throws_IllegalArgumentException() {
        AssertionsForClassTypes.assertThatThrownBy {
            application.run(*emptyArray())
        }.isInstanceOf(
                IllegalArgumentException::class.java)
    }
}