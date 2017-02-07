package pl.librus.client.db;

import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedHashMap;
import java.util.Map;

import io.requery.meta.Attribute;
import io.requery.proxy.CompositeKey;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.AverageType;
import pl.librus.client.datamodel.EmbeddedId;
import pl.librus.client.datamodel.HasId;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.LessonSubject;
import pl.librus.client.datamodel.LessonTeacher;
import pl.librus.client.datamodel.LessonType;

/**
 * Created by robwys on 05/02/2017.
 */
@RunWith(AndroidJUnit4.class)
public class AverageTest extends BaseDBTest {
    @Test
    public void shouldReadAverage() {
        //given
        String subjectId = "123";
        final Average original = new Average.Builder()
                .fullYear(4.18)
                .semester1(3.66)
                .semester2(0)
                .subject(EmbeddedId.of(subjectId))
                .build();

        data.upsert(original);
        clearCache();

        //when
        Average result = data.select(Average.class)
                .where(AverageType.SUBJECT_ID.eq(subjectId))
                .get()
                .first();

        //then
        Assert.assertThat(result, Matchers.is(original));
    }

}