package nl.freelist.freelist;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import nl.freelist.domain.events.EntryCreatedEvent;
import org.junit.Test;
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

  @Test
  public void addition_isCorrect() {
    EntryCreatedEvent event = EntryCreatedEvent
        .Create(OffsetDateTime.now(ZoneOffset.UTC), "ownerUuid", "parentUuid", "aggregateId", 0);
    Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create();
    String json = gson.toJson(event, EntryCreatedEvent.class);

    LocalDateTime localDateTime = LocalDateTime.now();
    String test = gson.toJson(localDateTime, LocalDateTime.class);

    OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC).plusYears(1);
    assertEquals(test, "2019-11-25 09:39:00");
  }
}