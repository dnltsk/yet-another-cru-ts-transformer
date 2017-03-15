package org.dnltsk.yetanothercrutstransformer.read

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.dnltsk.yetanothercrutstransformer.GoldenTestData
import org.dnltsk.yetanothercrutstransformer.MockedModels
import org.dnltsk.yetanothercrutstransformer.model.Period
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.FileNotFoundException

class ParserTest {

    @Mock
    lateinit var metadataParser: MetadataParser

    @Mock
    lateinit var gridParser: GridParser

    @InjectMocks
    lateinit var parser: Parser

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        `when`(metadataParser.parse(any<List<String>>())).thenReturn(MockedModels.SAMPLE_METADATA)
        `when`(gridParser.parse(any<List<String>>(), any<Period>())).thenReturn(MockedModels.EMPTY_GRID)
    }

    @Test
    fun non_existing_file_throws_FileNotFoundException() {
        assertThatThrownBy {
            parser.parse("file-does-not-exist.txt")
        }.isInstanceOf(
                FileNotFoundException::class.java)
                .hasMessageStartingWith("cannot access ")
                .hasMessageEndingWith("file-does-not-exist.txt")
    }

    @Test
    fun existing_file_does_not_throw_FileNotFoundException() {
        parser.parse(GoldenTestData.sampleCruTsPrePath())
        //no exception. fine.
    }

    @Test
    fun read_uses_metadata_and_grid_readers_once() {
        parser.parse(GoldenTestData.sampleCruTsPrePath())
        verify(metadataParser, times(1)).parse(any())
        verify(gridParser, times(1)).parse(any(), any())
    }


}