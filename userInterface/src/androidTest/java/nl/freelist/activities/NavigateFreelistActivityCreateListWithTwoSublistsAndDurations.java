package nl.freelist.activities;


import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import nl.freelist.freelist.R;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NavigateFreelistActivityCreateListWithTwoSublistsAndDurations {

  @Rule
  public ActivityTestRule<NavigateFreelistActivity> mActivityTestRule = new ActivityTestRule<>(
      NavigateFreelistActivity.class);

  @Test
  public void deleteAllEntriesFromRepository() {
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

    ViewInteraction appCompatTextView = onView(
        allOf(withId(R.id.title), withText("Delete all entries"),
            childAtPosition(
                childAtPosition(
                    withId(R.id.content),
                    0),
                0),
            isDisplayed()));
    appCompatTextView.perform(click());
  }

  public void navigateFreelistActivityTest2() {
    ViewInteraction floatingActionButton = onView(
        allOf(withId(R.id.button_add_entry),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                1),
            isDisplayed()));
    floatingActionButton.perform(click());

    ViewInteraction appCompatEditText = onView(
        allOf(withId(R.id.edit_text_title),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                1),
            isDisplayed()));
    appCompatEditText.perform(typeText("test1"), closeSoftKeyboard());

    ViewInteraction appCompatEditText2 = onView(
        allOf(withId(R.id.edit_text_notes),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                2),
            isDisplayed()));
    appCompatEditText2.perform(typeText("description1"), closeSoftKeyboard());

    ViewInteraction numberPickerDuration2 = onView(
        allOf(withId(R.id.minute_picker),
            childAtPosition(
                childAtPosition(
                    withClassName(is("android.widget.LinearLayout")),
                    3),
                4),
            isDisplayed()));
    numberPickerDuration2.perform(longClick());

    ViewInteraction customEditText = onView(
        allOf(withClassName(is("android.widget.NumberPicker$CustomEditText")), withText("0"),
            childAtPosition(
                allOf(withId(R.id.minute_picker),
                    childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        4)),
                0),
            isDisplayed()));
    customEditText.perform(typeText("1"));

    ViewInteraction customEditText3 = onView(
        allOf(withClassName(is("android.widget.NumberPicker$CustomEditText")), withText("1"),
            childAtPosition(
                allOf(withId(R.id.minute_picker),
                    childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        4)),
                0),
            isDisplayed()));
    customEditText3.perform(pressImeActionButton());

    ViewInteraction appCompatImageButton = onView(
        allOf(withContentDescription("Navigate up"),
            childAtPosition(
                allOf(withId(R.id.action_bar),
                    childAtPosition(
                        withId(R.id.action_bar_container),
                        0)),
                1),
            isDisplayed()));
    appCompatImageButton.perform(click());

    onView(new RecyclerViewMatcher(R.id.recycler_view)
        .atPositionOnView(0, R.id.text_view_duration))
        .check(matches(withText("1m")));

    onView(new RecyclerViewMatcher(R.id.recycler_view)
        .atPositionOnView(0, R.id.text_view_description))
        .check(matches(withText("description1")));

    onView(new RecyclerViewMatcher(R.id.recycler_view)
        .atPositionOnView(0, R.id.text_view_title))
        .check(matches(withText("test1")));

    ViewInteraction recyclerView = onView(new RecyclerViewMatcher(R.id.recycler_view)
        .atPositionOnView(0, R.id.text_view_title));
    recyclerView.perform(click());

    ViewInteraction floatingActionButton2 = onView(
        allOf(withId(R.id.button_add_entry),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                1),
            isDisplayed()));
    floatingActionButton2.perform(click());

    ViewInteraction appCompatEditText3 = onView(
        allOf(withId(R.id.edit_text_title),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                1),
            isDisplayed()));
    appCompatEditText3.perform(typeText("subTest1"), closeSoftKeyboard());

    ViewInteraction numberPickerDuration = onView(
        allOf(withId(R.id.edit_text_duration),
            childAtPosition(
                allOf(withId(R.id.duration_picker),
                    childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        3)),
                3),
            isDisplayed()));
    numberPickerDuration.perform(longClick());

    ViewInteraction customEditText0 = onView(
        allOf(withClassName(is("android.widget.NumberPicker$CustomEditText")), withText("0"),
            childAtPosition(
                allOf(withId(R.id.edit_text_duration),
                    childAtPosition(
                        withId(R.id.duration_picker),
                        3)),
                0),
            isDisplayed()));
    customEditText0.perform(replaceText("1"));

    ViewInteraction customEditText1 = onView(
        allOf(withClassName(is("android.widget.NumberPicker$CustomEditText")), withText("1"),
            childAtPosition(
                allOf(withId(R.id.edit_text_duration),
                    childAtPosition(
                        withId(R.id.duration_picker),
                        3)),
                0),
            isDisplayed()));
    customEditText1.perform(closeSoftKeyboard());

    ViewInteraction customEditText2 = onView(
        allOf(withClassName(is("android.widget.NumberPicker$CustomEditText")), withText("1"),
            childAtPosition(
                allOf(withId(R.id.edit_text_duration),
                    childAtPosition(
                        withId(R.id.duration_picker),
                        3)),
                0),
            isDisplayed()));
    customEditText2.perform(pressImeActionButton());

    ViewInteraction appCompatImageButton2 = onView(
        allOf(withContentDescription("Navigate up"),
            childAtPosition(
                allOf(withId(R.id.action_bar),
                    childAtPosition(
                        withId(R.id.action_bar_container),
                        0)),
                1),
            isDisplayed()));
    appCompatImageButton2.perform(click());

    ViewInteraction appCompatTextView = onView(
        allOf(withId(R.id.breadcrumb_level_0_text), withText("Home"),
            childAtPosition(
                allOf(withId(R.id.breadcrumb_level_0),
                    childAtPosition(
                        withClassName(is("android.support.v7.widget.LinearLayoutCompat")),
                        0)),
                0),
            isDisplayed()));
    appCompatTextView.perform(click());

    onView(new RecyclerViewMatcher(R.id.recycler_view)
        .atPositionOnView(0, R.id.text_view_duration))
        .check(matches(withText("1h / 1m")));

    ViewInteraction recyclerView2 = onView(new RecyclerViewMatcher(R.id.recycler_view)
        .atPositionOnView(0, R.id.text_view_title));
    recyclerView2.perform(click());

    ViewInteraction floatingActionButton3 = onView(
        allOf(withId(R.id.button_add_entry),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                1),
            isDisplayed()));
    floatingActionButton3.perform(click());

    ViewInteraction appCompatEditText4 = onView(
        allOf(withId(R.id.edit_text_title),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                1),
            isDisplayed()));
    appCompatEditText4.perform(typeText("subTest2"), closeSoftKeyboard());

    ViewInteraction numberPickerDuration4 = onView(
        allOf(withId(R.id.day_picker),
            childAtPosition(
                childAtPosition(
                    withClassName(is("android.widget.LinearLayout")),
                    3),
                2),
            isDisplayed()));
    numberPickerDuration4.perform(longClick());

    ViewInteraction customEditText7 = onView(
        allOf(withClassName(is("android.widget.NumberPicker$CustomEditText")), withText("0"),
            childAtPosition(
                allOf(withId(R.id.day_picker),
                    childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        2)),
                0),
            isDisplayed()));
    customEditText7.perform(typeText("2"));

    ViewInteraction customEditText8 = onView(
        allOf(withClassName(is("android.widget.NumberPicker$CustomEditText")), withText("2"),
            childAtPosition(
                allOf(withId(R.id.day_picker),
                    childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        2)),
                0),
            isDisplayed()));
    customEditText8.perform(closeSoftKeyboard());

    ViewInteraction customEditText9 = onView(
        allOf(withClassName(is("android.widget.NumberPicker$CustomEditText")), withText("2"),
            childAtPosition(
                allOf(withId(R.id.day_picker),
                    childAtPosition(
                        withClassName(is("android.widget.LinearLayout")),
                        2)),
                0),
            isDisplayed()));
    customEditText9.perform(pressImeActionButton());

    ViewInteraction appCompatImageButton3 = onView(
        allOf(withContentDescription("Navigate up"),
            childAtPosition(
                allOf(withId(R.id.action_bar),
                    childAtPosition(
                        withId(R.id.action_bar_container),
                        0)),
                1),
            isDisplayed()));
    appCompatImageButton3.perform(click());

    ViewInteraction appCompatTextView2 = onView(
        allOf(withId(R.id.breadcrumb_level_0_text), withText("Home"),
            childAtPosition(
                allOf(withId(R.id.breadcrumb_level_0),
                    childAtPosition(
                        withClassName(is("android.support.v7.widget.LinearLayoutCompat")),
                        0)),
                0),
            isDisplayed()));
    appCompatTextView2.perform(click());

    onView(new RecyclerViewMatcher(R.id.recycler_view)
        .atPositionOnView(0, R.id.text_view_duration))
        .check(matches(withText("2d1h / 1m")));

  }

  private static Matcher<View> childAtPosition(
      final Matcher<View> parentMatcher, final int position) {

    return new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup && parentMatcher.matches(parent)
            && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }
}
