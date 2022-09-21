package com.softartdev.notedelight

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import com.softartdev.mr.contextLocalized
import com.softartdev.notedelight.shared.base.IdlingResource.countingIdlingResource
import com.softartdev.notedelight.ui.descTagTriple
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.TestDescriptionHolder
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@FlakyTest
@RunWith(AndroidJUnit4::class)
class SettingsPasswordTest {

    private val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(TestDescriptionHolder)
        .around(DetectLeaksAfterTestSuccess())
        .around(composeTestRule)

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(countingIdlingResource)
        composeTestRule.registerIdlingResource(composeIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        composeTestRule.unregisterIdlingResource(composeIdlingResource)
        IdlingRegistry.getInstance().unregister(countingIdlingResource)
    }

    @Test
    fun settingPasswordTest() {
        composeTestRule.onNodeWithContentDescription(label = MR.strings.settings.contextLocalized())
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.pref_title_enable_encryption))
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOff()
            .performClick()

        val (confirmLabelTag, confirmVisibilityTag, confirmFieldTag) = MR.strings.enter_password.descTagTriple()
        val (confirmRepeatLabelTag, confirmRepeatVisibilityTag, confirmRepeatFieldTag) = MR.strings.confirm_password.descTagTriple()

        val confirmPasswordSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(confirmFieldTag, useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        val confirmLabelSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(confirmLabelTag, useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule.togglePasswordVisibility(confirmVisibilityTag)

        composeTestRule.onAllNodes(isRoot(), true).printToLog("🦄", maxDepth = Int.MAX_VALUE)

        val confirmRepeatPasswordSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(confirmRepeatFieldTag, useUnmergedTree = true)
            .assertIsDisplayed()

        val confirmRepeatLabelSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(confirmRepeatLabelTag, useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule.togglePasswordVisibility(confirmRepeatVisibilityTag)

        val confirmYesSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()

        composeTestRule.advancePerform(confirmYesSNI::performClick)

        confirmLabelSNI.assertTextEquals(context.getString(R.string.empty_password))

        confirmPasswordSNI.performTextReplacement(text = "1")
        Espresso.closeSoftKeyboard()

        composeTestRule.advancePerform(confirmYesSNI::performClick)

        confirmRepeatPasswordSNI.performClick()
        confirmRepeatPasswordSNI.performTextReplacement(text = "2")

        composeTestRule.onAllNodes(isRoot(), true).printToLog("🦄", maxDepth = Int.MAX_VALUE)
        confirmRepeatLabelSNI.assertTextEquals(context.getString(R.string.passwords_do_not_match))

        composeTestRule.advancePerform(confirmYesSNI::performClick)

        confirmRepeatLabelSNI.assertTextEquals(context.getString(R.string.passwords_do_not_match))

        confirmRepeatPasswordSNI.performTextReplacement(text = "1")
        composeTestRule.advancePerform(confirmYesSNI::performClick)

        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.pref_title_enable_encryption))
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()

        composeTestRule.onNodeWithText(text = MR.strings.pref_title_set_password.contextLocalized())
            .assertIsDisplayed()
            .performClick()

        val (changeOldLabelTag, changeOldVisibilityTag, changeOldFieldTag) = MR.strings.enter_old_password.descTagTriple()
        val (changeNewLabelTag, changeNewVisibilityTag, changeNewFieldTag) = MR.strings.enter_new_password.descTagTriple()
        val (changeRepeatNewLabelTag, changeRepeatNewVisibilityTag, changeRepeatNewFieldTag) = MR.strings.repeat_new_password.descTagTriple()

        val changeOldSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(changeOldFieldTag, useUnmergedTree = true)
            .assertIsDisplayed()

        val changeOldLabelSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(changeOldLabelTag, useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule.togglePasswordVisibility(changeOldVisibilityTag)

        val changeNewSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(changeNewFieldTag, useUnmergedTree = true)
            .assertIsDisplayed()

        val changeNewLabelSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(changeNewLabelTag, useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule.togglePasswordVisibility(changeNewVisibilityTag)

        val changeRepeatNewSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(changeRepeatNewFieldTag, useUnmergedTree = true)
            .assertIsDisplayed()

        val changeRepeatLabelSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(changeRepeatNewLabelTag, useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule.togglePasswordVisibility(changeRepeatNewVisibilityTag)

        val changeYesSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()

        composeTestRule.advancePerform(changeYesSNI::performClick)

        changeOldLabelSNI.assertTextEquals(context.getString(R.string.empty_password))
        changeOldSNI.performTextReplacement(text = "2")
        Espresso.closeSoftKeyboard()

        composeTestRule.advancePerform(changeYesSNI::performClick)

        changeNewLabelSNI.assertTextEquals(context.getString(R.string.empty_password))
        changeNewSNI.performTextReplacement(text = "2")
        Espresso.closeSoftKeyboard()

        composeTestRule.advancePerform(changeYesSNI::performClick)

        changeRepeatLabelSNI.assertTextEquals(context.getString(R.string.passwords_do_not_match))

        changeRepeatNewSNI.performTextReplacement(text = "2")
        Espresso.closeSoftKeyboard()

        composeTestRule.advancePerform(changeYesSNI::performClick)

        changeOldLabelSNI.assertTextEquals(context.getString(R.string.incorrect_password))
        changeOldSNI.performTextReplacement(text = "1")
        Espresso.closeSoftKeyboard()

        changeYesSNI.performClick()

        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.pref_title_enable_encryption))
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()
            .performClick()

        val (enterLabelTag, enterVisibilityTag, enterFieldTag) = MR.strings.enter_password.descTagTriple()

        val enterPasswordSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(enterFieldTag, useUnmergedTree = true)
            .assertIsDisplayed()

        val enterLabelSNI: SemanticsNodeInteraction = composeTestRule
            .onNodeWithTag(enterLabelTag, useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule.togglePasswordVisibility(enterVisibilityTag)

        val enterYesSNI = composeTestRule
            .onNodeWithText(text = context.getString(R.string.yes))
            .assertIsDisplayed()

        composeTestRule.advancePerform(enterYesSNI::performClick)

        enterLabelSNI.assertTextEquals(context.getString(R.string.empty_password))

        enterPasswordSNI.performTextReplacement(text = "1")

        composeTestRule.advancePerform(enterYesSNI::performClick)

        enterLabelSNI.assertTextEquals(context.getString(R.string.incorrect_password))

        enterPasswordSNI.performTextReplacement(text = "2")

        composeTestRule.advancePerform(enterYesSNI::performClick)

        composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.pref_title_enable_encryption))
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOff()
    }
}