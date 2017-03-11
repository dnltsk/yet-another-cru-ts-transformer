package org.dnltsk.yetanothercrutstransformer.read

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.dnltsk.yetanothercrutstransformer.GoldenTestData
import org.dnltsk.yetanothercrutstransformer.MockedModels
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.FileNotFoundException

class ReaderTest {

    @Mock
    lateinit var metadataParser: MetadataParser

    @Mock
    lateinit var pointParser: PointParser

    @InjectMocks
    lateinit var reader: Reader

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        `when`(metadataParser.parse(any<List<String>>())).thenReturn(MockedModels.mockedMetadata)
        `when`(pointParser.read(any<List<String>>())).thenReturn(MockedModels.mockedPoints)
    }

    @Test
    fun non_existing_file_throws_FileNotFoundException() {
        assertThatThrownBy {
            reader.read("file-does-not-exist.txt")
        }.isInstanceOf(
                FileNotFoundException::class.java)
                .hasMessageStartingWith("cannot access ")
                .hasMessageEndingWith("file-does-not-exist.txt")
    }

    @Test
    fun existing_file_does_not_throw_FileNotFoundException() {
        reader.read(GoldenTestData.pathToSampleCruTsFilePre())
        //no exception. fine.
    }

    @Test
    fun read_uses_metadata_and_point_readers_once() {
        reader.read(GoldenTestData.pathToSampleCruTsFilePre())
        verify(metadataParser, times(1)).parse(any())
        verify(pointParser, times(1)).read(any())
    }


}