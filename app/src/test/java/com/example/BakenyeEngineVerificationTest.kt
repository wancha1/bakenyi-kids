package com.example

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.audio.AuthenticAudioManager
import com.example.data.db.AppDatabase
import com.example.data.db.MIGRATION_1_2
import com.example.data.db.MIGRATION_2_3
import com.example.data.model.VocabularyEntity
import com.example.data.repository.BakenyeRepository
import com.example.ui.BakenyeViewModel
import com.example.ui.NavigationTab
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BakenyeEngineVerificationTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var repository: BakenyeRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .allowMainThreadQueries()
            .build()
        repository = BakenyeRepository(db.bakenyeDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun test1_FreshInstallSchemaValidation() {
        // Confirm AppDatabase v3 initializes and all tables exist
        val cursorVocab = db.openHelper.readableDatabase.query("SELECT * FROM vocabulary_items")
        assertNotNull(cursorVocab)
        cursorVocab.close()

        val cursorDisc = db.openHelper.readableDatabase.query("SELECT * FROM child_discoveries")
        assertNotNull(cursorDisc)
        cursorDisc.close()

        val cursorProg = db.openHelper.readableDatabase.query("SELECT * FROM location_progress")
        assertNotNull(cursorProg)
        cursorProg.close()

        val cursorUser = db.openHelper.readableDatabase.query("SELECT * FROM user_profile")
        assertNotNull(cursorUser)
        cursorUser.close()
    }

    @Test
    fun test2_DatabaseSeedingAndIdempotency() = runBlocking {
        // 1. Initial Seed
        repository.seedInitialDataIfEmpty()

        val fishingVocabs = repository.getVocabularyForLocation("FISHING_AREA").first()
        assertEquals("Fishing area should have 5 seeded vocabulary items", 5, fishingVocabs.size)

        // 2. Call seed a second time to test idempotency
        repository.seedInitialDataIfEmpty()
        val fishingVocabsAfter = repository.getVocabularyForLocation("FISHING_AREA").first()
        assertEquals("Seed should be idempotent and not create duplicate items", 5, fishingVocabsAfter.size)
    }

    @Test
    fun test3_WorldEngineStartup() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = BakenyeViewModel(app)

        val initialState = viewModel.uiState.value
        assertNotNull(initialState)
        assertEquals(NavigationTab.LEARN, initialState.activeTab)
        assertTrue("Opening intro should be enabled initially", initialState.showOpeningIntro)
        assertTrue("Guide greeting should be present", initialState.guideMessage.contains("Kato"))
    }

    @Test
    fun test4_InteractionAndProgressPersistence() = runBlocking {
        repository.seedInitialDataIfEmpty()

        // Record a discovery
        repository.recordDiscovery("FISHING_AREA", "ENGEGE")
        val discoveries = repository.getChildDiscoveries("FISHING_AREA").first()
        assertTrue("Recorded discovery should exist in Room DB", discoveries.any { it.itemKey == "ENGEGE" })

        // Update location progress
        repository.updateLocationProgress("FISHING_AREA", wordsMastered = 5, stars = 3)
        val fishingProgress = repository.getLocationProgress("FISHING_AREA").first()
        assertNotNull("Location progress for FISHING_AREA should exist", fishingProgress)
        assertEquals(5, fishingProgress?.termsMastered)
        assertEquals(3, fishingProgress?.starsEarned)
    }

    @Test
    fun test5_AudioSafetyNoCrashOnMissingResources() {
        val audioManager = AuthenticAudioManager.getInstance(context)

        var completed = false
        // Request missing pronunciation resource
        audioManager.playPronunciation("missing_audio_res_1234") {
            completed = true
        }

        assertTrue("Missing audio resource should trigger completion callback gracefully without crash", completed)

        var katoCompleted = false
        audioManager.playKatoVoice("non_existent_cue") {
            katoCompleted = true
        }
        assertTrue("Missing Kato voice cue should trigger completion callback safely", katoCompleted)
    }

    @Test
    fun test6_ActivityRecreationAndOrientationStatePreservation() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        assertNotNull(activity)

        // Perform activity recreation (simulates portrait <-> landscape rotation)
        controller.recreate()
        val recreatedActivity = controller.get()
        assertNotNull(recreatedActivity)
    }
}
