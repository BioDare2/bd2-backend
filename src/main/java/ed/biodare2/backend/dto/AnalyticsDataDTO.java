package ed.biodare2.backend.dto;

public class AnalyticsDataDTO {
    private String country;
    private long sessions;

    public AnalyticsDataDTO(String country, long sessions) {
        this.country = country;
        this.sessions = sessions;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getSessions() {
        return sessions;
    }

    public void setSessions(long sessions) {
        this.sessions = sessions;
    }
}
