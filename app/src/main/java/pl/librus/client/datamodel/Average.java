package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Embedded;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import pl.librus.client.api.IdDeserializer;

/**
 * Created by szyme on 08.12.2016. librus-client
 */
@Entity(builder = ImmutableAverage.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableAverage.class)
public abstract class Average implements Identifiable {

    @Key
    @JsonDeserialize(using = IdDeserializer.class)
    public abstract String subject();

    public abstract double semester1();

    public abstract double semester2();

    public abstract double fullYear();

    public String id(){
        return subject();
    }

}
