package com.cinevault.tv

data class CatalogItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val year: String,
    val rating: String,
    val category: String,
    val accent: Long
)

object Catalog {
    val movies = listOf(
        CatalogItem(1, "The Last Horizon", "A lone pilot crosses the edge of known space", "2025", "9.1", "Featured", 0xFF355C7D),
        CatalogItem(2, "Northern Lights", "A story of courage above the Arctic circle", "2024", "8.7", "Drama", 0xFF496A81),
        CatalogItem(3, "Red Signal", "Earth receives a message after 30 quiet years", "2023", "8.4", "Sci-Fi", 0xFF9E3D43),
        CatalogItem(4, "The Long Road", "Two strangers find a forgotten highway", "2025", "8.2", "Adventure", 0xFF9A6C43),
        CatalogItem(5, "Neon City", "An honest detective in a city of shadows", "2024", "8.8", "Crime", 0xFF5A3A76),
        CatalogItem(6, "Deep Blue", "An ocean rescue becomes a fight for survival", "2022", "7.9", "Thriller", 0xFF1F6B7A)
    )

    val continueWatching = listOf(
        movies[3], movies[1], movies[5], movies[2]
    )

    val trending = listOf(
        movies[4], movies[2], movies[0], movies[3], movies[5], movies[1]
    )

    fun search(query: String): List<CatalogItem> {
        val needle = query.trim().lowercase()
        if (needle.isEmpty()) return movies
        return movies.filter {
            it.title.lowercase().contains(needle) ||
                it.category.lowercase().contains(needle) ||
                it.subtitle.lowercase().contains(needle)
        }
    }
}
