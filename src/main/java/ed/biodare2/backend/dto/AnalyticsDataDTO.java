package ed.biodare2.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class AnalyticsDataDTO {
    private String country;
    private int activeUsers;

    @JsonCreator
    public AnalyticsDataDTO(@JsonProperty("country") String country, @JsonProperty("activeUsers") int activeUsers) {
        this.country = country;
        this.activeUsers = activeUsers;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalyticsDataDTO that = (AnalyticsDataDTO) o;
        return activeUsers == that.activeUsers && Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, activeUsers);
    }

}
