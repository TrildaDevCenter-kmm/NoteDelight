package com.softartdev.notedelight.ui.screen.dialog

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.ui.descTagTriple

@JvmInline
value class EnterPasswordDialog(val commonDialog: CommonDialog) : CommonDialog by commonDialog {

    val enterPasswordSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(enterFieldTag, useUnmergedTree = true)

    val enterLabelSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(enterLabelTag, useUnmergedTree = true)

    val enterVisibilitySNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithTag(enterVisibilityTag, useUnmergedTree = true)

    companion object {
        private val enterTags = MR.strings.enter_password.descTagTriple()
        private val enterLabelTag = enterTags.first
        private val enterVisibilityTag = enterTags.second
        private val enterFieldTag = enterTags.third
    }
}