package eu.darken.sdmse.common.pkgs

import eu.darken.sdmse.common.pkgs.sources.SharedLibraryPkgSource
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import testhelpers.BaseTest

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SharedLibraryPkgSourceTest : BaseTest() {

    @Test
    fun `parse raw parcel for product_app`() {
        val raw = """
        ${'$'}   a n d r o i d . c o n t e n t . p m . S h a r e d L i b r a r y I n f o     ����-   com.google.android.trichromelibrary_484408833         2   /product/app/TrichromeLibrary/TrichromeLibrary.apk  #   com.google.android.trichromelibrary ~�       #   a n d r o i d . c o n t e n t . p m . V e r s i o n e d P a c k a g e   #   com.google.android.trichromelibrary ~�          #   a n d r o i d . c o n t e n t . p m . V e r s i o n e d P a c k a g e      com.google.android.webview  ~�       #   a n d r o i d . c o n t e n t . p m . V e r s i o n e d P a c k a g e      com.android.chrome  ~�    ����    
        """.trimIndent()

        val path = SharedLibraryPkgSource.getClawPatterns("com.google.android.trichromelibrary").firstNotNullOfOrNull {
            it.find(raw)?.groupValues?.getOrNull(1)
        }

        path shouldBe "/product/app/TrichromeLibrary/TrichromeLibrary.apk"
    }

    @Test
    fun `parse raw parcel for product_private_app`() {
        val raw = """
        ${'$'}   a n d r o i d . c o n t e n t . p m . S h a r e d L i b r a r y I n f o     ����   com.google.android.gms        %   /product/priv-app/GmsCore/GmsCore.apk      com.google.android.gms  ��������   #   a n d r o i d . c o n t e n t . p m . V e r s i o n e d P a c k a g e      com.google.android.gms  =]&    ��������    
        """.trimIndent()

        val path = SharedLibraryPkgSource.getClawPatterns("com.google.android.gms").firstNotNullOfOrNull {
            it.find(raw)?.groupValues?.getOrNull(1)
        }

        path shouldBe "/product/priv-app/GmsCore/GmsCore.apk"
    }

    @Test
    fun `parse raw parcel for system_app`() {
        val raw = """
        ${'$'}   a n d r o i d . c o n t e n t . p m . S h a r e d L i b r a r y I n f o     ����   com.google.android.ext.shared         /   /system/app/GoogleExtShared/GoogleExtShared.apk    android.ext.shared  ��������   #   a n d r o i d . c o n t e n t . p m . V e r s i o n e d P a c k a g e      com.google.android.ext.shared          ��������    
        """.trimIndent()

        val path = SharedLibraryPkgSource.getClawPatterns("com.google.android.ext.shared").firstNotNullOfOrNull {
            it.find(raw)?.groupValues?.getOrNull(1)
        }

        path shouldBe "/system/app/GoogleExtShared/GoogleExtShared.apk"
    }

    @Test
    fun `parse raw parcel for apex_app`() {
        val raw = """
        ${'$'}   a n d r o i d . c o n t e n t . p m . S h a r e d L i b r a r y I n f o     ����   com.android.cts.ctsshim       7   /apex/com.android.apex.cts.shim/app/CtsShim/CtsShim.apk &   com.android.cts.ctsshim.shared_library  ��������   #   a n d r o i d . c o n t e n t . p m . V e r s i o n e d P a c k a g e      com.android.cts.ctsshim        ��������   
        """.trimIndent()

        val path = SharedLibraryPkgSource.getClawPatterns("com.android.cts.ctsshim").firstNotNullOfOrNull {
            it.find(raw)?.groupValues?.getOrNull(1)
        }

        path shouldBe "/apex/com.android.apex.cts.shim/app/CtsShim/CtsShim.apk"
    }

    @Test
    fun `parse raw parcel for random`() {
        val raw = """
        ${'$'}������a��n��d��r��o��i��d��.��c��o��n��t��e��n��t��.��p��m��.��S��h��a��r��e��d��L��i��b��r��a��r��y��I��n��f��o��������������������com.android.cts.ctsshim��������������7������/random/com.android.apex.cts.shim/app/CtsShim/CtsShim.apk��&������com.android.cts.ctsshim.shared_library������������������#������a��n��d��r��o��i��d��.��c��o��n��t��e��n��t��.��p��m��.��V��e��r��s��i��o��n��e��d��P��a��c��k��a��g��e������������com.android.cts.ctsshim��������������������������������
        """.trimIndent()

        val path = SharedLibraryPkgSource.getClawPatterns("com.android.cts.ctsshim").firstNotNullOfOrNull {
            it.find(raw)?.groupValues?.getOrNull(1)
        }

        path shouldBe "/random/com.android.apex.cts.shim/app/CtsShim/CtsShim.apk"
    }
}