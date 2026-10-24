package com.example

import com.example.agricare.data.model.CropLibraryData
import com.example.agricare.localization.AppLanguage
import com.example.agricare.localization.getStrings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testLocalizationCompleteness() {
        val en = getStrings(AppLanguage.ENGLISH)
        val bn = getStrings(AppLanguage.BANGLA)

        assertEquals("AgriCare", en.appName)
        assertEquals("অ্যাগ্রিকেয়ার", bn.appName)

        assertFalse(en.navHome.isBlank())
        assertFalse(bn.navHome.isBlank())

        assertFalse(en.viewTreatmentPlan.isBlank())
        assertFalse(bn.viewTreatmentPlan.isBlank())

        assertFalse(en.reportDisclaimer.isBlank())
        assertFalse(bn.reportDisclaimer.isBlank())
    }

    @Test
    fun testCuratedCropLibraryGuides() {
        val guides = CropLibraryData.curatedGuides
        assertTrue(guides.size >= 5)

        val riceBlast = guides.find { it.id == "rice_blast" }
        assertNotNull(riceBlast)
        assertEquals("Rice", riceBlast?.cropName)
        assertEquals("Rice Blast", riceBlast?.diseaseTitle)
        assertTrue(riceBlast!!.symptoms.isNotEmpty())
        assertTrue(riceBlast.safeManagement.isNotEmpty())
        assertTrue(riceBlast.prevention.isNotEmpty())
    }
}
