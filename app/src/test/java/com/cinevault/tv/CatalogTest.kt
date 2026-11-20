package com.cinevault.tv

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogTest {
    @Test
    fun emptySearchReturnsFullCatalog() {
        assertEquals(Catalog.movies, Catalog.search(""))
    }

    @Test
    fun searchMatchesTitleCaseInsensitively() {
        val results = Catalog.search("NEON")
        assertEquals(1, results.size)
        assertEquals("Neon City", results.single().title)
    }

    @Test
    fun searchMatchesCategory() {
        assertTrue(Catalog.search("sci-fi").isNotEmpty())
    }
}
