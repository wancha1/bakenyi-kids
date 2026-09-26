package com.example

import com.example.ui.sanctuary.PageTurnDirection
import com.example.ui.sanctuary.PhysicalPageTurnState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhysicalPageTurnTest {

    @Test
    fun testInitialState() {
        val state = PhysicalPageTurnState(pageCount = 5, initialPage = 0)
        assertEquals(0, state.currentPage)
        assertEquals(0f, state.turnFraction.value, 0.001f)
        assertFalse(state.isAnimating)
    }

    @Test
    fun testTurnToNext() = runTest {
        val state = PhysicalPageTurnState(pageCount = 4, initialPage = 0)
        val success = state.turnToNext()
        assertTrue(success)
        assertEquals(1, state.currentPage)
        assertEquals(0f, state.turnFraction.value, 0.001f)
        assertEquals(PageTurnDirection.FORWARD, state.turnDirection)
    }

    @Test
    fun testTurnToPrevious() = runTest {
        val state = PhysicalPageTurnState(pageCount = 4, initialPage = 2)
        val success = state.turnToPrevious()
        assertTrue(success)
        assertEquals(1, state.currentPage)
        assertEquals(0f, state.turnFraction.value, 0.001f)
        assertEquals(PageTurnDirection.BACKWARD, state.turnDirection)
    }

    @Test
    fun testBoundaryLimits() = runTest {
        val stateAtStart = PhysicalPageTurnState(pageCount = 3, initialPage = 0)
        val prevSuccess = stateAtStart.turnToPrevious()
        assertFalse(prevSuccess)
        assertEquals(0, stateAtStart.currentPage)

        val stateAtEnd = PhysicalPageTurnState(pageCount = 3, initialPage = 2)
        val nextSuccess = stateAtEnd.turnToNext()
        assertFalse(nextSuccess)
        assertEquals(2, stateAtEnd.currentPage)
    }

    @Test
    fun testSnapToPage() = runTest {
        val state = PhysicalPageTurnState(pageCount = 5, initialPage = 0)
        state.snapToPage(3)
        assertEquals(3, state.currentPage)
        assertEquals(0f, state.turnFraction.value, 0.001f)

        // Out of bounds should do nothing
        state.snapToPage(10)
        assertEquals(3, state.currentPage)
    }
}
