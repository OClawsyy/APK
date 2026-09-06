package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Data class representing a celestial body in the solar system.
 * Matches the user's data structure (id, name, r, size, color, dur, period, desc, comp)
 * with extensions for rich Gen Z styling, gradients, and custom visual effects.
 */
data class Planet(
    val id: String,
    val name: String,
    val r: Float, // Orbit radius distance ratio / px base (46 to 190)
    val size: Float, // Diameter size in dp/px (9 to 20)
    val color: Color,
    val secondaryColor: Color = color,
    val dur: Float, // Orbit period in seconds for 1 full revolution (6 to 42)
    val period: String, // Planetary orbital period label (e.g. "365 hari")
    val desc: String, // Casual, relaxed Gen Z educational description
    val comp: String, // Composition of the planet (gases, dust, core)
    val tagline: String = "",
    val hasRings: Boolean = false,
    val ringColor: Color = Color.Transparent,
    val isEarth: Boolean = false,
    val isSun: Boolean = false,
    val milkyWayInfo: String = "",
    val easterEggTag: String = "",
    val easterEggBody: String = ""
)

val SunBody = Planet(
    id = "sun",
    name = "Matahari",
    r = 0f,
    size = 36f,
    color = Color(0xFFF0A63A),
    secondaryColor = Color(0xFFFFE9A8),
    dur = 0f,
    period = "Pusat Orbit",
    desc = "Bintang induk raksasa di pusat tata surya kita. Menampung 99,8% massa tata surya dan memancarkan energi yang menghidupi Bumi!",
    comp = "73% Hidrogen, 25% Helium, plasma panas bersuhu ~5.500°C di permukaan.",
    tagline = "Si Bintang Utama ☀️",
    isSun = true
)

val SolarSystemData = listOf(
    Planet(
        id = "mercury",
        name = "Merkurius",
        r = 46f,
        size = 11f,
        color = Color(0xFF9C9088),
        secondaryColor = Color(0xFFC7BDB6),
        dur = 6f,
        period = "88 hari",
        desc = "Planet terkecil dan paling deket matahari. Nyaris nggak punya atmosfer, jadi suhunya ekstrem banget antara siang dan malam.",
        comp = "Permukaan batuan dan logam, atmosfer nyaris kosong (jejak natrium, oksigen)",
        tagline = "Si Kecil Gesit ⚡"
    ),
    Planet(
        id = "venus",
        name = "Venus",
        r = 66f,
        size = 14f,
        color = Color(0xFFE0C08A),
        secondaryColor = Color(0xFFF7DEB4),
        dur = 10f,
        period = "225 hari",
        desc = "Planet terpanas di tata surya karena efek rumah kaca super parah. Awannya bikin dia keliatan paling terang di langit malam.",
        comp = "Atmosfer 96% karbon dioksida, awan tebal asam sulfat",
        tagline = "Bintang Kejora Panas Membara 🔥"
    ),
    Planet(
        id = "earth",
        name = "Bumi",
        r = 88f,
        size = 15f,
        color = Color(0xFF3D8BCF),
        secondaryColor = Color(0xFF48C9B0),
        dur = 14f,
        period = "365 hari",
        desc = "Satu-satunya planet yang diketahui punya kehidupan. Air cair dan atmosfer seimbang bikin suhunya pas buat chill.",
        comp = "Atmosfer nitrogen dan oksigen, permukaan air (71%) dan daratan",
        tagline = "Home Sweet Home 🌍",
        isEarth = true,
        milkyWayInfo = "Bumi ada di galaksi Bima Sakti (Milky Way), salah satu dari ratusan miliar bintang di dalamnya.",
        easterEggTag = "Easter egg terverifikasi",
        easterEggBody = "Sertifikat sah: penduduk Bumi resmi jadi anggota Koperasi Desa Merah Putih galaksi Bima Sakti. 🌌🚜"
    ),
    Planet(
        id = "mars",
        name = "Mars",
        r = 110f,
        size = 13f,
        color = Color(0xFFC1440E),
        secondaryColor = Color(0xFFE67E22),
        dur = 18f,
        period = "687 hari",
        desc = "Planet merah karena debu besi oksida di permukaannya. Sering diincar buat misi luar angkasa dan koloni masa depan manusia.",
        comp = "Debu besi oksida (karat), atmosfer tipis karbon dioksida",
        tagline = "Si Planet Karat Merah 🚀"
    ),
    Planet(
        id = "jupiter",
        name = "Jupiter",
        r = 138f,
        size = 23f,
        color = Color(0xFFD2A679),
        secondaryColor = Color(0xFFEAD5B8),
        dur = 24f,
        period = "12 tahun",
        desc = "Planet terbesar, badannya raksasa gas. Bintik Merah Besarnya itu badai raksasa yang udah ada ratusan tahun dan belum kelar-kelar.",
        comp = "Hidrogen dan helium dominan, awan amonia",
        tagline = "The Big Boss Raksasa Gas 🌀"
    ),
    Planet(
        id = "saturn",
        name = "Saturnus",
        r = 168f,
        size = 20f,
        color = Color(0xFFE3C88E),
        secondaryColor = Color(0xFFF9E79F),
        dur = 30f,
        period = "29 tahun",
        desc = "Terkenal karena cincin es dan debunya yang megah, kelihatan jelas pakai teleskop kecil dari halaman rumah.",
        comp = "Hidrogen dan helium, cincin dari bongkahan es dan debu batuan",
        tagline = "Lord of the Rings 🪐",
        hasRings = true,
        ringColor = Color(0xCCEBD79E)
    ),
    Planet(
        id = "uranus",
        name = "Uranus",
        r = 198f,
        size = 17f,
        color = Color(0xFF9FD8D3),
        secondaryColor = Color(0xFFD1F2EB),
        dur = 36f,
        period = "84 tahun",
        desc = "Planet es raksasa yang porosnya miring ekstrem (98°), jadi rotasinya kayak 'menggelinding' di orbitnya.",
        comp = "Hidrogen, helium, dan metana (metana bikin warna biru kehijauan)",
        tagline = "Si Miring Mageran 🧊"
    ),
    Planet(
        id = "neptune",
        name = "Neptunus",
        r = 226f,
        size = 17f,
        color = Color(0xFF3F5EBF),
        secondaryColor = Color(0xFF5DADE2),
        dur = 42f,
        period = "165 tahun",
        desc = "Planet terjauh dan berangin paling kencang di tata surya, kecepatan anginnya bisa tembus lebih dari 2.000 km/jam!",
        comp = "Hidrogen, helium, dan metana, atmosfer badai super kencang",
        tagline = "Raja Badai Beku Super Deep 💨"
    )
)
