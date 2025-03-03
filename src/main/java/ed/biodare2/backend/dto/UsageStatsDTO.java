package ed.biodare2.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class UsageStatsDTO {

    private int year;
    private long sets;
    private long series;
    private long public_sets;
    private long public_series;
    private long users;

    @JsonCreator
    public UsageStatsDTO(@JsonProperty("year") int year, 
                         @JsonProperty("sets") long sets, 
                         @JsonProperty("series") long series, 
                         @JsonProperty("public_sets") long public_sets, 
                         @JsonProperty("public_series") long public_series, 
                         @JsonProperty("users") long users) {
        this.year = year;
        this.sets = sets;
        this.series = series;
        this.public_sets = public_sets;
        this.public_series = public_series;
        this.users = users;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getSets() {
        return sets;
    }

    public void setSets(long sets) {
        this.sets = sets;
    }

    public long getSeries() {
        return series;
    }

    public void setSeries(long series) {
        this.series = series;
    }

    public long getPublic_sets() {
        return public_sets;
    }

    public void setPublic_sets(long public_sets) {
        this.public_sets = public_sets;
    }

    public long getPublic_series() {
        return public_series;
    }

    public void setPublic_series(long public_series) {
        this.public_series = public_series;
    }

    public long getUsers() {
        return users;
    }

    public void setUsers(long users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsageStatsDTO that = (UsageStatsDTO) o;
        return year == that.year &&
                sets == that.sets &&
                series == that.series &&
                public_sets == that.public_sets &&
                public_series == that.public_series &&
                users == that.users;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, sets, series, public_sets, public_series, users);
    }

}