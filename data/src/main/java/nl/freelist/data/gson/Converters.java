/*
 * Copyright 2014 Greg Kopff
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nl.freelist.data.gson;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import nl.freelist.domain.valueObjects.Id;
import nl.freelist.domain.valueObjects.constraints.Constraint;


/**
 * The {@code Converters} class contains static methods for registering Java Time converters.
 */
@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
public class Converters {

  /**
   * The specific genericized type for {@code LocalDate}.
   */
  public static final Type LOCAL_DATE_TYPE = new TypeToken<LocalDate>() {
  }.getType();

  /**
   * The specific genericized type for {@code LocalDateTime}.
   */
  public static final Type LOCAL_DATE_TIME_TYPE = new TypeToken<LocalDateTime>() {
  }.getType();

  /**
   * The specific genericized type for {@code LocalTime}.
   */
  public static final Type LOCAL_TIME_TYPE = new TypeToken<LocalTime>() {
  }.getType();

  /**
   * The specific genericized type for {@code OffsetDateTime}.
   */
  public static final Type OFFSET_DATE_TIME_TYPE = new TypeToken<OffsetDateTime>() {
  }.getType();

  /**
   * The specific genericized type for {@code OffsetTime}.
   */
  public static final Type OFFSET_TIME_TYPE = new TypeToken<OffsetTime>() {
  }.getType();

  /**
   * The specific genericized type for {@code ZonedDateTime}.
   */
  public static final Type ZONED_DATE_TIME_TYPE = new TypeToken<ZonedDateTime>() {
  }.getType();

  /**
   * The specific genericized type for {@code Instant}.
   */
  public static final Type INSTANT_TYPE = new TypeToken<Instant>() {
  }.getType();

  /**
   * The specific genericized type for {@code Id}.
   */
  private static final Type ID_TYPE = new TypeToken<Id>() {
  }.getType();

  /**
   * The specific genericized type for {@code Constraint}.
   */
  private static final Type CONSTRAINT_TYPE = new TypeToken<Constraint>() {
  }.getType();


  /**
   * Registers all the Java Time converters.
   *
   * @param builder The GSON builder to register the converters with.
   * @return A reference to {@code builder}.
   */
  public static GsonBuilder registerAll(GsonBuilder builder) {
    if (builder == null) {
      throw new NullPointerException("builder cannot be null");
    }

    registerOffsetDateTime(builder);
    registerId(builder);
    registerConstraint(builder);
    return builder;
  }

  /**
   * Registers the {@link OffsetDateTimeConverter} converter.
   *
   * @param builder The GSON builder to register the converter with.
   * @return A reference to {@code builder}.
   */
  public static GsonBuilder registerOffsetDateTime(GsonBuilder builder) {
    builder.registerTypeAdapter(OFFSET_DATE_TIME_TYPE, new OffsetDateTimeConverter());

    return builder;
  }

  /**
   * Registers the {@link IdConverter} converter.
   *
   * @param builder The GSON builder to register the converter with.
   * @return A reference to {@code builder}.
   */
  public static GsonBuilder registerId(GsonBuilder builder) {
    builder.registerTypeAdapter(ID_TYPE, new IdConverter());
    return builder;
  }

  /**
   * Registers the {@link ConstraintConverter} converter.
   *
   * @param builder The GSON builder to register the converter with.
   * @return A reference to {@code builder}.
   */
  public static GsonBuilder registerConstraint(GsonBuilder builder) {
    builder.registerTypeAdapter(CONSTRAINT_TYPE, new ConstraintConverter());
    return builder;
  }
}
