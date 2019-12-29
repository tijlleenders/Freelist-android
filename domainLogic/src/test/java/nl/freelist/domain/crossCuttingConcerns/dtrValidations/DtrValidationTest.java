package nl.freelist.domain.crossCuttingConcerns.dtrValidations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.valueObjects.DateTimeRange;
import nl.freelist.domain.valueObjects.DtrConstraint;

public class DtrValidationTest {


  @org.junit.Test
  public void testDtrValidations() {

    NoMondaysDtrValidation noMondaysDtrValidation = new NoMondaysDtrValidation();
    Result testNoMondaysResult;
    DateTimeRange dateTimeRangeToTest;

    dateTimeRangeToTest = DateTimeRange.Create(
        OffsetDateTime.of(
            LocalDate.of(2019, 12, 29),
            LocalTime.of(00, 00),
            ZoneOffset.UTC),
        OffsetDateTime.of(
            LocalDate.of(2019, 12, 29),
            LocalTime.of(23, 59),
            ZoneOffset.UTC)
    );
    testNoMondaysResult = noMondaysDtrValidation.validate(dateTimeRangeToTest);
    assertTrue(testNoMondaysResult.isSuccess());

    dateTimeRangeToTest = DateTimeRange.Create(
        OffsetDateTime.of(
            LocalDate.of(2019, 12, 28),
            LocalTime.of(23, 59),
            ZoneOffset.UTC),
        OffsetDateTime.of(
            LocalDate.of(2019, 12, 30),
            LocalTime.of(00, 00),
            ZoneOffset.UTC)
    );
    testNoMondaysResult = noMondaysDtrValidation.validate(dateTimeRangeToTest);
    assertFalse(testNoMondaysResult.isSuccess());

    dateTimeRangeToTest = DateTimeRange.Create(
        OffsetDateTime.of(
            LocalDate.of(2019, 12, 28),
            LocalTime.of(23, 59),
            ZoneOffset.UTC),
        OffsetDateTime.of(
            LocalDate.of(2019, 12, 30),
            LocalTime.of(12, 00),
            ZoneOffset.UTC)
    );
    testNoMondaysResult = noMondaysDtrValidation.validate(dateTimeRangeToTest);
    assertFalse(testNoMondaysResult.isSuccess());

    dateTimeRangeToTest = DateTimeRange.Create(
        OffsetDateTime.of(
            LocalDate.of(2019, 12, 30),
            LocalTime.of(23, 59),
            ZoneOffset.UTC),
        OffsetDateTime.of(
            LocalDate.of(2020, 01, 03),
            LocalTime.of(00, 00),
            ZoneOffset.UTC)
    );
    testNoMondaysResult = noMondaysDtrValidation.validate(dateTimeRangeToTest);
    assertFalse(testNoMondaysResult.isSuccess());

    EndsBeforeDateTimeDtrValidation endsBeforeDateTimeDtrValidation = new EndsBeforeDateTimeDtrValidation(
        OffsetDateTime.of(
            LocalDate.of(2020, 01, 03),
            LocalTime.of(00, 01),
            ZoneOffset.UTC));
    Result testEndsBeforDue = endsBeforeDateTimeDtrValidation.validate(dateTimeRangeToTest);
    assertTrue(testEndsBeforDue.isSuccess());

    List<DtrConstraint> dtrConstraintList;

    AndDtrValidation andDtrValidation = new AndDtrValidation(
        endsBeforeDateTimeDtrValidation,
        noMondaysDtrValidation
    );
    dtrConstraintList = andDtrValidation.list();
    assertEquals("[OPENBRACKET, DUEBEFORE, OPERATORAND, NOMONDAYS, CLOSEBRACKET]",
        dtrConstraintList.toString());
    assertFalse(andDtrValidation.validate(dateTimeRangeToTest).isSuccess());

    OrDtrValidation orDtrValidation = new OrDtrValidation(
        endsBeforeDateTimeDtrValidation,
        noMondaysDtrValidation
    );
    dtrConstraintList = orDtrValidation.list();
    assertEquals("[OPENBRACKET, DUEBEFORE, OPERATOROR, NOMONDAYS, CLOSEBRACKET]",
        dtrConstraintList.toString());
    assertTrue(orDtrValidation.validate(dateTimeRangeToTest).isSuccess());

  }
}