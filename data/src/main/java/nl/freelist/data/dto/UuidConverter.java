package nl.freelist.data.dto;

import android.arch.persistence.room.TypeConverter;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UuidConverter {

  @TypeConverter
  public UUID toUuid(byte[] bytes) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    Long high = byteBuffer.getLong();
    Long low = byteBuffer.getLong();
    return new UUID(high, low);
  }

  @TypeConverter
  public byte[] fromUuid(UUID uuid) {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
    return bb.array();
//      Blob blob = new Blob();
//      blob.setBytes(0, bb.array());
//      return blob;
  }

}
