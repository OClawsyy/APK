package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.SolarSystemData
import com.example.model.SunBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Planetarium", appName)
  }

  @Test
  fun `solar system has 8 planets plus sun`() {
    assertEquals(8, SolarSystemData.size)
    assertEquals("Matahari", SunBody.name)
    
    val earth = SolarSystemData.find { it.id == "earth" }
    assertNotNull(earth)
    assertTrue(earth!!.isEarth)
    assertTrue(earth.milkyWayInfo.contains("Bima Sakti"))
    assertTrue(earth.easterEggBody.contains("Koperasi Desa Merah Putih"))
  }
}

