package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Data class representing a celestial body in the solar system.
 * Includes physical and educational properties, orbital metrics, and composition details.
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
    val desc: String, // Casual, clear educational description
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
    desc = "Bintang induk di pusat tata surya kita. Menampung 99,8% massa seluruh tata surya dan memancarkan energi gravitasi serta radiasi hangat ke seluruh planet.",
    comp = "73% Hidrogen, 25% Helium, dan plasma panas bersuhu 5.500°C di fotosfer.",
    tagline = "Bintang Induk Tata Surya",
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
        desc = "Planet terkecil dan paling dekat dengan Matahari. Karena hampir tidak memiliki atmosfer penahan panas, fluktuasi suhunya sangat ekstrem antara siang dan malam.",
        comp = "Inti logam besi masif (70%), kerak silikat dan batuan padat.",
        tagline = "Planet Logam Terdekat"
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
        desc = "Planet terpanas di tata surya akibat efek rumah kaca tak terkendali. Lapisan awan tebal memantulkan cahaya matahari, menjadikannya objek paling terang setelah Bulan.",
        comp = "Atmosfer 96% karbon dioksida, awan asam sulfat pekat, dan batuan vulkanik.",
        tagline = "Bintang Kejora Berkabut Asam"
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
        desc = "Satu-satunya objek luar angkasa yang terbukti menopang kehidupan. Memiliki hidrosfer air cair yang stabil, atmosfer kaya oksigen, dan medan magnet protektif.",
        comp = "Atmosfer 78% nitrogen dan 21% oksigen, kerak silikat, dan 71% permukaan berupa air laut.",
        tagline = "Dunia Biosfer Berpenghuni",
        isEarth = true,
        milkyWayInfo = "Bumi terletak di Lengan Orion, sekitar 26.000 tahun cahaya dari pusat galaksi Bima Sakti (Milky Way).",
        easterEggTag = "Catatan Wilayah Khusus",
        easterEggBody = "Sertifikat sah: Penduduk Bumi resmi terdaftar di Koperasi Desa Merah Putih galaksi Bima Sakti."
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
        desc = "Planet merah yang permukaannya dipenuhi debu besi oksida. Memiliki ngarai Valles Marineris dan gunung berapi terbesar di tata surya, Olympus Mons.",
        comp = "Debu besi oksida (karat), batuan basal vulkanik, atmosfer tipis karbon dioksida.",
        tagline = "Dunia Merah Oksida Besi"
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
        desc = "Planet terbesar di tata surya kita. Badai antisiklon raksasa Great Red Spot telah berputar ratusan tahun. Gravitasinya melindungi planet bagian dalam dari tabrakan komet.",
        comp = "Gas hidrogen (90%), helium (10%), amonia, dan inti batuan terkompresi.",
        tagline = "Raksasa Gas Pengendali Gravitasi"
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
        desc = "Terkenal dengan cincin konsentris memukau yang tersusun dari miliaran bongkahan es dan debu angkasa. Kerapatan jenisnya lebih ringan dari air.",
        comp = "Hidrogen, helium, es air, dan cincin partikel es kristal reflektif.",
        tagline = "Sistem Cincin Es Megah",
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
        desc = "Raksasa es unik dengan kemiringan sumbu rotasi 98 derajat, sehingga tampak menggelinding di bidang orbitnya. Memiliki atmosfer terdingin di tata surya.",
        comp = "Fluida es air, metana, amonia, dengan atmosfer kaya hidrogen dan helium.",
        tagline = "Raksasa Es Sumbu Miring"
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
        desc = "Planet terluar berwarna biru pekat akibat metana atmosferik. Dikenal memiliki hembusan angin badai supersonik paling cepat di tata surya mencapai lebih dari 2.100 km/jam.",
        comp = "Mantel es air, amonia, metana tebal, dan badai gas atmosferik cepat.",
        tagline = "Dunia Badai Supersonik Terluar"
    )
)
