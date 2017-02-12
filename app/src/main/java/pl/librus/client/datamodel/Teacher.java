package pl.librus.client.datamodel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import java.io.Serializable;

import io.requery.Column;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;

@Entity
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(as = ImmutableTeacher.class)
public abstract class Teacher implements Persistable, Serializable {

    @Key
    public abstract String id();

    public abstract String firstName();

    public abstract String lastName();

    @Nullable
    @JsonProperty("IsSchoolAdministrator")
    public abstract Boolean schoolAdministrator();

    public static class Builder extends ImmutableTeacher.Builder {

    }

    public String name() {
        return firstName() != null && lastName() != null
                ? firstName() + ' ' + lastName()
                : id();
    }
}
