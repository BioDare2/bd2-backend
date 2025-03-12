package ed.biodare2.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class SpeciesStatsDTO {

    private String species;
    private long sets;
    private long public_sets;
    private long series;
    private long public_series;

    @JsonCreator
    public SpeciesStatsDTO(@JsonProperty("species") String species, 
                           @JsonProperty("sets") long sets, 
                           @JsonProperty("public_sets") long public_sets, 
                           @JsonProperty("series") long series, 
                           @JsonProperty("public_series") long public_series) {
        this.species = species;
        this.sets = sets;
        this.public_sets = public_sets;
        this.series = series;
        this.public_series = public_series;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public long getSets() {
        return sets;
    }

    public void setSets(long sets) {
        this.sets = sets;
    }

    public long getPublic_sets() {
        return public_sets;
    }

    public void setPublic_sets(long public_sets) {
        this.public_sets = public_sets;
    }

    public long getSeries() {
        return series;
    }

    public void setSeries(long series) {
        this.series = series;
    }

    public long getPublic_series() {
        return public_series;
    }

    public void setPublic_series(long public_series) {
        this.public_series = public_series;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpeciesStatsDTO that = (SpeciesStatsDTO) o;
        return sets == that.sets &&
               public_sets == that.public_sets &&
               series == that.series &&
               public_series == that.public_series &&
               Objects.equals(species, that.species);
    }

    @Override
    public int hashCode() {
        return Objects.hash(species, sets, public_sets, series, public_series);
    }
}
