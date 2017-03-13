package org.dnltsk.yetanothercrutstransformer.write

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import org.dnltsk.yetanothercrutstransformer.MockedModels.Companion.SAMPLE_CRU_TS
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


class DbServiceTest {

    @Mock
    lateinit var metadataRepository: MetadataRepository

    @Mock
    lateinit var pointRepository: PointRepository

    @InjectMocks
    lateinit var dbService: DbService

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        `when`(metadataRepository.insertMetadata(any(), any())).thenReturn(666)
    }

    @Test
    fun metadata_and_points_are_passed_correctly() {
        dbService.persist(SAMPLE_CRU_TS)
        verify(metadataRepository).insertMetadata(any(), eq(SAMPLE_CRU_TS.metadata))
        verify(pointRepository).insertPoints(any(), eq(SAMPLE_CRU_TS.points), eq(666))
    }
}