package de.mvhs.android.zeiterfassung;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EditActivityTests {
    @Mock
    EditActivity _context;

    @Test
    public void getTitle_ReturnExpectedTranslation(){
        // Arrange
        when(_context.getString(R.string.ExportDialogTitle))
                .thenReturn("test");
        when(_context.getActivityTitle())
                .thenCallRealMethod();

        // Act
        String translation = _context.getActivityTitle();

        // Assert
        assertThat(translation, is("test"));
    }
















}
