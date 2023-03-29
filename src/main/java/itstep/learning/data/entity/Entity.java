package itstep.learning.data.entity;

import java.text.SimpleDateFormat;

public class Entity {
    public static final SimpleDateFormat sqlDatetimeFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final SimpleDateFormat Iso8601DatetimeFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
}
