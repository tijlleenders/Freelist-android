package nl.freelist;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import nl.freelist.data.gson.Converters;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GsonOffsetDateTimeConverterTest {

  @Test
  public void addition_isCorrect() {
    Gson gson;
    gson = Converters.registerAll(new GsonBuilder()).create();
    OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);
    String offsetDateTimeString = gson.toJson(offsetDateTime);
    OffsetDateTime offsetDateTime1 = gson.fromJson(offsetDateTimeString, OffsetDateTime.class);

    assertEquals(offsetDateTime1.toString(), offsetDateTime.toString());
  }
}