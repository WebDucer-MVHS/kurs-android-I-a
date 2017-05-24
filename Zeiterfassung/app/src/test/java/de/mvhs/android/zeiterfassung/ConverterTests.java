package de.mvhs.android.zeiterfassung;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import de.mvhs.android.zeiterfassung.db.TimeContract;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConverterTests {
    // Rules
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void formatForDb_WithValidValue_ReturnIso8601String(){
        // Arrange
        final String expected = "2017-05-24T19:58";
        final Calendar inputTime = Calendar.getInstance();
        inputTime.set(2017, 4, 24, 19, 58, 26);

        // Act
        String result = TimeContract.Converters.formatForDb(inputTime);

        // Assert
        assertThat(result, is(expected));
    }

    @Test
    public void formatForDb_WithNullInput_ThrowsException(){
        // Arrange
        exception.expect(NullPointerException.class);

        // Act
        TimeContract.Converters.formatForDb(null);
    }

    @Test
    public void parseFromDb_WithNull_ThrowsExeption() throws ParseException {
        // Arrange
        exception.expect(NullPointerException.class);

        // Act
        TimeContract.Converters.parseFromDb(null);
    }

    @Test
    public void parseFromDb_WithWrongFormat_ThrowsException() throws ParseException {
        // Arrange
        final String dbValue = "2017-05-24 19:47";
        exception.expect(ParseException.class);

        // Act
        TimeContract.Converters.parseFromDb(dbValue);
    }

    @Test
    public void parseFromDb_WithValidValue_ReturnsCaldendar() throws ParseException {
        // Arrange
        final String dbValue = "2017-05-24T19:48";

        // Act
        Calendar result = TimeContract.Converters.parseFromDb(dbValue);

        // Assert
        assertThat("Year should be 2017", result.get(Calendar.YEAR), is(2017));
        assertThat("Month should be 4 (0 leaded)", result.get(Calendar.MONTH), is(4));
        assertThat("Day should be 24", result.get(Calendar.DAY_OF_MONTH), is(24));
        assertThat("Hour should be 19", result.get(Calendar.HOUR_OF_DAY), is(19));
        assertThat("Minnutes should be 48", result.get(Calendar.MINUTE), is(48));
        assertThat("Seconds should be 0 (not stored)", result.get(Calendar.SECOND), is(0));
        assertThat(result.get(Calendar.YEAR), is(not(2016)));
    }





















}
