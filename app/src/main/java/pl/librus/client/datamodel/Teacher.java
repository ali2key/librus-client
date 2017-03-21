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

@Entity(builder = ImmutableTeacher.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableTeacher.class)
public abstract class Teacher implements Persistable, Serializable, Identifiable {

    @Key
    public abstract String id();

    @Nullable
    public abstract String firstName();

    @Nullable
    public abstract String lastName();

    @JsonProperty("IsSchoolAdministrator")
    @Value.Default
    public Boolean schoolAdministrator(){
        return false;
    };

    public String name() {
        return firstName() != null && lastName() != null
                ? firstName() + ' ' + lastName()
                : id();
    }
}
