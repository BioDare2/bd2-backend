package ed.biodare2.backend.dto;

public class AnalyticsDataDTO {
    private String country;
    private long activeUsers;

    public AnalyticsDataDTO(String country, long activeUsers) {
        this.country = country;
        this.activeUsers = activeUsers;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }
}
